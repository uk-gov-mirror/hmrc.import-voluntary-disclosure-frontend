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
import forms.RemoveUnderpaymentReasonFormProvider
import javax.inject.{Inject, Singleton}
import models.{UnderpaymentReason, UserAnswers}
import pages.{ChangeUnderpaymentReasonPage, UnderpaymentReasonsPage}
import play.api.i18n.I18nSupport
import play.api.libs.json.Format.GenericFormat
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.RemoveUnderpaymentReasonView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


@Singleton
class RemoveUnderpaymentReasonController @Inject()(identify: IdentifierAction,
                                                   getData: DataRetrievalAction,
                                                   requireData: DataRequiredAction,
                                                   sessionRepository: SessionRepository,
                                                   mcc: MessagesControllerComponents,
                                                   formProvider: RemoveUnderpaymentReasonFormProvider,
                                                   view: RemoveUnderpaymentReasonView)
  extends FrontendController(mcc) with I18nSupport {

  lazy val backLink: Call = controllers.routes.ChangeUnderpaymentReasonController.onLoad()

  private def getChangeReason(userAnswers: UserAnswers): UnderpaymentReason = {
    userAnswers.get(ChangeUnderpaymentReasonPage) match {
      case Some(reason) => reason.original
      case _ => throw new RuntimeException("No change reason found for remove")
    }
  }

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val changeReason = getChangeReason(request.userAnswers)
    Future.successful(Ok(view(formProvider(), backLink, changeReason.boxNumber, changeReason.itemNumber)))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val changeReason = getChangeReason(request.userAnswers)
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(
        BadRequest(
          view(
            formWithErrors,
            backLink,
            changeReason.boxNumber,
            changeReason.itemNumber)
        )
      ),
      value => {
        if (value) {
          val newReasonsOpt = for {
            allReasons <- request.userAnswers.get(UnderpaymentReasonsPage)
            removeReason <- request.userAnswers.get(ChangeUnderpaymentReasonPage)
          } yield {
            allReasons.filterNot(x => x == removeReason.original)
          }

          newReasonsOpt match {
            case Some(newReasons) =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(UnderpaymentReasonsPage, newReasons))
                _ <- sessionRepository.set(updatedAnswers)
              } yield {
                if (newReasons.isEmpty) {
                  Redirect(controllers.routes.BoxGuidanceController.onLoad())
                } else {
                  Redirect(controllers.routes.UnderpaymentReasonSummaryController.onLoad())
                }
              }
            case _ => Future.successful(InternalServerError("Invalid sequence of reasons"))
          }
        } else {
          Future.successful(Redirect(controllers.routes.ChangeUnderpaymentReasonController.onLoad()))
        }
      }
    )
  }

}
