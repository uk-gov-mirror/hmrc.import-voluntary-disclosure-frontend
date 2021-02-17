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

object AmendReasonValuesMessages extends BaseMessages {

  val box22PageTitle: String = "Box 22 invoice currency and total amount invoiced amendment"
  val box22P1: String = "You must include the currency code followed by the invoice price or the customs value, for example GBP871.12 or EUR2908946."
  val originalAmount: String =  "Original value"
  val amendedAmount: String = "Amended value"
  val originalNonEmpty: String = "Enter the original value"
  val amendedNonEmpty: String = "Enter the amended value"
  val amendedDifferent: String = "Amended value must be different from original value"
  val originalInvalidFormat: String = "Enter the original value in the correct format"
  val amendedInvalidFormat: String = "Enter the amended value in the correct format"

}
