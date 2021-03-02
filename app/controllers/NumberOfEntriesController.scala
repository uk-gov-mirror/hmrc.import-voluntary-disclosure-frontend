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
import forms.NumberOfEntriesFormProvider
import javax.inject.{Inject, Singleton}
import models.NumberOfEntries.{MoreThanOneEntry, OneEntry}
import models.{NumberOfEntries, UserAnswers}
import pages.NumberOfEntriesPage
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepository
import services.FlowService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.NumberOfEntriesView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class NumberOfEntriesController @Inject()(identify: IdentifierAction,
                                          getData: DataRetrievalAction,
                                          requireData: DataRequiredAction,
                                          sessionRepository: SessionRepository,
                                          appConfig: AppConfig,
                                          mcc: MessagesControllerComponents,
                                          flowService: FlowService,
                                          formProvider: NumberOfEntriesFormProvider,
                                          view: NumberOfEntriesView)
  extends FrontendController(mcc) with I18nSupport {

  implicit val config: AppConfig = appConfig

  val onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    val form = request.userAnswers.get(NumberOfEntriesPage).fold(formProvider()) {
      formProvider().fill
    }

    Future.successful(Ok(view(form, backLink(request.userAnswers))))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors, backLink(request.userAnswers)))),
      value => {
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(NumberOfEntriesPage, value))
          _ <- sessionRepository.set(updatedAnswers)
        } yield {
          redirect(value)
        }
      }
    )
  }

  private def redirect(entries: NumberOfEntries): Result = entries match {
    case OneEntry => Redirect(controllers.routes.EntryDetailsController.onLoad())
    case MoreThanOneEntry => Redirect(controllers.routes.NumberOfEntriesController.onLoad())
  }

  private[controllers] def backLink(userAnswers: UserAnswers): Call =
    (flowService.isRepFlow(userAnswers), flowService.doesImporterEORIExist(userAnswers)) match {
      case (true, true) => controllers.routes.ImporterEORINumberController.onLoad()
      case (true, false) => controllers.routes.ImporterEORIExistsController.onLoad()
      case _ => controllers.routes.UserTypeController.onLoad()
    }
}
