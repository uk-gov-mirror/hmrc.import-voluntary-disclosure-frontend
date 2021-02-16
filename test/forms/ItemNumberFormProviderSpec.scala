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
import play.api.data.Form

class ItemNumberFormProviderSpec extends SpecBase {


  private final val itemNumberKey = "itemNumber"

  def formBuilder(itemNumber: String = ""): Map[String, String] = Map(
    itemNumberKey -> itemNumber
  )

  def formBinder(formValues: Map[String, String] = Map(itemNumberKey -> "")): Form[Int] =
    new ItemNumberFormProvider()().bind(formValues)


  "Binding a form with invalid data" when {

    "with no data present" should {

      val missingOption: Map[String, String] = Map.empty
      val form = new ItemNumberFormProvider()().bind(missingOption)

      "result in a form with errors" in {
        form.hasErrors mustBe true
      }

      "throw one error" in {
        form.errors.size mustBe 1
      }

      "have an error with the correct message" in {
        form.errors.head.message mustBe "itemNo.error.required"
      }
    }

    "with a non numeric value present" should {

      val data = Map("itemNumber" -> "one")
      val form = new ItemNumberFormProvider()().bind(data)

      "result in a form with errors" in {
        form.hasErrors mustBe true
      }

      "throw one error" in {
        form.errors.size mustBe 1
      }

      "have an error with the correct message" in {
        form.errors.head.message mustBe "itemNo.error.nonNumeric"
      }
    }

    "with a number greater than 99 present" should {

      val data = Map("itemNumber" -> "100")
      val form = new ItemNumberFormProvider()().bind(data)

      "result in a form with errors" in {
        form.hasErrors mustBe true
      }

      "throw one error" in {
        form.errors.size mustBe 1
      }

      "have an error with the correct message" in {
        form.errors.head.message mustBe "itemNo.error.outOfRange"
      }
    }

    "with a decimal present" should {

      val data = Map("itemNumber" -> "1.1")
      val form = new ItemNumberFormProvider()().bind(data)

      "result in a form with errors" in {
        form.hasErrors mustBe true
      }

      "throw one error" in {
        form.errors.size mustBe 1
      }

      "have an error with the correct message" in {
        form.errors.head.message mustBe "itemNo.error.wholeNumber"
      }
    }

    "with the number 0 present" should {

      val data = Map("itemNumber" -> "0")
      val form = new ItemNumberFormProvider()().bind(data)

      "result in a form with errors" in {
        form.hasErrors mustBe true
      }

      "throw one error" in {
        form.errors.size mustBe 1
      }

      "have an error with the correct message" in {
        form.errors.head.message mustBe "itemNo.error.outOfRange"
      }
    }
  }

  "Binding a form with valid data" should {

    val form = formBinder(formBuilder("1"))

    "result in a form with no errors" in {
      form.hasErrors mustBe false
    }

  }

}
