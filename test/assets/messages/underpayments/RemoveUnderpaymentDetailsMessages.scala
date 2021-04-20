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

package messages.underpayments

import messages.{BaseMessages, ExpectedContent}

object RemoveUnderpaymentDetailsMessages extends BaseMessages {

  val B00pageTitle = "import VAT"
  val B00pageHeader = "import VAT"
  val A00pageTitle = "Customs Duty"
  val A00pageHeader = "Customs Duty"
  val E00pageTitle = "excise duty"
  val E00pageHeader = "excise duty"
  val A20pageTitle = "Additional Duty"
  val A20pageHeader = "Additional Duty"
  val A30pageTitle = "Definitive Anti-Dumping Duty"
  val A30pageHeader = "Definitive Anti-Dumping Duty"
  val A35pageTitle = "Provisional Anti-Dumping Duty"
  val A35pageHeader = "Provisional Anti-Dumping Duty"
  val A40pageTitle = "Definitive Countervailing Duty"
  val A40pageHeader = "Definitive Countervailing Duty"
  val A45pageTitle = "Provisional Countervailing Duty"
  val A45pageHeader = "Provisional Countervailing Duty"
  val A10pageTitle = "Customs Duty on Agricultural Products"
  val A10pageHeader = "Customs Duty on Agricultural Products"
  val D10pageTitle = "Compensatory Duty"
  val D10pageHeader = "Compensatory Duty"
  val beginningMessage = "Are you sure you want to remove this "
  val endingMessage = " underpayment?"
  val radioYes = "Yes"
  val radioNo = "No"

  val underpaymentTypeContent: Map[String, ExpectedContent] = Map(
    "B00" -> ExpectedContent(
      beginningMessage + B00pageTitle + endingMessage,
      beginningMessage + B00pageHeader + endingMessage,
      Some("Select yes if you want to remove this import VAT underpayment")
    ),
    "A00" -> ExpectedContent(
      beginningMessage + A00pageTitle + endingMessage,
      beginningMessage + A00pageHeader + endingMessage,
      Some("Select yes if you want to remove this Customs Duty VAT underpayment")
    ),
    "E00" -> ExpectedContent(
      beginningMessage + E00pageTitle + endingMessage,
      beginningMessage + E00pageHeader + endingMessage,
      Some("Select yes if you want to remove this excise duty underpayment")
    ),
    "A20" -> ExpectedContent(
      beginningMessage + A20pageTitle + endingMessage,
      beginningMessage + A20pageHeader + endingMessage,
      Some("Select yes if you want to remove this Additional Duty underpayment")
    ),
    "A30" -> ExpectedContent(
      beginningMessage + A30pageTitle + endingMessage,
      beginningMessage + A30pageHeader + endingMessage,
      Some("Select yes if you want to remove this Definitive Anti-Dumping Duty underpayment")
    ),
    "A35" -> ExpectedContent(
      beginningMessage + A35pageTitle + endingMessage,
      beginningMessage + A35pageHeader + endingMessage,
      Some("Select yes if you want to remove this Provisional Anti-Dumping Duty underpayment")
    ),
    "A40" -> ExpectedContent(
      beginningMessage + A40pageTitle + endingMessage,
      beginningMessage + A40pageHeader + endingMessage,
      Some("Select yes if you want to remove this Definitive Countervailing Duty underpayment")
    ),
    "A45" -> ExpectedContent(
      beginningMessage + A45pageTitle + endingMessage,
      beginningMessage + A45pageHeader + endingMessage,
      Some("Select yes if you want to remove this Provisional Countervailing Duty underpayment")
    ),
    "A10" -> ExpectedContent(
      beginningMessage + A10pageTitle + endingMessage,
      beginningMessage + A10pageHeader + endingMessage,
      Some("Select yes if you want to remove this Customs Duty on Agricultural Products underpayment")
    ),
    "D10" -> ExpectedContent(
      beginningMessage + D10pageTitle + endingMessage,
      beginningMessage + D10pageHeader + endingMessage,
      Some("Select yes if you want to remove this Compensatory Duty underpayment")
    )
  )

}
