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
      case 22 | 62 => foreignCurrencyFormMapping
      case 33 => textFormMapping(regex = """^([0-9]{10})($|[0-9a-zA-Z]{4}$)""")
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
      )(UnderpaymentReasonValue.apply)(UnderpaymentReasonValue.unapply)
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
      )(UnderpaymentReasonValue.apply)(UnderpaymentReasonValue.unapply)
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
