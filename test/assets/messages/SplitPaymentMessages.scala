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

object SplitPaymentMessages extends BaseMessages {

  val title: String = "Do you want to split payment between two deferment accounts?"
  val radioYes: String = "Yes, I want to use two deferment accounts"
  val radioNo: String = "No, I want to use one deferment account"
  val requiredError: String = "Select yes if you want to split payment between two deferment accounts"

}
