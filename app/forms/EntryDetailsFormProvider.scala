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

import config.AppConfig
import forms.mappings.Mappings
import javax.inject.Inject
import models.EntryDetails
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.i18n.Messages

class EntryDetailsFormProvider @Inject()(implicit appConfig: AppConfig) extends Mappings {

  def apply()(implicit messages: Messages): Form[EntryDetails] = {

    Form( mapping(
      "epu" -> text("entryDetails.epu.error.missing")
        .verifying(regexp("[0-9]{3}","entryDetails.epu.error.format")),
      "entryNumber" -> text("entryDetails.entryNumber.error.missing")
        .verifying(regexp("[0-9]{6}[a-z|A-Z]","entryDetails.entryNumber.error.format"))
        .transform[String](_.toUpperCase, _.toUpperCase),
      "entryDate" -> localDate(
        invalidKey = "entryDetails.entryDate.error.invalid",
        allRequiredKey = "entryDetails.entryDate.error.required.all",
        twoRequiredKey = "entryDetails.entryDate.error.required.two",
        requiredKey = "entryDetails.entryDate.error.required",
        yearLengthKey = "entryDetails.entryDate.error.year.length",
        validatePastKey = Some("entryDetails.entryDate.error.past")
      )
    )(EntryDetails.apply)(EntryDetails.unapply)
    )
  }
}
