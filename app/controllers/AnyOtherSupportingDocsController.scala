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
import forms.AnyOtherSupportingDocsFormProvider
import javax.inject.{Inject, Singleton}
import pages.AnyOtherSupportingDocsPage
import play.api.i18n.I18nSupport
import play.api.libs.json.Format.GenericFormat
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.AnyOtherSupportingDocsView
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class AnyOtherSupportingDocsController @Inject()(identify: IdentifierAction,
                                                 getData: DataRetrievalAction,
                                                 requireData: DataRequiredAction,
                                                 sessionRepository: SessionRepository,
                                                 mcc: MessagesControllerComponents,
                                                 formProvider: AnyOtherSupportingDocsFormProvider,
                                                 view: AnyOtherSupportingDocsView)
  extends FrontendController(mcc) with I18nSupport {

  private lazy val backLink: Call = controllers.routes.SupportingDocController.onLoad()

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val form = request.userAnswers.get(AnyOtherSupportingDocsPage).fold(formProvider()) {
      formProvider().fill
    }
    Future.successful(Ok(view(form, backLink)))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors, backLink))),
      value =>
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(AnyOtherSupportingDocsPage, value))
          _ <- sessionRepository.set(updatedAnswers)
        } yield {
          if (value) {
            Redirect(controllers.routes.AnyOtherSupportingDocsController.onLoad())
          } else {
            Redirect(controllers.routes.UploadFileController.onLoad())
          }
        }
    )
  }
}
