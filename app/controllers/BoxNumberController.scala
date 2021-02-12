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
import forms.BoxNumberFormProvider
import pages.UnderpaymentReasonBoxNumberPage
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.BoxNumberView

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class BoxNumberController @Inject()(identity: IdentifierAction,
                                    getData: DataRetrievalAction,
                                    requireData: DataRequiredAction,
                                    sessionRepository: SessionRepository,
                                    mcc: MessagesControllerComponents,
                                    formProvider: BoxNumberFormProvider,
                                    view: BoxNumberView,
                                    appConfig: AppConfig
                                   )
  extends FrontendController(mcc) with I18nSupport {

  private lazy val backLink: Call = controllers.routes.BoxGuidanceController.onLoad()

  def onLoad: Action[AnyContent] = (identity andThen getData andThen requireData).async { implicit request =>
    val form = request.userAnswers.get(UnderpaymentReasonBoxNumberPage).fold(formProvider()) {
      formProvider().fill
    }
    Future.successful(Ok(view(form, backLink)))
  }

  def onSubmit: Action[AnyContent] = (identity andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors, backLink))),
      value => {
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(UnderpaymentReasonBoxNumberPage, value))
          _ <- sessionRepository.set(updatedAnswers)
        } yield {
          appConfig.boxNumberItems.getOrElse(value, "notValid") match {
            case "item" => Redirect(controllers.routes.BoxNumberController.onLoad())
            case "entry" => Redirect(controllers.routes.BoxNumberController.onLoad())
            case _ => BadRequest(view(formProvider(), backLink))
          }
        }
      }
    )
  }

}
