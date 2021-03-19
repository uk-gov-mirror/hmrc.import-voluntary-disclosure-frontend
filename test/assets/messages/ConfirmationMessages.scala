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

object ConfirmationMessages extends BaseMessages {

  val pageTitle = "Confirmation screen"
  val heading = "Disclosure complete"
  val entryNumber = "Entry number:"
  val p1 = "We have received your underpayment disclosure."
  val printSave = "Print or save this page"
  val printSaveRestOfMessage = "as we will not send you an email confirmation."
  val whatHappensNext = "What happens next"
  val p2 =
    "We will check the information you have provided. " +
    "We will send a C18 Post Clearance Demand Note in the post within 10 days. " +
    "This demand note will confirm what is owed to HMRC and will tell you how to pay. " +
    "Payment must be made within 10 days of receiving the demand note, otherwise interest will become due."
  val p3 = "If you have any questions or have not received the demand note within 10 days contact the C18 team by email npcc@hmrc.gov.uk."
  val helpImproveService = "Help us improve this service"
  val helpImproveServiceLink = "What did you think of this service? (takes 30 seconds)"

}
