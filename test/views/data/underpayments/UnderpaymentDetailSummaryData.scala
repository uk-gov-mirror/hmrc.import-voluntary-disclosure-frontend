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
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import views.ViewUtils.displayMoney


object UnderpaymentDetailSummaryData {

  private lazy val changeAction: Call = controllers.underpayments.routes.UnderpaymentDetailSummaryController.onLoad()


  val summaryList: Option[SummaryList] = Some(
    SummaryList(
      Seq(
        SummaryListRow(
          key = Key(
            content = Text("import vat"),
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
                  Text("common.change"),
                  Some("key")
                )
              )
            )
          )
        )
      )
    )
  )

  val amountOwedSummaryList: Option[SummaryList] = Some(
    SummaryList(
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(UnderpaymentDetailSummaryMessages.owedToHMRC),
            classes = "govuk-summary-list__key govuk-!-width-two-thirds"
          ),
          value = Value(
            content = HtmlContent(displayMoney(amountInPence = 100)),
            classes = "govuk-summary-list__value"
          ),
          classes = "govuk-summary-list__row--no-border"
        )
      )
    )
  )

}
