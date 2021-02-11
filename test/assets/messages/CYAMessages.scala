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

object CYAMessages extends BaseMessages {

  val title = "Check your answers before sending your disclosure"
  val heading = "Check your answers before sending your disclosure"
  val sendDisclosure = "Now send your disclosure"
  val disclosureConfirmation = "By sending this disclosure you are confirming that, to the best of your knowledge, the details you are providing are correct."
  val acceptAndSend = "Accept and send"

  val disclosureDetails = "Disclosure details"
  val numberOfEntries = "Number of entries"
  val epu = "EPU"
  val entryNumber = "Entry number"
  val entryDate = "Entry date"
  val acceptanceDate = "Entry acceptance date before 1 January 2021?"

  val underpaymentDetails = "Underpayment details"
  val customsDuty = "Customs Duty"
  val importVAT = "Import VAT"
  val exciseDuty = "Excise Duty"

  val amendmentDetails = "Amendment details"
  val cpc = "Customs procedure code"
  val cpcAmended = "Amended customs procedure code"
  val cpcChanged = "Customs procedure code changed?"
  val numAmendments = "Number of amendments"
  val supportingInformation = "Supporting information"
  val supportingDocuments = "Supporting documents"
  def filesUploaded(numberOfFiles: Int): String = {
    if(numberOfFiles ==1) s"$numberOfFiles file uploaded" else s"$numberOfFiles files uploaded"
  }
  val yourDetails = "Your details"
  val name = "Name"
  val email = "Email address"
  val phone = "Telephone number"
  val address = "Address"

  val paymentInformation = "Payment information"
  val payingByDeferment = "By deferment?"

  val change = "Change"
}
