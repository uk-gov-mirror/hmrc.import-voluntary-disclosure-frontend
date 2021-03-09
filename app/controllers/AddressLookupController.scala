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
import models.addressLookup.AddressModel
import pages.{TraderAddressPage, ImporterAddressPage}
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

  def initialiseImporterJourney(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    addressLookupService.initialiseImporterJourney map {
      case Right(response) =>
        Redirect(response.redirectUrl)
      case Left(_) =>
        errorHandler.showInternalServerError
    }
  }

  def callback(id: String): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    addressLookupService.retrieveAddress(id) flatMap {
      case Right(address) =>
         for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(TraderAddressPage, formatAddress(address)))
          _ <- sessionRepository.set(updatedAnswers)
        } yield {
          Redirect(controllers.routes.DefermentController.onLoad())
        }

      case Left(_) =>
        Future.successful(errorHandler.showInternalServerError)
    }
  }

  def importerCallback(id: String): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    addressLookupService.retrieveAddress(id) flatMap {
      case Right(address) =>
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(ImporterAddressPage, formatAddress(address)))
          _ <- sessionRepository.set(updatedAnswers)
        } yield {
          Redirect(controllers.routes.ImporterEORIExistsController.onLoad())
        }

      case Left(_) =>
        Future.successful(errorHandler.showInternalServerError)
    }
  }

  private def formatAddress(address: AddressModel): ContactAddress = {
    val city = address.line4.getOrElse(address.line3.getOrElse(address.line2.getOrElse("")))
    val addressLine2: Option[String] = (address.line2, address.line3) match {
      case (Some(line2), _) if line2 == city => None
      case (Some(line2), Some(line3)) if line3 == city => Some(line2)
      case (Some(line2), Some(line3))  => Some(line2 + ", " + line3)
      case _ => None
    }

    ContactAddress(
      addressLine1 = address.line1.getOrElse(""),
      addressLine2 = addressLine2,
      city = city,
      postalCode = address.postcode,
      countryCode = address.countryCode.getOrElse("")
    )
  }
}
