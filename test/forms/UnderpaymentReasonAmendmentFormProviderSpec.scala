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
import play.api.data.{Form, FormError}

class UnderpaymentReasonAmendmentFormProviderSpec extends SpecBase {
  val originalKey = "original"
  val amendedKey = "amended"
  val originalFormatMessageKey = "amendmentValue.error.original.format"
  val amendedFormatMessageKey = "amendmentValue.error.amended.format"
  val originalMissingMessageKey = "amendmentValue.error.original.missing"
  val amendedMissingMessageKey = "amendmentValue.error.amended.missing"
  val keysDifferentMessageKey = "amendmentValue.error.amended.different"
  val box33Value1 = "2204109400X411"
  val box33Value2 = "2204109400X412"
  val box0Value1 = "2204109400X411"
  val box0Value2 = "2204109400X412"
  val box22Value1 = "GBP50"
  val box22Value2 = "GBP40"
  val nonNumeric = "@£$%FGB"

  def formBuilder(original: String = "", amended: String = ""): Map[String, String] = Map(
    originalKey -> original,
    amendedKey -> amended
  )

  def formBinderBox(formValues: Map[String, String] = Map(originalKey -> "", amendedKey -> ""), box: Int = 22): Form[UnderpaymentReasonValue] =
    new UnderpaymentReasonAmendmentFormProvider()(box).bind(formValues)


  "Binding a form with invalid data and box 22 selected" when {
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
        formBinderBox(formBuilder(amended = box22Value1), box = 22).errors mustBe
          Seq(
            FormError(originalKey, originalMissingMessageKey)
          )
      }
    }

    "no amended value provided" should {
      "result in a form with errors" in {
        formBinderBox(formBuilder(original = box22Value1), box = 22).errors mustBe
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
        formBinderBox(formBuilder(original = nonNumeric, amended = box22Value1), box = 22).errors mustBe
          Seq(
            FormError(originalKey, originalFormatMessageKey)
          )
      }
    }

    "non numeric amended value provided" should {
      "result in a form with errors" in {
        formBinderBox(formBuilder(original = box22Value1, amended = nonNumeric), box = 22).errors mustBe
          Seq(
            FormError(amendedKey, amendedFormatMessageKey)
          )
      }
    }

    "original and amended value are the same" should {
      "result in a form with errors" in {
        formBinderBox(formBuilder(original = box22Value1, amended = box22Value1), box = 22).errors mustBe
          Seq(
            FormError("", keysDifferentMessageKey)
          )
      }
    }
  }

  "Binding a form with valid data and box 22 selected" when {
    "provided with valid values" should {
      "result in a form with no errors" in {
        val form: Form[UnderpaymentReasonValue] = formBinderBox(formBuilder(original = box22Value2, amended = box22Value1), box = 22)
        form.hasErrors mustBe false
      }

      "generate the correct model" in {
        val form: Form[UnderpaymentReasonValue] = formBinderBox(formBuilder(original = box22Value2, amended = box22Value1), box = 22)
        form.value mustBe Some(UnderpaymentReasonValue(box22Value2, box22Value1))
      }
    }
  }

  "A form " when {
    "built from a valid model for box 22" should {
      "generate the correct mapping" in {
        val model: UnderpaymentReasonValue = UnderpaymentReasonValue(box22Value1, box22Value2)
        val form: Form[UnderpaymentReasonValue] = new UnderpaymentReasonAmendmentFormProvider()(22).fill(model)
        form.data mustBe formBuilder(original = box22Value1, amended = box22Value2)
      }
    }
  }

  "Binding a form with invalid data and box 33 selected" when {

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
        formBinderBox(formBuilder(amended = box33Value1), box = 33).errors mustBe
          Seq(
            FormError(originalKey, originalMissingMessageKey)
          )
      }
    }

    "no amended value provided" should {
      "result in a form with errors" in {
        formBinderBox(formBuilder(original = box33Value1), box = 33).errors mustBe
          Seq(
            FormError(amendedKey, amendedMissingMessageKey)
          )
      }
    }

    "original and amended value are the same" should {
      "result in a form with errors" in {
        formBinderBox(formBuilder(original = box33Value1, amended = box33Value1), box = 33).errors mustBe
          Seq(
            FormError("", keysDifferentMessageKey)
          )
      }
    }
  }

  "Binding a form with valid data and box 33 selected" when {
    "provided with valid values" should {
      "result in a form with no errors" in {
        val form: Form[UnderpaymentReasonValue] = formBinderBox(formBuilder(original = box33Value2, amended = box33Value1), box = 33)
        form.hasErrors mustBe false
      }

      "generate the correct model" in {
        val form: Form[UnderpaymentReasonValue] = formBinderBox(formBuilder(original = box33Value2, amended = box33Value1), box = 33)
        form.value mustBe Some(UnderpaymentReasonValue(box33Value2, box33Value1))
      }
    }
  }

  "A form " when {
    "built from a valid model for box 33" should {
      "generate the correct mapping" in {
        val model: UnderpaymentReasonValue = UnderpaymentReasonValue(box33Value1, box33Value2)
        val form: Form[UnderpaymentReasonValue] = new UnderpaymentReasonAmendmentFormProvider()(33).fill(model)
        form.data mustBe formBuilder(original = box33Value1, amended = box33Value2)
      }
    }
  }

  "Binding a form with invalid data and box 0 selected" when {
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
        formBinderBox(formBuilder(amended = box0Value1), box = 0).errors mustBe
          Seq(
            FormError(originalKey, originalMissingMessageKey)
          )
      }
    }

    "no amended value provided" should {
      "result in a form with errors" in {
        formBinderBox(formBuilder(original = box0Value1), box = 0).errors mustBe
          Seq(
            FormError(amendedKey, amendedMissingMessageKey)
          )
      }
    }

    "original and amended value are the same" should {
      "result in a form with errors" in {
        formBinderBox(formBuilder(original = box0Value1, amended = box0Value1), box = 0).errors mustBe
          Seq(
            FormError("", keysDifferentMessageKey)
          )
      }
    }
  }

  "Binding a form with valid data and box 0 selected" when {
    "provided with valid values" should {
      "result in a form with no errors" in {
        val form: Form[UnderpaymentReasonValue] = formBinderBox(formBuilder(original = box0Value2, amended = box0Value1), box = 0)
        form.hasErrors mustBe false
      }

      "generate the correct model" in {
        val form: Form[UnderpaymentReasonValue] = formBinderBox(formBuilder(original = box0Value2, amended = box0Value1), box = 0)
        form.value mustBe Some(UnderpaymentReasonValue(box0Value2, box0Value1))
      }
    }
  }

  "A form " when {
    "built from a valid model for box 0" should {
      "generate the correct mapping" in {
        val model: UnderpaymentReasonValue = UnderpaymentReasonValue(box0Value1, box0Value2)
        val form: Form[UnderpaymentReasonValue] = new UnderpaymentReasonAmendmentFormProvider()(0).fill(model)
        form.data mustBe formBuilder(original = box0Value1, amended = box0Value2)
      }
    }
  }

}
