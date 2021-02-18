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
import models.UnderpaymentReasonValue
import play.api.data.validation.{Invalid, Valid}
import play.api.data.{Form, FormError}

class UnderpaymentReasonAmendmentFormProviderSpec extends SpecBase {
  val originalKey = "original"
  val amendedKey = "amended"
  val originalFormatMessageKey = "amendmentValue.error.original.format"
  val amendedFormatMessageKey = "amendmentValue.error.amended.format"
  val originalMissingMessageKey = "amendmentValue.error.original.missing"
  val amendedMissingMessageKey = "amendmentValue.error.amended.missing"
  val keysDifferentMessageKey = "amendmentValue.error.amended.different"
  val commodityCodeAmendedValue = "2204109400X411"
  val commodityCodeOriginalValue = "2204109400X412"
  val invalidBoxAmendedValue = "2204109400X411"
  val invalidBoxOriginalValue = "2204109400X412"
  val foreignCurrencyAmendedValue = "GBP50"
  val foreignCurrencyOriginalValue = "GBP40"
  val nonNumeric = "@£$%FGB"

  def formBuilder(original: String = "", amended: String = ""): Map[String, String] = Map(
    originalKey -> original,
    amendedKey -> amended
  )

  def formBinderBox(formValues: Map[String, String] = Map(originalKey -> "", amendedKey -> ""), box: Int = 22): Form[UnderpaymentReasonValue] =
    new UnderpaymentReasonAmendmentFormProvider()(box).bind(formValues)

  "UnderpaymentReasonAmendmentFormProvider" when {
    "mapping for a given box" should {
      "produce the correct form for box 22" in {
        val form: Form[UnderpaymentReasonValue] = formBinderBox(formBuilder(original = foreignCurrencyOriginalValue, amended = foreignCurrencyAmendedValue), box = 22)
        form.value mustBe Some(UnderpaymentReasonValue(foreignCurrencyOriginalValue, foreignCurrencyAmendedValue))
      }
      "produce the correct form for box 33" in {
        val form: Form[UnderpaymentReasonValue] = formBinderBox(formBuilder(original = commodityCodeOriginalValue, amended = commodityCodeAmendedValue), box = 33)
        form.value mustBe Some(UnderpaymentReasonValue(commodityCodeOriginalValue, commodityCodeAmendedValue))
      }
    }
  }

  "Binding a form with invalid data for a foreignCurrency box selected" when {
    "no values provided" should {
      "result in a form with errors" in {
        formBinderBox(box = 22).errors mustBe Seq(
          FormError(originalKey, originalMissingMessageKey),
          FormError(amendedKey, amendedMissingMessageKey)
        )
      }
    }

    "no original value provided" should {
      "result in a form with errors" in {
        formBinderBox(formBuilder(amended = foreignCurrencyAmendedValue), box = 22).errors mustBe
          Seq(
            FormError(originalKey, originalMissingMessageKey)
          )
      }
    }

    "no amended value provided" should {
      "result in a form with errors" in {
        formBinderBox(formBuilder(original = foreignCurrencyAmendedValue), box = 22).errors mustBe
          Seq(
            FormError(amendedKey, amendedMissingMessageKey)
          )
      }
    }

    "non numeric values provided" should {
      "result in a form with errors" in {
        formBinderBox(formBuilder(original = nonNumeric, amended = nonNumeric), box = 22).errors mustBe Seq(
          FormError(originalKey, originalFormatMessageKey),
          FormError(amendedKey, amendedFormatMessageKey)
        )
      }
    }

    "non numeric original value provided" should {
      "result in a form with errors" in {
        formBinderBox(formBuilder(original = nonNumeric, amended = foreignCurrencyAmendedValue), box = 22).errors mustBe
          Seq(
            FormError(originalKey, originalFormatMessageKey)
          )
      }
    }

    "non numeric amended value provided" should {
      "result in a form with errors" in {
        formBinderBox(formBuilder(original = foreignCurrencyAmendedValue, amended = nonNumeric), box = 22).errors mustBe
          Seq(
            FormError(amendedKey, amendedFormatMessageKey)
          )
      }
    }

    "original and amended value are the same" should {
      "result in a form with errors" in {
        formBinderBox(formBuilder(original = foreignCurrencyAmendedValue, amended = foreignCurrencyAmendedValue), box = 22).errors mustBe
          Seq(
            FormError("", keysDifferentMessageKey)
          )
      }
    }
  }

  "Binding a form with valid data for a foreignCurrency box selected" when {
    "provided with valid values" should {
      "result in a form with no errors" in {
        val form: Form[UnderpaymentReasonValue] = formBinderBox(formBuilder(original = foreignCurrencyOriginalValue, amended = foreignCurrencyAmendedValue), box = 22)
        form.hasErrors mustBe false
      }

      "generate the correct model" in {
        val form: Form[UnderpaymentReasonValue] = formBinderBox(formBuilder(original = foreignCurrencyOriginalValue, amended = foreignCurrencyAmendedValue), box = 22)
        form.value mustBe Some(UnderpaymentReasonValue(foreignCurrencyOriginalValue, foreignCurrencyAmendedValue))
      }
    }
  }

  "A foreignCurrency form " when {
    "built from a valid model" should {
      "generate the correct mapping" in {
        val model: UnderpaymentReasonValue = UnderpaymentReasonValue(foreignCurrencyAmendedValue, foreignCurrencyOriginalValue)
        val form: Form[UnderpaymentReasonValue] = new UnderpaymentReasonAmendmentFormProvider()(22).fill(model)
        form.data mustBe formBuilder(original = foreignCurrencyAmendedValue, amended = foreignCurrencyOriginalValue)
      }
    }
  }

  "Binding a form with invalid data for a textForm box selected" when {

    "no values provided" should {
      "result in a form with errors" in {
        formBinderBox(box = 33).errors mustBe Seq(
          FormError(originalKey, originalMissingMessageKey),
          FormError(amendedKey, amendedMissingMessageKey)
        )
      }
    }

    "no original value provided" should {
      "result in a form with errors" in {
        formBinderBox(formBuilder(amended = commodityCodeAmendedValue), box = 33).errors mustBe
          Seq(
            FormError(originalKey, originalMissingMessageKey)
          )
      }
    }

    "no amended value provided" should {
      "result in a form with errors" in {
        formBinderBox(formBuilder(original = commodityCodeAmendedValue), box = 33).errors mustBe
          Seq(
            FormError(amendedKey, amendedMissingMessageKey)
          )
      }
    }

    "original and amended value are the same" should {
      "result in a form with errors" in {
        formBinderBox(formBuilder(original = commodityCodeAmendedValue, amended = commodityCodeAmendedValue), box = 33).errors mustBe
          Seq(
            FormError("", keysDifferentMessageKey)
          )
      }
    }
  }

  "Binding a form with valid data for a textForm box selected" when {
    "provided with valid values" should {
      "result in a form with no errors" in {
        val form: Form[UnderpaymentReasonValue] = formBinderBox(formBuilder(original = commodityCodeOriginalValue, amended = commodityCodeAmendedValue), box = 33)
        form.hasErrors mustBe false
      }

      "generate the correct model" in {
        val form: Form[UnderpaymentReasonValue] = formBinderBox(formBuilder(original = commodityCodeOriginalValue, amended = commodityCodeAmendedValue), box = 33)
        form.value mustBe Some(UnderpaymentReasonValue(commodityCodeOriginalValue, commodityCodeAmendedValue))
      }
    }
  }

  "A text form " when {
    "built from a valid model" should {
      "generate the correct mapping" in {
        val model: UnderpaymentReasonValue = UnderpaymentReasonValue(commodityCodeAmendedValue, commodityCodeOriginalValue)
        val form: Form[UnderpaymentReasonValue] = new UnderpaymentReasonAmendmentFormProvider()(33).fill(model)
        form.data mustBe formBuilder(original = commodityCodeAmendedValue, amended = commodityCodeOriginalValue)
      }
    }
  }

  "Binding a form with invalid data for an unrecognised box selected" when {
    "no values provided" should {
      "result in a form with errors" in {
        formBinderBox(box = 0).errors mustBe Seq(
          FormError(originalKey, originalMissingMessageKey),
          FormError(amendedKey, amendedMissingMessageKey)
        )
      }
    }

    "no original value provided" should {
      "result in a form with errors" in {
        formBinderBox(formBuilder(amended = invalidBoxAmendedValue), box = 0).errors mustBe
          Seq(
            FormError(originalKey, originalMissingMessageKey)
          )
      }
    }

    "no amended value provided" should {
      "result in a form with errors" in {
        formBinderBox(formBuilder(original = invalidBoxAmendedValue), box = 0).errors mustBe
          Seq(
            FormError(amendedKey, amendedMissingMessageKey)
          )
      }
    }

    "original and amended value are the same" should {
      "result in a form with errors" in {
        formBinderBox(formBuilder(original = invalidBoxAmendedValue, amended = invalidBoxAmendedValue), box = 0).errors mustBe
          Seq(
            FormError("", keysDifferentMessageKey)
          )
      }
    }
  }

  "Binding a form with valid data for an unrecognised box selected" when {
    "provided with valid values" should {
      "result in a form with no errors" in {
        val form: Form[UnderpaymentReasonValue] = formBinderBox(formBuilder(original = invalidBoxOriginalValue, amended = invalidBoxAmendedValue), box = 0)
        form.hasErrors mustBe false
      }

      "generate the correct model" in {
        val form: Form[UnderpaymentReasonValue] = formBinderBox(formBuilder(original = invalidBoxOriginalValue, amended = invalidBoxAmendedValue), box = 0)
        form.value mustBe Some(UnderpaymentReasonValue(invalidBoxOriginalValue, invalidBoxAmendedValue))
      }
    }
  }

  "An unrecognised box form " when {
    "built from a valid model" should {
      "generate the correct mapping" in {
        val model: UnderpaymentReasonValue = UnderpaymentReasonValue(invalidBoxAmendedValue, invalidBoxOriginalValue)
        val form: Form[UnderpaymentReasonValue] = new UnderpaymentReasonAmendmentFormProvider()(0).fill(model)
        form.data mustBe formBuilder(original = invalidBoxAmendedValue, amended = invalidBoxOriginalValue)
      }
    }
  }

  "different Constraint" must {

    lazy val diff = new UnderpaymentReasonAmendmentFormProvider().different("error.key")

    "return Valid if strings is different" in {
      diff(UnderpaymentReasonValue("Field1", "NotField1")) mustEqual Valid
    }

    "return Invalid for identical strings" in {
      diff(UnderpaymentReasonValue("Field1", "Field1")) mustEqual Invalid("error.key")
    }
  }
}
