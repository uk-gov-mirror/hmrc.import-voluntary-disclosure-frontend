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

package services

import config.AppConfig
import connectors.AddressLookupConnector

import javax.inject.{Inject, Singleton}
import models.ErrorModel
import models.addressLookup._
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.mvc.{AnyContent, Request}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AddressLookupService @Inject()(addressLookupConnector: AddressLookupConnector,
                                     implicit val messagesApi: MessagesApi,
                                     implicit val appConfig: AppConfig) {

  def initialiseJourney(implicit hc: HeaderCarrier, ec: ExecutionContext, request: Request[AnyContent]):
    Future[Either[ErrorModel, AddressLookupOnRampModel]] =
      addressLookupConnector.initialiseJourney(
        Json.toJson(AddressLookupJsonBuilder(appConfig.addressLookupCallbackUrl))
      )

  def initialiseImporterJourney(implicit hc: HeaderCarrier, ec: ExecutionContext, request: Request[AnyContent]):
    Future[Either[ErrorModel, AddressLookupOnRampModel]] =
      addressLookupConnector.initialiseJourney(
        Json.toJson(ImporterAddressLookupJsonBuilder(appConfig.importerAddressLookupCallbackUrl))
      )

  def retrieveAddress(id: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorModel, AddressModel]] =
    addressLookupConnector.getAddress(id)

}
