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
import models.UserAnswers
import pages.{UnderpaymentReasonBoxNumberPage, UnderpaymentReasonItemNumberPage}
import play.api.http.Status
import play.api.mvc.{AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, status}
import views.html.ItemNumberView

import scala.concurrent.Future

class ItemNumberControllerSpec extends ControllerSpecBase {

  val userAnswersWithItemNumber: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
    .set(UnderpaymentReasonBoxNumberPage, 33).success.value
    .set(UnderpaymentReasonItemNumberPage, 1).success.value
  )

  private def fakeRequestGenerator(itemNumber: String): FakeRequest[AnyContentAsFormUrlEncoded] =
    fakeRequest.withFormUrlEncodedBody(
      "itemNumber" -> itemNumber
    )

  trait Test extends MockSessionRepository {
    lazy val controller = new ItemNumberController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      mockSessionRepository,
      messagesControllerComponents,
      ItemNumberView,
      form
    )
    private lazy val ItemNumberView = app.injector.instanceOf[ItemNumberView]
    lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswersWithItemNumber)
    val formProvider: ItemNumberFormProvider = injector.instanceOf[ItemNumberFormProvider]
    MockedSessionRepository.set(Future.successful(true))
    val form: ItemNumberFormProvider = formProvider
  }

  "GET /" when {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }


    "return HTML" in new Test {
      override lazy val dataRetrievalAction = new FakeDataRetrievalAction(Some(UserAnswers("some-cred-id")))
      val result: Future[Result] = controller.onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

  }

  "POST /" when {

    "payload contains valid data" should {

      "return a SEE OTHER response when correct data with numeric only values" in new Test {
        lazy val result: Future[Result] = controller.onSubmit(fakeRequestGenerator("1"))
        status(result) mustBe Status.SEE_OTHER
      }

      "update the UserAnswers in session" in new Test {
        await(controller.onSubmit(fakeRequestGenerator("1")))
        verifyCalls()
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
