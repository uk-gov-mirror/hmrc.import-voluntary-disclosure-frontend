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
import forms.SplitPaymentFormProvider

import javax.inject.{Inject, Singleton}
import pages.{AdditionalDefermentNumberPage, DefermentAccountPage, DefermentTypePage, SplitPaymentPage, UploadAuthorityPage}
import play.api.i18n.I18nSupport
import play.api.libs.json.Format.GenericFormat
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.SplitPaymentView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


@Singleton
class SplitPaymentController @Inject()(identify: IdentifierAction,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       sessionRepository: SessionRepository,
                                       mcc: MessagesControllerComponents,
                                       formProvider: SplitPaymentFormProvider,
                                       view: SplitPaymentView)
  extends FrontendController(mcc) with I18nSupport {

  lazy val backLink: Call = controllers.routes.DefermentController.onLoad()

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val form = request.userAnswers.get(SplitPaymentPage).fold(formProvider()) {
      formProvider().fill
    }
    Future.successful(Ok(view(form, backLink)))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(
        BadRequest(
          view(
            formWithErrors,
            backLink)
        )
      ),
      value => {
        request.userAnswers.get(SplitPaymentPage) match {
          case Some(oldValue) if oldValue != value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(SplitPaymentPage, value))
              updatedAnswers <- Future.fromTry(updatedAnswers.remove(DefermentTypePage))
              updatedAnswers <- Future.fromTry(updatedAnswers.remove(DefermentAccountPage))
              updatedAnswers <- Future.fromTry(updatedAnswers.remove(AdditionalDefermentNumberPage))
              updatedAnswers <- Future.fromTry(updatedAnswers.remove(UploadAuthorityPage))
              _ <- sessionRepository.set(updatedAnswers)
            } yield {
              redirectTo(value)
            }
          case _ =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(SplitPaymentPage, value))
              _ <- sessionRepository.set(updatedAnswers)
            } yield {
              redirectTo(value)
            }
        }
      }
    )
  }

  def redirectTo(value: Boolean): Result = {
    if (value) {
      Redirect(controllers.routes.RepresentativeDanDutyController.onLoad())
    } else {
      Redirect(controllers.routes.RepresentativeDanController.onLoad())
    }
  }

}
