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
import models.{UnderpaymentReason, UserAnswers}
import pages.{UnderpaymentReasonBoxNumberPage, UnderpaymentReasonItemNumberPage, UnderpaymentReasonsPage}
import play.api.http.Status
import play.api.mvc.{AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, status}
import views.html.ItemNumberView

import scala.concurrent.Future

class ItemNumberControllerSpec extends ControllerSpecBase {

  private def fakeRequestGenerator(itemNumber: String): FakeRequest[AnyContentAsFormUrlEncoded] =
    fakeRequest.withFormUrlEncodedBody(
      "itemNumber" -> itemNumber
    )

  trait Test extends MockSessionRepository {

    private lazy val ItemNumberView = app.injector.instanceOf[ItemNumberView]

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id"))

    lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    val formProvider: ItemNumberFormProvider = injector.instanceOf[ItemNumberFormProvider]

    MockedSessionRepository.set(Future.successful(true))

    val form: ItemNumberFormProvider = formProvider

    lazy val controller = new ItemNumberController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      mockSessionRepository,
      messagesControllerComponents,
      ItemNumberView,
      form
    )
  }

  "GET /" when {
    "return OK" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("some-cred-id")
          .set(UnderpaymentReasonItemNumberPage, 1).success.value
      )
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

  }

  "POST /" when {

    "payload contains valid data" should {

      "return a SEE OTHER response when correct data with numeric only values" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("some-cred-id")
            .set(UnderpaymentReasonBoxNumberPage, 22).success.value
        )
        val result: Future[Result] = controller.onSubmit(fakeRequestGenerator("1"))
        status(result) mustBe Status.SEE_OTHER
        verifyCalls()
      }

//      "when there are existing underpayment reasons" in new Test {
//        override val userAnswers: Option[UserAnswers] = Some(
//          UserAnswers("some-cred-id")
//            .set(UnderpaymentReasonBoxNumberPage, 22).success.value
//            .set(UnderpaymentReasonsPage, Seq(UnderpaymentReason(22, 2, "40", "50"))).success.value
//        )
//        val result: Future[Result] = controller.onSubmit(fakeRequestGenerator("1"))
//        controller.existsSameBoxItem(33, 2, userAnswers.get) mustBe true
//        status(result) mustBe Status.OK
//      }

    }

    "payload contains invalid data" should {

      "return BAD REQUEST" in new Test {
        val result: Future[Result] = controller.onSubmit(fakeRequest)
        status(result) mustBe Status.BAD_REQUEST
      }

    }

  }

  "existsSameBoxItem" when {

    "current box and item number submitted matches existing item" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("some-cred-id")
          .set(UnderpaymentReasonBoxNumberPage, 22).success.value
          .set(UnderpaymentReasonItemNumberPage, 1).success.value
          .set(UnderpaymentReasonsPage, Seq(UnderpaymentReason(33, 2, "40", "50"))).success.value
      )
      controller.existsSameBoxItem(33, 2, userAnswers.get) mustBe true
    }

    "current box and item number submitted doesn't match an existing item" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("some-cred-id")
          .set(UnderpaymentReasonBoxNumberPage, 22).success.value
          .set(UnderpaymentReasonItemNumberPage, 1).success.value
          .set(UnderpaymentReasonsPage, Seq(UnderpaymentReason(33, 2, "40", "50"))).success.value
      )
      controller.existsSameBoxItem(33, 1, userAnswers.get) mustBe false
    }

    "there are no underpayment items" in new Test {
      controller.existsSameBoxItem(33, 1, userAnswers.get) mustBe false
    }

  }

}
