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

object UploadAuthorityMessages extends BaseMessages {

  val title: String = "Upload proof of authority to use this deferment account"
  val h1: String = "Upload proof of authority to use this deferment account"
  def para1(dan: String, dutyType: String): String = s"You must provide proof that you have one-off authority to use this deferment account ${dan} to pay for the ${dutyType} owed."
  val para2: String = "The proof needs to be dated and signed by the owner of the deferment account. The date must be after the date of the original import declaration."
  val h2: String = "File size and type"
  val para3: String = "The file must be 6MB or smaller and either be a PDF (.pdf) or a text (.txt) file."
  val legend: String = "Upload file"
  val button: String = "Upload file"
  val fileUploadId: String = "file"
}
