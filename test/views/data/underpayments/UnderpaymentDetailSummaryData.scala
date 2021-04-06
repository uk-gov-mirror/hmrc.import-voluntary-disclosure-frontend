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

package views.data.underpayments

import messages.underpayments.UnderpaymentDetailSummaryMessages
import models.underpayments.UnderpaymentDetail
import play.api.i18n.Messages
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, Actions, Key, SummaryList, SummaryListRow, Value}
import views.ViewUtils.displayMoney


object UnderpaymentDetailSummaryData {

def summaryList(underpaymentDetail: Option[Seq[UnderpaymentDetail]]
                                      )(implicit messages: Messages): Option[SummaryList] = {
    val changeAction: Call = controllers.underpayments.routes.UnderpaymentDetailSummaryController.onLoad()
    if (underpaymentDetail.isDefined) {
      Some(
        SummaryList(
          rows = for (underpayment <- underpaymentDetail.get.reverse) yield
            SummaryListRow(
              key = Key(
                content = Text(messages("import vat")),
                classes = "govuk-summary-list__key govuk-!-width-two-thirds"
              ),
              value = Value(
                content = HtmlContent(displayMoney(amountInPence = 100)),
                classes = "govuk-summary-list__value"
              ),
              actions = Some(
                Actions(
                  items = Seq(
                    ActionItem(
                      changeAction.url,
                      Text(messages("common.change")),
                      Some("key")
                    )
                  )
                )
              )
            )
        )
      )
    } else {
      None
    }
  }

  def amountOwedSummaryList(underpaymentDetail: Option[Seq[UnderpaymentDetail]])(implicit messages: Messages): Option[SummaryList] = {
    Some(
      SummaryList(
        rows = Seq(SummaryListRow(
          key = Key(
            content = Text(
              messages(UnderpaymentDetailSummaryMessages.owedToHMRC)
            ),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            content = HtmlContent(
              displayMoney(
                underpaymentDetail.map(
                  items =>
                    items.map(
                      item => item.amended - item.original
                    ).foldLeft(BigDecimal(0))((left, right) => left + right)
                ).getOrElse(0.0)
              )
            )
          ),
          classes = "govuk-summary-list__row--no-border"
        )
        )
      )
    )
  }

}
