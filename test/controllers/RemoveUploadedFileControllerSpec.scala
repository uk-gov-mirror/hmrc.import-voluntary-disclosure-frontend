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
import forms.RemoveUploadedFileFormProvider
import mocks.repositories.MockSessionRepository
import models.{FileUploadInfo, Index, UserAnswers}
import pages.FileUploadPage
import play.api.http.Status
import play.api.mvc.{AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.RemoveUploadedFileView

import java.time.LocalDateTime
import scala.concurrent.Future


class RemoveUploadedFileControllerSpec extends ControllerSpecBase {

  trait Test extends MockSessionRepository {
    private lazy val RemoveUploadedFileView: RemoveUploadedFileView = app.injector.instanceOf[RemoveUploadedFileView]

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    val formProvider: RemoveUploadedFileFormProvider = injector.instanceOf[RemoveUploadedFileFormProvider]
    val form: RemoveUploadedFileFormProvider = formProvider

    val index:Index = Index.apply(1)

    MockedSessionRepository.set(Future.successful(true))

    lazy val controller = new RemoveUploadedFileController(messagesApi,mockSessionRepository,authenticatedAction, dataRetrievalAction, dataRequiredAction,
       form,messagesControllerComponents, RemoveUploadedFileView)
  }


  "GET /" should {
    "redirect to SupportingDoc page if no uploaded-files in user answers" in new Test {
      val result: Future[Result] = controller.onLoad(index)(fakeRequest)
      status(result) mustBe Status.SEE_OTHER
    }

    "redirect to SupportingDoc page if all files removed" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("credId")
          .set(
            FileUploadPage,
            Seq.empty
          ).success.value
      )
      val result: Future[Result] = controller.onLoad(index)(fakeRequest)
      status(result) mustBe Status.SEE_OTHER
    }

    "redirect to RemoveUploadedFile page if files exist" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("credId")
          .set(
            FileUploadPage,
            Seq(FileUploadInfo(
              fileName = "file.txt",
              downloadUrl = "url",
              uploadTimestamp = LocalDateTime.now,
              checksum = "checksum",
              fileMimeType = "application/txt"
            ))
          ).success.value
      )
      val result: Future[Result] = controller.onLoad(index)(fakeRequest)
      status(result) mustBe Status.OK
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "POST /" when {
    "payload contains valid data" should {

      "return a SEE OTHER response when false" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "false")
        lazy val result: Future[Result] = controller.onSubmit(index)(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "return a SEE OTHER response when true" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result] = controller.onSubmit(index)(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "return the correct location header" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result] = controller.onSubmit(index)(request)
        redirectLocation(result) mustBe Some(controllers.routes.UploadAnotherFileController.onLoad().url)
      }

      "update the UserAnswers in session" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        await(controller.onSubmit(index)(request))
        verifyCalls()
      }
    }

    "payload contains invalid data" should {
      "return a BAD REQUEST" in new Test {
        val result: Future[Result] = controller.onSubmit(index)(fakeRequest)
        status(result) mustBe Status.BAD_REQUEST
      }
    }
  }

}



