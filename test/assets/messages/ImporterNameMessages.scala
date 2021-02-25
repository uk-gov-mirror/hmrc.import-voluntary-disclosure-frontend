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

object ImporterNameMessages extends BaseMessages {

  val title: String = "What is the importer’s name?"
  val h1: String = "What is the importer’s name?"
  val hint: String = "We will send a copy of the demand for payment note to the importer by post. We will use this name on the envelope and letter."
  val nonEmpty: String = "Enter the name of the importer"
  val nameMinLength: String = "Full name must be 2 characters or more"
  val nameMaxLength: String = "Full name must be 50 characters or fewer"
  val nameAllowableCharacters: String = "Full name must only include letters a to z, hyphens, spaces and apostrophes"


}
