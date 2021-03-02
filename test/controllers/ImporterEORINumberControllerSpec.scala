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
import forms.ImporterEORINumberFormProvider
import mocks.repositories.MockSessionRepository
import models.UserAnswers
import pages.ImporterEORINumberPage
import play.api.http.Status
import play.api.mvc.{AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, redirectLocation, status}
import views.html.ImporterEORINumberView

import scala.concurrent.Future

class ImporterEORINumberControllerSpec extends ControllerSpecBase {

  val importerEORINumber = "GB345834921000"

  val userAnswersWithImporterEORINumber: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
    .set(
      ImporterEORINumberPage, importerEORINumber
    ).success.value
  )

  private def fakeRequestGenerator(importerEORI: String): FakeRequest[AnyContentAsFormUrlEncoded] =
    fakeRequest.withFormUrlEncodedBody(
      "importerEORI" -> importerEORI
    )

  trait Test extends MockSessionRepository {
    lazy val controller = new ImporterEORINumberController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      mockSessionRepository,
      messagesControllerComponents,
      form,
      ImporterEORINumberView
    )
    private lazy val ImporterEORINumberView = app.injector.instanceOf[ImporterEORINumberView]
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)
    val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id"))
    val formProvider: ImporterEORINumberFormProvider = injector.instanceOf[ImporterEORINumberFormProvider]
    MockedSessionRepository.set(Future.successful(true))
    val form: ImporterEORINumberFormProvider = formProvider
  }

  "GET onLoad" should {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      override val userAnswers: Option[UserAnswers] = userAnswersWithImporterEORINumber
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "POST onSubmit" when {

    "payload contains valid data" should {

      "return a SEE OTHER response when correct data with character values" in new Test {
        lazy val result: Future[Result] = controller.onSubmit()(fakeRequestGenerator(importerEORINumber))
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.NumberOfEntriesController.onLoad().url)

      }
      "update the UserAnswers in session" in new Test {
        override val userAnswers: Option[UserAnswers] = userAnswersWithImporterEORINumber
        await(controller.onSubmit()(fakeRequestGenerator(importerEORINumber)))
        verifyCalls()
      }
    }

    "payload contains invalid data" should {

      "return BAD REQUEST when no data is sent" in new Test {
        lazy val result: Future[Result] = controller.onSubmit()(fakeRequestGenerator(""))
        status(result) mustBe Status.BAD_REQUEST
      }

      "return BAD REQUEST when incorrect data is sent" in new Test {
        lazy val result: Future[Result] = controller.onSubmit()(fakeRequestGenerator("345834921000"))
        status(result) mustBe Status.BAD_REQUEST
      }

      "return BAD REQUEST when data exceeds max length" in new Test {
        lazy val result: Future[Result] = controller.onSubmit()(fakeRequestGenerator("GB3458349210002222222"))
        status(result) mustBe Status.BAD_REQUEST
      }

      "return BAD REQUEST" in new Test {
        lazy val result: Future[Result] = controller.onSubmit()(fakeRequest)
        status(result) mustBe Status.BAD_REQUEST
      }
    }
  }
}
