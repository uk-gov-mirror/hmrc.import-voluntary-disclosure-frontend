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

package connectors

import base.ConnectorSpecBase
import mocks.MockHttp
import mocks.config.MockAppConfig
import models._
import play.api.libs.json.Json
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import utils.ReusableValues

class IvdSubmissionConnectorSpec extends ConnectorSpecBase with MockHttp with ReusableValues {

  lazy val target = new IvdSubmissionConnector(mockHttp)(MockAppConfig.appConfig)

  "Ivd Submission Connector" should {

    "return the Right response" in {
      setupMockHttpGet(target.getEoriDetailsUrl(idOne))(Right(eoriDetails))
      await(target.getEoriDetails(idOne)) mustBe Right(eoriDetails)
    }

    "return the error response" in {
      setupMockHttpGet(target.getEoriDetailsUrl(idOne))(Left(errorModel))
      await(target.getEoriDetails(idOne)) mustBe Left(errorModel)
    }
  }

  "called to post the Submission" should {

    val submissionResponse = SubmissionResponse("1234")

    "return the Right response" in {
      setupMockHttpPost(target.createCaseUrl)(Right(submissionResponse))
      await(target.createCase(Json.obj())) mustBe Right(submissionResponse)
    }
  }

  "called to update case" should {

    val updateResponse = UpdateCaseResponse("1234")

    "return the Right response" in {
      setupMockHttpPost(target.updateCaseUrl)(Right(updateResponse))
      await(target.updateCase(Json.obj())) mustBe Right(updateResponse)
    }
  }

}
