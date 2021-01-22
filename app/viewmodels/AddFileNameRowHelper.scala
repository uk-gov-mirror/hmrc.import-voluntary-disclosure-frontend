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

package viewmodels

import controllers.routes
import models.UserAnswers
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import queries.FileUploadQuery
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.ActionItem

class AddFileNameRowHelper(val userAnswers: UserAnswers)
                          (implicit val messages: Messages) extends AddToListRowHelper {

  def rows: List[AddToListRow] = {

    userAnswers.get(FileUploadQuery).map {
      files =>
        files.zipWithIndex.map {
          case (file, index) =>
            addToListRow(
              value = HtmlFormat.escape(file.fileName).toString,
              removeAction = Some(ActionItem(
                href = "",
                content = Text("Remove"),
                visuallyHiddenText = Some("")
              )
            )
          )
        }
    }.getOrElse(List.empty)
  }
}
