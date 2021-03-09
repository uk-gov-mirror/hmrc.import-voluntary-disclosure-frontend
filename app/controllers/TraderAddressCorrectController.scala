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
import config.ErrorHandler
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.TraderAddressCorrectFormProvider
import models.ContactAddress
import pages.{TraderAddressPage, KnownEoriDetails, TraderAddressCorrectPage}
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.EoriDetailsService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.TraderAddressCorrectView

import javax.inject.Singleton
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class TraderAddressCorrectController @Inject()(identify: IdentifierAction,
                                               getData: DataRetrievalAction,
                                               requireData: DataRequiredAction,
                                               sessionRepository: SessionRepository,
                                               eoriDetailsService: EoriDetailsService,
                                               val errorHandler: ErrorHandler,
                                               mcc: MessagesControllerComponents,
                                               formProvider: TraderAddressCorrectFormProvider,
                                               view: TraderAddressCorrectView
                                         )
  extends FrontendController(mcc) with I18nSupport {

  private val logger = Logger("application." + getClass.getCanonicalName)

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val form = request.userAnswers.get(TraderAddressCorrectPage).fold(formProvider()) {
      formProvider().fill
    }
    eoriDetailsService.retrieveEoriDetails(request.eori).flatMap {
      case Right(eoriDetails) =>
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(KnownEoriDetails, eoriDetails))
          _ <- sessionRepository.set(updatedAnswers)
        } yield Ok(view(form, eoriDetails.address))
      case Left(error) =>
        logger.error(error.message + " " + error.status)
        Future.successful(NotFound(error.message + " " + error.status))
    }
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val traderAddress: ContactAddress = request.userAnswers.get(KnownEoriDetails).get.address
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors, traderAddress))),
      value => {
        if (value) {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(TraderAddressCorrectPage, value))
            updatedAnswers <- Future.fromTry(updatedAnswers.set(TraderAddressPage, traderAddress))
            _ <- sessionRepository.set(updatedAnswers)
          } yield {
            Redirect(controllers.routes.DefermentController.onLoad())
          }
        } else {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(TraderAddressCorrectPage, value))
            _ <- sessionRepository.set(updatedAnswers)
          } yield {
            Redirect(controllers.routes.AddressLookupController.initialiseJourney())
          }
        }
      }
    )
  }

}
