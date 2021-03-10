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
  val originalWeightMissingMessageKey = "amendmentValue.error.original.weight.missing"
  val amendedWeightMissingMessageKey = "amendmentValue.error.amended.weight.missing"
  val originalWeightFormatMessageKey = "amendmentValue.error.original.weight.nonNumeric"
  val amendedWeightFormatMessageKey = "amendmentValue.error.amended.weight.nonNumeric"
  val originalWeightDecimalMessageKey = "amendmentValue.error.original.weight.invalidDecimals"
  val amendedWeightDecimalMessageKey = "amendmentValue.error.amended.weight.invalidDecimals"
  val originalWeightRangeMessageKey = "amendmentValue.error.original.weight.outOfRange"
  val amendedWeightRangeMessageKey = "amendmentValue.error.amended.weight.outOfRange"
  val commodityCodeAmendedValue = "2204109400X411"
  val commodityCodeOriginalValue = "2204109400X412"
  val commodityCodeOriginalLowerValue = "2204109400x412"
  val invalidBoxAmendedValue = "2204109400X411"
  val invalidBoxOriginalValue = "2204109400X412"
  val foreignCurrencyAmendedValue = "GBP50"
  val foreignCurrencyOriginalValue = "GBP40"
  val foreignCurrencyOriginalLowerValue = "gbp40"
  val nonNumeric = "@£$%FGB"
  val weightOriginalValue = 1500
  val weightAmendedValue = 3593.44
  val tooManyDecimalValue = 950.3829
  val outOfRangeValue = 95043953


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
      "produce the correct form for box 62" in {
        val form: Form[UnderpaymentReasonValue] = formBinderBox(formBuilder(original = foreignCurrencyOriginalValue, amended = foreignCurrencyAmendedValue), box = 62)
        form.value mustBe Some(UnderpaymentReasonValue(foreignCurrencyOriginalValue, foreignCurrencyAmendedValue))
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
      "result in a form with errors due to case sensitive diff" in {
        formBinderBox(formBuilder(original = foreignCurrencyAmendedValue.toUpperCase, amended = foreignCurrencyAmendedValue.toLowerCase), box = 22).errors mustBe
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
      "result in a form with errors if difference is case sensitivity" in {
        formBinderBox(formBuilder(original = commodityCodeAmendedValue.toUpperCase, amended = commodityCodeAmendedValue.toLowerCase), box = 33).errors mustBe
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
      "result in a form with errors due to case sensitive diff" in {
        formBinderBox(formBuilder(
          original = invalidBoxAmendedValue.toUpperCase,
          amended = invalidBoxAmendedValue.toLowerCase), box = 0).errors mustBe
          Seq(
            FormError("", keysDifferentMessageKey)
          )
      }
    }
  }

  "Binding a form with invalid data for a weight box selected" when {
    "no values provided" should {
      "result in a form with errors" in {
        formBinderBox(box = 35).errors mustBe Seq(
          FormError(originalKey, originalWeightMissingMessageKey),
          FormError(amendedKey, amendedWeightMissingMessageKey)
        )
      }
    }

    "no amended value provided" should {
      "result in a form with errors" in {
        formBinderBox(formBuilder(original = weightOriginalValue.toString), box = 35).errors mustBe
          Seq(
            FormError(amendedKey, amendedWeightMissingMessageKey)
          )
      }
    }

    "non numeric values provided" should {
      "result in a form with errors" in {
        formBinderBox(formBuilder(original = nonNumeric, amended = nonNumeric), box = 35).errors mustBe Seq(
          FormError(originalKey, originalWeightFormatMessageKey),
          FormError(amendedKey, amendedWeightFormatMessageKey)
        )
      }
    }

    "non numeric amended value provided" should {
      "result in a form with errors" in {
        formBinderBox(formBuilder(original = weightOriginalValue.toString, amended = nonNumeric), box = 35).errors mustBe
          Seq(
            FormError(amendedKey, amendedWeightFormatMessageKey)
          )
      }
    }

    "too many decimal values provided" should {
      "result in a form with errors" in {
        formBinderBox(formBuilder(original = tooManyDecimalValue.toString, amended = tooManyDecimalValue.toString), box = 35).errors mustBe Seq(
          FormError(originalKey, originalWeightDecimalMessageKey),
          FormError(amendedKey, amendedWeightDecimalMessageKey)
        )
      }
    }

    "too many decimal original value provided" should {
      "result in a form with errors" in {
        formBinderBox(formBuilder(original = tooManyDecimalValue.toString, amended = weightAmendedValue.toString), box = 35).errors mustBe
          Seq(
            FormError(originalKey, originalWeightDecimalMessageKey)
          )
      }
    }

    "too many decimal amended value provided" should {
      "result in a form with errors" in {
        formBinderBox(formBuilder(original = weightOriginalValue.toString, amended = tooManyDecimalValue.toString), box = 35).errors mustBe
          Seq(
            FormError(amendedKey, amendedWeightDecimalMessageKey)
          )
      }
    }

    "out of range values provided" should {
      "result in a form with errors" in {
        val rangeValueArgs = Seq(0, 9999999.999)
        formBinderBox(formBuilder(original = outOfRangeValue.toString, amended = outOfRangeValue.toString), box = 35).errors mustBe Seq(
          FormError(originalKey, originalWeightRangeMessageKey, rangeValueArgs),
          FormError(amendedKey, amendedWeightRangeMessageKey,  rangeValueArgs)
        )
      }
    }

    "original and amended value are the same" should {
      "result in a form with errors" in {
        formBinderBox(formBuilder(original = weightAmendedValue.toString, amended = weightAmendedValue.toString), box = 35).errors mustBe
          Seq(
            FormError("", keysDifferentMessageKey)
          )
      }
    }
  }

  "Binding a form with valid data for a weightForm box selected" when {
    "provided with valid values" should {
      "result in a form with no errors" in {
        val form: Form[UnderpaymentReasonValue] = formBinderBox(formBuilder(original = weightOriginalValue.toString, amended = weightAmendedValue.toString), box = 35)
        form.hasErrors mustBe false
      }

      "generate the correct model" in {
        val form: Form[UnderpaymentReasonValue] = formBinderBox(formBuilder(original = weightOriginalValue.toString, amended = weightAmendedValue.toString), box = 35)
        form.value mustBe Some(UnderpaymentReasonValue(weightOriginalValue.toString, weightAmendedValue.toString))
      }
    }
  }

  "A weight form " when {
    "built from a valid model" should {
      "generate the correct mapping" in {
        val model: UnderpaymentReasonValue = UnderpaymentReasonValue(weightOriginalValue.toString, weightAmendedValue.toString)
        val form: Form[UnderpaymentReasonValue] = new UnderpaymentReasonAmendmentFormProvider()(35).fill(model)
        form.data mustBe formBuilder(original = weightOriginalValue.toString, amended = weightAmendedValue.toString)
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
