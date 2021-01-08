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

import java.time.LocalDate

import base.SpecBase
import mocks.config.MockAppConfig
import models.EntryDetails
import play.api.data.FormError

class EntryDetailsFormProviderSpec extends SpecBase {

  def buildFormData(epu: Option[String] = Some("123"),
                    entryNumber: Option[String] = Some("123456Q"),
                    day: Option[String] = Some("31"),
                    month: Option[String] = Some("12"),
                    year: Option[String] = Some("2020")): Map[String, String] =
    (
      epu.map(_ => "epu" -> epu.get) ++
      entryNumber.map(_ => "entryNumber" -> entryNumber.get) ++
      day.map(_ => "entryDate.day" -> day.get) ++
      month.map(_ => "entryDate.month" -> month.get) ++
      year.map(_ => "entryDate.year" -> year.get)
    ).toMap

  "Binding a form with invalid data" when {

    "no values provided" should {
      val missingOption: Map[String, String] = Map.empty
      val form = new EntryDetailsFormProvider()(MockAppConfig).apply().bind(missingOption)

      "result in a form with errors" in {
        form.errors mustBe Seq(
          FormError("epu", "entryDetails.epu.error.missing"),
          FormError("entryNumber", "entryDetails.entryNumber.error.missing"),
          FormError("entryDate.day", "entryDetails.entryDate.error.required.all", List("day", "month", "year"))
        )
      }
    }

    "missing entry date day" should {
      val form = new EntryDetailsFormProvider()(MockAppConfig).apply().bind(buildFormData(day = None))

      "result in a form with errors" in {
        form.errors.size mustBe 1
        form.errors.head.key mustBe "entryDate.day"
        form.errors.head.message mustBe "entryDetails.entryDate.error.required"
        form.errors.head.args mustBe List("day")
      }
    }
    "missing entry date month" should {
      val form = new EntryDetailsFormProvider()(MockAppConfig).apply().bind(buildFormData(month = None))

      "result in a form with errors" in {
        form.errors.size mustBe 1
        form.errors.head.key mustBe "entryDate.month"
        form.errors.head.message mustBe "entryDetails.entryDate.error.required"
        form.errors.head.args mustBe List("month")
      }
    }
    "missing entry date year" should {
      val form = new EntryDetailsFormProvider()(MockAppConfig).apply().bind(buildFormData(year = None))

      "result in a form with errors" in {
        form.errors.size mustBe 1
        form.errors.head.key mustBe "entryDate.year"
        form.errors.head.message mustBe "entryDetails.entryDate.error.required"
        form.errors.head.args mustBe List("year")
      }
    }
    "missing multiple entry date fields" should {
      val form = new EntryDetailsFormProvider()(MockAppConfig).apply().bind(buildFormData(day = None, year = None))

      "result in a form with errors" in {
        form.errors.size mustBe 1
        form.errors.head.key mustBe "entryDate.day"
        form.errors.head.message mustBe "entryDetails.entryDate.error.required.two"
        form.errors.head.args mustBe List("day", "year")
      }
    }

    "epu invalid" should {
      val form = new EntryDetailsFormProvider()(MockAppConfig).apply().bind(buildFormData(epu = Some("aaa")))

      "result in a form with errors" in {
        form.errors.size mustBe 1
        form.errors.head.key mustBe "epu"
        form.errors.head.message mustBe "entryDetails.epu.error.format"
      }
    }

    "entryNumber invalid" should {
      val form = new EntryDetailsFormProvider()(MockAppConfig).apply().bind(buildFormData(entryNumber = Some("12345Q")))

      "result in a form with errors" in {
        form.errors.size mustBe 1
        form.errors.head.key mustBe "entryNumber"
        form.errors.head.message mustBe "entryDetails.entryNumber.error.format"
      }
    }

    "entryDate invalid" should {
      "result in a form with Day format error" in {
        val form = new EntryDetailsFormProvider()(MockAppConfig).apply().bind(buildFormData(day = Some("32")))

        form.errors.size mustBe 1
        form.errors.head.key mustBe "entryDate.day"
        form.errors.head.message mustBe "entryDetails.entryDate.error.invalid"
      }
      "result in a form with Month format error" in {
        val form = new EntryDetailsFormProvider()(MockAppConfig).apply().bind(buildFormData(month = Some("13")))

        form.errors.size mustBe 1
        form.errors.head.key mustBe "entryDate.day"
        form.errors.head.message mustBe "entryDetails.entryDate.error.invalid"
      }
      "result in a form with Year format error" in {
        val form = new EntryDetailsFormProvider()(MockAppConfig).apply().bind(buildFormData(year = Some("202A")))

        form.errors.size mustBe 1
        form.errors.head.key mustBe "entryDate.year"
        form.errors.head.message mustBe "entryDetails.entryDate.error.invalid"
      }
      "result in a form with Year length error" in {
        val form = new EntryDetailsFormProvider()(MockAppConfig).apply().bind(buildFormData(year = Some("20")))

        form.errors.size mustBe 1
        form.errors.head.key mustBe "entryDate.year"
        form.errors.head.message mustBe "entryDetails.entryDate.error.year.length"
      }
      "result in a form with Date in Past error" in {
        val form = new EntryDetailsFormProvider()(MockAppConfig).apply().bind(
          buildFormData(
            day = Some(s"${LocalDate.now().getDayOfMonth + 1}"),
            month = Some(s"${LocalDate.now().getMonthValue}"),
            year = Some(s"${LocalDate.now().getYear}")
          ))

        form.errors.size mustBe 1
        form.errors.head.key mustBe "entryDate.day"
        form.errors.head.message mustBe "entryDetails.entryDate.error.past"
      }
      "result in a form with Not A Date error" in {
        val form = new EntryDetailsFormProvider()(MockAppConfig).apply().bind(buildFormData(day = Some("43")))

        form.errors.size mustBe 1
        form.errors.head.key mustBe "entryDate.day"
        form.errors.head.message mustBe "entryDetails.entryDate.error.invalid"
      }

    }

  }

  "Binding a form with valid data" should {
    val form = new EntryDetailsFormProvider()(MockAppConfig).apply().bind(buildFormData())

    "result in a form with no errors" in {
      form.hasErrors mustBe false
    }

    "generate the correct model" in {
      form.value mustBe Some(EntryDetails("123", "123456Q", LocalDate.of(2020, 12, 31)))
    }
  }

  "A form built from a valid model" should {
    "generate the correct mapping" in {
      val model = EntryDetails("123", "123456Q", LocalDate.of(2020, 12, 31))
      val form = new EntryDetailsFormProvider()(MockAppConfig).apply().fill(model)
      form.data mustBe buildFormData()
    }
  }

}

