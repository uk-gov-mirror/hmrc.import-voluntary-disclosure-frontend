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

import forms.mappings.Mappings
import models.underpayments.UnderpaymentAmount
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.validation.{Constraint, Invalid, Valid}

class UnderpaymentDetailsFormProvider extends Mappings {

  private final val minimum: BigDecimal = 0
  private final val maximum: BigDecimal = 9999999999.99

  def apply(): Form[UnderpaymentAmount] =
    Form(
      mapping(
        "original" -> numeric(
          isCurrency = true,
          requiredKey = "underpaymentDetails.error.originalNonEmpty",
          invalidDecimalPlacesKey = "underpaymentDetails.error.originalNonNumber",
          nonNumericKey = "underpaymentDetails.error.originalNonNumber"
        ).verifying(inRange[BigDecimal](minimum, maximum, "underpaymentDetails.error.originalOutOfRange")),
        "amended" -> numeric(
          isCurrency = true,
          requiredKey = "underpaymentDetails.error.amendedNonEmpty",
          invalidDecimalPlacesKey = "underpaymentDetails.error.amendedNonNumber",
          nonNumericKey = "underpaymentDetails.error.amendedNonNumber"
        ).verifying(inRange[BigDecimal](minimum, maximum, "underpaymentDetails.error.amendedOutOfRange"))
      )(UnderpaymentAmount.apply)(UnderpaymentAmount.unapply)
        .verifying(positiveAmountOwing())
    )

  private[forms] def positiveAmountOwing(): Constraint[UnderpaymentAmount] =
    Constraint {
      input =>
        if (input.original < input.amended) {
          Valid
        } else {
          Invalid("underpaymentDetails.error.positiveAmountOwed")
        }
    }

}
