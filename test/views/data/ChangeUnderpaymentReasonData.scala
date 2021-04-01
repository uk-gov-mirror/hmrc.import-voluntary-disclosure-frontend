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

import models.{ChangeUnderpaymentReason, UnderpaymentReason}
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._

object ChangeUnderpaymentReasonData {

  private lazy val changeItemAction: Call = controllers.routes.ChangeItemNumberController.onLoad()
  private lazy val changeDetailsAction: Call = controllers.routes.ChangeUnderpaymentReasonController.onLoad()

  val singleItemReason: ChangeUnderpaymentReason = ChangeUnderpaymentReason(
    original = UnderpaymentReason(35, 1, "50", "60"),
    changed = UnderpaymentReason(35, 1, "50", "60")
  )

  val singleEntryLevelReason: ChangeUnderpaymentReason = ChangeUnderpaymentReason(
    original = UnderpaymentReason(22, 0, "50", "60"),
    changed = UnderpaymentReason(22, 0, "50", "60")
  )


  val summaryList: SummaryList =
    SummaryList(
      Seq(
        SummaryListRow(
          key = Key(
            content = Text("Item number")
          ),
          value = Value(
            HtmlContent("1")
          ),
          actions = Some(
            Actions(
              items = Seq(
                ActionItem(
                  changeItemAction.url,
                  Text("Change")
                )
              )
            )
          )
        ),
        SummaryListRow(
          key = Key(
            content = Text("Original value"),
            classes = "govuk-!-padding-bottom-0"
          ),
          value = Value(
            HtmlContent("50"),
            classes = "govuk-!-padding-bottom-0"
          ),
          actions = Some(
            Actions(
              items = Seq(
                ActionItem(
                  changeDetailsAction.url,
                  Text("Change")
                )
              ),
              classes = "govuk-!-padding-bottom-0"
            )
          ),
          classes = "govuk-summary-list__row--no-border"
        ),
        SummaryListRow(
          key = Key(
            content = Text("Amended value"),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
          ),
          value = Value(
            HtmlContent("60"),
            classes = "govuk-!-padding-top-0"
          )
        )
      )
  )

  val entryLevelSummaryList: SummaryList =
    SummaryList(
      Seq(
        SummaryListRow(
          key = Key(
            content = Text("Original value"),
            classes = "govuk-!-padding-bottom-0"
          ),
          value = Value(
            HtmlContent("50"),
            classes = "govuk-!-padding-bottom-0"
          ),
          actions = Some(
            Actions(
              items = Seq(
                ActionItem(
                  changeDetailsAction.url,
                  Text("Change")
                )
              ),
              classes = "govuk-!-padding-bottom-0"
            )
          ),
          classes = "govuk-summary-list__row--no-border"
        ),
        SummaryListRow(
          key = Key(
            content = Text("Amended value"),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
          ),
          value = Value(
            HtmlContent("60"),
            classes = "govuk-!-padding-top-0"
          )
        )
      )
  )
}
