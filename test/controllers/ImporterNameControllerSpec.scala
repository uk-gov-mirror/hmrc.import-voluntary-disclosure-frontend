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
import forms.ImporterNameFormProvider
import mocks.repositories.MockSessionRepository
import models.UserAnswers
import pages.ImporterNamePage
import play.api.http.Status
import play.api.mvc.{AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, redirectLocation, status}
import views.html.ImporterNameView

import scala.concurrent.Future

class ImporterNameControllerSpec extends ControllerSpecBase {

  val userAnswersWithImporterName: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
    .set(
      ImporterNamePage, "test"
    ).success.value
  )

  private def fakeRequestGenerator(importerName: String): FakeRequest[AnyContentAsFormUrlEncoded] =
    fakeRequest.withFormUrlEncodedBody(
      "fullName" -> importerName
    )

  trait Test extends MockSessionRepository {
    lazy val controller = new ImporterNameController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      mockSessionRepository,
      messagesControllerComponents,
      form,
      ImporterNameView
    )
    private lazy val ImporterNameView = app.injector.instanceOf[ImporterNameView]
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)
    val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id"))
    val formProvider: ImporterNameFormProvider = injector.instanceOf[ImporterNameFormProvider]
    MockedSessionRepository.set(Future.successful(true))
    val form: ImporterNameFormProvider = formProvider
  }

  "GET onLoad" should {
    "return OK" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("some-cred-id").set(ImporterNamePage, "test").success.value
      )
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "POST onSubmit" when {

    "payload contains valid data" should {

      "return a SEE OTHER response when correct data with character values" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))
        lazy val result: Future[Result] = controller.onSubmit()(fakeRequestGenerator("test"))
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.ImporterNameController.onLoad().url)

      }
      "update the UserAnswers in session" in new Test {
        override val userAnswers: Option[UserAnswers] = userAnswersWithImporterName
        await(controller.onSubmit()(fakeRequestGenerator("test")))
        verifyCalls()
      }
    }

    "payload contains invalid data" should {

      "return BAD REQUEST when no data is sent" in new Test {
        lazy val result: Future[Result] = controller.onSubmit()(fakeRequestGenerator(""))
        status(result) mustBe Status.BAD_REQUEST
      }

      "return BAD REQUEST when data is less than 2 in length" in new Test {
        lazy val result: Future[Result] = controller.onSubmit()(fakeRequestGenerator("t"))
        status(result) mustBe Status.BAD_REQUEST
      }

      "return BAD REQUEST when data is greater than 50 in length" in new Test {
        lazy val result: Future[Result] = controller.onSubmit()(fakeRequestGenerator
        ("huihruidoshhgufidoshgufdishgfudisohgfuidoshgufidoshgfidoshgfudioshgf"))
        status(result) mustBe Status.BAD_REQUEST
      }

      "return BAD REQUEST when incorrect data is sent" in new Test {
        lazy val result: Future[Result] = controller.onSubmit()(fakeRequestGenerator("123!"))
        status(result) mustBe Status.BAD_REQUEST
      }

      "return BAD REQUEST" in new Test {
        lazy val result: Future[Result] = controller.onSubmit()(fakeRequest)
        status(result) mustBe Status.BAD_REQUEST
      }
    }
  }
}
