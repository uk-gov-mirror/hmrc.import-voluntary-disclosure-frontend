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
import models.UnderpaymentDetail
import pages.UnderpaymentReasonsPage
import pages.underpayments.UnderpaymentDetailSummaryPage
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
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
    val underpaymentDetails = request.userAnswers.get(UnderpaymentDetailSummaryPage)
    Future.successful(
      Ok(
        view(
          formProvider.apply(),
          summaryList(underpaymentDetails),
          amountOwedSummaryList(underpaymentDetails),
          underpaymentDetails.length
        )
      )
    )
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val underpaymentDetails = request.userAnswers.get(UnderpaymentDetailSummaryPage)
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(
        BadRequest(
          view(
            formWithErrors,
            summaryList(underpaymentDetails),
            amountOwedSummaryList(underpaymentDetails),
            underpaymentDetails.length
          )
        )
      ),
      value => {
        if (value) {
          Future.successful(Redirect(controllers.underpayments.routes.UnderpaymentTypeController.onLoad()))
        } else {
          Future.successful(Redirect(controllers.routes.BoxGuidanceController.onLoad()))
        }
      }
    )
  }

  private[controllers] def summaryList(underpaymentDetail: Option[Seq[UnderpaymentDetail]]
                                      )(implicit messages: Messages): Option[SummaryList] = {
    val changeAction: Call = controllers.underpayments.routes.UnderpaymentDetailSummaryController.onLoad()
    underpaymentDetail.map { underpayment =>
      SummaryList(
        rows = for (underpayment <- underpayment) yield
          SummaryListRow(
            key = Key(
              content = Text(messages(s"underpaymentDetailsSummary.${underpayment.duty}"))
            ),
            value = Value(
              content = HtmlContent(displayMoney(underpayment.amended - underpayment.original))
            ),
            actions = Some(
              Actions(
                items = Seq(
                  ActionItem(
                    changeAction.url,
                    Text(messages("common.change")),
                    Some("key")
                  )
                ),
                classes = "govuk-!-width-one-third"
              )
            )
          )
      )
    }
  }

  def amountOwedSummaryList(underpaymentDetail: Option[Seq[UnderpaymentDetail]])(implicit messages: Messages): Option[SummaryList] = {
    Some(
      SummaryList(
        rows = Seq(SummaryListRow(
          key = Key(
            content = Text(
              messages(s"underpaymentDetailsSummary.owedToHMRC")
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
