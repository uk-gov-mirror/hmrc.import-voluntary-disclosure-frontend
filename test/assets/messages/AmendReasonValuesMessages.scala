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

package messages

case class ExpectedContent(title: String, heading: String, body: Option[String])

object AmendReasonValuesMessages extends BaseMessages {

  val box22PageTitle: String = "Box 22 invoice currency and total amount invoiced amendment"
  val box22P1: String = "You must include the currency code followed by the invoice price or the customs value, for example GBP871.12 or EUR2908946."
  val box35PageTitle: String = "Box 35 gross mass amendment for item 1"
  val box35P1: String = "Must be in kilograms and can be up to 3 decimal places."
  val box46PageTitle: String = "Box 46 statistical value amendment for item 1"
  val originalAmount: String = "Original value"
  val amendedAmount: String = "Amended value"
  val originalNonEmpty: String = "Enter the original value"
  val amendedNonEmpty: String = "Enter the amended value"
  val amendedDifferent: String = "Amended value must be different from original value"
  val originalInvalidFormat: String = "Enter the original value in the correct format"
  val amendedInvalidFormat: String = "Enter the amended value in the correct format"
  val originalWeightNonEmpty: String = "Enter the original value, in kilograms"
  val amendedWeightNonEmpty: String = "Enter the amended value, in kilograms"
  val originalInvalidWeightFormat: String = "Original value must be a number"
  val amendedInvalidWeightFormat: String = "Amended value must be a number"
  val originalInvalidWeightDecimal: String = "Original value must have 3 decimal places or fewer"
  val amendedInvalidWeightDecimal: String = "Amended value must have 3 decimal places or fewer"
  val originalInvalidWeightOutOfRange: String = "Original value must be between 0 and 9999999.999kg"
  val amendedInvalidWeightOutOfRange: String = "Amended value must be between 0 and 9999999.999kg"
  val originalCurrencyNonEmpty: String = "Enter the original value, in pounds"
  val amendedCurrencyNonEmpty: String = "Enter the amended value, in pounds"
  val originalInvalidCurrencyFormat: String = "Original value must be a number"
  val amendedInvalidCurrencyFormat: String = "Amended value must be a number"
  val originalInvalidCurrencyDecimal: String = "Original value must have 2 decimal places or fewer"
  val amendedInvalidCurrencyDecimal: String = "Amended value must have 2 decimal places or fewer"
  val originalInvalidCurrencyOutOfRange: String = "Original value must be between £0 and £999999999999.99"
  val amendedInvalidCurrencyOutOfRange: String = "Amended value must be between £0 and £999999999999.99"

  val boxContent: Map[Int, ExpectedContent] = Map(
    22 -> ExpectedContent(
      "Box 22 invoice currency and total amount invoiced amendment",
      "Box 22 invoice currency and total amount invoiced amendment",
      Some("You must include the currency code followed by the invoice price or the customs value, for example GBP871.12 or EUR2908946.")),
    33 -> ExpectedContent(
      "Box 33 commodity code amendment for item 1",
      "Box 33 commodity code amendment for item 1",
      Some("Must be 10 numbers, sometimes followed by a code of 4 characters, for example 1806321000 or 2204109400X411.")),
    34 -> ExpectedContent(
      "Box 34 country of origin code amendment for item 1",
      "Box 34 country of origin code amendment for item 1",
      Some("Must be 2 characters, for example GB or CN.")),
    35 -> ExpectedContent(
      "Box 35 gross mass amendment for item 1",
      "Box 35 gross mass amendment for item 1",
      Some("Must be in kilograms and can be up to 3 decimal places.")),
    36 -> ExpectedContent(
      "Box 36 preference amendment for item 1",
      "Box 36 preference amendment for item 1",
      Some("Must be 3 numbers, for example 100 or 350.")),
    37 -> ExpectedContent(
      "Box 37 customs procedure code amendment for item 1",
      "Box 37 customs procedure code amendment for item 1",
      Some("Must be 7 numbers, sometimes including a letter, for example 4000000 or 4000C10.")),
    38 -> ExpectedContent(
      "Box 38 net mass amendment for item 1",
      "Box 38 net mass amendment for item 1",
      Some("Must be in kilograms and can be up to 3 decimal places.")),
    39 -> ExpectedContent(
      "Box 39 quota amendment for item 1",
      "Box 39 quota amendment for item 1",
      Some("Must be 7 characters.")),
    41 -> ExpectedContent(
      "Box 41 supplementary units amendment for item 1",
      "Box 41 supplementary units amendment for item 1", None),
    42 -> ExpectedContent(
      "Box 42 item price amendment for item 1",
      "Box 42 item price amendment for item 1",
      Some("The currency of the item price should be the same as the currency used for box 22 (invoice currency and total amount invoiced).")),
    43 -> ExpectedContent("n/a", "n/a", Some("n/a")),
    45 -> ExpectedContent("n/a", "n/a", Some("n/a")),
    46 -> ExpectedContent(
      "Box 46 statistical value amendment for item 1",
      "Box 46 statistical value amendment for item 1",
      Some("n/a")),
    62 -> ExpectedContent(
      "Box 62 air transport costs amendment",
      "Box 62 air transport costs amendment",
      Some("Must be the currency code followed by the invoice price or the customs value, for example GBP871.12 or EUR2908946.")),
    63 -> ExpectedContent(
      "Box 63 AWB or freight charges amendment",
      "Box 63 AWB or freight charges amendment",
      Some("Must be the currency code followed by the invoice price or the customs value, for example GBP871.12 or EUR2908946.")),
    66 -> ExpectedContent(
      "Box 66 insurance amendment",
      "Box 66 insurance amendment",
      Some("Must be the currency code followed by the invoice price or the customs value, for example GBP871.12 or EUR2908946.")),
    67 -> ExpectedContent(
      "Box 67 other charges or deductions amendment",
      "Box 67 other charges or deductions amendment",
      Some("Must be the currency code followed by the invoice price or the customs value, for example GBP871.12 or EUR2908946.")),
    68 -> ExpectedContent(
      "Box 68 adjustment for VAT value amendment",
      "Box 68 adjustment for VAT value amendment",
      Some("Must be the currency code followed by the invoice price or the customs value, for example GBP871.12 or EUR2908946."))
  )

}
