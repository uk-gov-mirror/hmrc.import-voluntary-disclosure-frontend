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

package controllers.docUpload

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import forms.shared.UploadFileFormProvider
import messages.docUpload.UploadFileMessages
import mocks.config.MockAppConfig
import mocks.repositories.{MockFileUploadRepository, MockSessionRepository}
import mocks.services.MockUpScanService
import models.upscan.{FileUpload, Reference, UpScanInitiateResponse, UploadFormTemplate}
import models.{FileUploadInfo, UserAnswers}
import pages.CheckModePage
import pages.docUpload.FileUploadPage
import pages.shared.AnyOtherSupportingDocsPage
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.test.Helpers._
import views.html.docUpload.UploadFileView
import views.html.shared.FileUploadProgressView

import java.time.Instant
import scala.concurrent.Future

class UploadFileControllerSpec extends ControllerSpecBase {

  private val callbackReadyJson: JsValue = Json.parse(s"""
       | {
       |   "reference" : "11370e18-6e24-453e-b45a-76d3e32ea33d",
       |   "fileStatus" : "READY",
       |   "downloadUrl" : "https://bucketName.s3.eu-west-2.amazonaws.com?1235676",
       |   "uploadDetails": {
       |     "uploadTimestamp": "2018-04-24T09:30:00Z",
       |     "checksum": "396f101dd52e8b2ace0dcf5ed09b1d1f030e608938510ce46e7a5c7a4e775100",
       |     "fileName": "test.pdf",
       |     "fileMimeType": "application/pdf"
       |   }
       | }""".stripMargin)

  private val callbackFailedRejectedJson: JsValue = Json.parse(s"""
       | {
       |   "reference" : "11370e18-6e24-453e-b45a-76d3e32ea33d",
       |   "fileStatus" : "FAILED",
       |    "failureDetails": {
       |        "failureReason": "REJECTED",
       |        "message": "MIME type .foo is not allowed for service import-voluntary-disclosure-frontend"
       |    }
       | }""".stripMargin)

  trait Test extends MockSessionRepository with MockFileUploadRepository with MockUpScanService {
    private lazy val uploadFileView: UploadFileView             = app.injector.instanceOf[UploadFileView]
    private lazy val uploadProgressView: FileUploadProgressView = app.injector.instanceOf[FileUploadProgressView]

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    def setupMocks(): Unit = {
      MockedFileUploadRepository.updateRecord(Future.successful(true))
      MockedFileUploadRepository.getRecord(Future.successful(Some(Json.fromJson[FileUpload](callbackReadyJson).get)))
      MockedSessionRepository.set(Future.successful(true))

      MockedUpScanService.initiateNewJourney(
        Future.successful(
          UpScanInitiateResponse(
            Reference("11370e18-6e24-453e-b45a-76d3e32ea33d"),
            UploadFormTemplate(
              "https://bucketName.s3.eu-west-2.amazonaws.com",
              Map("Content-Type" -> "application/xml")
            )
          )
        )
      )
    }

    lazy val controller = {
      setupMocks()
      new UploadFileController(
        authenticatedAction,
        dataRetrievalAction,
        dataRequiredAction,
        messagesControllerComponents,
        mockFileUploadRepository,
        mockSessionRepository,
        mockUpScanService,
        uploadFileView,
        uploadProgressView,
        form,
        MockAppConfig.appConfig,
        ec
      )
    }
  }

  val formProvider: UploadFileFormProvider = injector.instanceOf[UploadFileFormProvider]
  val form: UploadFileFormProvider         = formProvider

  "GET onLoad" should {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

    "return correct back link for no additional documents" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("credId")
          .set(AnyOtherSupportingDocsPage, false).success.value
      )
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      contentAsString(result).contains(
        controllers.docUpload.routes.AnyOtherSupportingDocsController.onLoad().url
      ) mustBe true
    }

    "return correct back link for additional documents" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("credId")
          .set(AnyOtherSupportingDocsPage, true).success.value
      )
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      contentAsString(result).contains(
        controllers.docUpload.routes.OptionalSupportingDocsController.onLoad().url
      ) mustBe true
    }

    "return correct back link if come from Summary screen" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("credId")
          .set(
            FileUploadPage,
            Seq(
              FileUploadInfo(
                reference = "file-ref-1",
                fileName = "file1",
                downloadUrl = "url",
                uploadTimestamp = Instant.now(),
                checksum = "checksum",
                fileMimeType = "mime"
              )
            )
          ).success.value
      )
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      contentAsString(result).contains(
        controllers.docUpload.routes.UploadAnotherFileController.onLoad().url
      ) mustBe true
    }

    "return no back link if in check mode and all files removed" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("credId")
          .set(CheckModePage, true).success.value
      )
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      contentAsString(result).contains("govuk-back-link") mustBe false
    }

    "Display error when file uploaded is Too Small" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest.withFlash("uploadError" -> "TooSmall"))
      status(result) mustBe Status.OK
      contentAsString(result).contains(UploadFileMessages.fileTooSmall) mustBe true
    }

    "Display error when file uploaded is Too Big" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest.withFlash("uploadError" -> "TooBig"))
      status(result) mustBe Status.OK
      contentAsString(result).contains(UploadFileMessages.fileTooBig) mustBe true
    }

    "Display error when file uploaded is Unknown" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest.withFlash("uploadError" -> "Unknown"))
      status(result) mustBe Status.OK
      contentAsString(result).contains(UploadFileMessages.fileUnknown) mustBe true
    }

    "Display error when file uploaded is Rejected" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest.withFlash("uploadError" -> "Rejected"))
      status(result) mustBe Status.OK
      contentAsString(result).contains(UploadFileMessages.fileRejected) mustBe true
    }

    "Display error when file uploaded is Quarantined" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest.withFlash("uploadError" -> "Quarantined"))
      status(result) mustBe Status.OK
      contentAsString(result).contains(UploadFileMessages.fileQuarantined) mustBe true
    }
  }

  "GET upscanResponseHandler" when {
    "upscan returns an error on upload" should {
      "redirect to error page" in new Test {
        val result = controller.upscanResponseHandler(
          Some("key"),
          Some("errorCode"),
          Some("errorMessage"),
          Some("errorResource"),
          Some("errorRequestId")
        )(fakeRequest)

        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.docUpload.routes.UploadFileController.onLoad().url)
      }
    }

    "upscan returns an error on upload when only an error code is returned" should {
      "redirect to error page" in new Test {
        val result = controller.upscanResponseHandler(
          Some("key"),
          Some("errorCode"),
          None,
          None,
          None
        )(fakeRequest)

        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.docUpload.routes.UploadFileController.onLoad().url)
      }
    }

    "upscan returns success on upload" should {
      "for a valid key, redirect to holding page" in new Test {
        val result = controller.upscanResponseHandler(
          Some("key"),
          None,
          None,
          None,
          None
        )(fakeRequest)

        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(
          controllers.docUpload.routes.UploadFileController.uploadProgress("key").url
        )
      }
      "for a valid key, create record in file Repository" in new Test {
        override def setupMocks(): Unit =
          MockedFileUploadRepository.updateRecord(Future.successful(true))

        await(
          controller.upscanResponseHandler(
            Some("key"),
            None,
            None,
            None,
            None
          )(fakeRequest)
        )

        verifyCalls()
      }

      "for an invalid key" in new Test {
        val result = intercept[RuntimeException](
          await(
            controller.upscanResponseHandler(
              None,
              None,
              None,
              None,
              None
            )(fakeRequest)
          )
        )

        assert(result.getMessage.contains("No key returned for successful upload"))
      }
    }
  }

  "GET uploadProgress" when {
    "called following a successful file upload callback" should {
      "update UserAnswers and redirect to the Summary Page" in new Test {
        override def setupMocks(): Unit = {
          MockedFileUploadRepository.getRecord(
            Future.successful(Some(Json.fromJson[FileUpload](callbackReadyJson).get))
          )
          MockedSessionRepository.set(Future.successful(true))
        }

        val result: Future[Result] = controller.uploadProgress("key")(fakeRequest)

        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.docUpload.routes.UploadAnotherFileController.onLoad().url)

        verifyCalls()
      }
    }
    "called following a file upload callback for a failure" should {
      "NOT update userAnswers and redirect to the Error Page" in new Test {
        override def setupMocks(): Unit =
          MockedFileUploadRepository.getRecord(
            Future.successful(Some(Json.fromJson[FileUpload](callbackFailedRejectedJson).get))
          )

        val result: Future[Result] = controller.uploadProgress("key")(fakeRequest)

        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.docUpload.routes.UploadFileController.onLoad().url)

        verifyCalls()
      }
    }
    "called before any file upload callback" should {
      "NOT update userAnswers and redirect to the Progress Page" in new Test {
        override def setupMocks(): Unit =
          MockedFileUploadRepository.getRecord(Future.successful(Some(FileUpload("reference"))))

        val result: Future[Result] = controller.uploadProgress("key")(fakeRequest)

        status(result) mustBe Status.OK
        verifyCalls()
      }
    }
    "called for a Key no longer in repository" should {
      "return 500 Internal Server Error" in new Test {
        override def setupMocks(): Unit =
          MockedFileUploadRepository.getRecord(Future.successful(None))

        val result: Future[Result] = controller.uploadProgress("key")(fakeRequest)

        status(result) mustBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }

}
