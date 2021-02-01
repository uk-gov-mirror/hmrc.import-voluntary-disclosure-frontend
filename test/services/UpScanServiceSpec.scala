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

package services

import base.SpecBase
import connectors.httpParsers.UpScanInitiateHttpParser.UpscanInitiateResponse
import mocks.config.MockAppConfig
import mocks.connectors.MockUpScanConnector
import models.InvalidJson
import models.upscan.{Reference, UpScanInitiateRequest, UpScanInitiateResponse, UploadFormTemplate}
import uk.gov.hmrc.http.InternalServerException

import scala.concurrent.Future

class UpScanServiceSpec extends SpecBase {

  private val callBackUrl = MockAppConfig.upScanCallbackUrlForSuccessOrFailureOfFileUpload
  private val success = MockAppConfig.upScanSuccessRedirectForUser
  private val error = MockAppConfig.upScanErrorRedirectForUser
  private val minFileSize = MockAppConfig.upScanMinFileSize
  private val maxFileSize = MockAppConfig.upScanMaxFileSize

  trait Test extends MockUpScanConnector {
    lazy val service = new UpScanService(mockUpScanConnector, MockAppConfig)
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

  "initiateNewJourney" should {
    val model = UpScanInitiateResponse(Reference("foo"), UploadFormTemplate("", Map.empty))
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

}
