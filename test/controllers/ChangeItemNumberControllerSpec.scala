/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import forms.ItemNumberFormProvider
import mocks.repositories.MockSessionRepository
import models.{ChangeUnderpaymentReason, UnderpaymentReason, UserAnswers}
import pages.ChangeUnderpaymentReasonPage
import play.api.http.Status
import play.api.mvc.{AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, status}
import views.html.ItemNumberView

import scala.concurrent.Future

class ChangeItemNumberControllerSpec extends ControllerSpecBase {

  private def fakeRequestGenerator(itemNumber: String): FakeRequest[AnyContentAsFormUrlEncoded] =
    fakeRequest.withFormUrlEncodedBody(
      "itemNumber" -> itemNumber
    )

  trait Test extends MockSessionRepository {

    def underpayment(boxNumber: Int, itemNumber: Int = 0, original: String = "60", amended: String = "70"): UnderpaymentReason = {
      UnderpaymentReason(boxNumber, itemNumber, original, amended)
    }

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id"))

    lazy val controller = new ChangeItemNumberController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      mockSessionRepository,
      messagesControllerComponents,
      itemNumberView,
      form
    )
    private lazy val itemNumberView = app.injector.instanceOf[ItemNumberView]
    lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)
    val formProvider: ItemNumberFormProvider = injector.instanceOf[ItemNumberFormProvider]
    MockedSessionRepository.set(Future.successful(true))
    val form: ItemNumberFormProvider = formProvider
  }

  "GET onLoad" when {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }


    "return HTML" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
        .set(ChangeUnderpaymentReasonPage, ChangeUnderpaymentReason(
          underpayment(boxNumber = 35, itemNumber = 1),
          underpayment(boxNumber = 35, itemNumber = 1))).success.value
      )
      override lazy val dataRetrievalAction = new FakeDataRetrievalAction(Some(UserAnswers("some-cred-id")))
      val result: Future[Result] = controller.onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

  }

  "POST onSubmit" when {

    "payload contains valid data" should {

      "return a SEE OTHER response when correct data with numeric only values" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
          .set(ChangeUnderpaymentReasonPage, ChangeUnderpaymentReason(
            underpayment(boxNumber = 35, itemNumber = 1),
            underpayment(boxNumber = 35, itemNumber = 1))).success.value
        )
        lazy val result: Future[Result] = controller.onSubmit(fakeRequestGenerator("2"))
        status(result) mustBe Status.SEE_OTHER
      }

      "update the UserAnswers in session" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
          .set(ChangeUnderpaymentReasonPage, ChangeUnderpaymentReason(
            underpayment(boxNumber = 35, itemNumber = 1),
            underpayment(boxNumber = 35, itemNumber = 1))).success.value
        )
        await(controller.onSubmit(fakeRequestGenerator("2")))
        verifyCalls()
      }

      "return an Internal Server Error" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("itemNumber" -> "1")
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.INTERNAL_SERVER_ERROR
      }

    }

    "payload contains invalid data" should {

      "return BAD REQUEST" in new Test {
        val result: Future[Result] = controller.onSubmit(fakeRequest)
        status(result) mustBe Status.BAD_REQUEST
      }

    }

  }

}
