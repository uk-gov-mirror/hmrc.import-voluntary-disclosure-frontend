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
import forms.underpayments.UnderpaymentDetailSummaryFormProvider
import models.underpayments.UnderpaymentDetail
import pages.underpayments.UnderpaymentDetailSummaryPage
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
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
                                                    mcc: MessagesControllerComponents,
                                                    view: UnderpaymentDetailSummaryView,
                                                    formProvider: UnderpaymentDetailSummaryFormProvider
                                                   )
  extends FrontendController(mcc) with I18nSupport {

  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    val fallbackResponse = Redirect(controllers.underpayments.routes.UnderpaymentStartController.onLoad())

    val result = request.userAnswers.get(UnderpaymentDetailSummaryPage).map {
      case Nil => fallbackResponse
      case underpayments => Ok(
        view(formProvider(), summaryList(underpayments), amountOwedSummaryList(underpayments), underpayments.length)
      )
    }.getOrElse(fallbackResponse)

    Future.successful(result)
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val result = formProvider().bindFromRequest().fold(
      formWithErrors => {
        val underpayments = request.userAnswers.get(UnderpaymentDetailSummaryPage).getOrElse(Seq.empty)
        BadRequest(view(formWithErrors, summaryList(underpayments), amountOwedSummaryList(underpayments), underpayments.length))
      },
      value => {
        if (value) {
          Redirect(controllers.underpayments.routes.UnderpaymentTypeController.onLoad())
        } else {
          Redirect(controllers.routes.BoxGuidanceController.onLoad())
        }
      }
    )

    Future.successful(result)
  }

  private[controllers] def summaryList(underpaymentDetail: Seq[UnderpaymentDetail])
                                      (implicit messages: Messages): SummaryList = {
    SummaryList(
      rows = for (underpayment <- underpaymentDetail.reverse) yield
        SummaryListRow(
          key = Key(
            content = Text(messages(s"underpaymentDetailsSummary.${underpayment.duty}")),
            classes = "govuk-summary-list__key govuk-!-width-two-thirds"
          ),
          value = Value(
            content = HtmlContent(displayMoney(underpayment.amended - underpayment.original)),
            classes = "govuk-summary-list__value"
          ),
          actions = Some(
            Actions(items = Seq(ActionItem(controllers.underpayments.routes.ChangeUnderpaymentDetailsController.onLoad(underpayment.duty).url, Text(messages("common.change")), Some("key"))))
          )
        )
    )
  }

  def amountOwedSummaryList(underpaymentDetail: Seq[UnderpaymentDetail])(implicit messages: Messages): SummaryList = {
    val amountOwed = underpaymentDetail.map(underpayment => underpayment.amended - underpayment.original).sum

    SummaryList(
      rows = Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages(s"underpaymentDetailsSummary.owedToHMRC")),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(content = HtmlContent(displayMoney(amountOwed))),
          classes = "govuk-summary-list__row--no-border"
        )
      )
    )
  }

}
