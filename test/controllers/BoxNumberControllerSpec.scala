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
import forms.BoxNumberFormProvider
import mocks.repositories.MockSessionRepository
import models.{UnderpaymentReason, UserAnswers}
import pages.{UnderpaymentReasonBoxNumberPage, UnderpaymentReasonsPage}
import play.api.http.Status
import play.api.mvc.{AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, redirectLocation, status}
import views.html.BoxNumberView

import scala.concurrent.Future

class BoxNumberControllerSpec extends ControllerSpecBase {

  val BOX_NUMBER = 22

  val underpaymentReasonBoxNumber: UserAnswers = UserAnswers("some-cred-id")
    .set(UnderpaymentReasonBoxNumberPage, BOX_NUMBER).success.value

  private def fakeRequestGenerator(value: String): FakeRequest[AnyContentAsFormUrlEncoded] =
    fakeRequest.withFormUrlEncodedBody(
      "value" -> value
    )

  trait Test extends MockSessionRepository {
    lazy val controller = new BoxNumberController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      mockSessionRepository,
      messagesControllerComponents,
      form,
      boxNumberView
    )
    val userAnswers: UserAnswers = UserAnswers("some-cred-id")
    private lazy val boxNumberView = app.injector.instanceOf[BoxNumberView]
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(Some(userAnswers))
    val formProvider: BoxNumberFormProvider = injector.instanceOf[BoxNumberFormProvider]
    MockedSessionRepository.set(Future.successful(true))
    val form: BoxNumberFormProvider = formProvider
  }

  "GET /" when {

    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      override val userAnswers: UserAnswers = UserAnswers("some-cred-id")
        .set(UnderpaymentReasonBoxNumberPage, BOX_NUMBER).success.value

      val result: Future[Result] = controller.onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

  }

  "POST /" when {

    "payload contains valid data" should {

      "return a SEE OTHER entry level response when correct data is sent" in new Test {
        lazy val result: Future[Result] = controller.onSubmit(
          fakeRequestGenerator("62")
        )
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.UnderpaymentReasonAmendmentController.onLoad(62).url)
      }

      "return a SEE OTHER item level response when correct data is sent" in new Test {
        override val userAnswers: UserAnswers = underpaymentReasonBoxNumber
        lazy val result: Future[Result] = controller.onSubmit(
          fakeRequestGenerator("33")
        )
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.ItemNumberController.onLoad().url)
      }

      "update the UserAnswers in session" in new Test {
        override val userAnswers: UserAnswers = underpaymentReasonBoxNumber
        await(controller.onSubmit(fakeRequestGenerator("22")))
        verifyCalls()
      }


    }

    "payload contains invalid data" should {

      "return BAD REQUEST when no value is sent" in new Test {
        val result: Future[Result] = controller.onSubmit(fakeRequestGenerator(""))
        status(result) mustBe Status.BAD_REQUEST
      }

    }

    "underpaymentReasonSelected call" should {
      "return true if a box Number exists " in new Test {
        val answers: UserAnswers = UserAnswers("some-cred-id")
          .set(UnderpaymentReasonsPage, Seq(UnderpaymentReason(BOX_NUMBER, 0, "", "")))
          .success.value

        controller.underpaymentReasonSelected(answers, BOX_NUMBER) mustBe true
      }

      "return false if userAnswers is empty " in new Test {
        controller.underpaymentReasonSelected(userAnswers, BOX_NUMBER) mustBe false
      }

    }

  }

}
