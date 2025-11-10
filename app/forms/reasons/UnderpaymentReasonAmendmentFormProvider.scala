/*
 * Copyright 2025 HM Revenue & Customs
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

package forms.reasons

import forms.mappings.Mappings
import forms.utils.FormHelpers
import models.reasons.BoxNumber.BoxNumber
import models.reasons.{BoxNumber, UnderpaymentReasonValue}
import play.api.data.Forms._
import play.api.data.validation._
import play.api.data.{Form, Forms}

import scala.annotation.nowarn

class UnderpaymentReasonAmendmentFormProvider extends Mappings with FormHelpers {

  @nowarn("msg=match may not be exhaustive")
  def apply(boxNumber: BoxNumber): Form[UnderpaymentReasonValue] = {
    boxNumber match {
      case BoxNumber.Box22 | BoxNumber.Box62 | BoxNumber.Box63 | BoxNumber.Box66 | BoxNumber.Box67 | BoxNumber.Box68 =>
        foreignCurrencyFormMapping
      case BoxNumber.Box33 => textFormMapping(regex = """^([0-9]{10})($|[0-9a-zA-Z]{4}$)""")
      case BoxNumber.Box34 => textFormMapping(regex = """^[a-zA-Z]{2}$""")
      case BoxNumber.Box35 | BoxNumber.Box38 =>
        decimalFormMapping(
          isCurrency = false,
          requiredKey = "weight.missing",
          nonNumericKey = "weight.nonNumeric",
          invalidDecimalPlacesKey = "weight.invalidDecimals",
          outOfRangeKey = "weight.outOfRange",
          numDecimalPlaces = 3,
          rangeMin = Some(BigDecimal(0)),
          rangeMax = Some(BigDecimal(9999999.999))
        )
      case BoxNumber.Box36 => textFormMapping(regex = """^[0-9]{3}$""")
      case BoxNumber.Box37 => textFormMapping(regex = """^[0-9]{4}[A-Za-z0-9][0-9]{2}$""")
      case BoxNumber.Box39 => textFormMapping(regex = """^[0-9]{6}$""")
      case BoxNumber.Box41 =>
        decimalFormMapping(
          isCurrency = false,
          requiredKey = "unit.missing",
          nonNumericKey = "unit.nonNumeric",
          invalidDecimalPlacesKey = "unit.invalidDecimals",
          outOfRangeKey = "unit.outOfRange",
          numDecimalPlaces = 3,
          rangeMin = Some(BigDecimal(0)),
          rangeMax = Some(BigDecimal(9999999.999))
        )
      case BoxNumber.Box42 =>
        decimalFormMapping(
          isCurrency = false,
          requiredKey = "decimal.missing",
          nonNumericKey = "decimal.nonNumeric",
          invalidDecimalPlacesKey = "decimal.invalidDecimals",
          outOfRangeKey = "decimal.outOfRange",
          numDecimalPlaces = 2,
          rangeMin = Some(BigDecimal(0)),
          rangeMax = Some(BigDecimal(999999999999.99))
        )
      case BoxNumber.Box43 =>
        textFormMapping(regex = """^[1-7]{1}$""")
      case BoxNumber.Box45 =>
        textFormMapping(regex =
          """^[A-M]{1}$|^[A-M]{1}[0-9]{1,2}$|^[A-M]{1}[0-9]{1,2}[.][0-9]{1}$|^[0-9]{1,2}[.][0-9]{1}$|^[0-9]{1,2}$"""
        )
      case BoxNumber.Box46 =>
        decimalFormMapping(
          isCurrency = true,
          requiredKey = "currency.missing",
          nonNumericKey = "currency.nonNumeric",
          invalidDecimalPlacesKey = "currency.invalidDecimals",
          outOfRangeKey = "currency.outOfRange",
          numDecimalPlaces = 2,
          rangeMin = Some(BigDecimal(0)),
          rangeMax = Some(BigDecimal(999999999999.99))
        )
      case BoxNumber.OtherItem => otherReason()
    }
  }

  private def foreignCurrencyFormMapping: Form[UnderpaymentReasonValue] = {
    Form(
      mapping(
        "original" -> foreignCurrency("amendmentValue.error.original.missing", "amendmentValue.error.original.format"),
        "amended"  -> foreignCurrency("amendmentValue.error.amended.missing", "amendmentValue.error.amended.format")
      )(UnderpaymentReasonValue.apply)(u => Some(Tuple.fromProductTyped(u)))
        .verifying(different("amendmentValue.error.amended.different"))
    )
  }

  private def textFormMapping(regex: String): Form[UnderpaymentReasonValue] = {
    Form(
      mapping(
        "original" -> text("amendmentValue.error.original.missing")
          .transform[String](toUpperNoSpaces(_), toUpperNoSpaces(_))
          .verifying(regexp(regex, "amendmentValue.error.original.format")),
        "amended" -> text("amendmentValue.error.amended.missing")
          .transform[String](toUpperNoSpaces(_), toUpperNoSpaces(_))
          .verifying(regexp(regex, "amendmentValue.error.amended.format"))
      )(UnderpaymentReasonValue.apply)(u => Some(Tuple.fromProductTyped(u)))
        .verifying(different("amendmentValue.error.amended.different"))
    )
  }

  private def decimalFormMapping(
    isCurrency: Boolean,
    requiredKey: String,
    nonNumericKey: String,
    invalidDecimalPlacesKey: String,
    outOfRangeKey: String,
    numDecimalPlaces: Int,
    rangeMin: Option[BigDecimal],
    rangeMax: Option[BigDecimal]
  ): Form[UnderpaymentReasonValue] = {
    Form(
      mapping(
        "original" -> numeric(
          isCurrency = isCurrency,
          numDecimalPlaces = numDecimalPlaces,
          requiredKey = "amendmentValue.error.original." + requiredKey,
          nonNumericKey = "amendmentValue.error.original." + nonNumericKey,
          invalidDecimalPlacesKey = "amendmentValue.error.original." + invalidDecimalPlacesKey
        )
          .verifying(minMaxRange(rangeMin, rangeMax, "amendmentValue.error.original." + outOfRangeKey)),
        "amended" -> numeric(
          isCurrency = isCurrency,
          numDecimalPlaces = numDecimalPlaces,
          requiredKey = "amendmentValue.error.amended." + requiredKey,
          nonNumericKey = "amendmentValue.error.amended." + nonNumericKey,
          invalidDecimalPlacesKey = "amendmentValue.error.amended." + invalidDecimalPlacesKey
        )
          .verifying(minMaxRange(rangeMin, rangeMax, "amendmentValue.error.amended." + outOfRangeKey))
      )((original, amended) => UnderpaymentReasonValue.apply(original.toString(), amended.toString()))(value =>
        Some((BigDecimal(value.original), BigDecimal(value.amended)))
      )
        .verifying(different("amendmentValue.error.amended.different"))
    )
  }

  private[forms] def different(errorKey: String): Constraint[UnderpaymentReasonValue] =
    Constraint { input =>
      if (input.original.toUpperCase != input.amended.toUpperCase) {
        Valid
      } else {
        Invalid(errorKey)
      }
    }

  private[forms] def minMaxRange(
    rangeMin: Option[BigDecimal] = None,
    rangeMax: Option[BigDecimal] = None,
    errorKey: String
  ): Constraint[BigDecimal] = {
    (rangeMin, rangeMax) match {
      case (Some(min), Some(max)) => inRange(min, max, errorKey)
      case (Some(min), None)      => minimumValue(min, errorKey)
      case (None, Some(max))      => maximumValue(max, errorKey)
      case _                      => Constraint(_ => Valid)
    }
  }

  private[forms] def otherReason(): Form[UnderpaymentReasonValue] = {
    Form(
      mapping(
        "original" -> text("otherReason.error.required")
          .verifying(maxLength(1500, "otherReason.error.maxLength")),
        "amended" -> Forms.text
      )(UnderpaymentReasonValue.apply)(u => Some(Tuple.fromProductTyped(u)))
    )
  }
}
