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
import pages.{UnderpaymentReasonAmendmentPage, UnderpaymentReasonBoxNumberPage, UnderpaymentReasonItemNumberPage}
import play.api.data.{Form, FormError}
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents, Request}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.TextAmendmentView

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
                                                      implicit val appConfig: AppConfig
                                   )
  extends FrontendController(mcc) with I18nSupport {

  private[controllers] def backLink(boxNumber: Int): Call = {
    appConfig.boxNumberTypes.getOrElse(boxNumber, appConfig.invalidBox).boxLevel match {
      case "item" => controllers.routes.ItemNumberController.onLoad()
      case _ => controllers.routes.BoxNumberController.onLoad()
    }
  }

  def onLoad(boxNumber: Int): Action[AnyContent] = (identity andThen getData andThen requireData).async { implicit request =>
    val itemNumber = request.userAnswers.get(UnderpaymentReasonItemNumberPage).getOrElse(0)

    val form = request.userAnswers.get(UnderpaymentReasonAmendmentPage).fold(formProvider(boxNumber)) {
      formProvider(boxNumber).fill
    }

    Future.successful(routeToView(boxNumber, itemNumber, form))
  }

  def onSubmit(boxNumber: Int): Action[AnyContent] = (identity andThen getData andThen requireData).async { implicit request =>
    val itemNumber = request.userAnswers.get(UnderpaymentReasonItemNumberPage).getOrElse(0)
    formProvider(boxNumber).bindFromRequest().fold(
      formWithErrors => {
        val newErrors = formWithErrors.errors.map { error =>
          if (error.key.isEmpty) {FormError("amended", error.message)} else {error}
        }
        Future.successful(routeToView(boxNumber, itemNumber, formWithErrors.copy(errors = newErrors)))
      },
      value => {
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(UnderpaymentReasonAmendmentPage, value))
          _ <- sessionRepository.set(updatedAnswers)
        } yield {
          Redirect(controllers.routes.UnderpaymentReasonAmendmentController.onLoad(boxNumber))
        }
      }
    )
  }

  def routeToView(boxNumber: Int, itemNumber: Int, form: Form[_])(implicit request: Request[_], messages: Messages) = {
    appConfig.boxNumberTypes.getOrElse(boxNumber, appConfig.invalidBox) match {
      case box if(box.boxType.equals("text")) => Ok(textAmendmentView(form, box, itemNumber, backLink(boxNumber))(request, messages))
      case box => Ok(textAmendmentView(form, box, itemNumber, backLink(boxNumber))(request, messages))
    }
  }

}
