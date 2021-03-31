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
import models.{ChangeUnderpaymentReason, UnderpaymentReason}
import pages.{ChangeUnderpaymentReasonPage, UnderpaymentReasonsPage}
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.ChangeUnderpaymentReasonView
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

class ChangeUnderpaymentReasonController @Inject()(identify: IdentifierAction,
                                                   getData: DataRetrievalAction,
                                                   requireData: DataRequiredAction,
                                                   sessionRepository: SessionRepository,
                                                   mcc: MessagesControllerComponents,
                                                   view: ChangeUnderpaymentReasonView,
                                                   formProvider: UnderpaymentReasonSummaryFormProvider)
  extends FrontendController(mcc) with I18nSupport {

  private lazy val backLink: Call = controllers.routes.UnderpaymentReasonSummaryController.onLoad()

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    request.userAnswers.get(ChangeUnderpaymentReasonPage) match {
      case Some(reason) =>
        summaryList(reason.original) match {
          case Some(summaryLists) => Future.successful(Ok(view(formProvider.apply(), backLink, summaryLists, reason.original.boxNumber)))
          case None => Future.successful(InternalServerError("Couldn't find Underpayment reasons"))
        }
      case _ => Future.successful(InternalServerError("No change underpayment reasons found"))
    }
  }

  def change(boxNumber: Int, itemNumber: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    request.userAnswers.get(UnderpaymentReasonsPage) match {
      case Some(reasons) => {
        val originalReason = reasons.filter(x => x.boxNumber == boxNumber && x.itemNumber == itemNumber).head
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(ChangeUnderpaymentReasonPage, ChangeUnderpaymentReason(originalReason, originalReason)))
          _ <- sessionRepository.set(updatedAnswers)
        } yield {
          Redirect(controllers.routes.ChangeUnderpaymentReasonController.onLoad())
        }
      }
      case _ => Future.successful(InternalServerError("No underpayment reason list found"))
    }
  }

  def summaryList(underpaymentReason: UnderpaymentReason)(implicit messages: Messages): Option[Seq[SummaryList]] = {

    def itemNumberSummaryListRow: Seq[SummaryListRow] = {
        if (underpaymentReason.itemNumber != 0) {
          Seq(
            SummaryListRow(
              key = Key(
                content = Text(messages("confirmReason.itemNumber"))
              ),
              value = Value(
                content = HtmlContent(underpaymentReason.itemNumber.toString)
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

    val originalAmountSummaryListRow: Seq[SummaryListRow] = Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("confirmReason.original")),
            classes = "govuk-!-padding-bottom-0"
          ),
          value = Value(
            content = HtmlContent(underpaymentReason.original),
            classes = "govuk-!-padding-bottom-0"
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem(controllers.routes.UnderpaymentReasonSummaryController.onLoad().url, Text(messages("confirmReason.change")))
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
            content = HtmlContent(underpaymentReason.amended),
            classes = "govuk-!-padding-top-0"
          )
        )
      )

    val rows = itemNumberSummaryListRow ++ originalAmountSummaryListRow

    if (rows.nonEmpty) {
      Some(Seq(SummaryList(rows)))
    } else {
      None
    }
  }


}
