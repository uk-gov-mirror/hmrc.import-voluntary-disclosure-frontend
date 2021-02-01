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

package models.addressLookup

import play.api.libs.json._

case class AddressModel(line1: Option[String],
                        line2: Option[String],
                        line3: Option[String],
                        line4: Option[String],
                        postcode: Option[String],
                        countryCode: Option[String])

object AddressModel {

  implicit val customerAddressReads: Reads[AddressModel] = for {
    lines <- (__ \\ "lines").readNullable[Seq[String]]
    postcode <- (__ \\ "postcode").readNullable[String]
    countryCode <- (__ \\ "code").readNullable[String]
  } yield {
    lines match {
      case Some(someSequence) => AddressModel(
        someSequence.headOption,
        someSequence.lift(1),
        someSequence.lift(2),
        someSequence.lift(3),
        postcode, countryCode)
      case None => AddressModel(None, None, None, None, postcode, countryCode)
    }
  }

  implicit val format: Format[AddressModel] = Json.format[AddressModel]

}

