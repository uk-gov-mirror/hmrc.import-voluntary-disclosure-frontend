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

package models

import play.api.data.Form
import play.api.i18n.Messages
import play.api.libs.json.Json
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.ViewUtils.hint

case class UnderpaymentType(customsDuty: Boolean, importVAT: Boolean, exciseDuty: Boolean)

object UnderpaymentType {
  implicit val format = Json.format[UnderpaymentType]

  def options(form: Form[_])(implicit messages: Messages): Seq[RadioItem] =
    Seq(
      RadioItem(
        value = Some("true"),
        content = Text(messages("deferment.payingByDeferment")),
        hint = None,
        checked = form("value").value.contains("true")
      ),
      RadioItem(
        value = Some("false"),
        content = Text(messages("deferment.payingByOther")),
        hint = Some(hint("deferment.hint")),
        checked = form("value").value.contains("false")
      )
    )

}
