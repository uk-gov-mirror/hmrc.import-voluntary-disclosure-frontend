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
import connectors.httpParsers.ResponseHttpParser.HttpGetResult
import mocks.MockHttp
import models.TraderAddress
import utils.ReusableValues

import scala.concurrent.Future

class ImporterAddressConnectorSpec extends SpecBase with MockHttp with ReusableValues {

  object Connector extends ImporterAddressConnector(mockHttp, appConfig)

  "Importer Address Connector" should {

    def getAddressResult(): Future[HttpGetResult[TraderAddress]] = Connector.getAddress(idOne)

    "return the Right response" in {
      setupMockHttpGet(Connector.getAddressUrl(idOne))(Right(traderAddress))
      await(getAddressResult()) mustBe Right(traderAddress)
    }

    "return the error response" in {
      setupMockHttpGet(Connector.getAddressUrl(idOne))(Left(errorModel))
      await(getAddressResult()) mustBe Left(errorModel)
    }

  }


}
