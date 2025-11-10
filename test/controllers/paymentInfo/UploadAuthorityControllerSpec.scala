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

package controllers.paymentInfo

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import forms.shared.UploadFileFormProvider
import messages.paymentInfo.UploadAuthorityMessages
import mocks.config.MockAppConfig
import mocks.repositories.{MockFileUploadRepository, MockSessionRepository}
import mocks.services.MockUpScanService
import models.SelectedDutyTypes.{Vat, _}
import models.importDetails.UserType
import models.requests._
import models.underpayments.UnderpaymentDetail
import models.upscan._
import models.{FileUploadInfo, UploadAuthority, UserAnswers}
import pages._
import pages.importDetails.{ImporterNamePage, UserTypePage}
import pages.paymentInfo._
import pages.underpayments.UnderpaymentDetailSummaryPage
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.api.test.Helpers._
import views.html.paymentInfo.UploadAuthorityView
import views.html.shared.{FileUploadProgressView, FileUploadSuccessView}

import java.time.Instant
import scala.concurrent.Future

class UploadAuthorityControllerSpec extends ControllerSpecBase {

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
    private lazy val uploadAuthorityView: UploadAuthorityView = app.injector.instanceOf[UploadAuthorityView]
    private lazy val progressView: FileUploadProgressView     = app.injector.instanceOf[FileUploadProgressView]
    private lazy val successView: FileUploadSuccessView       = app.injector.instanceOf[FileUploadSuccessView]

    val userAnswers: Option[UserAnswers] = Some(
      UserAnswers("credId")
        .set(DefermentAccountPage, "1234567").success.value
        .set(AdditionalDefermentNumberPage, "1234568").success.value
        .set(UserTypePage, UserType.Representative).success.value
        .set(ImporterNamePage, "importer").success.value
    )

    val userAnswersADNP: Option[UserAnswers] = Some(
      UserAnswers("credId")
        .set(AdditionalDefermentNumberPage, "1234568").success.value
        .set(UserTypePage, UserType.Representative).success.value
        .set(ImporterNamePage, "importer").success.value
    )
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    val dan: String                = "1234567"
    val dutyType: SelectedDutyType = Both
    val vat: SelectedDutyType      = Vat

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

    def setupMocks(): Unit = {
      MockedFileUploadRepository.updateRecord(Future.successful(true))
      MockedFileUploadRepository.getRecord(Future.successful(Some(Json.fromJson[FileUpload](callbackReadyJson).get)))
      MockedSessionRepository.set(Future.successful(true))

      MockedUpScanService.initiateAuthorityJourney(
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
      new UploadAuthorityController(
        authenticatedAction,
        dataRetrievalAction,
        dataRequiredAction,
        messagesControllerComponents,
        mockFileUploadRepository,
        mockSessionRepository,
        mockUpScanService,
        uploadAuthorityView,
        progressView,
        form,
        successView,
        errorHandler,
        MockAppConfig.appConfig,
        ec
      )
    }
  }

  val formProvider: UploadFileFormProvider = injector.instanceOf[UploadFileFormProvider]
  val form: UploadFileFormProvider         = formProvider

  "GET onLoad" should {
    "return OK when called for combine duty and vat" in new Test {
      val result: Future[Result] = controller.onLoad(Both)(fakeRequest)

      status(result) mustBe Status.OK
    }

    "return OK when called for duty" in new Test {
      val result: Future[Result] = controller.onLoad(Duty)(fakeRequest)

      status(result) mustBe Status.OK
    }

    "return OK when called for vat" in new Test {
      val result: Future[Result] = controller.onLoad(Vat)(fakeRequest)

      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      val result: Future[Result] = controller.onLoad(Neither)(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

    "Display error when file uploaded is Too Small" in new Test {
      val result: Future[Result] = controller.onLoad(Vat)(fakeRequest.withFlash("uploadError" -> "TooSmall"))
      status(result) mustBe Status.OK
      contentAsString(result).contains(UploadAuthorityMessages.fileTooSmall) mustBe true
    }

    "Display error when file uploaded is Too Big" in new Test {
      val result: Future[Result] = controller.onLoad(Vat)(fakeRequest.withFlash("uploadError" -> "TooBig"))
      status(result) mustBe Status.OK
      contentAsString(result).contains(UploadAuthorityMessages.fileTooBig) mustBe true
    }

    "Display error when file uploaded is Unknown" in new Test {
      val result: Future[Result] = controller.onLoad(Vat)(fakeRequest.withFlash("uploadError" -> "Unknown"))
      status(result) mustBe Status.OK
      contentAsString(result).contains(UploadAuthorityMessages.fileUnknown) mustBe true
    }

    "Display error when file uploaded is Rejected" in new Test {
      val result: Future[Result] = controller.onLoad(Vat)(fakeRequest.withFlash("uploadError" -> "Rejected"))
      status(result) mustBe Status.OK
      contentAsString(result).contains(UploadAuthorityMessages.fileRejected) mustBe true
    }

    "Display error when file uploaded is Quarantined" in new Test {
      val result: Future[Result] = controller.onLoad(Vat)(fakeRequest.withFlash("uploadError" -> "Quarantined"))
      status(result) mustBe Status.OK
      contentAsString(result).contains(UploadAuthorityMessages.fileQuarantined) mustBe true
    }

    "return 500 when dan number is not present" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))
      val result: Future[Result]                    = controller.onLoad(Vat)(fakeRequest)
      status(result) mustBe Status.INTERNAL_SERVER_ERROR
    }

  }

  "GET upscanResponseHandler" when {
    "upscan returns an error on upload" should {
      "redirect to error page" in new Test {
        val result = controller.upscanResponseHandler(
          dutyType,
          Some("key"),
          Some("errorCode"),
          Some("errorMessage"),
          Some("errorResource"),
          Some("errorRequestId")
        )(fakeRequest)

        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(
          controllers.paymentInfo.routes.UploadAuthorityController.onLoad(dutyType).url
        )
      }
    }

    "upscan returns an error on upload when only an error code is returned" should {
      "redirect to error page" in new Test {
        val result = controller.upscanResponseHandler(
          dutyType,
          Some("key"),
          Some("errorCode"),
          None,
          None,
          None
        )(fakeRequest)

        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(
          controllers.paymentInfo.routes.UploadAuthorityController.onLoad(dutyType).url
        )
      }
    }

    "upscan returns success on upload" should {
      "for a valid key, redirect to holding page" in new Test {
        val result = controller.upscanResponseHandler(
          dutyType,
          Some("key"),
          None,
          None,
          None,
          None
        )(fakeRequest)

        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(
          controllers.paymentInfo.routes.UploadAuthorityController.uploadProgress(dutyType, "key").url
        )
      }
      "for a valid key, create record in file Repository" in new Test {
        override def setupMocks(): Unit =
          MockedFileUploadRepository.updateRecord(Future.successful(true))

        await(
          controller.upscanResponseHandler(
            dutyType,
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
              dutyType,
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

        val result: Future[Result] = controller.uploadProgress(dutyType, key = "key")(fakeRequest)

        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(
          controllers.paymentInfo.routes.UploadAuthorityController.onSuccess(dutyType).url
        )

        verifyCalls()
      }
    }
    "called following a file upload callback for a failure" should {
      "NOT update userAnswers and redirect to the Error Page" in new Test {
        override def setupMocks(): Unit =
          MockedFileUploadRepository.getRecord(
            Future.successful(Some(Json.fromJson[FileUpload](callbackFailedRejectedJson).get))
          )

        val result: Future[Result] = controller.uploadProgress(dutyType, "key")(fakeRequest)

        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(
          controllers.paymentInfo.routes.UploadAuthorityController.onLoad(dutyType).url
        )

        verifyCalls()
      }
    }
    "called before any file upload callback" should {
      "NOT update userAnswers and redirect to the Progress Page" in new Test {
        override def setupMocks(): Unit =
          MockedFileUploadRepository.getRecord(Future.successful(Some(FileUpload("reference"))))

        val result: Future[Result] = controller.uploadProgress(dutyType, "key")(fakeRequest)

        status(result) mustBe Status.OK
        verifyCalls()
      }
    }
    "called for a Key no longer in repository" should {
      "return 500 Internal Server Error" in new Test {
        override def setupMocks(): Unit =
          MockedFileUploadRepository.getRecord(Future.successful(None))

        val result: Future[Result] = controller.uploadProgress(dutyType, "key")(fakeRequest)

        status(result) mustBe Status.INTERNAL_SERVER_ERROR
      }
    }

    "return 500 when dan number is not present" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))
      val result: Future[Result]                    = controller.uploadProgress(dutyType, "key")(fakeRequest)
      status(result) mustBe Status.INTERNAL_SERVER_ERROR
    }

  }

  "GET onSuccess" should {

    "return OK" in new Test {
      val result: Future[Result] = controller.onSuccess(dutyType)(fakeRequest)
      status(result) mustBe Status.OK
    }
    "return HTML with Continue action to CYA" in new Test {
      override val userAnswers: Option[UserAnswers] =
        Some(UserAnswers("credId").set(SplitPaymentPage, true).success.value)
      val result: Future[Result] = controller.onSuccess(dutyType)(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
    "return HTML with Continue action to Representative VAT Dan" in new Test {
      override val userAnswers: Option[UserAnswers] =
        Some(UserAnswers("credId").set(SplitPaymentPage, true).success.value)
      val result: Future[Result] = controller.onSuccess(Duty)(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
    "return HTML with correct filename" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("credId")
          .set(SplitPaymentPage, true).success.value
          .set(
            UploadAuthorityPage,
            Seq(
              UploadAuthority(dan, Duty, FileUploadInfo("file-ref-1", "filename.txt", "", Instant.now(), "", ""))
            )
          ).success.value
      )
      val result: Future[Result] = controller.onSuccess(Duty)(fakeRequest)
      contentAsString(result).contains("filename.txt") mustBe true
    }

    "return HTML with RepresentativeDanImportVAT url when checkMode is false and dutyType is duty" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("credId")
          .set(SplitPaymentPage, true).success.value
          .set(
            UploadAuthorityPage,
            Seq(UploadAuthority(dan, Duty, FileUploadInfo("", "", "", Instant.now(), "", "")))
          ).success.value
          .set(CheckModePage, false).success.value
          .set(
            UnderpaymentDetailSummaryPage,
            Seq(UnderpaymentDetail("B00", 0.0, 1.0), UnderpaymentDetail("A00", 0.0, 1.0))
          ).success.value
      )
      val result: Future[Result] = controller.onSuccess(Duty)(fakeRequest)
      contentAsString(result).contains("/disclosure/deferment-account-details-vat") mustBe true
    }

    "return HTML with CheckYourAnswers url when checkMode is true" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("credId")
          .set(SplitPaymentPage, true).success.value
          .set(
            UploadAuthorityPage,
            Seq(
              UploadAuthority(dan, Duty, FileUploadInfo("file-ref-1", "filename.txt", "", Instant.now(), "", ""))
            )
          ).success.value
          .set(CheckModePage, true).success.value
          .set(
            UnderpaymentDetailSummaryPage,
            Seq(UnderpaymentDetail("B00", 0.0, 1.0), UnderpaymentDetail("A00", 0.0, 1.0))
          ).success.value
      )
      val result: Future[Result] = controller.onSuccess(Duty)(fakeRequest)
      contentAsString(result).contains("/disclose-import-taxes-underpayment/disclosure/check-your-answers") mustBe true
    }
  }

  "backLink" when {
    "checkMode is false" should {
      "return link to Rep Duty Dan" in new Test {
        override val userAnswers: Option[UserAnswers] =
          Some(UserAnswers("credId").set(SplitPaymentPage, true).success.value)
        val result: Call = controller.backLink(Duty, Both, splitPayment = true)(dataRequest)
        result mustBe controllers.paymentInfo.routes.RepresentativeDanDutyController.onLoad()
      }
      "return link to Rep Vat Dan" in new Test {
        override val userAnswers: Option[UserAnswers] =
          Some(UserAnswers("credId").set(SplitPaymentPage, true).success.value)
        val result: Call = controller.backLink(Vat, Both, splitPayment = true)(dataRequest)
        result mustBe controllers.paymentInfo.routes.RepresentativeDanImportVATController.onLoad()
      }
      "return link to Rep Dan" in new Test {
        override val userAnswers: Option[UserAnswers] =
          Some(UserAnswers("credId").set(SplitPaymentPage, false).success.value)
        val result: Call = controller.backLink(Both, Both, splitPayment = true)(dataRequest)
        result mustBe controllers.paymentInfo.routes.RepresentativeDanController.onLoad()
      }
    }

    "checkMode is true" should {
      "return link to check your answers" in new Test {
        override val userAnswers: Option[UserAnswers] =
          Some(UserAnswers("credId").set(CheckModePage, true).success.value)
        val result: Call = controller.backLink(Duty, Both, splitPayment = true)(dataRequest)
        result mustBe controllers.cya.routes.CheckYourAnswersController.onLoad()
      }
    }
  }

  "getDanNumber" should {
    "return additional-deferment-number if dutyType is Vat" in new Test {
      override val userAnswersADNP: Option[UserAnswers] =
        Some(UserAnswers("credId").set(SplitPaymentPage, true).success.value)
      val result = controller.getDanNumber(Vat, splitPayment = true)(dataRequest)
      result mustBe Some("1234568")
    }
  }

}
