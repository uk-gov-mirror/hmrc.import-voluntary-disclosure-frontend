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

package assets

import messages.{AddressLookupMessages, BaseMessages}
import models.addressLookup.AddressModel
import play.api.libs.json.{JsObject, Json}

object AddressLookupTestConstants extends BaseMessages {

  val phaseBannerHtml = "This is a new service – your <a id='beta-banner-feedback' href='TBC'>feedback</a> will help us to improve it."

  val addressLine1 = "line 1"
  val addressLine2 = "line 2"
  val addressLine3 = "line 3"
  val addressLine4 = "line 4"
  val postcode = "aa1 1aa"
  val countryName = "United Kingdom"
  val countryCode = "UK"

  val customerAddressMax: AddressModel = AddressModel(
    Some(addressLine1),
    Some(addressLine2),
    Some(addressLine3),
    Some(addressLine4),
    Some(postcode),
    Some(countryCode)
  )

  val customerAddressSome: AddressModel = AddressModel(
    Some(addressLine1),
    Some(addressLine2),
    Some(addressLine3),
    None,
    Some(postcode),
    Some(countryCode)
  )

  val customerAddressMin: AddressModel = AddressModel(None, None, None, None, None, None)

  val customerAddressJsonMax: JsObject = Json.obj(
    "address" -> Json.obj(
      "lines" -> Json.arr(addressLine1, addressLine2, addressLine3, addressLine4),
      "postcode" -> postcode,
      "country" -> Json.obj(
        "name" -> countryName,
        "code" -> countryCode
      )
    )
  )

  val customerAddressJsonSome: JsObject = Json.obj(
    "address" -> Json.obj(
      "lines" -> Json.arr(addressLine1, addressLine2, addressLine3),
      "postcode" -> postcode,
      "country" -> Json.obj(
        "name" -> countryName,
        "code" -> countryCode
      )
    )
  )

  val customerAddressJsonMin: JsObject = Json.obj()

  val customerAddressToJsonMax: JsObject = Json.obj(
    "line1" -> addressLine1,
    "line2" -> addressLine2,
    "line3" -> addressLine3,
    "line4" -> addressLine4,
    "postcode" -> postcode,
    "countryCode" -> countryCode
  )

  val customerAddressToJsonMin: JsObject = Json.obj()

  val customerAddressJsonError: JsObject = Json.obj(
    "address" -> Json.obj(
      "lines" -> 4
    )
  )

  val addressLookupV2Json: JsObject = Json.obj(fields =
    "version" -> 2,
    "options" -> Json.obj(
      "continueUrl" -> "/lookup-address/confirmed",
      "accessibilityFooterUrl" -> "TBC",
      "deskProServiceName" -> "TBC",
      "showPhaseBanner" -> true,
      "ukMode" -> true,
      "timeoutConfig" -> Json.obj(
        "timeoutAmount" -> 900,
        "timeoutUrl" -> "TBC"
      )
    ),
    "labels" -> Json.obj(
      "en" -> Json.obj(
        "appLevelLabels" -> Json.obj(
          "navTitle" -> "import-voluntary-disclosure-frontend",
          "phaseBannerHtml" -> phaseBannerHtml
        ),
        "selectPageLabels" -> Json.obj(
          "title" -> AddressLookupMessages.selectHeading,
          "heading" -> AddressLookupMessages.selectHeading,
          "submitLabel" -> continue,
          "editAddressLinkText" -> AddressLookupMessages.editAddressLinkText
        ),
        "lookupPageLabels" -> Json.obj(
          "title" -> AddressLookupMessages.startHeading,
          "heading" -> AddressLookupMessages.startHeading,
          "filterLabel" -> AddressLookupMessages.filter,
          "postcodeLabel" -> AddressLookupMessages.postcode,
          "submitLabel" -> AddressLookupMessages.submitLabel
        ),
        "confirmPageLabels" -> Json.obj(
          "title" -> AddressLookupMessages.confirmHeading,
          "heading" -> AddressLookupMessages.confirmHeading,
          "showConfirmChangeText" -> false
        ),
        "editPageLabels" -> Json.obj(
          "submitLabel" -> continue
        )
      ),
      "cy" -> Json.obj(
        "appLevelLabels" -> Json.obj(
          "navTitle" -> "import-voluntary-disclosure-frontend",
          "phaseBannerHtml" -> phaseBannerHtml
        ),
        "selectPageLabels" -> Json.obj(
          "title" -> AddressLookupMessages.selectHeading,
          "heading" -> AddressLookupMessages.selectHeading,
          "submitLabel" -> continue,
          "editAddressLinkText" -> AddressLookupMessages.editAddressLinkText
        ),
        "lookupPageLabels" -> Json.obj(
          "title" -> AddressLookupMessages.startHeading,
          "heading" -> AddressLookupMessages.startHeading,
          "filterLabel" -> AddressLookupMessages.filter,
          "postcodeLabel" -> AddressLookupMessages.postcode,
          "submitLabel" -> AddressLookupMessages.submitLabel
        ),
        "confirmPageLabels" -> Json.obj(
          "title" -> AddressLookupMessages.confirmHeading,
          "heading" -> AddressLookupMessages.confirmHeading,
          "showConfirmChangeText" -> false
        ),
        "editPageLabels" -> Json.obj(
          "submitLabel" -> continue
        )
      )
    )
  )

}
