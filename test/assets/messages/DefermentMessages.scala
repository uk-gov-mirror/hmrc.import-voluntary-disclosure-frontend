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

object DefermentMessages extends BaseMessages {

  val headingOnlyVAT: String = "How will you pay for the import VAT owed?"
  val headingVATandDuty: String = "How will you pay for the import VAT and duty owed?"
  val headingDutyOnly: String = "How will you pay for the duty owed?"
  val hint: String = "This can include BACS, CHAPS, Faster Payments, cheque, or banker’s draft. More information will be sent to you with the C18 Post Clearance Demand Note."
  val payingByDeferment: String = "By duty deferment account"
  val payingByOther: String = "Another payment method"
  val requiredError: String = "Select how you will pay for the underpayment"

}
