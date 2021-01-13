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
import models.UnderpaymentType

class UnderpaymentTypeFormProviderSpec extends SpecBase {

  "Binding a form with invalid data" when {

    "the no value selected" should {
      val missingOption: Map[String, String] = Map.empty
      val form = new UnderpaymentTypeFormProvider()().bind(missingOption)

      "result in a form with errors" in {
        form.hasErrors mustBe true
      }

      "throw one error" in {
        form.errors.size mustBe 1
      }

      "have an error with the correct message" in {
        form.errors.head.message mustBe messages("underpaymentType.error.required")
      }
    }

  }

  "Binding a form with valid data" should {

    val data = Map("customsDuty" -> "true")
    val form = new UnderpaymentTypeFormProvider()().bind(data)

    "result in a form with no errors" in {
      form.hasErrors mustBe false
    }

    "generate the correct model" in {
      form.value mustBe Some(UnderpaymentType(customsDuty = true, importVAT = false, exciseDuty = false))
    }
  }

  "A form built from a valid model" should {
    "generate the correct mapping" in {
      val model = UnderpaymentType(customsDuty = true, importVAT = false, exciseDuty = false)
      val form = new UnderpaymentTypeFormProvider()().fill(model)
      form.data mustBe Map("customsDuty" -> "true",
        "importVAT" -> "false",
        "exciseDuty" -> "false")
    }
  }

}
