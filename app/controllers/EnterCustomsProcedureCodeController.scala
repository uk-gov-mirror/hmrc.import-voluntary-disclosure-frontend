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

import com.google.inject.Inject
import config.AppConfig
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.EnterCustomsProcedureCodeFormProvider
import models.{EntryDetails, UnderpaymentType}
import pages.{EnterCustomsProcedureCodePage, EntryDetailsPage, UnderpaymentTypePage}
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.EnterCustomsProcedureCodeView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EnterCustomsProcedureCodeController @Inject()(identify: IdentifierAction,
                                                    getData: DataRetrievalAction,
                                                    requireData: DataRequiredAction,
                                                    appConfig: AppConfig,
                                                    sessionRepository: SessionRepository,
                                                    mcc: MessagesControllerComponents,
                                                    view: EnterCustomsProcedureCodeView,
                                                    formProvider: EnterCustomsProcedureCodeFormProvider
                                     )
  extends FrontendController(mcc) with I18nSupport {

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val form = request.userAnswers.get(EnterCustomsProcedureCodePage).fold(formProvider()) {
      formProvider().fill
    }
    Future.successful(Ok(view(form, backLink(request.userAnswers.get(EntryDetailsPage)))))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors,
        backLink(request.userAnswers.get(EntryDetailsPage))
      ))),
      value => {
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(EnterCustomsProcedureCodePage, value))
          _ <- sessionRepository.set(updatedAnswers)
        } yield {
          Redirect(controllers.routes.UnderpaymentTypeController.onLoad())
        }
      }
    )
  }

  private[controllers] def backLink(entryDetails: Option[EntryDetails]): Call =
    if (entryDetails.get.entryDate.isBefore(appConfig.euExitDate)) {
      Call("GET",controllers.routes.AcceptanceDateController.onLoad().url)
    } else {
      Call("GET",controllers.routes.EntryDetailsController.onLoad().url)
    }

}
