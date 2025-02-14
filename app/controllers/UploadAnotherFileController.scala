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
import forms.UploadAnotherFileFormProvider

import javax.inject.Inject
import models.requests.DataRequest
import pages.FileUploadPage
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import viewmodels.AddFileNameRowHelper
import views.html.UploadAnotherFileView

import scala.concurrent.{ExecutionContext, Future}

class UploadAnotherFileController @Inject()(identify: IdentifierAction,
                                            getData: DataRetrievalAction,
                                            requireData: DataRequiredAction,
                                            mcc: MessagesControllerComponents,
                                            formProvider: UploadAnotherFileFormProvider,
                                            view: UploadAnotherFileView)(implicit ec: ExecutionContext)

  extends FrontendController(mcc) with I18nSupport {


  val onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async {
      implicit request =>
        request.userAnswers.get(FileUploadPage).fold(Future(Redirect(controllers.routes.SupportingDocController.onLoad().url))) { files =>
            val helper = new AddFileNameRowHelper(files)
            if(files.isEmpty) {
              Future.successful(Redirect(controllers.routes.UploadFileController.onLoad()))
            } else {
              Future.successful(Ok(view(formProvider(), helper.rows)))
            }
      }
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => resultWithErrors(formWithErrors),
      value => {
          if (value) {
            Future.successful(Redirect(controllers.routes.UploadFileController.onLoad()))
          } else {
            Future.successful(Redirect(controllers.routes.DeclarantContactDetailsController.onLoad()))
          }
      }
    )
  }

  private def resultWithErrors(formWithErrors: Form[Boolean])(implicit request: DataRequest[AnyContent]): Future[Result] = {
    request.userAnswers.get(FileUploadPage).fold(Future(Redirect(controllers.routes.UploadFileController.onLoad().url))) { files =>
      val helper = new AddFileNameRowHelper(files)

      Future.successful(BadRequest(view(formWithErrors, helper.rows)))
    }
  }
}
