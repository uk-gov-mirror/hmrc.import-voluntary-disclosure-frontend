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

import java.time.LocalDate

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import forms.EnterCustomsProcedureCodeFormProvider
import mocks.repositories.MockSessionRepository
import models.{ EntryDetails, UserAnswers}
import pages.EntryDetailsPage
import play.api.http.Status
import play.api.mvc.{AnyContentAsFormUrlEncoded, Call, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, status}
import views.html.EnterCustomsProcedureCodeView

import scala.concurrent.Future

class EnterCustomsProcedureCodeControllerSpec extends ControllerSpecBase {

  val userAnswersWithEntryDetails: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
    .set(
      EntryDetailsPage,
      EntryDetails("123", "123456Q", LocalDate of (2020, 1, 1))
    ).success.value
  )

  private def fakeRequestGenerator(cpc: String): FakeRequest[AnyContentAsFormUrlEncoded] =
    fakeRequest.withFormUrlEncodedBody(
     "cpc" -> cpc
    )

  trait Test extends MockSessionRepository {
    lazy val controller = new EnterCustomsProcedureCodeController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      appConfig,
      mockSessionRepository,
      messagesControllerComponents,
      EnterCustomsProcedureCodeView,
      form
    )
    private lazy val EnterCustomsProcedureCodeView = app.injector.instanceOf[EnterCustomsProcedureCodeView]
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)
    val userAnswers: Option[UserAnswers] = userAnswersWithEntryDetails
    val formProvider: EnterCustomsProcedureCodeFormProvider = injector.instanceOf[EnterCustomsProcedureCodeFormProvider]
    MockedSessionRepository.set(Future.successful(true))
    val form: EnterCustomsProcedureCodeFormProvider = formProvider
  }

  "GET /" when {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

    "the backLink functionality is called" should {
      "redirect to the Acceptance date page" in new Test {
        controller.backLink(Some(EntryDetails("false", "true", LocalDate of (2020, 1, 1)))) mustBe Call("GET", controllers.routes.AcceptanceDateController.onLoad().url)
      }

      "redirect to the Entry details page" in new Test {
        controller.backLink(Some(EntryDetails("false", "true", LocalDate of (2021, 1, 1)))) mustBe Call("GET", controllers.routes.EntryDetailsController.onLoad().url)
      }

    }

  }

  "POST /" when {

    "payload contains valid data" should {

      "return a SEE OTHER response when correct data with numeric only values" in new Test {
        override val userAnswers: Option[UserAnswers] = userAnswersWithEntryDetails
        lazy val result: Future[Result] = controller.onSubmit(fakeRequestGenerator("1234567"))
        status(result) mustBe Status.SEE_OTHER
      }
      "return a SEE OTHER response when correct data with an alphanumeric value" in new Test {
        override val userAnswers: Option[UserAnswers] = userAnswersWithEntryDetails
        lazy val result: Future[Result] = controller.onSubmit(fakeRequestGenerator("1234A12"))
        status(result) mustBe Status.SEE_OTHER
      }
      "update the UserAnswers in session" in new Test {
        override val userAnswers: Option[UserAnswers] = userAnswersWithEntryDetails
        await(controller.onSubmit(fakeRequestGenerator("1234567")))
        verifyCalls()
      }

    }

    "payload contains invalid data" should {

      "return BAD REQUEST when invalid data is sent" in new Test {
        override val userAnswers: Option[UserAnswers] = userAnswersWithEntryDetails
        val result: Future[Result] = controller.onSubmit(fakeRequestGenerator("123456!"))
        status(result) mustBe Status.BAD_REQUEST
      }

      "return BAD REQUEST when data is more than 7 in length" in new Test {
        override val userAnswers: Option[UserAnswers] = userAnswersWithEntryDetails
        val result: Future[Result] = controller.onSubmit(fakeRequestGenerator("12345678"))
        status(result) mustBe Status.BAD_REQUEST
      }

      "return BAD REQUEST when data there is an alpha character at the beginning" in new Test {
        override val userAnswers: Option[UserAnswers] = userAnswersWithEntryDetails
        val result: Future[Result] = controller.onSubmit(fakeRequestGenerator("A2345678"))
        status(result) mustBe Status.BAD_REQUEST
      }

      "return BAD REQUEST when data there is an alpha character at the end" in new Test {
        override val userAnswers: Option[UserAnswers] = userAnswersWithEntryDetails
        val result: Future[Result] = controller.onSubmit(fakeRequestGenerator("1234567A"))
        status(result) mustBe Status.BAD_REQUEST
      }

      "return BAD REQUEST" in new Test {
        val result: Future[Result] = controller.onSubmit(fakeRequest)
        status(result) mustBe Status.BAD_REQUEST
      }

    }

  }

}
