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
import controllers.actions.IdentifierAction
import javax.inject.{Inject, Singleton}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.AddressLookupService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.ExecutionContext

@Singleton
class AddressLookupController @Inject()(identify: IdentifierAction,
                                        addressLookupService: AddressLookupService,
                                        val errorHandler: ErrorHandler,
                                        val mcc: MessagesControllerComponents,
                                        implicit val appConfig: AppConfig,
                                        implicit val ec: ExecutionContext) extends FrontendController(mcc) with I18nSupport {

  val initialiseJourney: Action[AnyContent] = identify.async { implicit request =>
    addressLookupService.initialiseJourney map {
      case Right(response) =>
        Redirect(response.redirectUrl)
      case Left(_) =>
        errorHandler.showInternalServerError
    }
  }

  val callback: String => Action[AnyContent] = id => identify.async { implicit user =>
    addressLookupService.retrieveAddress(id) map {
      case Right(address) =>
       Ok(address.toString)
      case Left(_) =>
        errorHandler.showInternalServerError
    }
  }
}
