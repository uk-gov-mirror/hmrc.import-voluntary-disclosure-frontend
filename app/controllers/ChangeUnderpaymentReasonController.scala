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

package controllers

import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.UnderpaymentReasonSummaryFormProvider
import javax.inject.Inject
import models.UnderpaymentReason
import pages.UnderpaymentReasonsPage
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.ChangeUnderpaymentReasonView

import scala.concurrent.Future

class ChangeUnderpaymentReasonController @Inject()(identify: IdentifierAction,
                                                   getData: DataRetrievalAction,
                                                   requireData: DataRequiredAction,
                                                   mcc: MessagesControllerComponents,
                                                   view: ChangeUnderpaymentReasonView,
                                                   formProvider: UnderpaymentReasonSummaryFormProvider)
  extends FrontendController(mcc) with I18nSupport {

  private lazy val backLink: Call = controllers.routes.UnderpaymentReasonSummaryController.onLoad()

  def onLoad(boxNumber: String): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    summaryList(request.userAnswers.get(UnderpaymentReasonsPage), boxNumber) match {
      case Some(value) => Future.successful(Ok(view(formProvider.apply(), backLink, value, boxNumber.toInt)))
      case None => Future.successful(InternalServerError("Couldn't find Underpayment reasons"))
    }
  }

  def summaryList(underpaymentReason: Option[Seq[UnderpaymentReason]], boxNumber: String)(implicit messages: Messages): Option[Seq[SummaryList]] = {

    def itemNumberSummaryListRow: Option[Seq[SummaryListRow]] = {
      underpaymentReason.map { itemNumber =>
        val sortedReasons = itemNumber.find(item => item.boxNumber == boxNumber.toInt)
        val itemNumberValue = sortedReasons.map(value => value.itemNumber).head.toString
        if (sortedReasons.map(a => a.itemNumber).head != 0) {
          Seq(
            SummaryListRow(
              key = Key(
                content = Text(messages("confirmReason.itemNumber"))
              ),
              value = Value(
                content = HtmlContent(itemNumberValue)
              ),
              actions = Some(Actions(
                items = Seq(
                  ActionItem(controllers.routes.ItemNumberController.onLoad().url, Text(messages("confirmReason.change")))
                )
              ))
            )
          )
        } else {
          Seq.empty
        }
      }
    }

    val originalAmountSummaryListRow: Option[Seq[SummaryListRow]] = underpaymentReason.map { underPaymentReasonValue =>
      val sortedReasons = underPaymentReasonValue.find(item => item.boxNumber == boxNumber.toInt)
      val originalValue = sortedReasons.map(a => a.original).head
      val amendedValue = sortedReasons.map(a => a.amended).head
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("confirmReason.original")),
            classes = "govuk-!-padding-bottom-0"
          ),
          value = Value(
            content = HtmlContent(originalValue),
            classes = "govuk-!-padding-bottom-0"
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem("", Text(messages("confirmReason.change")))
            ),
            classes = "govuk-!-padding-bottom-0")
          ),
          classes = "govuk-summary-list__row--no-border"
        ),
        SummaryListRow(
          key = Key(
            content = Text(messages("confirmReason.amended")),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
          ),
          value = Value(
            content = HtmlContent(amendedValue),
            classes = "govuk-!-padding-top-0"
          )
        )
      )
    }

    val rows = itemNumberSummaryListRow.getOrElse(Seq.empty) ++
      originalAmountSummaryListRow.getOrElse(Seq.empty)

    if (rows.nonEmpty) {
      Some(Seq(SummaryList(rows)))
    } else {
      None
    }
  }


}
