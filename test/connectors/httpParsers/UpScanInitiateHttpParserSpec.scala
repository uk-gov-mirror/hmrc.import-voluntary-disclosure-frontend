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
import models.{BadRequest, InvalidJson, UnexpectedFailure}
import models.upscan.{Reference, UpScanInitiateResponse, UploadFormTemplate}
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.HttpResponse

class UpScanInitiateHttpParserSpec extends SpecBase {

  class Test(status: Int, json: JsValue = Json.obj(), responseHeaders: Map[String, Seq[String]] = Map.empty) {
    private val httpMethod = "POST"
    private val url = "/"
    val httpResponse: HttpResponse = HttpResponse(status, json.toString, responseHeaders)

    def readResponse: UpScanInitiateHttpParser.UpscanInitiateResponse =
      UpScanInitiateHttpParser.UpScanInitiateResponseReads.read(httpMethod, url, httpResponse)
  }

  val validModel: UpScanInitiateResponse =  UpScanInitiateResponse(
    Reference("11370e18-6e24-453e-b45a-76d3e32ea33d"),
    UploadFormTemplate(
      "https://bucketName.s3.eu-west-2.amazonaws.com",
      Map("Content-Type" -> "application/xml")
    )
  )

  val exampleResponse: JsValue = Json.parse(
    s""" {
       |    "reference": "11370e18-6e24-453e-b45a-76d3e32ea33d",
       |    "uploadRequest": {
       |        "href": "https://bucketName.s3.eu-west-2.amazonaws.com",
       |        "fields": {
       |            "Content-Type": "application/xml"
       |        }
       |    }
       |}
    """.stripMargin
  )

  "reads" should {
    s"return model if ${Status.OK} + Json valid" in new Test(Status.OK, exampleResponse) {
      readResponse mustBe Right(validModel)
    }
    s"return Invalid Json if ${Status.OK} + json invalid" in new Test(Status.OK) {
      readResponse mustBe Left(InvalidJson)
    }
    s"return $BadRequest if ${Status.BAD_REQUEST} returned" in new Test(Status.BAD_REQUEST) {
      readResponse mustBe Left(BadRequest)
    }
    s"return $UnexpectedFailure if random non Success status code returned" in new Test(Status.INTERNAL_SERVER_ERROR) {
      readResponse mustBe Left(UnexpectedFailure(Status.INTERNAL_SERVER_ERROR, "Unexpected response, status 500 returned"))
    }
  }
}
