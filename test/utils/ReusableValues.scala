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

package utils

import models.TraderAddress
import play.api.http.Status
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.http.HttpResponse

trait ReusableValues {

  val idOne: String = "1"

  val traderAddress: TraderAddress = TraderAddress("first", "second", Some("third"), "fourth")
  val traderAddressWithoutPostcode: TraderAddress = TraderAddress("first", "second", Some("None"), "fourth")

  val errorModel: HttpResponse = HttpResponse(Status.NOT_FOUND, "Error Message")

  val traderAddressJson: JsObject = Json.obj(
    "streetAndNumber" -> "first",
    "city" -> "second",
    "postalCode" -> Some("third"),
    "countryCode" -> "fourth"
  )

}
