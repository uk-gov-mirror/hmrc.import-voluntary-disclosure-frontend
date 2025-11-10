/*
 * Copyright 2025 HM Revenue & Customs
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

package controllers.reasons

import base.ControllerSpecBase
import config.AppConfig
import controllers.actions.FakeDataRetrievalAction
import forms.reasons.UnderpaymentReasonSummaryFormProvider
import mocks.config.MockAppConfig
import models.UserAnswers
import models.reasons.{BoxNumber, UnderpaymentReason}
import pages.CheckModePage
import pages.reasons.UnderpaymentReasonsPage
import play.api.http.Status
import play.api.mvc.{AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.data.reasons.UnderpaymentReasonSummaryData
import views.html.reasons.UnderpaymentReasonSummaryView

import scala.concurrent.Future

class UnderpaymentReasonSummaryControllerSpec extends ControllerSpecBase {

  trait Test {

    val testConfig                                       = appConfig
    private lazy val view: UnderpaymentReasonSummaryView = app.injector.instanceOf[UnderpaymentReasonSummaryView]
    private lazy val formProvider: UnderpaymentReasonSummaryFormProvider =
      app.injector.instanceOf[UnderpaymentReasonSummaryFormProvider]

    val userAnswers: Option[UserAnswers] = Some(
      UserAnswers("credId")
        .set(CheckModePage, false).success.value
        .set(
          UnderpaymentReasonsPage,
          Seq(
            UnderpaymentReason(boxNumber = BoxNumber.Box33, itemNumber = 15, original = "50", amended = "60"),
            UnderpaymentReason(boxNumber = BoxNumber.Box22, original = "50", amended = "60")
          )
        ).success.value
    )
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    lazy val controller = new UnderpaymentReasonSummaryController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      messagesControllerComponents,
      view,
      formProvider
    )
  }

  "GET onLoad" when {

    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

    "return Internal Server Error" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("credId").set(
          UnderpaymentReasonsPage,
          Seq(
            UnderpaymentReason(boxNumber = BoxNumber.Box33, itemNumber = 15, original = "50", amended = "60")
          )
        ).success.value
      )
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.OK
    }

  }

  "POST onSubmit" when {

    "payload contains valid data when check mode is false" should {

      "return a SEE OTHER on yes" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result]                      = controller.onSubmit()(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.reasons.routes.BoxNumberController.onLoad().url)
      }

      "return a SEE OTHER on Other Reason feature switch enabled" in new Test {
        override val testConfig: AppConfig                   = MockAppConfig.appConfig
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "false")
        lazy val result: Future[Result]                      = controller.onSubmit()(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.docUpload.routes.SupportingDocController.onLoad().url)
      }

    }

    "payload contains valid data when check mode is true" should {

      "return a SEE OTHER on yes" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("some-cred-id")
            .set(CheckModePage, true).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result]                      = controller.onSubmit()(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.reasons.routes.BoxNumberController.onLoad().url)
      }

      "return a SEE OTHER on no" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("some-cred-id")
            .set(CheckModePage, true).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "false")
        lazy val result: Future[Result]                      = controller.onSubmit()(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.cya.routes.CheckYourAnswersController.onLoad().url)
      }

    }

    "payload contains invalid data" should {
      "return a BAD REQUEST" in new Test {
        val result: Future[Result] = controller.onSubmit()(fakeRequest)
        status(result) mustBe Status.BAD_REQUEST
      }
    }

  }

  "summaryList" when {

    "single item is passed" should {
      "produce summary list with one item" in new Test {
        controller.buildSummaryList(
          UnderpaymentReasonSummaryData.singleItemReason
        ) mustBe UnderpaymentReasonSummaryData.singleItemSummaryList
      }
    }

    "multiple items are passed" should {
      "produce summary list with multiple items" in new Test {
        controller.buildSummaryList(
          UnderpaymentReasonSummaryData.multipleItemReason
        ) mustBe UnderpaymentReasonSummaryData.multipleItemSummaryList
      }
    }

    "no items are passed" should {
      "produce an empty summary list" in new Test {
        controller.buildSummaryList(None) mustBe None
      }
    }

  }

}
