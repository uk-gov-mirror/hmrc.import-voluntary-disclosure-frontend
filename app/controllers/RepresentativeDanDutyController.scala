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
import forms.RepresentativeDanFormProvider
import models.RepresentativeDan
import models.SelectedDutyTypes.Duty
import pages.{DefermentAccountPage, DefermentTypePage}
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.RepresentativeDanDutyView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RepresentativeDanDutyController @Inject()(identify: IdentifierAction,
                                                getData: DataRetrievalAction,
                                                requireData: DataRequiredAction,
                                                sessionRepository: SessionRepository,
                                                mcc: MessagesControllerComponents,
                                                view: RepresentativeDanDutyView,
                                                formProvider: RepresentativeDanFormProvider
                                               )
  extends FrontendController(mcc) with I18nSupport {

  private[controllers] lazy val backLink: Call = controllers.routes.SplitPaymentController.onLoad()

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val form = (for {
      danType <- request.userAnswers.get(DefermentTypePage)
      accountNumber <- request.userAnswers.get(DefermentAccountPage)
    } yield {
      formProvider().fill(RepresentativeDan(accountNumber, danType))
    }).getOrElse(formProvider())

    Future.successful(Ok(view(form, backLink)))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors,
        backLink
      ))),
      dan => {
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(DefermentTypePage, dan.danType))
          updatedAnswers <- Future.fromTry(updatedAnswers.set(DefermentAccountPage, dan.accountNumber))
          _ <- sessionRepository.set(updatedAnswers)
        } yield {
          dan.danType match {
            case "A" | "C" => Redirect(controllers.routes.RepresentativeDanImportVATController.onLoad())
            case _ => Redirect(controllers.routes.UploadAuthorityController.onLoad(Duty, dan.accountNumber))
          }
        }
      }
    )
  }
}
