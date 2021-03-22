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

object UnderpaymentDetailsMessages extends BaseMessages {

  val originalAmount = "Amount that was paid to HMRC"
  val amendedAmount = "Amount that should have been paid"

  val B00pageTitle = "Import VAT underpayment details"
  val B00pageHeader = "Import VAT underpayment details"
  val A00pageTitle = "Customs Duty underpayment details"
  val A00pageHeader = "Customs Duty underpayment details"
  val E00pageTitle = "Excise duty underpayment details"
  val E00pageHeader = "Excise duty underpayment details"
  val A20pageTitle = "Additional Duty underpayment details"
  val A20pageHeader = "Additional Duty underpayment details"
  val A30pageTitle = "Definitive Anti-Dumping Duty underpayment details"
  val A30pageHeader = "Definitive Anti-Dumping Duty underpayment details"
  val A35pageTitle = "Provisional Anti-Dumping Duty underpayment details"
  val A35pageHeader = "Provisional Anti-Dumping Duty underpayment details"
  val A40pageTitle = "Definitive Countervailing Duty underpayment details"
  val A40pageHeader = "Definitive Countervailing Duty underpayment details"
  val A45pageTitle = "Provisional Countervailing Duty underpayment details"
  val A45pageHeader = "Provisional Countervailing Duty underpayment details"
  val A10pageTitle = "Customs Duty on Agricultural Products underpayment details"
  val A10pageHeader = "Customs Duty on Agricultural Products underpayment details"
  val D10pageTitle = "Compensatory Duty underpayment details"
  val D10pageHeader = "Compensatory Duty underpayment details"

  val originalNonEmpty = "Enter the amount that was paid to HMRC, in pounds"
  val originalNonNumber = "Amount that was paid to HMRC must be a number like 7235 or 67.39"
  val originalOutOfRange = "Amount that was paid to HMRC must be between £0 and £9,999,999,999.99"
  val amendedNonEmpty = "Enter the amount that should have been paid, in pounds"
  val amendedNonNumber = "Amount that should have been paid must be a number like 7235 or 67.39"
  val amendedOutOfRange = "Amount that should have been paid must be between £0 and £9,999,999,999.99"
  val amendedDifferent = "Amount that should have been paid must be more than amount that was paid to HMRC"

  val underpaymentTypeContent: Map[String, ExpectedContent] = Map(
    "B00" -> ExpectedContent(
      B00pageTitle,
      B00pageHeader,
      None
    ),
    "A00" -> ExpectedContent(
      A00pageTitle,
      A00pageHeader,
      None
    ),
    "E00" -> ExpectedContent(
      E00pageTitle,
      E00pageHeader,
      None
    ),
    "A20" -> ExpectedContent(
      A20pageTitle,
      A20pageHeader,
      None
    ),
    "A30" -> ExpectedContent(
      A30pageTitle,
      A30pageHeader,
      None
    ),
    "A35" -> ExpectedContent(
      A35pageTitle,
      A35pageHeader,
      None
    ),
    "A40" -> ExpectedContent(
      A40pageTitle,
      A40pageHeader,
      None
    ),
    "A45" -> ExpectedContent(
      A45pageTitle,
      A45pageHeader,
      None
    ),
    "A10" -> ExpectedContent(
      A10pageTitle,
      A10pageHeader,
      None
    ),
    "D10" -> ExpectedContent(
      D10pageTitle,
      D10pageHeader,
      None
    ),
  )

}
