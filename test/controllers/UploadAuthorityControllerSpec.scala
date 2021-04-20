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
import mocks.config.MockAppConfig
import mocks.repositories.{MockFileUploadRepository, MockSessionRepository}
import mocks.services.MockUpScanService
import models.SelectedDutyTypes._
import models.UserAnswers
import models.upscan.{FileUpload, Reference, UpScanInitiateResponse, UploadFormTemplate}
import pages.SplitPaymentPage
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.test.Helpers._
import views.html.{UploadAuthorityProgressView, UploadAuthoritySuccessView, UploadAuthorityView}

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
    private lazy val uploadAuthorityProgressView: UploadAuthorityProgressView = app.injector.instanceOf[UploadAuthorityProgressView]
    private lazy val uploadAuthoritySuccessView: UploadAuthoritySuccessView = app.injector.instanceOf[UploadAuthoritySuccessView]

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    val dan: String = "1234567"
    val dutyType: SelectedDutyType = Both

    def setupMocks():Unit = {
      MockedFileUploadRepository.updateRecord(Future.successful(true))
      MockedFileUploadRepository.getRecord(Future.successful(Some(Json.fromJson[FileUpload](callbackReadyJson).get)))
      MockedSessionRepository.set(Future.successful(true))

      MockedUpScanService.initiateAuthorityJourney(
        Future.successful(UpScanInitiateResponse(
          Reference("11370e18-6e24-453e-b45a-76d3e32ea33d"),
          UploadFormTemplate(
            "https://bucketName.s3.eu-west-2.amazonaws.com",
            Map("Content-Type" -> "application/xml")
          )
        ))
      )
    }

    lazy val controller = {
      setupMocks()
      new UploadAuthorityController(authenticatedAction, dataRetrievalAction, dataRequiredAction, messagesControllerComponents,
        mockFileUploadRepository, mockSessionRepository, mockUpScanService, uploadAuthorityView,
        uploadAuthorityProgressView, uploadAuthoritySuccessView, MockAppConfig)
    }
  }

  "GET onLoad" should {
    "return OK when called for combine duty and vat" in new Test {
      val result: Future[Result] = controller.onLoad(Both, dan)(fakeRequest)

      status(result) mustBe Status.OK
    }

    "return OK when called for duty" in new Test {
      val result: Future[Result] = controller.onLoad(Duty, dan)(fakeRequest)

      status(result) mustBe Status.OK
    }

    "return OK when called for vat" in new Test {
      val result: Future[Result] = controller.onLoad(Vat, dan)(fakeRequest)

      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      val result: Future[Result] = controller.onLoad(Neither, dan)(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "GET upscanResponseHandler" when {
    "upscan returns an error on upload" should {
      "redirect to error page" in new Test {
        val result = controller.upscanResponseHandler(
          dutyType, dan, Some("key"), Some("errorCode"), Some("errorMessage"), Some("errorResource"), Some("errorRequestId")
        )(fakeRequest)

        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.UploadAuthorityController.onLoad(dutyType, dan).url)
      }
    }

    "upscan returns success on upload" should {
      "for a valid key, redirect to holding page" in new Test {
        val result = controller.upscanResponseHandler(
          dutyType, dan, Some("key"), None, None, None, None
        )(fakeRequest)

        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.UploadAuthorityController.uploadProgress(dutyType, dan, "key").url)
      }
      "for a valid key, create record in file Repository" in new Test {
        override def setupMocks(): Unit = {
          MockedFileUploadRepository.updateRecord(Future.successful(true))
        }
        val result = await(controller.upscanResponseHandler(
          dutyType, dan, Some("key"), None, None, None, None
        )(fakeRequest))

        verifyCalls()
      }

      "for an invalid key" in new Test {
        val result = intercept[RuntimeException](await(controller.upscanResponseHandler(
          dutyType, dan, None, None, None, None, None
        )(fakeRequest)))

        assert(result.getMessage.contains("No key returned for successful upload"))
      }
    }
  }

  "GET uploadProgress" when {
    "called following a successful file upload callback" should {
      "update UserAnswers and redirect to the Success Page" in new Test {
        override def setupMocks(): Unit = {
          MockedFileUploadRepository.getRecord(Future.successful(Some(Json.fromJson[FileUpload](callbackReadyJson).get)))
          MockedSessionRepository.set(Future.successful(true))
        }

        val result: Future[Result] = controller.uploadProgress(dutyType, dan, key = "key")(fakeRequest)

        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.UploadAuthorityController.onSuccess(dutyType, dan).url)

        verifyCalls()
      }
    }
    "called following a file upload callback for a failure" should {
      "NOT update userAnswers and redirect to the Error Page" in new Test {
        override def setupMocks(): Unit = {
          MockedFileUploadRepository.getRecord(Future.successful(Some(Json.fromJson[FileUpload](callbackFailedRejectedJson).get)))
        }

        val result: Future[Result] = controller.uploadProgress(dutyType, dan, "key")(fakeRequest)

        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.UploadAuthorityController.onLoad(dutyType, dan).url)

        verifyCalls()
      }
    }
    "called before any file upload callback" should {
      "NOT update userAnswers and redirect to the Progress Page" in new Test {
        override def setupMocks(): Unit = {
          MockedFileUploadRepository.getRecord(Future.successful(Some(FileUpload("reference"))))
        }

        val result: Future[Result] = controller.uploadProgress(dutyType, dan, "key")(fakeRequest)

        status(result) mustBe Status.OK
        verifyCalls()
      }
    }
    "called for a Key no longer in repository" should {
      "return 500 Internal Server Error" in new Test {
        override def setupMocks(): Unit = {
          MockedFileUploadRepository.getRecord(Future.successful(None))
        }

        val result: Future[Result] = controller.uploadProgress(dutyType, dan, "key")(fakeRequest)

        status(result) mustBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }

  "GET onSuccess" should {

    "return OK" in new Test {
      val result: Future[Result] = controller.onSuccess(dutyType, dan)(fakeRequest)
      status(result) mustBe Status.OK
    }
    "return HTML with Continue action to CYA" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId").set(SplitPaymentPage, true).success.value)
      val result: Future[Result] = controller.onSuccess(dutyType, dan)(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
    "return HTML with Continue action to Representative VAT Dan" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId").set(SplitPaymentPage, true).success.value)
      val result: Future[Result] = controller.onSuccess(Duty, dan)(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

  }

  "backLink" should {
    "return link to Rep Duty Dan" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId").set(SplitPaymentPage, true).success.value)
      val result = controller.backLink(Duty, dan, Both, true)
      result mustBe controllers.routes.RepresentativeDanDutyController.onLoad()
    }
    "return link to Rep Vat Dan" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId").set(SplitPaymentPage, true).success.value)
      val result = controller.backLink(Vat, dan, Both, true)
      result mustBe controllers.routes.RepresentativeDanImportVATController.onLoad()
    }
    "return link to Rep Dan" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId").set(SplitPaymentPage, false).success.value)
      val result = controller.backLink(Both, dan, Both, true)
      result mustBe controllers.routes.RepresentativeDanController.onLoad()
    }
  }

}
