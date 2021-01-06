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

object NumberOfEntriesMessages extends BaseMessages {

  val title: String = "How many entries are you disclosing an underpayment for?"
  val h1: String = "How many entries are you disclosing an underpayment for?"
  val radioButtonOne: String = "One entry"
  val radioButtonTwo: String = "More than one entry"
  val hint: String = "Multiple entries must be for the same importer, and have the same reason for underpayment."
  val requiredError: String = "Select if you are disclosing an underpayment for one declaration or more than one declaration"

}
