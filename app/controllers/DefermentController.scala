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
import forms.{DefermentFormProvider, UserTypeFormProvider}
import models.UserAnswers
import pages.{DefermentPage, UserTypePage}
import play.api.i18n.I18nSupport
import play.api.libs.json.Format.GenericFormat
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.{DefermentView, UserTypeView}
import javax.inject.{Inject, Singleton}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


@Singleton
class DefermentController @Inject()(identify: IdentifierAction,
                                    getData: DataRetrievalAction,
                                    requireData: DataRequiredAction,
                                    sessionRepository: SessionRepository,
                                    mcc: MessagesControllerComponents,
                                    formProvider: DefermentFormProvider,
                                    view: DefermentView)
  extends FrontendController(mcc) with I18nSupport {

  val onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    val form = request.userAnswers.get(DefermentPage).fold(formProvider()) {
      formProvider().fill
    }
    Future.successful(Ok(view(form, backLink)))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors,
        backLink
      ))),
      value => {
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(DefermentPage, value))
          _ <- sessionRepository.set(updatedAnswers)
        }  yield {
          if (value) {
            Redirect(controllers.routes.DefermentController.onLoad())
          } else {
            Redirect(controllers.routes.DefermentController.onLoad())
          }
        }
      }
    )
  }

  private[controllers] def backLink: Call = Call("GET",controllers.routes.UnderpaymentSummaryController.onLoad().url)

}
