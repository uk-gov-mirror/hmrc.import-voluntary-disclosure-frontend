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

import base.SpecBase
import connectors.httpParsers.IvdSubmissionHttpParser.{SubmissionResponseReads, TraderAddressReads}
import models.{ErrorModel, SubmissionResponse}
import play.api.http.Status
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.http.HttpResponse
import utils.ReusableValues

class IvdSubmissionHttpParserSpec extends SpecBase with ReusableValues {

  val traderAddressJsonWithoutPostcode: JsObject = Json.obj(
    "streetAndNumber" -> "first",
    "city" -> "second",
    "postalCode" -> Some("None"),
    "countryCode" -> "fourth"
  )

  val submissionResponseJson: JsObject = Json.obj(
    "id" -> "1234567890"
  )

  val submissionResponseModel = SubmissionResponse("1234567890")

  "IVD Submission HttpParser" when {

    "called to parse a Trader Address" should {
      "the http response status is OK with valid Json" in {
        TraderAddressReads.read("", "",
          HttpResponse(Status.OK, traderAddressJson, Map.empty[String, Seq[String]])) mustBe Right(traderAddress)
      }

      "the http response status is OK with valid Json - postcode none" in {
        TraderAddressReads.read("", "",
          HttpResponse(Status.OK, traderAddressJsonWithoutPostcode, Map.empty[String, Seq[String]])) mustBe Right(traderAddressWithoutPostcode)
      }

      "return an ErrorModel when invalid Json is returned" in {
        TraderAddressReads.read("", "",
          HttpResponse(Status.OK, Json.obj(), Map.empty[String, Seq[String]])) mustBe
          Left(ErrorModel(Status.INTERNAL_SERVER_ERROR,
            "Invalid Json returned from SUB09 API for TraderAddressHttpParser"))
      }

      "return an ErrorModel when NOT_FOUND is returned" in {
        TraderAddressReads.read("", "",
          HttpResponse(Status.NOT_FOUND, "")) mustBe
          Left(ErrorModel(Status.NOT_FOUND,
            "Downstream error returned when retrieving TraderAddress model from back end"))
      }
    }

    "called to parse a Submission Response" should {
      "the http response status is OK with valid Json" in {
        SubmissionResponseReads.read("", "",
          HttpResponse(Status.OK, submissionResponseJson, Map.empty[String, Seq[String]])) mustBe Right(submissionResponseModel)
      }

      "return an ErrorModel when invalid Json is returned" in {
        SubmissionResponseReads.read("", "",
          HttpResponse(Status.OK, Json.obj(), Map.empty[String, Seq[String]])) mustBe
          Left(ErrorModel(Status.INTERNAL_SERVER_ERROR,
            "Invalid Json returned from IVD Submission"))
      }

      "return an ErrorModel when NOT_FOUND is returned" in {
        SubmissionResponseReads.read("", "",
          HttpResponse(Status.NOT_FOUND, "")) mustBe
          Left(ErrorModel(Status.NOT_FOUND,
            "Downstream error returned when retrieving SubmissionResponse from back end"))
      }
    }

  }
}
