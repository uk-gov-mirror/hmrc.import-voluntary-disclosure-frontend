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
import forms.DefermentFormProvider
import messages.DefermentMessages
import mocks.repositories.MockSessionRepository
import models.{UnderpaymentType, UserAnswers, UserType}
import pages.{DefermentPage, UnderpaymentTypePage, UserTypePage}
import play.api.http.Status
import play.api.mvc.{AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, redirectLocation, status}
import services.FlowService
import views.html.DefermentView

import scala.concurrent.Future


class DefermentControllerSpec extends ControllerSpecBase {

  trait Test extends MockSessionRepository {
    private lazy val defermentView: DefermentView = app.injector.instanceOf[DefermentView]

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    val formProvider: DefermentFormProvider = injector.instanceOf[DefermentFormProvider]
    val form: DefermentFormProvider = formProvider
    val flowService: FlowService = app.injector.instanceOf[FlowService]

    MockedSessionRepository.set(Future.successful(true))

    lazy val controller = new DefermentController(authenticatedAction, dataRetrievalAction, dataRequiredAction,
      mockSessionRepository, messagesControllerComponents, form, flowService, defermentView)
  }

  val acceptanceDateYes: Boolean = true

  "GET onLoad" should {
    "return OK" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("some-cred-id")
          .set(DefermentPage, true).success.value
          .set(UnderpaymentTypePage, UnderpaymentType(true, false, false)).success.value
      )
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("some-cred-id")
          .set(DefermentPage, true).success.value
          .set(UnderpaymentTypePage, UnderpaymentType(true, false, false)).success.value
      )
      val result: Future[Result] = controller.onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

  }

  "Controller getHeaderMessage" should {

    "return VAT only header and title" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("some-cred-id")
          .set(DefermentPage, true).success.value
          .set(UnderpaymentTypePage, UnderpaymentType(false, true, false)).success.value
      )
      messages(controller.getHeaderMessage(userAnswers.get)) mustBe DefermentMessages.headingOnlyVAT
    }

    "return duty only header and title" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("some-cred-id")
          .set(DefermentPage, true).success.value
          .set(UnderpaymentTypePage, UnderpaymentType(true, false, false)).success.value
      )
      messages(controller.getHeaderMessage(userAnswers.get)) mustBe DefermentMessages.headingDutyOnly
    }

    "return duty and VAT header and title" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("some-cred-id")
          .set(DefermentPage, true).success.value
          .set(UnderpaymentTypePage, UnderpaymentType(true, true, false)).success.value
      )
      messages(controller.getHeaderMessage(userAnswers.get)) mustBe DefermentMessages.headingVATandDuty
    }

  }

  "POST onSubmit" when {
    "payload contains valid data" should {

      "return a SEE OTHER response when false" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "false")
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "return a SEE OTHER response when true" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
          .set(UserTypePage, UserType.Importer).success.value
          .set(UnderpaymentTypePage, UnderpaymentType(true, true, false)).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "return the correct location header when user is importer and has import VAT and custom duties" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
          .set(UserTypePage, UserType.Importer).success.value
          .set(UnderpaymentTypePage, UnderpaymentType(true, true, false)).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result] = controller.onSubmit(request)
      }

      "return the correct location header when user is representative and has import VAT and excise duty" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
          .set(UserTypePage, UserType.Representative).success.value
          .set(UnderpaymentTypePage, UnderpaymentType(false, true, true)).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result] = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(controllers.routes.SplitPaymentController.onLoad().url)
      }

      "return the correct location header when user is representative only has import VAT" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
          .set(UserTypePage, UserType.Representative).success.value
          .set(UnderpaymentTypePage, UnderpaymentType(false, true, false)).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result] = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(controllers.routes.SplitPaymentController.onLoad().url)
      }

      "return the correct location header when user is representative and other duties and no import VAT" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
          .set(UserTypePage, UserType.Representative).success.value
          .set(UnderpaymentTypePage, UnderpaymentType(true, false, true)).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result] = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(controllers.routes.SplitPaymentController.onLoad().url)
      }

      "return an internal server error when user is representative only has no underpayment type" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
          .set(UserTypePage, UserType.Representative).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.INTERNAL_SERVER_ERROR
      }

      "update the UserAnswers in session" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
          .set(UserTypePage, UserType.Importer).success.value
          .set(UnderpaymentTypePage, UnderpaymentType(true, true, false)).success.value
        )
        private val request = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        await(controller.onSubmit(request))
        verifyCalls()
      }
    }

    "payload contains invalid data" should {
      "return a BAD REQUEST" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("some-cred-id")
            .set(DefermentPage, true).success.value
            .set(UnderpaymentTypePage, UnderpaymentType(true, true, false)).success.value
        )
        val result: Future[Result] = controller.onSubmit(fakeRequest)
        status(result) mustBe Status.BAD_REQUEST
      }
    }
  }

}



