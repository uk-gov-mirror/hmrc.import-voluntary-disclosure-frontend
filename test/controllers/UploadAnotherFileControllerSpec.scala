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
import forms.UploadAnotherFileFormProvider
import mocks.repositories.MockSessionRepository
import models.UserAnswers
import pages.UploadAnotherFilePage
import play.api.http.Status
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, redirectLocation, status}
import views.html.UploadAnotherFileView

import scala.concurrent.Future


class UploadAnotherFileControllerSpec extends ControllerSpecBase {

  trait Test extends MockSessionRepository {
    private lazy val uploadAnotherFileView: UploadAnotherFileView = app.injector.instanceOf[UploadAnotherFileView]

    val data: JsObject = Json.obj("uploaded-files" -> Json.arr(Json.obj("fileName" -> "text.txt")))

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId", data))

    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    val formProvider: UploadAnotherFileFormProvider = injector.instanceOf[UploadAnotherFileFormProvider]
    val form: UploadAnotherFileFormProvider = formProvider

    MockedSessionRepository.set(Future.successful(true))

    lazy val controller = new UploadAnotherFileController(authenticatedAction, dataRetrievalAction, dataRequiredAction,
      mockSessionRepository, messagesControllerComponents, form, uploadAnotherFileView)
  }

  val acceptanceDateYes: Boolean = true

  "GET /" should {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id", data).set(UploadAnotherFilePage, true).success.value)
      val result: Future[Result] = controller.onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

    "redirect to supporting Doc page when no data present" in new Test {
      override val data = Json.obj("" -> "")
      override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id", data).set(UploadAnotherFilePage, true).success.value)
        val result: Future[Result] = controller.onLoad(fakeRequest)
        status(result) mustBe Status.SEE_OTHER
      }

  }

  "POST /" when {
    "payload contains valid data" should {

      "return a SEE OTHER response when false" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "false")
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "return a SEE OTHER response when true" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "return the correct location header when true" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result] = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(controllers.routes.UploadAnotherFileController.onLoad().url)
      }

      "return the correct location header when false" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "false")
        lazy val result: Future[Result] = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(controllers.routes.UploadAnotherFileController.onLoad().url)
      }

      "update the UserAnswers in session" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody("value" -> "true")
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



