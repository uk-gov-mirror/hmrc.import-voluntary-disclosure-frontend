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
import forms.UserTypeFormProvider
import mocks.repositories.MockSessionRepository
import models.{UserAnswers, UserType}
import pages.UserTypePage
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers._
import views.html.UserTypeView

import scala.concurrent.Future

class UserTypeControllerSpec extends ControllerSpecBase {

  trait Test extends MockSessionRepository {
    private lazy val userTypePage: UserTypeView = app.injector.instanceOf[UserTypeView]
    val userAnswers: Option[UserAnswers] = None
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    val formProvider: UserTypeFormProvider = injector.instanceOf[UserTypeFormProvider]
    val form: UserTypeFormProvider = formProvider

    MockedSessionRepository.set(Future.successful(true))

    lazy val controller = new UserTypeController(authenticatedAction, dataRetrievalAction,
      mockSessionRepository, messagesControllerComponents, form, userTypePage)
  }

  "GET /" should {
    "return OK" in new Test {
      private val previousAnswers = UserAnswers("some cred ID").set(UserTypePage, UserType.Importer).success.value
      override val userAnswers: Option[UserAnswers] = Some(previousAnswers)
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "POST /" when {
    "payload contains valid data" should {

      "return a SEE OTHER response" in new Test {

        private val request = fakeRequest.withFormUrlEncodedBody("value" -> UserType.Importer.toString)
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "return the correct location header" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody("value" -> UserType.Importer.toString)
        lazy val result: Future[Result] = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(controllers.routes.NumberOfEntriesController.onLoad().url)
      }

      "update the UserAnswers in session" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody("value" -> UserType.Importer.toString)
        await(controller.onSubmit(request))
        MockedSessionRepository.verifyCalls()
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
