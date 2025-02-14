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
import models.CustomsProcedureCode
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.i18n.Messages


class EnterCustomsProcedureCodeFormProvider @Inject() extends Mappings {

  def apply()(implicit messages: Messages): Form[String] =
    Form(
        "cpc" -> text("enterCustomsProcedureCode.cpc.error.required")
          .verifying(regexp("^[0-9]{4}[A-Za-z0-9][0-9]{2}$","enterCustomsProcedureCode.cpc.error.format"))
      )


}
