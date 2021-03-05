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

package forms

import base.SpecBase

class MoreInformationFormProviderSpec extends SpecBase {

  "Binding a form with invalid data" when {

    "no data entered" should {

      val missingOption: Map[String, String] = Map.empty
      val form = new MoreInformationFormProvider()().bind(missingOption)

      "result in a form with errors" in {
        form.hasErrors mustBe true
      }

      "throw one error" in {
        form.errors.size mustBe 1
      }

      "have an error with the correct message" in {
        form.errors.head.message mustBe "moreInformation.error.required"
      }
    }

    "maxLength data entered" should {

      val maxLengthData: Map[String, String] = Map("value" -> "c"*1501)
      val form = new MoreInformationFormProvider()().bind(maxLengthData)

      "result in a form with errors" in {
        form.hasErrors mustBe true
      }

      "throw one error" in {
        form.errors.size mustBe 1
      }

      "have an error with the correct message" in {
        form.errors.head.message mustBe "moreInformation.error.maxLength"
      }
    }
  }

  "Binding a form with valid data" should {

    val data = Map("value" -> "some test")
    val form = new MoreInformationFormProvider()().bind(data)

    "result in a form with no errors" in {
      form.hasErrors mustBe false
    }

  }

  "Binding a form with valid punctuation characters" should {

    val data = Map("value" ->
      """"!?@£$%^&*(na
        |
        |sd671263-'][./,"""".stripMargin)
    val form = new MoreInformationFormProvider()().bind(data)

    "result in a form with no errors" in {
      form.hasErrors mustBe false
    }
  }

}

