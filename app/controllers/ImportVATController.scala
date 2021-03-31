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
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.ImportVATFormProvider
import models.UnderpaymentType
import pages.{ImportVATPage, UnderpaymentTypePage}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents, Result}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.ImportVATView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ImportVATController @Inject()(identify: IdentifierAction,
                                    getData: DataRetrievalAction,
                                    requireData: DataRequiredAction,
                                    sessionRepository: SessionRepository,
                                    mcc: MessagesControllerComponents,
                                    view: ImportVATView,
                                    formProvider: ImportVATFormProvider
                                     )
  extends FrontendController(mcc) with I18nSupport {

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val form = request.userAnswers.get(ImportVATPage).fold(formProvider()) {
      formProvider().fill
    }
    Future.successful(Ok(view(form, backLink(request.userAnswers.get(UnderpaymentTypePage)))))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors,
        backLink(request.userAnswers.get(UnderpaymentTypePage))
      ))),
      value => {
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(ImportVATPage, value))
          _ <- sessionRepository.set(updatedAnswers)
        } yield {
          redirect(request.userAnswers.get(UnderpaymentTypePage))
        }
      }
    )
  }

  private[controllers] def redirect(underpaymentType: Option[UnderpaymentType]): Result =
    underpaymentType match {
      case Some(UnderpaymentType(_, _, true)) => Redirect(controllers.routes.ExciseDutyController.onLoad())
      case _ => Redirect(controllers.underpayments.routes.UnderpaymentDetailSummaryController.onLoad())
    }

  private[controllers] def backLink(underpaymentType: Option[UnderpaymentType]): Call =
    underpaymentType match {
      case Some(UnderpaymentType(true, _, _)) => Call("GET",controllers.routes.CustomsDutyController.onLoad().url)
      case _ => Call("GET",controllers.routes.UnderpaymentTypeController.onLoad().url)
    }

}
