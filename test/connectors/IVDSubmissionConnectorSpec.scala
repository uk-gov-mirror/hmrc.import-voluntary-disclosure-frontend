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
import models._
import utils.ReusableValues

import java.time.LocalDate
import scala.concurrent.Future

class IVDSubmissionConnectorSpec extends SpecBase with MockHttp with ReusableValues {

  object Connector extends IVDSubmissionConnector(mockHttp, appConfig)

  "Importer Address Connector" should {

    def getAddressResult(): Future[HttpGetResult[ContactAddress]] = Connector.getAddress(idOne)

    "return the Right response" in {
      setupMockHttpGet(Connector.getAddressUrl(idOne))(Right(traderAddress))
      await(getAddressResult()) mustBe Right(traderAddress)
    }

    "return the error response" in {
      setupMockHttpGet(Connector.getAddressUrl(idOne))(Left(errorModel))
      await(getAddressResult()) mustBe Left(errorModel)
    }

  }

  "called to post the Submission" should {

    val submission = IVDSubmission(
      userType = UserType.Importer,
      numEntries = NumberOfEntries.OneEntry,
      acceptanceDate = None,
      additionalInfo = None,
      entryDetails = EntryDetails("123", "123456Q", LocalDate.of(2020, 1, 12)),
      originalCpc = "cpc",
      amendedCpc = None,
      traderContactDetails = ContactDetails("name", "email", "phone"),
      traderAddress = traderAddress,
      defermentType = None,
      defermentAccountNumber = None,
      additionalDefermentNumber = None,
      underpaymentReasons = None,
      underpaymentDetails = None,
      documentList = None
    )

    val submissionResponse = SubmissionResponse("1234")

    "return the Right response" in {
      setupMockHttpPost(Connector.postSubmissionUrl)(Right(submissionResponse))
      await(Connector.postSubmission(submission)) mustBe Right(submissionResponse)
    }
  }

}
