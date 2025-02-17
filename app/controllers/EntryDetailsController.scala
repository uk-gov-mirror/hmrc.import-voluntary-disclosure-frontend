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
import forms.EntryDetailsFormProvider
import javax.inject.{Inject, Singleton}
import models.EntryDetails
import pages.EntryDetailsPage
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.EntryDetailsView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class EntryDetailsController @Inject()(identify: IdentifierAction,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       sessionRepository: SessionRepository,
                                       appConfig: AppConfig,
                                       mcc: MessagesControllerComponents,
                                       formProvider: EntryDetailsFormProvider,
                                       view: EntryDetailsView)
  extends FrontendController(mcc) with I18nSupport {

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    val form = request.userAnswers.get(EntryDetailsPage).fold(formProvider()) {
      formProvider().fill
    }

    Future.successful(Ok(view(form)))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors))),
      value => {
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(EntryDetailsPage, value))
          _ <- sessionRepository.set(updatedAnswers)
        } yield {
          redirect(value)
        }
      }
    )
  }

  private def redirect(entryDetails: EntryDetails): Result =
    if (entryDetails.entryDate.isBefore(appConfig.euExitDate)) {
      Redirect(controllers.routes.AcceptanceDateController.onLoad())
    } else {
      Redirect(controllers.routes.EnterCustomsProcedureCodeController.onLoad())
    }


}
