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

package forms.underpayments

import base.SpecBase
import models.underpayments.UnderpaymentAmount
import play.api.data.Form

class UnderpaymentDetailsFormProviderSpec extends SpecBase {

  private final val nonNumericValue = "Hello!@£$%^&8"
  private final val fifty = "50"
  private final val outOfRangeValue = "99999999999.99"
  private final val originalKey = "original"
  private final val amendedKey = "amended"
  private final val originalNonEmptyErrorKey = "underpaymentDetails.error.originalNonEmpty"
  private final val amendedNonEmptyErrorKey = "underpaymentDetails.error.amendedNonEmpty"
  private final val originalNonNumberErrorKey = "underpaymentDetails.error.originalNonNumber"
  private final val amendedNonNumberErrorKey = "underpaymentDetails.error.amendedNonNumber"
  private final val originalOutOfRangeErrorKey = "underpaymentDetails.error.originalOutOfRange"
  private final val amendedOutOfRangeErrorKey = "underpaymentDetails.error.amendedOutOfRange"
  private final val originalGreaterThanAmendedErrorKey = "underpaymentDetails.error.positiveAmountOwed"

  def formBuilder(original: String = "", amended: String = ""): Map[String, String] = Map(
    originalKey -> original,
    amendedKey -> amended
  )

  def formBinder(formValues: Map[String, String] = Map(originalKey -> "", amendedKey -> "")): Form[UnderpaymentAmount] =
    new UnderpaymentDetailsFormProvider()().bind(formValues)

  "Binding a form with invalid data" when {

    "with no data present for original and amended amount" should {

      val missingOption: Map[String, String] = Map.empty
      val form = new UnderpaymentDetailsFormProvider()().bind(missingOption)

      "result in a form with errors" in {
        form.hasErrors mustBe true
      }

      "throw one error" in {
        form.errors.size mustBe 2
      }

      "have an error with the correct message" in {
        form.errors.head.message mustBe originalNonEmptyErrorKey
        form.errors(1).message mustBe amendedNonEmptyErrorKey
      }
    }

    "with no data present for original amount" should {

      val missingOption: Map[String, String] = formBuilder(amended = fifty)
      val form = new UnderpaymentDetailsFormProvider()().bind(missingOption)

      "result in a form with errors" in {
        form.hasErrors mustBe true
      }

      "throw one error" in {
        form.errors.size mustBe 1
      }

      "have an error with the correct message" in {
        form.errors.head.message mustBe originalNonEmptyErrorKey
      }
    }

    "with no data present for amended amount" should {

      val missingOption: Map[String, String] = formBuilder(original = fifty)
      val form = new UnderpaymentDetailsFormProvider()().bind(missingOption)

      "result in a form with errors" in {
        form.hasErrors mustBe true
      }

      "throw one error" in {
        form.errors.size mustBe 1
      }

      "have an error with the correct message" in {
        form.errors.head.message mustBe amendedNonEmptyErrorKey
      }
    }

    "with non number data present for original amount" should {

      val missingOption: Map[String, String] = formBuilder(original = nonNumericValue, amended = fifty)
      val form = new UnderpaymentDetailsFormProvider()().bind(missingOption)

      "result in a form with errors" in {
        form.hasErrors mustBe true
      }

      "throw one error" in {
        form.errors.size mustBe 1
      }

      "have an error with the correct message" in {
        form.errors.head.message mustBe originalNonNumberErrorKey
      }
    }

    "with non number data present for amended amount" should {

      val missingOption: Map[String, String] = formBuilder(original = fifty, amended = nonNumericValue)
      val form = new UnderpaymentDetailsFormProvider()().bind(missingOption)

      "result in a form with errors" in {
        form.hasErrors mustBe true
      }

      "throw one error" in {
        form.errors.size mustBe 1
      }

      "have an error with the correct message" in {
        form.errors.head.message mustBe amendedNonNumberErrorKey
      }
    }

    "with out of range data present for original amount" should {

      val missingOption: Map[String, String] = formBuilder(original = outOfRangeValue, amended = fifty)
      val form = new UnderpaymentDetailsFormProvider()().bind(missingOption)

      "result in a form with errors" in {
        form.hasErrors mustBe true
      }

      "throw one error" in {
        form.errors.size mustBe 1
      }

      "have an error with the correct message" in {
        form.errors.head.message mustBe originalOutOfRangeErrorKey
      }
    }

    "with out of range data present for amended amount" should {

      val missingOption: Map[String, String] = formBuilder(original = fifty, amended = outOfRangeValue)
      val form = new UnderpaymentDetailsFormProvider()().bind(missingOption)

      "result in a form with errors" in {
        form.hasErrors mustBe true
      }

      "throw one error" in {
        form.errors.size mustBe 1
      }

      "have an error with the correct message" in {
        form.errors.head.message mustBe amendedOutOfRangeErrorKey
      }
    }

    "with original amount being greater than amended" should {

      val missingOption: Map[String, String] = formBuilder(original = fifty, amended = "40")
      val form = new UnderpaymentDetailsFormProvider()().bind(missingOption)

      "result in a form with errors" in {
        form.hasErrors mustBe true
      }

      "throw one error" in {
        form.errors.size mustBe 1
      }

      "have an error with the correct message" in {
        form.errors.head.message mustBe originalGreaterThanAmendedErrorKey
      }
    }

  }

  "Binding a form with valid data" when {

    "with original and amended values being correct" should {

      val missingOption: Map[String, String] = formBuilder(original = "35", amended = fifty)
      val form = new UnderpaymentDetailsFormProvider()().bind(missingOption)

      "result in a form with no errors" in {
        form.hasErrors mustBe false
      }

    }

  }


  }
