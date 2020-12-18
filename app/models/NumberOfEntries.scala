/*
 * Copyright 2020 HM Revenue & Customs
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
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.hint.Hint
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.ViewUtils.hint

sealed trait NumberOfEntries

object NumberOfEntries extends Enumerable.Implicits {

  case object OneEntry extends WithName("oneEntry") with NumberOfEntries
  case object MoreThanOneEntry extends WithName("moreThanOneEntry") with NumberOfEntries

  val values: Seq[NumberOfEntries] = Seq(
    OneEntry, MoreThanOneEntry
  )

  def options(form: Form[_])(implicit messages: Messages): Seq[RadioItem] = values.map {

    val hintMap = Map[NumberOfEntries, Option[Hint]](
      OneEntry -> None,
      MoreThanOneEntry -> Some(hint(s"numberOfEntries.moreThanOneEntry.hint"))
    )

    value =>
      RadioItem(
        value = Some(value.toString),
        content = Text(messages(s"numberOfEntries.${value.toString}")),
        hint = hintMap(value),
        checked = form("value").value.contains(value.toString)
      )
  }

  implicit val enumerable: Enumerable[NumberOfEntries] =
    Enumerable(values.map(v => v.toString -> v): _*)


}