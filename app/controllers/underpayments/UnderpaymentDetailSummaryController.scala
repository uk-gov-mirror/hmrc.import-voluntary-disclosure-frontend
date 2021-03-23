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

package controllers.underpayments

import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import models.underpayments.UnderpaymentAmount
import pages.underpayments.UnderpaymentDetailsPage
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.ViewUtils.displayMoney
import views.html.underpayments.UnderpaymentDetailSummaryView

import javax.inject.Inject
import scala.concurrent.Future

class UnderpaymentDetailSummaryController @Inject()(identify: IdentifierAction,
                                                    getData: DataRetrievalAction,
                                                    requireData: DataRequiredAction,
                                                    sessionRepository: SessionRepository,
                                                    mcc: MessagesControllerComponents,
                                                    view: UnderpaymentDetailSummaryView)
  extends FrontendController(mcc) with I18nSupport {

  def onLoad(underpaymentType: String): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val underpaymentDetail = request.userAnswers.get(UnderpaymentDetailsPage).getOrElse(UnderpaymentAmount(0, 0))
    Future.successful(Ok(view(underpaymentType, summaryList(underpaymentType, underpaymentDetail))))
  }

  def onSubmit(underpaymentType: String): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    ???

  }

  private[controllers] def summaryList(underpaymentType: String, underpaymentAmount: UnderpaymentAmount)(implicit messages: Messages): SummaryList = {
    SummaryList(
      classes = "govuk-!-margin-bottom-9",
      rows = Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("underpaymentDetailsSummary.originalAmount")),
            classes = "govuk-!-width-two-thirds govuk-!-padding-bottom-0"
          ),
          value = Value(
            content = HtmlContent(displayMoney(underpaymentAmount.original)),
            classes = "govuk-!-padding-bottom-0"
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem(
                controllers.underpayments.routes.UnderpaymentDetailsController.onLoad(underpaymentType).url,
                Text(messages("underpaymentSummary.change"))
              )
            ),
            classes = "govuk-!-padding-bottom-0")
          ),
          classes = "govuk-summary-list__row--no-border"
        ),
        SummaryListRow(
          key = Key(
            content = Text(messages("underpaymentDetailsSummary.amendedAmount")),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
          ),
          value = Value(
            content = HtmlContent(displayMoney(underpaymentAmount.amended)),
            classes = "govuk-!-padding-top-0"
          )
        ),
        SummaryListRow(
          key = Key(
            content = Text(
              messages(s"underpaymentDetails.$underpaymentType.pageHeader") + " " +
                messages("underpaymentDetailsSummary.dueToHmrc")),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(content = HtmlContent(displayMoney(underpaymentAmount.amended - underpaymentAmount.original))),
          classes = "govuk-summary-list__row--no-border"
        )
      )
    )
  }

}
