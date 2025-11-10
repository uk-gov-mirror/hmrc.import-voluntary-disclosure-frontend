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
import messages.docUpload.BulkUploadFileMessages
import mocks.config.MockAppConfig
import mocks.repositories.{MockFileUploadRepository, MockSessionRepository}
import mocks.services.MockUpScanService
import models.requests.{DataRequest, IdentifierRequest, OptionalDataRequest}
import models.upscan.{FileUpload, Reference, UpScanInitiateResponse, UploadFormTemplate}
import models.{FileUploadInfo, UserAnswers}
import pages.CheckModePage
import pages.docUpload.FileUploadPage
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContentAsEmpty, Call, Result}
import play.api.test.Helpers._
import views.html.docUpload.BulkUploadFileView
import views.html.shared.{FileUploadProgressView, FileUploadSuccessView}

import java.time.Instant
import scala.concurrent.Future

class BulkUploadFileControllerSpec extends ControllerSpecBase {

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
    private lazy val bulkUploadFileView: BulkUploadFileView       = app.injector.instanceOf[BulkUploadFileView]
    private lazy val uploadProgressView: FileUploadProgressView   = app.injector.instanceOf[FileUploadProgressView]
    private lazy val bulkUploadSuccessView: FileUploadSuccessView = app.injector.instanceOf[FileUploadSuccessView]

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    def setupMocks(): Unit = {
      MockedFileUploadRepository.updateRecord(Future.successful(true))
      MockedFileUploadRepository.getRecord(Future.successful(Some(Json.fromJson[FileUpload](callbackReadyJson).get)))
      MockedSessionRepository.set(Future.successful(true))

      MockedUpScanService.initiateBulkJourney(
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

    implicit lazy val dataRequest: DataRequest[AnyContentAsEmpty.type] = DataRequest(
      OptionalDataRequest(
        IdentifierRequest(fakeRequest, "credId", "eori"),
        "credId",
        "eori",
        userAnswers
      ),
      "credId",
      "eori",
      userAnswers.get
    )

    lazy val controller = {
      setupMocks()
      new BulkUploadFileController(
        authenticatedAction,
        dataRetrievalAction,
        dataRequiredAction,
        messagesControllerComponents,
        mockFileUploadRepository,
        mockSessionRepository,
        mockUpScanService,
        bulkUploadFileView,
        uploadProgressView,
        form,
        bulkUploadSuccessView,
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

    "Display error when file uploaded is Too Small" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest.withFlash("uploadError" -> "TooSmall"))
      status(result) mustBe Status.OK
      contentAsString(result).contains(BulkUploadFileMessages.fileTooSmall) mustBe true
    }

    "Display error when file uploaded is Too Big" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest.withFlash("uploadError" -> "TooBig"))
      status(result) mustBe Status.OK
      contentAsString(result).contains(BulkUploadFileMessages.fileTooBig) mustBe true
    }

    "Display error when file uploaded is Unknown" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest.withFlash("uploadError" -> "Unknown"))
      status(result) mustBe Status.OK
      contentAsString(result).contains(BulkUploadFileMessages.fileUnknown) mustBe true
    }

    "Display error when file uploaded is Rejected" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest.withFlash("uploadError" -> "Rejected"))
      status(result) mustBe Status.OK
      contentAsString(result).contains(BulkUploadFileMessages.fileRejected) mustBe true
    }

    "Display error when file uploaded is Quarantined" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest.withFlash("uploadError" -> "Quarantined"))
      status(result) mustBe Status.OK
      contentAsString(result).contains(BulkUploadFileMessages.fileQuarantined) mustBe true
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
        redirectLocation(result) mustBe Some(controllers.docUpload.routes.BulkUploadFileController.onLoad().url)
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
        redirectLocation(result) mustBe Some(controllers.docUpload.routes.BulkUploadFileController.onLoad().url)
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
          controllers.docUpload.routes.BulkUploadFileController.uploadProgress("key").url
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
      "update UserAnswers and redirect to the Success Page" in new Test {
        override def setupMocks(): Unit = {
          MockedFileUploadRepository.getRecord(
            Future.successful(Some(Json.fromJson[FileUpload](callbackReadyJson).get))
          )
          MockedSessionRepository.set(Future.successful(true))
        }

        val result: Future[Result] = controller.uploadProgress("key")(fakeRequest)

        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.docUpload.routes.BulkUploadFileController.onSuccess().url)

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
        redirectLocation(result) mustBe Some(controllers.docUpload.routes.BulkUploadFileController.onLoad().url)

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

  "GET onSuccess" should {

    "return OK" in new Test {
      val result: Future[Result] = controller.onSuccess()(fakeRequest)
      status(result) mustBe Status.OK
    }
    "return HTML with Continue action" in new Test {
      val result: Future[Result] = controller.onSuccess()(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
    "return HTML with Continue action to Representative VAT Dan" in new Test {
      val result: Future[Result] = controller.onSuccess()(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
    "return HTML with correct filename" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("credId")
          .set(
            FileUploadPage,
            Seq(
              FileUploadInfo(
                reference = "file-ref-1",
                fileName = "filename.txt",
                downloadUrl = "url",
                uploadTimestamp = Instant.now,
                checksum = "checksum",
                fileMimeType = "filename/txt"
              )
            )
          ).success.value
      )
      val result: Future[Result] = controller.onSuccess()(fakeRequest)
      contentAsString(result).contains("filename.txt") mustBe true
    }

    "return HTML with MoreInformation url when checkMode is false " in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("credId")
          .set(
            FileUploadPage,
            Seq(
              FileUploadInfo(
                reference = "file-ref-1",
                fileName = "file.txt",
                downloadUrl = "url",
                uploadTimestamp = Instant.now,
                checksum = "checksum",
                fileMimeType = "application/txt"
              )
            )
          ).success.value
          .set(CheckModePage, false).success.value
      )
      val result: Future[Result] = controller.onSuccess()(fakeRequest)
      contentAsString(result).contains("/disclosure/more-information") mustBe true
    }

    "return HTML with CheckYourAnswers url when checkMode is true" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("credId")
          .set(
            FileUploadPage,
            Seq(
              FileUploadInfo(
                reference = "file-ref-1",
                fileName = "file.txt",
                downloadUrl = "url",
                uploadTimestamp = Instant.now,
                checksum = "checksum",
                fileMimeType = "application/txt"
              )
            )
          ).success.value
          .set(CheckModePage, true).success.value
      )
      val result: Future[Result] = controller.onSuccess()(fakeRequest)
      contentAsString(result).contains("/disclosure/check-your-answers") mustBe true
    }

  }

  "backLink" when {

    "not in change mode" should {
      "when loading page back button should take you to UnderpaymentDetailsSummary page" in new Test {
        override val userAnswers: Option[UserAnswers] =
          Some(
            UserAnswers("some-cred-id")
              .set(CheckModePage, false).success.value
          )
        lazy val result: Call = controller.backLink
        result mustBe controllers.underpayments.routes.UnderpaymentDetailSummaryController.onLoad()
      }
    }

    "in change mode" should {
      "when loading page back button should take you to Check your answers page" in new Test {
        override val userAnswers: Option[UserAnswers] =
          Some(
            UserAnswers("some-cred-id")
              .set(CheckModePage, true).success.value
          )
        lazy val result: Call = controller.backLink
        result mustBe controllers.cya.routes.CheckYourAnswersController.onLoad()
      }
    }

  }

}
