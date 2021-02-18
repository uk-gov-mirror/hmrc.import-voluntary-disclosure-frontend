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

package models

import play.api.libs.json.{Format, Json, Reads, _}

case class ContactAddress(streetAndNumber: String, city: String, postalCode: Option[String], countryCode: String)

object ContactAddress {

  implicit val reads: Reads[ContactAddress] = for {
    streetAndNumber <- (__ \\ "streetAndNumber").read[String]
    city <- (__ \\ "city").read[String]
    postalCode <- (__ \\ "postalCode").readNullable[String]
    countryCode <- (__ \\ "countryCode").read[String]
  } yield {
    ContactAddress(streetAndNumber, city, postalCode, countryCode)
  }

  implicit val format: Format[ContactAddress] = Json.format[ContactAddress]

}
