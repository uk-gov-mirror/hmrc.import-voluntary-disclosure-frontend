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
import javax.inject.{Inject, Singleton}
import models.UserAnswers
import pages.{ChangeUnderpaymentReasonPage, UnderpaymentReasonsPage}
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.ConfirmChangeReasonDetailView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ConfirmChangeReasonDetailController @Inject()(identify: IdentifierAction,
                                                    getData: DataRetrievalAction,
                                                    requireData: DataRequiredAction,
                                                    sessionRepository: SessionRepository,
                                                    mcc: MessagesControllerComponents,
                                                    view: ConfirmChangeReasonDetailView)
  extends FrontendController(mcc) with I18nSupport {


  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val boxNumber = request.userAnswers.get(ChangeUnderpaymentReasonPage).fold(0) { reason =>
      reason.original.boxNumber
    }
    val summary = summaryList(request.userAnswers, boxNumber)
    Future.successful(Ok(view(summary, boxNumber, controllers.routes.ChangeUnderpaymentReasonDetailsController.onLoad(boxNumber))))
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    request.userAnswers.get(UnderpaymentReasonsPage) match {
      case Some(oldReasonList) => {
        request.userAnswers.get(ChangeUnderpaymentReasonPage) match {
          case Some(reason) =>
            val newReasonList = oldReasonList.filterNot(x => x.boxNumber == reason.original.boxNumber && x.itemNumber == reason.original.itemNumber) ++ Seq(reason.changed)
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(UnderpaymentReasonsPage, newReasonList))
              _ <- sessionRepository.set(updatedAnswers)
            } yield {
              Redirect(controllers.routes.UnderpaymentReasonSummaryController.onLoad())
            }
          case _ => Future.successful(InternalServerError("Changed underpayment reason not found"))
        }
      }
      case _ => Future.successful(InternalServerError("Existing underpayment reasons not found"))
    }

  }

  def summaryList(userAnswers: UserAnswers, boxNumber: Int)(implicit messages: Messages): SummaryList = {
    val itemNumberSummaryListRow: Seq[SummaryListRow] = userAnswers.get(ChangeUnderpaymentReasonPage) match {
      case Some(reason) if reason.changed.itemNumber != 0 =>
        Seq(SummaryListRow(
          key = Key(
            content = Text(messages("confirmReason.itemNumber")),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            content = HtmlContent(reason.changed.itemNumber.toString)
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem(controllers.routes.ChangeItemNumberController.onLoad().url, Text(messages("confirmReason.change")))
            )
          ))
        )
        )
      case _ => Seq.empty
    }

    val originalAmountSummaryListRow: Seq[SummaryListRow] = userAnswers.get(ChangeUnderpaymentReasonPage) match {
      case Some(reason) =>
        Seq(
          SummaryListRow(
            key = Key(
              content = Text(messages("confirmReason.original")),
              classes = "govuk-!-width-two-thirds govuk-!-padding-bottom-0"
            ),
            value = Value(
              content = HtmlContent(reason.changed.original),
              classes = "govuk-!-padding-bottom-0"
            ),
            actions = Some(Actions(
              items = Seq(
                ActionItem(controllers.routes.ChangeUnderpaymentReasonDetailsController.onLoad(boxNumber).url, Text(messages("confirmReason.change")))
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
              content = HtmlContent(reason.changed.amended),
              classes = "govuk-!-padding-top-0"
            )
          )
        )
      case _ => Seq.empty
    }

    SummaryList(itemNumberSummaryListRow ++ originalAmountSummaryListRow)
  }

}
