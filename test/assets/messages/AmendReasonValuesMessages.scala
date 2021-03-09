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
  val originalAmount: String =  "Original value"
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
  val originalInvalidWeightOutOfRange: String = "Original value must be 9999999.999kg or fewer"
  val amendedInvalidWeightOutOfRange: String = "Amended value must be 9999999.999kg or fewer"

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
    36 -> ExpectedContent("n/a", "n/a", Some("n/a")),
    37 -> ExpectedContent("n/a", "n/a", Some("n/a")),
    38 -> ExpectedContent(
      "Box 38 net mass amendment for item 1",
      "Box 38 net mass amendment for item 1",
      Some("Must be in kilograms and can be up to 3 decimal places.")),
    39 -> ExpectedContent("n/a", "n/a", Some("n/a")),
    41 -> ExpectedContent("n/a", "n/a", Some("n/a")),
    42 -> ExpectedContent("n/a", "n/a", Some("n/a")),
    43 -> ExpectedContent("n/a", "n/a", Some("n/a")),
    45 -> ExpectedContent("n/a", "n/a", Some("n/a")),
    46 -> ExpectedContent("n/a", "n/a", Some("n/a")),
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
