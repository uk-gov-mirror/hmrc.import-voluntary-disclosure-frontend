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

package services

import base.ServiceSpecBase
import connectors.httpParsers.UpScanInitiateHttpParser.UpscanInitiateResponse
import mocks.config.MockAppConfig
import mocks.connectors.MockUpScanConnector
import models.InvalidJson
import models.upscan._
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.InternalServerException

import scala.concurrent.Future

class UpScanServiceSpec extends ServiceSpecBase {

  private val dutyType = "duty"

  private val callBackUrl = MockAppConfig.appConfig.upScanCallbackUrlForSuccessOrFailureOfFileUpload
  private val success     = MockAppConfig.appConfig.upScanSuccessRedirectForUser
  private val error       = MockAppConfig.appConfig.upScanErrorRedirectForUser
  private val minFileSize = MockAppConfig.appConfig.upScanMinFileSize
  private val maxFileSize = MockAppConfig.appConfig.upScanMaxFileSize

  private val upScanAuthoritySuccessRedirectForUser =
    MockAppConfig.appConfig.upScanAuthoritySuccessRedirectForUser ++ s"/$dutyType/upscan-response"

  private val upScanAuthorityErrorRedirectForUser =
    MockAppConfig.appConfig.upScanAuthorityErrorRedirectForUser ++ s"/$dutyType/upscan-response"

  trait Test extends MockUpScanConnector {
    lazy val service = new UpScanService(mockUpScanConnector, MockAppConfig.appConfig)
  }

  "buildInitiateRequest" should {
    "return all values from AppConfig" in new Test {
      service.buildInitiateRequest mustBe UpScanInitiateRequest(
        callBackUrl,
        success,
        error,
        minFileSize,
        maxFileSize
      )
    }
  }

  "buildAuthorityInitiateRequest" should {
    "return all correct values from AppConfig including augmented callback" in new Test {
      service.buildAuthorityInitiateRequest("duty") mustBe UpScanInitiateRequest(
        callBackUrl,
        upScanAuthoritySuccessRedirectForUser,
        upScanAuthorityErrorRedirectForUser,
        minFileSize,
        maxFileSize
      )
    }
  }

  "initiateNewJourney" should {
    val model   = UpScanInitiateResponse(Reference("foo"), UploadFormTemplate("", Map.empty))
    val request = UpScanInitiateRequest(callBackUrl, success, error, minFileSize, maxFileSize)

    "throw exception if Left returned from connector" in new Test {
      val res: UpscanInitiateResponse = Left(InvalidJson)

      MockedUpScanConnector.postToInitiate(request, Future.successful(res))
      intercept[InternalServerException](await(service.initiateNewJourney()))
    }

    "return model if Right model returned from connector" in new Test {
      val res: UpscanInitiateResponse = Right(model)

      MockedUpScanConnector.postToInitiate(request, Future.successful(res))
      await(service.initiateNewJourney()) mustBe model
    }
  }

  "initiateAuthorityJourney" should {
    val model = UpScanInitiateResponse(Reference("foo"), UploadFormTemplate("", Map.empty))
    val request = UpScanInitiateRequest(
      callBackUrl,
      upScanAuthoritySuccessRedirectForUser,
      upScanAuthorityErrorRedirectForUser,
      minFileSize,
      maxFileSize
    )

    "throw exception if Left returned from connector" in new Test {
      val res: UpscanInitiateResponse = Left(InvalidJson)

      MockedUpScanConnector.postToInitiate(request, Future.successful(res))
      intercept[InternalServerException](await(service.initiateAuthorityJourney(dutyType)))
    }

    "return model if Right model returned from connector" in new Test {
      val res: UpscanInitiateResponse = Right(model)

      MockedUpScanConnector.postToInitiate(request, Future.successful(res))
      await(service.initiateAuthorityJourney(dutyType)) mustBe model
    }
  }

}
