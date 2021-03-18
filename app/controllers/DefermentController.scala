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
import forms.DefermentFormProvider
import javax.inject.{Inject, Singleton}
import models.{UnderpaymentType, UserAnswers}
import pages.DefermentPage
import play.api.i18n.I18nSupport
import play.api.libs.json.Format.GenericFormat
import play.api.mvc._
import repositories.SessionRepository
import services.FlowService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.DefermentView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


@Singleton
class DefermentController @Inject()(identify: IdentifierAction,
                                    getData: DataRetrievalAction,
                                    requireData: DataRequiredAction,
                                    sessionRepository: SessionRepository,
                                    mcc: MessagesControllerComponents,
                                    formProvider: DefermentFormProvider,
                                    flowService: FlowService,
                                    view: DefermentView)
  extends FrontendController(mcc) with I18nSupport {

  lazy val backLink: Call = controllers.routes.TraderAddressCorrectController.onLoad()

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val form = request.userAnswers.get(DefermentPage).fold(formProvider()) {
      formProvider().fill
    }
    Future.successful(Ok(view(form, backLink, getHeaderMessage(request.userAnswers), UnderpaymentType.options(form))))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(
        BadRequest(
          view(
            formWithErrors,
            backLink,
            getHeaderMessage(request.userAnswers),
            UnderpaymentType.options(formWithErrors)
          )
        )
      ),
      value => {
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(DefermentPage, value))
          _ <- sessionRepository.set(updatedAnswers)
        } yield {
          if (value) {
            redirectToSplitPayment(request.userAnswers)
          } else {
            Redirect(controllers.routes.CheckYourAnswersController.onLoad())
          }
        }
      }
    )
  }

  private[controllers] def redirectToSplitPayment(userAnswers: UserAnswers): Result = {
    if (flowService.isRepFlow(userAnswers)) {
      flowService.dutyType(userAnswers) match {
        case underpaymentType if underpaymentType == "vat" => Redirect(controllers.routes.SplitPaymentController.onLoad()) //TODO - routing to be updated as part of CIVDT-264
        case underpaymentType if underpaymentType == "duty" => Redirect(controllers.routes.SplitPaymentController.onLoad()) //TODO - routing to be updated as part of CIVDT-264
        case underpaymentType if underpaymentType == "both" => Redirect(controllers.routes.SplitPaymentController.onLoad())
        case _ => InternalServerError("Couldn't find Underpayment types")
      }
    } else {
      Redirect(controllers.routes.ImporterDanController.onLoad())
    }
  }

  private[controllers] def getHeaderMessage(userAnswers: UserAnswers): String = {
    flowService.dutyType(userAnswers) match {
        case "vat" => "deferment.headingOnlyVAT"
        case "duty" => "deferment.headingDutyOnly"
        case "both" => "deferment.headingVATandDuty"
      }
    }

}
