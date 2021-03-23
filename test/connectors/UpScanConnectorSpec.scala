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

package connectors

import base.SpecBase
import mocks.MockHttp
import mocks.config.MockAppConfig
import models.BadRequest
import models.upscan.{Reference, UpScanInitiateRequest, UpScanInitiateResponse, UploadFormTemplate}

class UpScanConnectorSpec extends SpecBase with MockHttp {

  object TestConnector extends UpScanConnector(mockHttp, MockAppConfig)

  val exampleModel: UpScanInitiateRequest = UpScanInitiateRequest(
    MockAppConfig.upScanCallbackUrlForSuccessOrFailureOfFileUpload,
    MockAppConfig.upScanSuccessRedirectForUser,
    MockAppConfig.upScanErrorRedirectForUser,
    MockAppConfig.upScanMinFileSize,
    MockAppConfig.upScanMaxFileSize
  )
  val response: UpScanInitiateResponse =  UpScanInitiateResponse(
    Reference("11370e18-6e24-453e-b45a-76d3e32ea33d"),
    UploadFormTemplate(
      "https://bucketName.s3.eu-west-2.amazonaws.com",
      Map("Content-Type" -> "application/xml")
    )
  )

  "return right when parser returns right" in {
    setupMockHttpPostWithBody(TestConnector.urlForPostInitiate, exampleModel)(
      Right(response)
    )
    val actualResult = TestConnector.postToInitiate(exampleModel)(hc, ec)

    await(actualResult) mustBe Right(response)
  }
  "return Left when parser returns Left" in {
    setupMockHttpPostWithBody(TestConnector.urlForPostInitiate, exampleModel)(
      Left(BadRequest)
    )
    val actualResult = TestConnector.postToInitiate(exampleModel)(hc, ec)

    await(actualResult) mustBe Left(BadRequest)
  }

}