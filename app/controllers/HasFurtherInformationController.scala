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
import forms.HasFurtherInformationFormProvider
import pages.{FurtherInformationPage, HasFurtherInformationPage}
import play.api.i18n.I18nSupport
import play.api.libs.json.Format.GenericFormat
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.HasFurtherInformationView

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


@Singleton
class HasFurtherInformationController @Inject()(identify: IdentifierAction,
                                                getData: DataRetrievalAction,
                                                requireData: DataRequiredAction,
                                                sessionRepository: SessionRepository,
                                                mcc: MessagesControllerComponents,
                                                formProvider: HasFurtherInformationFormProvider,
                                                view: HasFurtherInformationView)
  extends FrontendController(mcc) with I18nSupport {

  private lazy val backLink: Call = controllers.routes.HasFurtherInformationController.onLoad

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val form = request.userAnswers.get(HasFurtherInformationPage).fold(formProvider()) {
      formProvider().fill
    }
    Future.successful(Ok(view(form, backLink)))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors, backLink))),
      hasFurtherInfo => {
        if (hasFurtherInfo) {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(HasFurtherInformationPage, hasFurtherInfo))
            _ <- sessionRepository.set(updatedAnswers)
          } yield {
            Redirect(controllers.routes.HasFurtherInformationController.onLoad()) // Further Info page
          }
        } else {
          for {
            hasFurtherInfoAnswers <- Future.fromTry(request.userAnswers.set(HasFurtherInformationPage, hasFurtherInfo))
            updatedAnswers <- Future.fromTry(hasFurtherInfoAnswers.set(FurtherInformationPage, " "))
            _ <- sessionRepository.set(updatedAnswers)
          } yield {
            Redirect(controllers.routes.UploadFileController.onLoad())
          }
        }
      }
    )
  }

}
