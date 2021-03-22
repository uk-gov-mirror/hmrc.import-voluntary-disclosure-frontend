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
import models.{UnderpaymentReason, UnderpaymentReasonValue, UserAnswers}
import pages.{UnderpaymentReasonAmendmentPage, UnderpaymentReasonBoxNumberPage, UnderpaymentReasonItemNumberPage, UnderpaymentReasonsPage}
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.ConfirmReasonDetailView

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ConfirmReasonDetailController @Inject()(identify: IdentifierAction,
                                              getData: DataRetrievalAction,
                                              requireData: DataRequiredAction,
                                              sessionRepository: SessionRepository,
                                              mcc: MessagesControllerComponents,
                                              view: ConfirmReasonDetailView)
  extends FrontendController(mcc) with I18nSupport {


  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val boxNumber = request.userAnswers.get(UnderpaymentReasonBoxNumberPage).getOrElse(0)
    val summary = summaryList(request.userAnswers, boxNumber).getOrElse(Seq.empty)
    Future.successful(Ok(view(summary, controllers.routes.UnderpaymentReasonAmendmentController.onLoad(boxNumber))))
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val underpaymentReason = Seq(
      UnderpaymentReason(
        request.userAnswers.get(UnderpaymentReasonBoxNumberPage).getOrElse(0),
        request.userAnswers.get(UnderpaymentReasonItemNumberPage).getOrElse(0),
        request.userAnswers.get(UnderpaymentReasonAmendmentPage).getOrElse(UnderpaymentReasonValue("", "")).original,
        request.userAnswers.get(UnderpaymentReasonAmendmentPage).getOrElse(UnderpaymentReasonValue("", "")).amended
      )
    )
    val currentReasons = request.userAnswers.get(UnderpaymentReasonsPage).getOrElse(Seq.empty)
    for {
      updatedAnswers <- Future.fromTry(request.userAnswers.set(UnderpaymentReasonsPage, currentReasons ++ underpaymentReason))
      _ <- sessionRepository.set(updatedAnswers)
    } yield {
      Redirect(controllers.routes.UnderpaymentReasonSummaryController.onLoad())
    }
  }

  def summaryList(userAnswers: UserAnswers, boxNumber: Int)(implicit messages: Messages): Option[Seq[SummaryList]] = {
    val boxNumberSummaryListRow: Option[Seq[SummaryListRow]] = userAnswers.get(UnderpaymentReasonBoxNumberPage) map { boxNumber =>
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("confirmReason.boxNumber")),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            content = HtmlContent(boxNumber.toString)
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem(controllers.routes.BoxNumberController.onLoad().url, Text(messages("confirmReason.change")))
            )
          ))
        )
      )
    }
    val itemNumberSummaryListRow: Option[Seq[SummaryListRow]] = userAnswers.get(UnderpaymentReasonItemNumberPage) map { itemNumber =>
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("confirmReason.itemNumber")),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            content = HtmlContent(itemNumber.toString)
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem(controllers.routes.ItemNumberController.onLoad().url, Text(messages("confirmReason.change")))
            )
          ))
        )
      )
    }

    val originalAmountSummaryListRow: Option[Seq[SummaryListRow]] = userAnswers.get(UnderpaymentReasonAmendmentPage) map { underPaymentReasonValue =>
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("confirmReason.original")),
            classes = "govuk-!-width-two-thirds govuk-!-padding-bottom-0"
          ),
          value = Value(
            content = HtmlContent(underPaymentReasonValue.original),
            classes = "govuk-!-padding-bottom-0"
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem(controllers.routes.UnderpaymentReasonAmendmentController.onLoad(boxNumber).url, Text(messages("confirmReason.change")))
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
            content = HtmlContent(underPaymentReasonValue.amended),
            classes = "govuk-!-padding-top-0"
          )
        )
      )
    }

    val rows = boxNumberSummaryListRow.getOrElse(Seq.empty) ++
      itemNumberSummaryListRow.getOrElse(Seq.empty) ++
      originalAmountSummaryListRow.getOrElse(Seq.empty)

    if (rows.nonEmpty) {
      Some(Seq(SummaryList(rows)))
    } else {
      None
    }

  }

}
