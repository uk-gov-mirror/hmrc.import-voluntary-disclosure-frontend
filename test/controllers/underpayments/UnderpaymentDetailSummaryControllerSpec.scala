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
import forms.underpayments.UnderpaymentDetailSummaryFormProvider
import mocks.repositories.MockSessionRepository
import models.UserAnswers
import models.underpayments.UnderpaymentDetail
import pages.underpayments.UnderpaymentDetailSummaryPage
import play.api.mvc.Result
import play.api.test.Helpers
import play.api.test.Helpers.{contentType, defaultAwaitTimeout, redirectLocation, status}
import play.mvc.Http.Status
import utils.ReusableValues
import views.html.underpayments.UnderpaymentDetailSummaryView

import scala.concurrent.Future

class UnderpaymentDetailSummaryControllerSpec extends ControllerSpecBase with ReusableValues {

  trait Test extends MockSessionRepository {
    private lazy val underpaymentDetailSummaryView: UnderpaymentDetailSummaryView = app.injector.instanceOf[UnderpaymentDetailSummaryView]

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    val formProvider: UnderpaymentDetailSummaryFormProvider = injector.instanceOf[UnderpaymentDetailSummaryFormProvider]
    val form: UnderpaymentDetailSummaryFormProvider = formProvider

    MockedSessionRepository.set(Future.successful(true))

    lazy val controller = new UnderpaymentDetailSummaryController(authenticatedAction, dataRetrievalAction, dataRequiredAction,
      messagesControllerComponents, underpaymentDetailSummaryView, form)
  }

  "GET onLoad" should {
    "return OK" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("credId").set(UnderpaymentDetailSummaryPage, Seq(UnderpaymentDetail("A00", 0.0, 1.0))).success.value
      )
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return SEE_OTHER when no UnderpaymentDetails exist" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("credId")
      )
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.SEE_OTHER
    }

    "return HTML" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("credId").set(UnderpaymentDetailSummaryPage, allUnderpaymentDetailsSelected()).success.value
      )
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      contentType(result) mustBe Some("text/html")
      Helpers.charset(result) mustBe Some("utf-8")
    }
  }

  "POST onSubmit" when {

    "payload contains valid data" should {

      "return a SEE OTHER Underpayment Type page when true is selected" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId").set(UnderpaymentDetailSummaryPage, Seq(UnderpaymentDetail("A00", 0.0, 1.0))).success.value
        )
        lazy val result: Future[Result] = controller.onSubmit()(
          fakeRequest.withFormUrlEncodedBody("value" -> "true")
        )
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.underpayments.routes.UnderpaymentTypeController.onLoad().url)
      }

      "return a SEE OTHER Box Guidance page when false is selected" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId").set(UnderpaymentDetailSummaryPage, Seq(UnderpaymentDetail("A00", 0.0, 1.0))).success.value
        )
        lazy val result: Future[Result] = controller.onSubmit()(
          fakeRequest.withFormUrlEncodedBody("value" -> "false")
        )
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.routes.BoxGuidanceController.onLoad().url)
      }

    }

    "payload contains invalid data" should {
      "return BAD REQUEST when no value is sent" in new Test {
        val result: Future[Result] = controller.onSubmit()(fakeRequest.withFormUrlEncodedBody("" -> ""))
        status(result) mustBe Status.BAD_REQUEST
      }
    }

  }

}
