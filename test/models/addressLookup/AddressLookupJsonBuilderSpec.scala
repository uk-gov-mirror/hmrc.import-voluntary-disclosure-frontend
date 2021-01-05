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

import assets.AddressLookupTestConstants.addressLookupV2Json
import base.SpecBase
import mocks.config.MockAppConfig
import play.api.libs.json.Json

class AddressLookupJsonBuilderSpec extends SpecBase {

  "AddressLookupJsonBuilder" must {

    "Serialize to new address lookup Json when using addressLookup v2" when {

      "the continueUrl is given to the user" in {

        Json.toJson(AddressLookupJsonBuilder("/lookup-address/confirmed")(fakeRequest, messagesApi, MockAppConfig)) mustBe addressLookupV2Json
      }
    }
  }

}

