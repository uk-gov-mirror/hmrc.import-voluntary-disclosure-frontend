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
import models.UnderpaymentReason
import pages.UnderpaymentReasonsPage
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.UnderpaymentReasonSummaryView

import javax.inject.Inject
import scala.concurrent.Future

class UnderpaymentReasonSummaryController @Inject()(identify: IdentifierAction,
                                                    getData: DataRetrievalAction,
                                                    requireData: DataRequiredAction,
                                                    mcc: MessagesControllerComponents,
                                                    view: UnderpaymentReasonSummaryView,
                                                    formProvider: UnderpaymentReasonSummaryFormProvider
                                                   )
  extends FrontendController(mcc) with I18nSupport {

  private lazy val backLink: Call = controllers.routes.BoxGuidanceController.onLoad()

  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    summaryList(request.userAnswers.get(UnderpaymentReasonsPage)) match {
      case Some(value) => Future.successful(Ok(view(formProvider.apply(), backLink, Some(value))))
      case None => Future.successful(InternalServerError("Couldn't find Underpayment reasons"))
    }
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(
        BadRequest(
          view(
            formWithErrors,
            backLink,
            summaryList(request.userAnswers.get(UnderpaymentReasonsPage))
          )
        )
      ),
      anotherReason => {
        if (anotherReason) {
          Future.successful(Redirect(controllers.routes.BoxNumberController.onLoad()))
        } else {
          Future.successful(Redirect(controllers.routes.UploadFileController.onLoad()))
        }
      }
    )
  }

  private[controllers] def summaryList(underpaymentReason: Option[Seq[UnderpaymentReason]]
                                      )(implicit messages: Messages): Option[SummaryList] = {
    val changeAction: Call = controllers.routes.UnderpaymentReasonSummaryController.onLoad()
    underpaymentReason.map { reasons =>
      val sortedReasons = reasons.sortBy(item => item.boxNumber)
      SummaryList(
        rows = for (underpayment <- sortedReasons) yield
          SummaryListRow(
            key = Key(
              content = Text("Box " + underpayment.boxNumber)
            ),
            value = Value(
              content = if (underpayment.itemNumber == 0) {
                HtmlContent("Entry level")
              } else {
                HtmlContent("Item " + underpayment.itemNumber)
              }
            ),
            actions = Some(
              Actions(
                items = Seq(
                  ActionItem(
                    changeAction.url,
                    Text(messages("common.change")),
                    Some("key")
                  ),
                  ActionItem(
                    changeAction.url,
                    Text(messages("common.remove")),
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

}
