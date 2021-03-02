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

class ImporterEORINumberFormProviderSpec extends SpecBase {

  private final val importerEORINumber = "GB345834921000"
  private final val importerEORINumberNonEmptyKey = "importerEORINumber.error.nonEmpty"
  private final val importerEORINumberIncorrectFormat = "importerEORINumber.error.incorrectFormat"

  def formBuilder(importerEORI: String = ""): Map[String, String] = Map(
    "importerEORI" -> importerEORI
  )

  def formBinder(formValues: Map[String, String] = Map(importerEORINumber -> "")): Form[String] =
    new ImporterEORINumberFormProvider()().bind(formValues)

  "Binding a form with invalid data" when {

    "with no data present" should {

      val missingOption: Map[String, String] = Map.empty
      val form = new ImporterEORINumberFormProvider()().bind(missingOption)

      "result in a form with errors" in {
        form.hasErrors mustBe true
      }

      "throw one error" in {
        form.errors.size mustBe 1
      }

      "have an error with the correct message" in {
        form.errors.head.message mustBe importerEORINumberNonEmptyKey
      }
    }

    "with EORI Number entered in an incorrect format" should {

      val data = Map("importerEORI" -> "345834921000")
      val form = new ImporterEORINumberFormProvider()().bind(data)

      "result in a form with errors" in {
        form.hasErrors mustBe true
      }

      "throw one error" in {
        form.errors.size mustBe 1
      }

      "have an error with the correct message" in {
        form.errors.head.message mustBe importerEORINumberIncorrectFormat
      }
    }

    "with EORI Number exceeding max length" should {

      val data = Map("importerEORI" -> "GB3458349210002222222")
      val form = new ImporterEORINumberFormProvider()().bind(data)

      "result in a form with errors" in {
        form.hasErrors mustBe true
      }

      "throw one error" in {
        form.errors.size mustBe 1
      }

      "have an error with the correct message" in {
        form.errors.head.message mustBe importerEORINumberIncorrectFormat
      }
    }

    "with EORI Number not minimum length" should {

      val data = Map("importerEORI" -> "345834921000")
      val form = new ImporterEORINumberFormProvider()().bind(data)

      "result in a form with errors" in {
        form.hasErrors mustBe true
      }

      "throw one error" in {
        form.errors.size mustBe 1
      }

      "have an error with the correct message" in {
        form.errors.head.message mustBe importerEORINumberIncorrectFormat
      }
    }

    "Binding a form with valid data" when {

      "Valid data present" should {
        val form = formBinder(formBuilder(importerEORINumber))

        "result in a form with no errors" in {
          form.hasErrors mustBe false
        }
      }
    }
  }
}
