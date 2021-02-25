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

class ImporterNameFormProviderSpec extends SpecBase {

  private final val fullName1 = "fullName"
  private final val fullNameNonEmptyKey = "importerName.error.nameNonEmpty"
  private final val fullNameTooShortKey = "importerName.error.nameMinLength"
  private final val fullNameTooLongKey = "importerName.error.nameMaxLength"
  private final val fullNameInvalidCharactersKey = "importerName.error.nameAllowableCharacters"

  def formBuilder(fullName: String = ""): Map[String, String] = Map(
    "fullName" -> fullName
  )

  def formBinder(formValues: Map[String, String] = Map(fullName1 -> "")): Form[String] =
    new ImporterNameFormProvider()().bind(formValues)

  "Binding a form with invalid data" when {

    "with no data present" should {

      val missingOption: Map[String, String] = Map.empty
      val form = new ImporterNameFormProvider()().bind(missingOption)

      "result in a form with errors" in {
        form.hasErrors mustBe true
      }

      "throw one error" in {
        form.errors.size mustBe 1
      }

      "have an error with the correct message" in {
        form.errors.head.message mustBe fullNameNonEmptyKey
      }
    }

    "wth full name too short" should {

      val data = Map("fullName" -> "a")
      val form = new ImporterNameFormProvider()().bind(data)

      "result in a form with errors" in {
        form.hasErrors mustBe true
      }

      "throw one error" in {
        form.errors.size mustBe 1
      }

      "have an error with the correct message" in {
        form.errors.head.message mustBe fullNameTooShortKey
      }
    }

    "wth full name too long" should {

      val data = Map("fullName" -> "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
      val form = new ImporterNameFormProvider()().bind(data)

      "result in a form with errors" in {
        form.hasErrors mustBe true
      }

      "throw one error" in {
        form.errors.size mustBe 1
      }

      "have an error with the correct message" in {
        form.errors.head.message mustBe fullNameTooLongKey
      }
    }

    "wth full name using invalid characters" should {

      val data = Map("fullName" -> "First/Name")
      val form = new ImporterNameFormProvider()().bind(data)

      "result in a form with errors" in {
        form.hasErrors mustBe true
      }

      "throw one error" in {
        form.errors.size mustBe 1
      }

      "have an error with the correct message" in {
        form.errors.head.message mustBe fullNameInvalidCharactersKey
      }
    }

    "Binding a form with valid data" when {

      "Valid data present" should {
        val form = formBinder(formBuilder("First Second"))

        "result in a form with no errors" in {
          form.hasErrors mustBe false
        }
      }
    }
  }
}
