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

object CustomsDutyMessages extends BaseMessages {

  val pageTitle = "Customs Duty underpayment details"
  val pageHeader = "Customs Duty underpayment details"
  val originalAmount = "Original amount, in pounds"
  val amendedAmount = "Amended amount, in pounds"
  val originalNonEmpty = "Enter the original amount, in pounds"
  val originalNonNumber = "Original amount must be a number like 7235 or 67.39"
  val originalUpperLimit = "Original amount cannot be more than £9,999,999,999.99"
  val amendedNonEmpty = "Enter the amended amount, in pounds"
  val amendedNonNumber = "Amended amount must be a number like 7235 or 67.39"

}
