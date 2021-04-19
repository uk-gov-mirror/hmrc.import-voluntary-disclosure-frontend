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

package controllers.underpayments

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import forms.underpayments.RemoveUnderpaymentDetailsFormProvider
import mocks.repositories.MockSessionRepository
import models.UserAnswers
import models.underpayments.UnderpaymentDetail
import pages.underpayments.UnderpaymentDetailSummaryPage
import play.api.http.Status
import play.api.mvc.{AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, redirectLocation, status}
import views.html.underpayments.RemoveUnderpaymentDetailsView

import scala.concurrent.Future


class RemoveUnderpaymentDetailsControllerSpec extends ControllerSpecBase {


  trait Test extends MockSessionRepository {
    private lazy val removeUnderpaymentDetailsView: RemoveUnderpaymentDetailsView = app.injector.instanceOf[RemoveUnderpaymentDetailsView]

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    val underpaymentType = "B00"

    val formProvider: RemoveUnderpaymentDetailsFormProvider = injector.instanceOf[RemoveUnderpaymentDetailsFormProvider]
    val form: RemoveUnderpaymentDetailsFormProvider = formProvider

    MockedSessionRepository.set(Future.successful(true))

    lazy val controller = new RemoveUnderpaymentDetailsController(authenticatedAction, dataRetrievalAction, dataRequiredAction,
      mockSessionRepository, messagesControllerComponents, form, removeUnderpaymentDetailsView)
  }

  "GET onLoad" should {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad(underpaymentType)(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      val result: Future[Result] = controller.onLoad(underpaymentType)(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

  }

  "POST onSubmit" when {
    "payload contains valid data" should {

      "return a SEE OTHER response when false" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
          .set(UnderpaymentDetailSummaryPage, Seq(UnderpaymentDetail("B00", 50, 60))).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "false")
        lazy val result: Future[Result] = controller.onSubmit(underpaymentType)(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.underpayments.routes.ChangeUnderpaymentDetailsController.onLoad(underpaymentType).url)
      }

      "redirect to Details Underpayment Summary page" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId")
          .set(UnderpaymentDetailSummaryPage, Seq(UnderpaymentDetail("B00", 50, 60))).success.value
          .set(UnderpaymentDetailSummaryPage, Seq(UnderpaymentDetail("A35", 50, 60))).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result] = controller.onSubmit(underpaymentType)(request)
        redirectLocation(result) mustBe Some(controllers.underpayments.routes.UnderpaymentDetailSummaryController.onLoad().url)
      }

      "redirect to Details Underpayment Start page" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId")
          .set(UnderpaymentDetailSummaryPage, Seq.empty).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result] = controller.onSubmit(underpaymentType)(request)
        redirectLocation(result) mustBe Some(controllers.underpayments.routes.UnderpaymentStartController.onLoad().url)
      }

      "return an Internal Server Error" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result] = controller.onSubmit(underpaymentType)(request)
        status(result) mustBe Status.INTERNAL_SERVER_ERROR
      }

      "update the UserAnswers in session" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId")
          .set(UnderpaymentDetailSummaryPage, Seq(UnderpaymentDetail("B00", 50, 60))).success.value
          .set(UnderpaymentDetailSummaryPage, Seq(UnderpaymentDetail("A35", 50, 60))).success.value
        )
        private val request = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        await(controller.onSubmit(underpaymentType)(request))
        verifyCalls()
      }
    }

    "payload contains invalid data" should {
      "return a BAD REQUEST" in new Test {
        val result: Future[Result] = controller.onSubmit(underpaymentType)(fakeRequest)
        status(result) mustBe Status.BAD_REQUEST
      }
    }
  }
}






