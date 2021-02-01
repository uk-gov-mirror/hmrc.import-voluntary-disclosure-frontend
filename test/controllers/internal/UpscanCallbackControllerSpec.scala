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

package controllers.internal

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import mocks.config.MockAppConfig
import mocks.repositories.{MockFileUploadRepository, MockSessionRepository}
import mocks.services.MockUpScanService
import models.UserAnswers
import models.upscan._
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.test.Helpers._
import views.html.{UploadFileView, UploadProgressView}

import scala.concurrent.Future

class UpscanCallbackControllerSpec extends ControllerSpecBase {

  private val callbackReadyJson: JsValue = Json.parse("""
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

  private def callbackFailedBuilder(reason: String, message: String): JsValue = Json.parse(s"""
    | {
    |   "reference" : "11370e18-6e24-453e-b45a-76d3e32ea33d",
    |   "fileStatus" : "READY",
    |    "failureDetails": {
    |        "failureReason": "$reason",
    |        "message": "$message"
    |    }
    | }""".stripMargin)

  private val callbackFailedQuarentineJson: JsValue = callbackFailedBuilder(
    reason = "QUARANTINE",
    message = "e.g. This file has a virus"
  )

  private val callbackFailedRejectedJson: JsValue = callbackFailedBuilder(
    reason = "REJECTED",
    message = "MIME type .foo is not allowed for service import-voluntary-disclosure-frontend"
  )

  private val callbackFailedUnknownJson: JsValue = callbackFailedBuilder(
    reason = "UNKNOWN",
    message = "Something unknown happened"
  )

  trait Test extends MockFileUploadRepository {

    def setupMocks():Unit = {
      MockedFileUploadRepository.updateRecord(Future.successful(true))
    }

    lazy val controller = {
      setupMocks()
      new UpscanCallbackController(messagesControllerComponents, mockFileUploadRepository, MockAppConfig)
    }
  }


  "POST callbackHandler" when {
    "valid file upload callback" should {
      "return 204 (NoContent)" in new Test {
        val result = controller.callbackHandler()(fakeRequest.withBody(callbackReadyJson))

        status(result) mustBe Status.NO_CONTENT
      }
      "return 500 (InternalServerError)" in new Test {
        override def setupMocks(): Unit = {
          MockedFileUploadRepository.updateRecord(Future.successful(false))
        }
        val result = controller.callbackHandler()(fakeRequest.withBody(callbackReadyJson))

        status(result) mustBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }

  "deriveFileStatus" when {
    "processing successful response" should {
      "return original model" in new Test {
        val result = controller.deriveFileStatus(Json.fromJson[FileUpload](callbackReadyJson).get)
        result.fileStatus mustBe Some(FileStatusEnum.READY)
      }
    }
    "processing failure response" should {
      "return FAILED_QUARENTINE" in new Test {
        val result = controller.deriveFileStatus(Json.fromJson[FileUpload](callbackFailedQuarentineJson).get)
        result.fileStatus mustBe Some(FileStatusEnum.FAILED_QUARANTINE)
      }
      "return FAILED_REJECTED" in new Test {
        val result = controller.deriveFileStatus(Json.fromJson[FileUpload](callbackFailedRejectedJson).get)
        result.fileStatus mustBe Some(FileStatusEnum.FAILED_REJECTED)
      }
      "return FAILED_UNKNOWN" in new Test {
        val result = controller.deriveFileStatus(Json.fromJson[FileUpload](callbackFailedUnknownJson).get)
        result.fileStatus mustBe Some(FileStatusEnum.FAILED_UNKNOWN)
      }
    }
  }

}
