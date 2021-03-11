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

import models.UnderpaymentReasonValue
import forms.mappings.Mappings
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.{Constraint, Invalid, Valid}
import play.api.i18n.Messages

class UnderpaymentReasonAmendmentFormProvider extends Mappings {

  def apply(boxNumber: Int)(implicit messages: Messages): Form[UnderpaymentReasonValue] = {
    boxNumber match {
      case 22 | 62 | 63 | 66 | 67 | 68 => foreignCurrencyFormMapping
      case 33 => textFormMapping(regex = """^([0-9]{10})($|[0-9a-zA-Z]{4}$)""")
      case 34 => textFormMapping(regex = """^[a-zA-Z]{2}$""")
      case 35 | 38  => decimalFormMapping(
        requiredKey = "weight.missing",
        nonNumericKey = "weight.nonNumeric",
        invalidDecimalPlacesKey = "weight.invalidDecimals",
        outOfRangeKey = "weight.outOfRange",
        numDecimalPlaces = 3,
        rangeMin = Some(BigDecimal(0)),
        rangeMax = Some(BigDecimal(9999999.999))
      )
      case 36 => textFormMapping(regex = """^[0-9]{3}$""")
      case 37 => textFormMapping(regex = """^[0-9]{4}[A-Za-z0-9][0-9]{2}$""")
      case 39 => textFormMapping(regex = """^[0-9a-zA-Z]{7}$""")
      case 41  => decimalFormMapping(
        requiredKey = "unit.missing",
        nonNumericKey = "unit.nonNumeric",
        invalidDecimalPlacesKey = "unit.invalidDecimals",
        outOfRangeKey = "unit.outOfRange",
        numDecimalPlaces = 3,
        rangeMin = Some(BigDecimal(0)),
        rangeMax = Some(BigDecimal(9999999.999))
      )
      case _ => textFormMapping(regex = """^.*$""") // TODO: Remove this when all box numbers added to story
    }
  }

  private def foreignCurrencyFormMapping: Form[UnderpaymentReasonValue] = {
    Form(
      mapping(
        "original" -> foreignCurrency(
          "amendmentValue.error.original.missing",
          "amendmentValue.error.original.format"),
        "amended" -> foreignCurrency(
          "amendmentValue.error.amended.missing",
          "amendmentValue.error.amended.format")
      ) (UnderpaymentReasonValue.apply) (UnderpaymentReasonValue.unapply)
      .verifying(different("amendmentValue.error.amended.different"))
    )
  }

  private def textFormMapping(regex: String): Form[UnderpaymentReasonValue] = {
    Form(
      mapping(
        "original" -> text("amendmentValue.error.original.missing")
          .verifying(regexp(regex, "amendmentValue.error.original.format")),
        "amended" -> text("amendmentValue.error.amended.missing")
          .verifying(regexp(regex, "amendmentValue.error.amended.format"))
      ) (UnderpaymentReasonValue.apply) (UnderpaymentReasonValue.unapply)
        .verifying(different("amendmentValue.error.amended.different"))
    )
  }

  private def decimalFormMapping(
                                  requiredKey: String,
                                  nonNumericKey: String,
                                  invalidDecimalPlacesKey: String,
                                  outOfRangeKey: String,
                                  numDecimalPlaces: Int,
                                  rangeMin: Option[BigDecimal] = None,
                                  rangeMax: Option[BigDecimal] = None
                                ): Form[UnderpaymentReasonValue] = {
    Form(
      mapping(
        "original" -> numeric(
          numDecimalPlaces = numDecimalPlaces,
          requiredKey = "amendmentValue.error.original." + requiredKey,
          nonNumericKey = "amendmentValue.error.original." + nonNumericKey,
          invalidDecimalPlacesKey = "amendmentValue.error.original." + invalidDecimalPlacesKey)
          .verifying(minMaxRange(rangeMin, rangeMax, "amendmentValue.error.original." + outOfRangeKey)),
        "amended" -> numeric(
          numDecimalPlaces = numDecimalPlaces,
          requiredKey = "amendmentValue.error.amended." + requiredKey,
          nonNumericKey = "amendmentValue.error.amended." + nonNumericKey,
          invalidDecimalPlacesKey = "amendmentValue.error.amended." + invalidDecimalPlacesKey)
          .verifying(minMaxRange(rangeMin, rangeMax, "amendmentValue.error.amended." + outOfRangeKey))
      )
      ((original, amended) => UnderpaymentReasonValue.apply(original.toString(), amended.toString()))
      (value => Some(BigDecimal(value.original), BigDecimal(value.amended)))
        .verifying(different("amendmentValue.error.amended.different"))
    )
  }

  private[forms] def different(errorKey: String): Constraint[UnderpaymentReasonValue] =
    Constraint {
      input =>
        if (input.original.toUpperCase != input.amended.toUpperCase) {
          Valid
        } else {
          Invalid(errorKey)
        }
    }

  private[forms] def minMaxRange(rangeMin: Option[BigDecimal] = None,
                                  rangeMax: Option[BigDecimal] = None,
                                  errorKey: String): Constraint[BigDecimal] = {
    (rangeMin, rangeMax) match {
      case (Some(min), Some(max)) => inRange(min, max, errorKey)
      case (Some(min), None) => minimumValue(min, errorKey)
      case (None, Some(max)) => maximumValue(max, errorKey)
    }
  }
}
