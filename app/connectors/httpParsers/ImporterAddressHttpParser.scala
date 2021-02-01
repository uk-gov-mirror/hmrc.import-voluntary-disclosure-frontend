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

import connectors.httpParsers.ResponseHttpParser.HttpGetResult
import models.{ErrorModel, TraderAddress}
import play.api.Logger
import play.api.http.Status
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object ImporterAddressHttpParser {

  implicit object TraderAddressReads extends HttpReads[HttpGetResult[TraderAddress]] {

    private val logger = Logger("application." + getClass.getCanonicalName)

    override def read(method: String, url: String, response: HttpResponse): HttpGetResult[TraderAddress] = {

      response.status match {
        case Status.OK =>
          response.json.validate[TraderAddress](TraderAddress.reads).fold(
            invalid => {
              logger.error("Failed to validate JSON with errors: " + invalid)
              Left(ErrorModel(Status.INTERNAL_SERVER_ERROR, "Invalid Json returned from SUB09 API for TraderAddressHttpParser"))
            },
            valid => Right(valid)
          )
        case status =>
          logger.error("Failed to validate JSON with status: " + status + " body: " + response.body)
          Left(ErrorModel(status, "Downstream error returned when retrieving TraderAddress model from back end"))
      }
    }
  }

}
