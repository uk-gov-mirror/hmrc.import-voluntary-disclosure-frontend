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

import config.{AppConfig, ErrorHandler}
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import models.ContactAddress
import pages.ImporterAddressFinalPage
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.AddressLookupService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AddressLookupController @Inject()(identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        sessionRepository: SessionRepository,
                                        addressLookupService: AddressLookupService,
                                        val errorHandler: ErrorHandler,
                                        val mcc: MessagesControllerComponents,
                                        implicit val appConfig: AppConfig,
                                        implicit val ec: ExecutionContext) extends FrontendController(mcc) with I18nSupport {

  def initialiseJourney(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    addressLookupService.initialiseJourney map {
      case Right(response) =>
        Redirect(response.redirectUrl)
      case Left(_) =>
        errorHandler.showInternalServerError
    }
  }

  def callback(id: String): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    addressLookupService.retrieveAddress(id) flatMap {
      case Right(address) =>
        val traderAddress = ContactAddress(
          streetAndNumber = address.line1.getOrElse(""),
          city = address.line4.getOrElse(address.line3.getOrElse(address.line2.getOrElse(""))),
          postalCode = address.postcode,
          countryCode = address.countryCode.getOrElse("")
        )

        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(ImporterAddressFinalPage, traderAddress))
          _ <- sessionRepository.set(updatedAnswers)
        } yield {
          Redirect(controllers.routes.DefermentController.onLoad())
        }

      case Left(_) =>
        Future.successful(errorHandler.showInternalServerError)
    }
  }
}
