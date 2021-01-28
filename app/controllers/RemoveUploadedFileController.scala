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

import controllers.actions._
import forms.RemoveUploadedFileFormProvider
import javax.inject.Inject
import models.Index
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import queries.{FileUploadQuery, RemoveUploadedFileQuery}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.RemoveUploadedFileView

import scala.concurrent.{ExecutionContext, Future}

class RemoveUploadedFileController @Inject()(
                                              override val messagesApi: MessagesApi,
                                              sessionRepository: SessionRepository,
                                              identify: IdentifierAction,
                                              getData: DataRetrievalAction,
                                              requireData: DataRequiredAction,
                                              formProvider: RemoveUploadedFileFormProvider,
                                              mcc: MessagesControllerComponents,
                                              view: RemoveUploadedFileView
                                 )(implicit ec: ExecutionContext) extends FrontendController(mcc) with I18nSupport {


  def onLoad(index: Index): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      request.userAnswers.get(FileUploadQuery) match {
        case None => Future.successful(Redirect(controllers.routes.SupportingDocController.onLoad()))
        case Some(files) if(files.isEmpty) => Future.successful(Redirect(controllers.routes.SupportingDocController.onLoad()))
        case _ => Future.successful(Ok(view(formProvider(), index)))
      }
  }

  def onSubmit(index: Index): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
        formProvider().bindFromRequest().fold(
          formWithErrors =>
            Future.successful(BadRequest(view(formWithErrors, index))),
          value => {
            if (value) {
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.remove(RemoveUploadedFileQuery(index)))
                _ <- sessionRepository.set(updatedAnswers)
              } yield
                  Redirect(controllers.routes.UploadAnotherFileController.onLoad())
            } else {
              Future.successful(Redirect(controllers.routes.UploadAnotherFileController.onLoad()))
            }
          }
      )
  }
}
