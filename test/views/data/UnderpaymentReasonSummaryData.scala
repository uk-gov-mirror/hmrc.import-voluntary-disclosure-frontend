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

package views.data

import models.UnderpaymentReason
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._

object UnderpaymentReasonSummaryData {

  private def changeAction(boxNumber: Int, itemNumber: Int): Call = controllers.routes.ChangeUnderpaymentReasonController.change(boxNumber, itemNumber)

  val singleItemReason: Option[Seq[UnderpaymentReason]] = Some(
    Seq(
      UnderpaymentReason(22, 0, "50", "60")
    )
  )

  val multipleItemReason: Option[Seq[UnderpaymentReason]] = Some(
    Seq(
      UnderpaymentReason(22, 0, "50", "60"),
      UnderpaymentReason(33, 1, "50", "60")
    )
  )

  val singleItemSummaryList: Option[SummaryList] = Some(
    SummaryList(
      Seq(
        SummaryListRow(
          key = Key(
            content = Text("Box 22")
          ),
          value = Value(
            HtmlContent("Entry level")
          ),
          actions = Some(
            Actions(
              items = Seq(
                ActionItem(
                  changeAction(boxNumber = 22, itemNumber = 0).url,
                  Text("Change"),
                  Some("key")
                )
              ),
              classes = "govuk-!-width-one-third"
            )
          )
        )
      )
    )
  )

  val multipleItemSummaryList: Option[SummaryList] = Some(
    SummaryList(
      Seq(
        SummaryListRow(
          key = Key(
            content = Text("Box 22")
          ),
          value = Value(
            HtmlContent("Entry level")
          ),
          actions = Some(
            Actions(
              items = Seq(
                ActionItem(
                  changeAction(boxNumber = 22,itemNumber = 0).url,
                  Text("Change"),
                  Some("key")
                )
              ),
              classes = "govuk-!-width-one-third"
            )
          )
        ),
        SummaryListRow(
          key = Key(
            content = Text("Box 33")
          ),
          value = Value(
            HtmlContent("Item 1")
          ),
          actions = Some(
            Actions(
              items = Seq(
                ActionItem(
                  changeAction(boxNumber = 33, itemNumber = 1).url,
                  Text("Change"),
                  Some("key")
                )
              ),
              classes = "govuk-!-width-one-third"
            )
          )
        )
      )
    )
  )

}
