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

import messages.BoxNumberMessages
import messages.underpayments.UnderpaymentTypeMessages
import models.{ContactAddress, EoriDetails}
import play.api.http.Status
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import uk.gov.hmrc.http.HttpResponse

trait ReusableValues {

  val idOne: String = "1"

  val addressDetails: ContactAddress = ContactAddress(
    addressLine1 = "99 Avenue Road",
    addressLine2 = None,
    city = "Anyold Town",
    postalCode = Some("99JZ 1AA"),
    countryCode = "GB"
  )

  val eoriDetails: EoriDetails = EoriDetails(
    "GB987654321000",
    "Fast Food ltd",
    ContactAddress(
      addressLine1 = "99 Avenue Road",
      addressLine2 = None,
      city = "Anyold Town",
      postalCode = Some("99JZ 1AA"),
      countryCode = "GB"
    )
  )

  val errorModel: HttpResponse = HttpResponse(Status.NOT_FOUND, "Error Message")

  val detailsJson: JsObject = Json.obj(
    "responseDetail" -> Json.obj(
      "EORINo" -> "GB987654321000",
      "CDSFullName" -> "Fast Food ltd",
      "CDSEstablishmentAddress" -> Json.obj(
        "streetAndNumber" -> "99 Avenue Road",
        "city" -> "Anyold Town",
        "postalCode" -> "99JZ 1AA",
        "countryCode" -> "GB"
      )
    )
  )

  val cleanedDetailsJson: JsObject = Json.obj(
    "eori" -> "GB987654321000",
    "name" -> "Fast Food ltd",
    "streetAndNumber" -> "99 Avenue Road",
    "city" -> "Anyold Town",
    "postalCode" -> "99JZ 1AA",
    "countryCode" -> "GB"
  )

  val underpaymentTypeRadioButtons = Seq(
    createRadioButton("B00", UnderpaymentTypeMessages.importVAT),
    createRadioButton("A00", UnderpaymentTypeMessages.customsDuty),
    createRadioButton("E00", UnderpaymentTypeMessages.exciseDuty),
    createRadioButton("A20", UnderpaymentTypeMessages.additionalDuty),
    createRadioButton("A30", UnderpaymentTypeMessages.definitiveAntiDumpingDuty),
    createRadioButton("A35", UnderpaymentTypeMessages.provisionalAntiDumpingDuty),
    createRadioButton("A40", UnderpaymentTypeMessages.definitiveCountervailingDuty),
    createRadioButton("A45", UnderpaymentTypeMessages.provisionalCountervailingDuty),
    createRadioButton("A10", UnderpaymentTypeMessages.agriculturalDuty),
    createRadioButton("D10", UnderpaymentTypeMessages.compensatoryDuty)
  )


   def createRadioButton(value: String, message: String): RadioItem = {
    RadioItem(
      value = Some(value),
      content = Text(message),
      checked = false
    )
  }

}
