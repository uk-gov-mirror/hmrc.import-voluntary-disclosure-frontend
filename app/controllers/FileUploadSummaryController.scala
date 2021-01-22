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
import javax.inject.Inject
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import queries.{FileUploadJsonQuery}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import viewmodels.AddFileNameRowHelper
import views.html.FileUploadSummaryView

import scala.concurrent.{ExecutionContext, Future}

class FileUploadSummaryController @Inject()(identify: IdentifierAction,
                                            getData: DataRetrievalAction,
                                            requireData: DataRequiredAction,
                                            sessionRepository: SessionRepository,
                                            mcc: MessagesControllerComponents,
                                            view: FileUploadSummaryView)(implicit ec: ExecutionContext)

  extends FrontendController(mcc) with I18nSupport {

  val onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async {
      implicit request =>
        //TODO - Redirect at line 43 to be defined, will redirect back to the upload a file page
        request.userAnswers.get(FileUploadJsonQuery).fold(Future(Redirect(""))) { possibleFiles =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(FileUploadJsonQuery, possibleFiles))
            _ <- sessionRepository.set(updatedAnswers)
          } yield {
            val helper = new AddFileNameRowHelper(updatedAnswers)
            val rows = helper.rows

            Ok(view(rows))
          }
      }
  }

  private[controllers] def backLink: Call = Call("GET",controllers.routes.EnterCustomsProcedureCodeController.onLoad().url)

}
