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

import messages.BaseMessages

object UnderpaymentDetailSummaryMessages extends BaseMessages {

  val pageTitle = "Underpayment summary"
  val pageHeader = "Underpayment summary"
  val pageHeaderSmall = "Amount owed for each type of duty or tax"
  val owedToHMRC = "Total owed to HMRC"
  val radioMessage = "Add another underpayment?"
  val radioMessageHint = "You must tell us about all the types of duty or tax that were underpaid on the original import declaration."
  val fullList = "You cannot add any more underpayment details as you have selected all the possible types of tax or duty that can apply to an import declaration."
  val errorRequired = "Select yes if you want to add another type of tax or duty underpayment"
  val B00 = "Import VAT"
  val A00 = "Customs Duty"
  val E00 = "Excise duty"
  val A20 = "Additional Duty"
  val A30 = "Definitive Anti-Dumping Duty"
  val A35 = "Provisional Anti-Dumping Duty"
  val A40 = "Definitive Countervailing Duty"
  val A45 = "Provisional Countervailing Duty"
  val A10 = "Customs Duty on Agricultural Products"
  val D10 = "Compensatory Duty"

}



