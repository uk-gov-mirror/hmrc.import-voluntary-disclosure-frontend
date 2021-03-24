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

object UnderpaymentDetailSummaryMessages extends BaseMessages {

  val originalAmount = "Amount that was paid to HMRC"
  val amendedAmount = "Amount that should have been paid"

  val B00pageTitle = "import VAT"
  val B00pageHeader = "import VAT"
  val B00pageHeaderUpperCase = "Import VAT"
  val A00pageTitle = "Customs Duty"
  val A00pageHeader = "Customs Duty"
  val E00pageTitle = "excise duty"
  val E00pageHeader = "excise duty"
  val E00pageHeaderUpperCase = "Excise duty"
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
  val beginningMessage = "Confirm the"
  val endingMessage = "underpayment details"
  val dueToHMRC = "owed to HMRC"
  val emptySpace = " "

  val underpaymentTypeContent: Map[String, ExpectedContent] = Map(
    "B00" -> ExpectedContent(
      beginningMessage + emptySpace + B00pageTitle + emptySpace + endingMessage,
      beginningMessage + emptySpace + B00pageHeader + emptySpace + endingMessage,
      Some(B00pageHeaderUpperCase + emptySpace + dueToHMRC)
    ),
    "A00" -> ExpectedContent(
      beginningMessage + emptySpace + A00pageTitle + emptySpace + endingMessage,
      beginningMessage + emptySpace + A00pageHeader + emptySpace + endingMessage,
      Some(A00pageTitle + emptySpace + dueToHMRC)
    ),
    "E00" -> ExpectedContent(
      beginningMessage + emptySpace + E00pageTitle + emptySpace + endingMessage,
      beginningMessage + emptySpace + E00pageHeader + emptySpace + endingMessage,
      Some(E00pageHeaderUpperCase + emptySpace + dueToHMRC)
    ),
    "A20" -> ExpectedContent(
      beginningMessage + emptySpace + A20pageTitle + emptySpace + endingMessage,
      beginningMessage + emptySpace + A20pageHeader + emptySpace + endingMessage,
      Some(A20pageTitle + emptySpace + dueToHMRC)
    ),
    "A30" -> ExpectedContent(
      beginningMessage + emptySpace + A30pageTitle + emptySpace + endingMessage,
      beginningMessage + emptySpace + A30pageHeader + emptySpace + endingMessage,
      Some(A30pageTitle + emptySpace + dueToHMRC)
    ),
    "A35" -> ExpectedContent(
      beginningMessage + emptySpace + A35pageTitle + emptySpace + endingMessage,
      beginningMessage + emptySpace + A35pageHeader + emptySpace + endingMessage,
      Some(A35pageTitle + emptySpace + dueToHMRC)
    ),
    "A40" -> ExpectedContent(
      beginningMessage + emptySpace + A40pageTitle + emptySpace + endingMessage,
      beginningMessage + emptySpace + A40pageHeader + emptySpace + endingMessage,
      Some(A40pageTitle + emptySpace + dueToHMRC)
    ),
    "A45" -> ExpectedContent(
      beginningMessage + emptySpace + A45pageTitle + emptySpace + endingMessage,
      beginningMessage + emptySpace + A45pageHeader + emptySpace + endingMessage,
      Some(A45pageTitle + emptySpace + dueToHMRC)
    ),
    "A10" -> ExpectedContent(
      beginningMessage + emptySpace + A10pageTitle + emptySpace + endingMessage,
      beginningMessage + emptySpace + A10pageHeader + emptySpace + endingMessage,
      Some(A10pageTitle + emptySpace + dueToHMRC)
    ),
    "D10" -> ExpectedContent(
      beginningMessage + emptySpace + D10pageTitle + emptySpace + endingMessage,
      beginningMessage + emptySpace + D10pageHeader + emptySpace + endingMessage,
      Some(D10pageTitle + emptySpace + dueToHMRC)
    ),
  )

}
