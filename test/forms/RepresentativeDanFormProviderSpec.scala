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
import mocks.config.MockAppConfig
import models.RepresentativeDan
import play.api.data.FormError

class RepresentativeDanFormProviderSpec extends SpecBase {

  def buildFormData(accountNumber: Option[String] = Some("1234567"),
                    danType: Option[String] = Some("A")): Map[String, String] =
    (
      accountNumber.map(_ => "accountNumber" -> accountNumber.get) ++
        danType.map(_ => "value" -> danType.get)).toMap

  "Binding a form with invalid data" when {

    "no values provided" should {
      val missingOption: Map[String, String] = Map.empty
      val form = new RepresentativeDanFormProvider()(MockAppConfig).apply().bind(missingOption)

      "result in a form with errors" in {
        form.errors mustBe Seq(
          FormError("accountNumber", "repDan.error.input.required"),
          FormError("value", "repDan.error.radio.required")
        )
      }
    }

    "invalid data for account number" should {
      val form = new RepresentativeDanFormProvider()(MockAppConfig).apply().bind(buildFormData(accountNumber = Some("!*&£%!")))

      "result in a form with errors" in {
        form.errors.size mustBe 1
        form.errors.head.message mustBe "repDan.error.input.format"
      }
    }
  }

  "Binding a form with valid data" should {
    val form = new RepresentativeDanFormProvider()(MockAppConfig).apply().bind(buildFormData())

    "result in a form with no errors" in {
      form.hasErrors mustBe false
    }

    "generate the correct model" in {
      form.value mustBe Some(RepresentativeDan("1234567", "A"))
    }
  }

  "A form built from a valid model" should {
    "generate the correct mapping" in {
      val model = RepresentativeDan("1234567", "A")
      val form = new RepresentativeDanFormProvider()(MockAppConfig).apply().fill(model)
      form.data mustBe buildFormData()
    }
  }

}

