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

import assets.AddressLookupTestConstants.customerAddressMax
import base.SpecBase
import connectors.httpParsers.ResponseHttpParser.{HttpGetResult, HttpPostResult}
import mocks.MockHttp
import models.addressLookup.{AddressLookupJsonBuilder, AddressLookupOnRampModel, AddressModel}
import play.api.http.Status
import uk.gov.hmrc.http.HttpResponse
import assets.BaseTestConstants._

import scala.concurrent.Future

class AddressLookupConnectorSpec extends SpecBase with MockHttp {

  val errorModel: HttpResponse = HttpResponse(Status.BAD_REQUEST, "Error Message")

  object TestAddressLookupConnector extends AddressLookupConnector(mockHttp, appConfig)

  "AddressLookupConnector" must {

    def getAddressResult: Future[HttpGetResult[AddressModel]] = TestAddressLookupConnector.getAddress(id)

    "for getAddress method" when {

      "called for a Right with CustomerDetails" should {

        "return a CustomerAddressModel" in {
          setupMockHttpGet(TestAddressLookupConnector.getAddressUrl(id))(Right(customerAddressMax))
          await(getAddressResult) mustBe Right(customerAddressMax)
        }
      }

      "given an error should" should {

        "return an Left with an ErrorModel" in {
          setupMockHttpGet(TestAddressLookupConnector.getAddressUrl(id))(Left(errorModel))
          await(getAddressResult) mustBe  Left(errorModel)
        }
      }
    }

    val continueUrl = "continue-url"
    def initaliseJourneyResult: Future[HttpPostResult[AddressLookupOnRampModel]] =
      TestAddressLookupConnector.initialiseJourney(AddressLookupJsonBuilder(continueUrl)(fakeRequest, messagesApi, appConfig))

    "for initialiseJourney method" when {

      "using v2 of address lookup frontend" when {

        "when given a successful response" should {

          "return a Right with an AddressLookupOnRampModel" in {

            val successfulResponse = HttpResponse(Status.ACCEPTED, continueUrl)
            setupMockHttpPost(s"${appConfig.addressLookupFrontend}/api/v2/init")(successfulResponse)
            await(initaliseJourneyResult) mustBe successfulResponse
          }
        }

        "given a non successful response should" should {

          "return an Left with an ErrorModel" in {

            val failedResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, continueUrl)
            setupMockHttpPost(s"${appConfig.addressLookupFrontend}/api/v2/init")(failedResponse)
            await(initaliseJourneyResult) mustBe failedResponse
          }
        }
      }
    }
  }
}
