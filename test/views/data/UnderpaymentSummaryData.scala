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

import messages.UnderpaymentSummaryMessages
import models.UnderpaymentAmount
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, Actions, Key, SummaryList, SummaryListRow, Value}
import views.ViewUtils.displayMoney

object UnderpaymentSummaryData {
  val cdUnderpayment: UnderpaymentAmount = UnderpaymentAmount(123, 5123)
  val ivUnderpayment: UnderpaymentAmount = UnderpaymentAmount(100, 1000)
  val edUnderpayment: UnderpaymentAmount = UnderpaymentAmount(550, 690)


  val customsDuty: Option[SummaryList] = Some(SummaryList(
    classes = "govuk-!-margin-bottom-9",
    rows = Seq(
      SummaryListRow(
        key = Key(
          Text(UnderpaymentSummaryMessages.originalAmount),
          classes = "govuk-!-width-two-thirds govuk-!-padding-bottom-0"
        ),
        value = Value(
          HtmlContent(displayMoney(cdUnderpayment.original)),
          classes = "govuk-!-padding-bottom-0"
        ),
        actions = Some(Actions(items = Seq(
          ActionItem(controllers.routes.UnderpaymentSummaryController.onLoad().url,
            Text(UnderpaymentSummaryMessages.change),
            Some(UnderpaymentSummaryMessages.customsDutyTitle))
        ), classes = "govuk-!-padding-bottom-0")),
        classes = "govuk-summary-list__row--no-border"
      ),
      SummaryListRow(
        key = Key(
          Text(UnderpaymentSummaryMessages.amendedAmount),
          classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
        ),
        value = Value(
          HtmlContent(displayMoney(cdUnderpayment.amended)),
          classes = "govuk-!-padding-top-0")
      ),
      SummaryListRow(
        key = Key(
          Text(UnderpaymentSummaryMessages.customsDutyTitle + UnderpaymentSummaryMessages.dueToHmrc),
          classes = "govuk-!-width-two-thirds"
        ),
        value = Value(HtmlContent(displayMoney(cdUnderpayment.amended - cdUnderpayment.original))))
    )
  ))

  val importVat: Option[SummaryList] = Some(SummaryList(
    classes = "govuk-!-margin-bottom-9",
    rows = Seq(
      SummaryListRow(
        key = Key(
          Text(UnderpaymentSummaryMessages.originalAmount),
          classes = "govuk-!-width-two-thirds govuk-!-padding-bottom-0"
        ),
        value = Value(
          HtmlContent(displayMoney(ivUnderpayment.original)),
          classes = "govuk-!-padding-bottom-0"
        ),
        actions = Some(Actions(items = Seq(
          ActionItem(controllers.routes.UnderpaymentSummaryController.onLoad().url,
            Text(UnderpaymentSummaryMessages.change),
            Some(UnderpaymentSummaryMessages.importVatTitle))
        ), classes = "govuk-!-padding-bottom-0")),
        classes = "govuk-summary-list__row--no-border"
      ),
      SummaryListRow(
        key = Key(
          Text(UnderpaymentSummaryMessages.amendedAmount),
          classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
        ),
        value = Value(
          HtmlContent(displayMoney(ivUnderpayment.amended)),
          classes = "govuk-!-padding-top-0")
      ),
      SummaryListRow(
        key = Key(
          Text(UnderpaymentSummaryMessages.importVatTitle + UnderpaymentSummaryMessages.dueToHmrc),
          classes = "govuk-!-width-two-thirds"
        ),
        value = Value(HtmlContent(displayMoney(ivUnderpayment.amended - ivUnderpayment.original))))
    )
  ))

  val exciseDuty: Option[SummaryList] = Some(SummaryList(
    classes = "govuk-!-margin-bottom-9",
    rows = Seq(
      SummaryListRow(
        key = Key(
          Text(UnderpaymentSummaryMessages.originalAmount),
          classes = "govuk-!-width-two-thirds govuk-!-padding-bottom-0"
        ),
        value = Value(
          HtmlContent(displayMoney(edUnderpayment.original)),
          classes = "govuk-!-padding-bottom-0"
        ),
        actions = Some(Actions(items = Seq(
          ActionItem(controllers.routes.UnderpaymentSummaryController.onLoad().url,
            Text(UnderpaymentSummaryMessages.change),
            Some(UnderpaymentSummaryMessages.exciseDutyTitle))
        ), classes = "govuk-!-padding-bottom-0")),
        classes = "govuk-summary-list__row--no-border"
      ),
      SummaryListRow(
        key = Key(
          Text(UnderpaymentSummaryMessages.amendedAmount),
          classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
        ),
        value = Value(
          HtmlContent(displayMoney(edUnderpayment.amended)),
          classes = "govuk-!-padding-top-0")
      ),
      SummaryListRow(
        key = Key(
          Text(UnderpaymentSummaryMessages.exciseDutyTitle + UnderpaymentSummaryMessages.dueToHmrc),
          classes = "govuk-!-width-two-thirds"
        ),
        value = Value(HtmlContent(displayMoney(edUnderpayment.amended - edUnderpayment.original))))
    )
  ))

}
