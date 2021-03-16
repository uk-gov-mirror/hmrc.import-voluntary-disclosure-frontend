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
import forms.ImporterDanFormProvider
import mocks.repositories.MockSessionRepository
import models.UserAnswers
import pages.DefermentAccountPage
import play.api.http.Status
import play.api.mvc.{AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, status}
import views.html.ImporterDanView

import scala.concurrent.Future

class ImporterDanControllerSpec extends ControllerSpecBase {

  val userAnswersWithImporterDan: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
    .set(DefermentAccountPage, "1234567").success.value
  )

  private def fakeRequestGenerator(dan: String): FakeRequest[AnyContentAsFormUrlEncoded] =
    fakeRequest.withFormUrlEncodedBody(
     "value" -> dan
    )

  trait Test extends MockSessionRepository {
    lazy val controller = new ImporterDanController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      mockSessionRepository,
      messagesControllerComponents,
      form,
      view
    )
    private lazy val view = app.injector.instanceOf[ImporterDanView]
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)
    val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id"))
    val formProvider: ImporterDanFormProvider = injector.instanceOf[ImporterDanFormProvider]
    MockedSessionRepository.set(Future.successful(true))
    val form: ImporterDanFormProvider = formProvider
  }

  "GET onLoad" when {
    "return OK when form not previously filled" in new Test {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return OK when form filled" in new Test {
      override val userAnswers: Option[UserAnswers] = userAnswersWithImporterDan
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "POST onSubmit" when {

    "payload contains valid data" should {

      "return a SEE OTHER response when correct data supplied" in new Test {
        lazy val result: Future[Result] = controller.onSubmit(fakeRequestGenerator("1234567"))
        status(result) mustBe Status.SEE_OTHER
      }
      "update the UserAnswers in session" in new Test {
        await(controller.onSubmit(fakeRequestGenerator("1234567")))
        verifyCalls()
      }

    }

    "payload contains invalid data" should {

      "return BAD REQUEST when invalid data is sent" in new Test {
        val result: Future[Result] = controller.onSubmit(fakeRequestGenerator("ABC"))
        status(result) mustBe Status.BAD_REQUEST
      }

      "return BAD REQUEST when data is more than 7 in length" in new Test {
        val result: Future[Result] = controller.onSubmit(fakeRequestGenerator("12345678"))
        status(result) mustBe Status.BAD_REQUEST
      }

      "return BAD REQUEST if no form data supplied" in new Test {
        val result: Future[Result] = controller.onSubmit(fakeRequest)
        status(result) mustBe Status.BAD_REQUEST
      }
    }

  }

}
