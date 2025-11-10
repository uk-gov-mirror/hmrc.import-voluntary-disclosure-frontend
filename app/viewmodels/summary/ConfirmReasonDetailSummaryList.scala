/*
 * Copyright 2025 HM Revenue & Customs
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

package viewmodels.summary

import models.UserAnswers
import models.reasons.BoxNumber.BoxNumber
import models.reasons.{BoxNumber, UnderpaymentReasonValue}
import pages.reasons.{UnderpaymentReasonAmendmentPage, UnderpaymentReasonBoxNumberPage, UnderpaymentReasonItemNumberPage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.SummaryListHelper

trait ConfirmReasonDetailSummaryList extends SummaryListHelper {

  def buildSummaryList(userAnswers: UserAnswers, boxNumber: BoxNumber)(implicit
    messages: Messages
  ): Seq[SummaryList] = {
    val rows =
      if (boxNumber == BoxNumber.OtherItem) {
        userAnswers
          .get(UnderpaymentReasonAmendmentPage)
          .map(value => buildOtherReasonSummaryList(value.original))
          .getOrElse(Seq.empty)
      } else {
        buildBoxNumberSummaryListRow(userAnswers.get(UnderpaymentReasonBoxNumberPage)) ++
          buildItemNumberSummaryListRow(userAnswers.get(UnderpaymentReasonItemNumberPage)) ++
          buildOriginalAmountSummaryListRow(userAnswers.get(UnderpaymentReasonAmendmentPage), boxNumber)
      }

    Seq(SummaryList(rows))
  }

  private def buildBoxNumberSummaryListRow(
    boxNumberOpt: Option[BoxNumber]
  )(implicit messages: Messages): Seq[SummaryListRow] = {
    boxNumberOpt map { boxNumber =>
      Seq(
        createRow(
          Text(messages("confirmReason.boxNumber")),
          Text(boxNumber.id.toString),
          Some(
            createChangeActionItem(
              controllers.reasons.routes.BoxNumberController.onLoad().url,
              messages("confirmReason.box.change")
            )
          ),
          "govuk-!-width-two-thirds"
        )
      )
    }
  } getOrElse Seq.empty

  private def buildItemNumberSummaryListRow(
    itemNumberOpt: Option[Int]
  )(implicit messages: Messages): Seq[SummaryListRow] = {
    itemNumberOpt map { itemNumber =>
      Seq(
        createRow(
          Text(messages("confirmReason.itemNumber")),
          Text(itemNumber.toString),
          Some(
            createChangeActionItem(
              controllers.reasons.routes.ItemNumberController.onLoad().url,
              messages("confirmReason.item.change")
            )
          ),
          "",
          "govuk-!-width-two-thirds"
        )
      )
    }
  } getOrElse Seq.empty

  private def buildOriginalAmountSummaryListRow(
    amendmentAnswersOpt: Option[UnderpaymentReasonValue],
    boxNumber: BoxNumber
  )(implicit messages: Messages): Seq[SummaryListRow] = {
    amendmentAnswersOpt map { underPaymentReasonValue =>
      Seq(
        createRow(
          Text(messages("confirmReason.original")),
          Text(underPaymentReasonValue.original),
          Some(
            createChangeActionItem(
              controllers.reasons.routes.UnderpaymentReasonAmendmentController.onLoad(boxNumber.id).url,
              messages("confirmReason.values.original.change")
            )
          ),
          "",
          "govuk-!-width-two-thirds"
        ),
        createRow(
          Text(messages("confirmReason.amended")),
          Text(underPaymentReasonValue.amended),
          Some(
            createChangeActionItem(
              controllers.reasons.routes.UnderpaymentReasonAmendmentController.onLoad(boxNumber.id).url,
              messages("confirmReason.values.amended.change")
            )
          ),
          "",
          "govuk-!-width-two-thirds"
        )
      )
    } getOrElse Seq.empty
  }

  private def buildOtherReasonSummaryList(value: String)(implicit messages: Messages): Seq[SummaryListRow] =
    Seq(
      createRow(
        Text(messages("confirmReason.otherReason")),
        Text(value),
        Some(
          createChangeActionItem(
            controllers.reasons.routes.UnderpaymentReasonAmendmentController.onLoad(BoxNumber.OtherItem.id).url,
            messages("confirmReason.values.otherReason.change")
          )
        ),
        ""
      )
    )
}
