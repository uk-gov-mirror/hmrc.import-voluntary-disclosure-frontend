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
import models.underpayments.UnderpaymentAmount

import javax.inject.{Inject, Singleton}
import pages._
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc._
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.ViewUtils.displayMoney
import views.html.UnderpaymentSummaryView

import scala.concurrent.Future

@Singleton
class UnderpaymentSummaryController @Inject()(identify: IdentifierAction,
                                              getData: DataRetrievalAction,
                                              requireData: DataRequiredAction,
                                              mcc: MessagesControllerComponents,
                                              view: UnderpaymentSummaryView)
  extends FrontendController(mcc) with I18nSupport {

  val onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    val customsDuty = request.userAnswers.get(CustomsDutyPage).map(
      summaryList(_, Messages("underpaymentSummary.customsDuty.title"), controllers.routes.CustomsDutyController.onLoad))

    val importVat = request.userAnswers.get(ImportVATPage).map(
      summaryList(_, Messages("underpaymentSummary.importVat.title"), controllers.routes.ImportVATController.onLoad))

    val exciseDuty = request.userAnswers.get(ExciseDutyPage).map(
      summaryList(_, Messages("underpaymentSummary.exciseDuty.title"), controllers.routes.ExciseDutyController.onLoad))

    Future.successful(Ok(view(customsDuty, importVat, exciseDuty, controllers.routes.UnderpaymentTypeController.onLoad)))
  }

  private[controllers] def summaryList(underpayment: UnderpaymentAmount, key: String, changeAction: Call)(implicit messages: Messages): SummaryList = {
    SummaryList(
      classes = "govuk-!-margin-bottom-9",
      rows = Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("underpaymentSummary.originalAmount")),
            classes = "govuk-!-width-two-thirds govuk-!-padding-bottom-0"
          ),
          value = Value(
            content = HtmlContent(displayMoney(underpayment.original)),
            classes = "govuk-!-padding-bottom-0"
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem(changeAction.url, Text(messages("underpaymentSummary.change")), Some(key))
            ),
            classes = "govuk-!-padding-bottom-0")
          ),
          classes = "govuk-summary-list__row--no-border"
        ),
        SummaryListRow(
          key = Key(
            content = Text(messages("underpaymentSummary.amendedAmount")),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
          ),
          value = Value(
            content = HtmlContent(displayMoney(underpayment.amended)),
            classes = "govuk-!-padding-top-0"
          )
        ),
        SummaryListRow(
          key = Key(
            content = Text(messages("underpaymentSummary.dueToHmrc", key)),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(content = HtmlContent(displayMoney(underpayment.amended - underpayment.original)))
        )
      )
    )
  }

}
