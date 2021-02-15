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

import config.AppConfig
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.UnderpaymentReasonAmendmentFormProvider
import models.BoxType
import pages.{UnderpaymentReasonAmendmentPage, UnderpaymentReasonBoxNumberPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents, Request}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.{CommodityAmendmentView, TextAmendmentView}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class UnderpaymentReasonAmendmentController @Inject()(identity: IdentifierAction,
                                                      getData: DataRetrievalAction,
                                                      requireData: DataRequiredAction,
                                                      sessionRepository: SessionRepository,
                                                      mcc: MessagesControllerComponents,
                                                      formProvider: UnderpaymentReasonAmendmentFormProvider,
                                                      textAmendmentView: TextAmendmentView,
                                                      commodityAmendmentView: CommodityAmendmentView,
                                                      appConfig: AppConfig
                                   )
  extends FrontendController(mcc) with I18nSupport {

  private[controllers] def backLink(boxNumber: Int): Call = {
    appConfig.boxNumberTypes.getOrElse(boxNumber, appConfig.invalidBox).boxLevel match {
      case "item" => controllers.routes.BoxNumberController.onLoad()
      case _ => controllers.routes.BoxNumberController.onLoad()
    }
  }

  def onLoad: Action[AnyContent] = (identity andThen getData andThen requireData).async { implicit request =>
    val boxNumber = request.userAnswers.get(UnderpaymentReasonBoxNumberPage).getOrElse(0)

    val form = request.userAnswers.get(UnderpaymentReasonAmendmentPage).fold(formProvider()) {
      formProvider().fill
    }

    Future.successful(routeToView(boxNumber, form))
  }

  def onSubmit: Action[AnyContent] = (identity andThen getData andThen requireData).async { implicit request =>
    val boxNumber = request.userAnswers.get(UnderpaymentReasonBoxNumberPage).getOrElse(0)
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(routeToView(boxNumber, formWithErrors)),
      value => {
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(UnderpaymentReasonAmendmentPage, value))
          _ <- sessionRepository.set(updatedAnswers)
        } yield {
          Redirect(controllers.routes.UnderpaymentReasonAmendmentController.onLoad())
        }
      }
    )
  }

  def routeToView(boxNumber: Int, form: Form[_])(implicit request: Request[_], messages: Messages) = {
    appConfig.boxNumberTypes.getOrElse(boxNumber, appConfig.invalidBox) match {
      case box if(box.boxType.equals("commodity")) => Ok(commodityAmendmentView(form, box, backLink(boxNumber))(request, messages))
      case box if(box.boxType.equals("text")) => Ok(textAmendmentView(form, box, backLink(boxNumber))(request, messages))
      case box => Ok(textAmendmentView(form, box, backLink(boxNumber))(request, messages))
    }
  }

}
