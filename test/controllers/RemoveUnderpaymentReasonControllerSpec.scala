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
import forms.RemoveUnderpaymentReasonFormProvider
import mocks.repositories.MockSessionRepository
import models.{ChangeUnderpaymentReason, UnderpaymentReason, UserAnswers}
import pages.{ChangeUnderpaymentReasonPage, UnderpaymentReasonsPage}
import play.api.http.Status
import play.api.mvc.{AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, redirectLocation, status}
import views.html.RemoveUnderpaymentReasonView

import scala.concurrent.Future


class RemoveUnderpaymentReasonControllerSpec extends ControllerSpecBase {

  private def fakeRequestGenerator(value: String): FakeRequest[AnyContentAsFormUrlEncoded] =
    fakeRequest.withFormUrlEncodedBody(
      "value" -> value
    )

  def underpaymentReason(boxNumber: Int, itemNumber: Int = 0, original: String = "50", amended: String = "60") = {
    UnderpaymentReason(boxNumber, itemNumber, original, amended)
  }

  trait Test extends MockSessionRepository {
    private lazy val removeUnderpaymentReasonView: RemoveUnderpaymentReasonView = app.injector.instanceOf[RemoveUnderpaymentReasonView]

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    val formProvider: RemoveUnderpaymentReasonFormProvider = injector.instanceOf[RemoveUnderpaymentReasonFormProvider]
    val form: RemoveUnderpaymentReasonFormProvider = formProvider

    MockedSessionRepository.set(Future.successful(true))

    lazy val controller = new RemoveUnderpaymentReasonController(authenticatedAction, dataRetrievalAction, dataRequiredAction,
      mockSessionRepository, messagesControllerComponents, form, removeUnderpaymentReasonView)
  }

  "GET onLoad" should {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
        .set(ChangeUnderpaymentReasonPage, ChangeUnderpaymentReason(underpaymentReason(boxNumber = 22), underpaymentReason(boxNumber = 22))).success.value
      )
      val result: Future[Result] = controller.onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

  }

  "POST onSubmit" when {
    "payload contains valid data" should {

      "return a SEE OTHER response when false" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "false")
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.ChangeUnderpaymentReasonController.onLoad().url)
      }

      "return a SEE OTHER response when true" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "redirect to Reason Underpayment Summary page" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId")
          .set(UnderpaymentReasonsPage, Seq(
            underpaymentReason(boxNumber = 35, itemNumber = 1),
            underpaymentReason(boxNumber = 35, itemNumber = 2))).success.value
          .set(ChangeUnderpaymentReasonPage, ChangeUnderpaymentReason(
            original = underpaymentReason(boxNumber = 35, itemNumber = 1),
            changed = underpaymentReason(boxNumber = 35, itemNumber = 1))).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result] = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(controllers.routes.UnderpaymentReasonSummaryController.onLoad().url)
      }

      "redirect to Box Guidance page" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId")
          .set(UnderpaymentReasonsPage, Seq(
            underpaymentReason(boxNumber = 35, itemNumber = 1))).success.value
          .set(ChangeUnderpaymentReasonPage, ChangeUnderpaymentReason(
            original = underpaymentReason(boxNumber = 35, itemNumber = 1),
            changed = underpaymentReason(boxNumber = 35, itemNumber = 1))).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result] = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(controllers.routes.BoxGuidanceController.onLoad().url)
      }

      "return an Internal Server Error" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.INTERNAL_SERVER_ERROR
      }

      "update the UserAnswers in session" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        await(controller.onSubmit(request))
        verifyCalls()
      }
    }

    "payload contains invalid data" should {
      "return a BAD REQUEST" in new Test {
        val result: Future[Result] = controller.onSubmit(fakeRequest)
        status(result) mustBe Status.BAD_REQUEST
      }
    }
  }

}



