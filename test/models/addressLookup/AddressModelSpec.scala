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

import assets.AddressLookupTestConstants.{customerAddressJsonMax, customerAddressJsonMin, customerAddressJsonSome, customerAddressMax, customerAddressMin, customerAddressSome, customerAddressToJsonMax, customerAddressToJsonMin}
import base.SpecBase
import play.api.libs.json.Json

class AddressModelSpec extends SpecBase {

  "CustomerAddressModel" must {

    "Deserialize from JSON" when {

      "all optional fields are populated" in {
        customerAddressJsonMax.as[AddressModel](AddressModel.customerAddressReads) mustBe customerAddressMax
      }

      "some optional fields are populated" in {
        customerAddressJsonSome.as[AddressModel](AddressModel.customerAddressReads) mustBe customerAddressSome
      }

      "no optional fields are returned" in {
        customerAddressJsonMin.as[AddressModel](AddressModel.customerAddressReads) mustBe customerAddressMin
      }

    }

    "Serialize to JSON" when {

      "all optional fields are populated" in {
        Json.toJson(customerAddressMax) mustBe customerAddressToJsonMax
      }

      "no optional fields are returned" in {
        Json.toJson(customerAddressMin) mustBe customerAddressToJsonMin
      }
    }
  }

}
