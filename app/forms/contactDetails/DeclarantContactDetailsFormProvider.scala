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

package forms.contactDetails

import forms.mappings.Mappings
import models.ContactDetails
import play.api.data.Form
import play.api.data.Forms.mapping

import javax.inject.Inject

class DeclarantContactDetailsFormProvider @Inject() extends Mappings {

  val fullNameRegex    = "^[a-zA-Z '-]+$"
  val phoneNumberRegex = "^(\\+)?[0-9\\(\\)\\- ]{9,16}$"
  val emailRegex =
    "^[a-zA-Z0-9\\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$"

  def apply(): Form[ContactDetails] =
    Form(
      mapping(
        "fullName" -> text("declarantContactDetails.error.nameNonEmpty")
          .verifying("declarantContactDetails.error.nameMinLength", value => value.length >= 2)
          .verifying("declarantContactDetails.error.nameMaxLength", value => value.length <= 50)
          .verifying(regexp(fullNameRegex, "declarantContactDetails.error.nameAllowableCharacters")),
        "email" -> text("declarantContactDetails.error.emailNonEmpty")
          .verifying(
            regexp(
              emailRegex,
              "declarantContactDetails.error.emailInvalidFormat"
            )
          ),
        "phoneNumber" -> text("declarantContactDetails.error.phoneNumberNonEmpty")
          .verifying(
            regexp(
              phoneNumberRegex,
              "declarantContactDetails.error.phoneNumberInvalidFormat"
            )
          )
      )(ContactDetails.apply)(c => Some(Tuple.fromProductTyped(c)))
    )

}
