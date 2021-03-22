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
import models.underpayments.UnderpaymentAmount
import play.api.data.{Form, FormError}

class CustomsDutyFormProviderSpec extends SpecBase {

  private final val originalKey = "original"
  private final val amendedKey = "amended"
  private final val originalNonNumberMessageKey = "customsDuty.error.originalNonNumber"
  private final val amendedNonNumberMessageKey = "customsDuty.error.amendedNonNumber"
  private final val originalNonEmptyMessageKey = "customsDuty.error.originalNonEmpty"
  private final val amendedNonEmptyMessageKey = "customsDuty.error.amendedNonEmpty"
  private final val fifty = "50"
  private final val forty = "40"
  private final val nonNumeric = "@£$%FGB"

  def formBuilder(original: String = "", amended: String = ""): Map[String, String] = Map(
    originalKey -> original,
    amendedKey -> amended
  )

  def formBinder(formValues: Map[String, String] = Map(originalKey -> "", amendedKey -> "")): Form[UnderpaymentAmount] =
    new CustomsDutyFormProvider()(MockAppConfig).apply().bind(formValues)

  "Binding a form with invalid data" when {

    "no values provided" should {
      "result in a form with errors" in {
        formBinder().errors mustBe Seq(
          FormError(originalKey, originalNonEmptyMessageKey),
          FormError(amendedKey, amendedNonEmptyMessageKey)
        )
      }
    }

    "no original value provided" should {
      "result in a form with errors" in {
        formBinder(formBuilder(amended = fifty)).errors.head mustBe FormError(originalKey, originalNonEmptyMessageKey)
      }
    }

    "no amended value provided" should {
      "result in a form with errors" in {
        formBinder(formBuilder(original = fifty)).errors.head mustBe FormError(amendedKey, amendedNonEmptyMessageKey)
      }
    }

    "non numeric values provided" should {
      "result in a form with errors" in {
        formBinder(formBuilder(original = nonNumeric, amended = nonNumeric)).errors mustBe Seq(
          FormError(originalKey, originalNonNumberMessageKey),
          FormError(amendedKey, amendedNonNumberMessageKey)
        )
      }
    }

    "non numeric original value provided" should {
      "result in a form with errors" in {
        formBinder(
          formBuilder(original = nonNumeric, amended = fifty)
        ).errors.head mustBe FormError(originalKey, originalNonNumberMessageKey)
      }
    }

    "non numeric amended value provided" should {
      "result in a form with errors" in {
        formBinder(
          formBuilder(original = fifty, amended = nonNumeric)
        ).errors.head mustBe FormError(amendedKey, amendedNonNumberMessageKey)
      }
    }

    "original amount exceeding the limit" should {
      "result in a form with errors" in {
        formBinder(
          formBuilder(original = "10000000000", amended = fifty)
        ).errors.head mustBe FormError(originalKey, messages("customsDuty.error.originalUpperLimit"))
      }
    }

  }

  "Binding a form with valid data" should {
    val form = formBinder(formBuilder(original = forty, amended = fifty))

    "result in a form with no errors" in {
      form.hasErrors mustBe false
    }

    "generate the correct model" in {
      form.value mustBe Some(UnderpaymentAmount(BigDecimal(forty), BigDecimal(fifty)))
    }

  }

  "A form built from a valid model" should {
    "generate the correct mapping" in {
      val sixty = "60.0"
      val zero = "0.0"
      val model = UnderpaymentAmount(BigDecimal(zero), BigDecimal(sixty))
      val form = new CustomsDutyFormProvider()(MockAppConfig).apply().fill(model)
      form.data mustBe formBuilder(original = zero, amended = sixty)
    }
  }

}
