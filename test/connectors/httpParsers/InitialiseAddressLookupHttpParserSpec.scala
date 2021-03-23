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

package connectors.httpParsers

import assets.AddressLookupTestConstants.customerAddressJsonError
import base.SpecBase
import connectors.httpParsers.InitialiseAddressLookupHttpParser.InitialiseAddressLookupReads
import models.ErrorModel
import models.addressLookup.AddressLookupOnRampModel
import play.api.http.Status
import uk.gov.hmrc.http.HttpResponse

class InitialiseAddressLookupHttpParserSpec extends SpecBase {

  val errorModel: ErrorModel = ErrorModel(Status.INTERNAL_SERVER_ERROR, "Response Header did not contain location redirect")

  "The InitialiseAddressLookupHttpParser" when {

    "the http response status is OK" should {

      "return a InitialiseAddressLookupModel" in {
        val validResponse = HttpResponse(
          Status.ACCEPTED,
          "",
          Map("Location" -> Seq("redirectUrl"))
        )
        InitialiseAddressLookupReads.read("", "", validResponse) mustBe
          Right(AddressLookupOnRampModel("redirectUrl"))
      }
    }

    "the http response status is INTERNAL_SERVER_ERROR when no redirect uri is returned" should {

      "return a InitialiseAddressLookupModel" in {
        InitialiseAddressLookupReads.read("", "", HttpResponse(Status.ACCEPTED, "")) mustBe Left(errorModel)
      }
    }

    "the http response status is INTERNAL_SERVER_ERROR when UNAUTHORISED is returned" should {

      "return an ErrorModel" in {
        InitialiseAddressLookupReads.read("", "",
          HttpResponse(Status.UNAUTHORIZED, customerAddressJsonError, Map.empty[String, Seq[String]])) mustBe
          Left(ErrorModel(Status.INTERNAL_SERVER_ERROR,"Downstream error returned from Address Lookup"))
      }
    }
  }
}
