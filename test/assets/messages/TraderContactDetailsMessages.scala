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

object TraderContactDetailsMessages extends BaseMessages {

  val title: String = "Who should we contact if we have questions about this disclosure?"
  val h1: String = "Who should we contact if we have questions about this disclosure?"

  val errorNameNonEmpty: String = "Enter a name"
  val errorEmailNonEmpty: String = "Enter an email address"
  val errorPhoneNumberNonEmpty: String = "Enter a UK telephone number"
  val errorNameMinLength: String = "Name must be 2 characters or more"
  val errorNameMaxLength: String = "Name must be 50 characters or fewer"
  val errorNameAllowableCharacters: String = "Name must only include letters a to z, hyphens, spaces and apostrophes"
  val errorEmailInvalidFormat: String = "Enter an email address in the correct format, like name@example.com"
  val errorPhoneNumberInvalidFormat: String = "Enter a UK telephone number, like 01632 960 001 or 07700 900 982"

}
