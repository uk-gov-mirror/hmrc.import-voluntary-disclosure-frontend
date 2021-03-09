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
      case 22 | 62 | 63 | 66 | 67 | 68 => foreignCurrencyFormMapping()
      case 33 => textFormMapping(regex = """^([0-9]{10})($|[0-9a-zA-Z]{4}$)""")
      case 34 => textFormMapping(regex = """^([a-zA-Z]{2}$)""")
      case 35 | 38 => weightFormMapping
      case 37 => textFormMapping(regex = """^([0-9]{4}[A-Za-z0-9][0-9]{2}$)""")
      case _ => textFormMapping(regex = """^.*$""") // TODO: Remove this when all box numbers added to story
    }
  }

  private def foreignCurrencyFormMapping(toUpper: Boolean = true): Form[UnderpaymentReasonValue] = {
    Form(
      mapping(
        "original" -> foreignCurrency(
          "amendmentValue.error.original.missing",
          "amendmentValue.error.original.format"),
        "amended" -> foreignCurrency(
          "amendmentValue.error.amended.missing",
          "amendmentValue.error.amended.format")
      )
      ((original, amended) =>
        if (toUpper) UnderpaymentReasonValue.apply(original.toUpperCase(), amended.toUpperCase())
        else UnderpaymentReasonValue.apply(original, amended)
      )
      (value => Some(value.original, value.amended)).verifying(different("amendmentValue.error.amended.different"))
    )
  }

  private def textFormMapping(regex: String, toUpper: Boolean = true): Form[UnderpaymentReasonValue] = {
    Form(
      mapping(
        "original" -> text("amendmentValue.error.original.missing")
          .verifying(regexp(regex, "amendmentValue.error.original.format")),
        "amended" -> text("amendmentValue.error.amended.missing")
          .verifying(regexp(regex, "amendmentValue.error.amended.format"))
      )
      ((original, amended) =>
        if (toUpper) UnderpaymentReasonValue.apply(original.toUpperCase(), amended.toUpperCase())
        else UnderpaymentReasonValue.apply(original, amended)
      )
      (value => Some(value.original, value.amended)).verifying(different("amendmentValue.error.amended.different"))
    )
  }

  private def weightFormMapping: Form[UnderpaymentReasonValue] = {
    Form(
      mapping(
        "original" -> weightNumeric(
          requiredKey = "amendmentValue.error.original.weight.missing",
          nonNumericKey = "amendmentValue.error.original.weight.nonNumeric",
          invalidDecimalPoints = "amendmentValue.error.original.weight.invalidDecimals")
          .verifying(inRange[BigDecimal](0, 9999999.999, "amendmentValue.error.original.weight.outOfRange")),
        "amended" -> weightNumeric(
          requiredKey = "amendmentValue.error.amended.weight.missing",
          nonNumericKey = "amendmentValue.error.amended.weight.nonNumeric",
          invalidDecimalPoints = "amendmentValue.error.amended.weight.invalidDecimals")
          .verifying(inRange[BigDecimal](0, 9999999.999, "amendmentValue.error.amended.weight.outOfRange"))
      )
      ((original, amended) => UnderpaymentReasonValue.apply(original.toString(), amended.toString()))
      (value => Some(BigDecimal(value.original), BigDecimal(value.amended)))
        .verifying(different("amendmentValue.error.amended.different"))
    )
  }

  private[forms] def different(errorKey: String): Constraint[UnderpaymentReasonValue] =
    Constraint {
      input =>
        if (input.original != input.amended) {
          Valid
        } else {
          Invalid(errorKey)
        }
    }
}
