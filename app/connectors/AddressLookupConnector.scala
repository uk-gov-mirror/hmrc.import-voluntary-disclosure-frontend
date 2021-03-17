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

package connectors

import config.AppConfig
import connectors.httpParsers.AddressLookupHttpParser.AddressLookupReads
import connectors.httpParsers.InitialiseAddressLookupHttpParser.InitialiseAddressLookupReads
import connectors.httpParsers.ResponseHttpParser.{HttpGetResult, HttpPostResult}

import javax.inject.{Inject, Singleton}
import models.addressLookup.{AddressLookupOnRampModel, AddressModel}
import play.api.libs.json.JsValue
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AddressLookupConnector @Inject()(val http: HttpClient,
                                       implicit val config: AppConfig) {

  def initialiseJourney(addressLookupJsonBuilder: JsValue)
                      (implicit hc: HeaderCarrier,ec: ExecutionContext): Future[HttpPostResult[AddressLookupOnRampModel]] = {

    val url = s"${config.addressLookupFrontend}${config.addressLookupInitialise}"

    http.POST[JsValue, HttpPostResult[AddressLookupOnRampModel]](
      url, addressLookupJsonBuilder
    )(implicitly, InitialiseAddressLookupReads, hc, ec)
  }

  private[connectors] def getAddressUrl(id: String) = s"${config.retrieveAddressUrl}/api/confirmed?id=$id"

  def getAddress(id: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[AddressModel]] ={
    http.GET[HttpGetResult[AddressModel]](getAddressUrl(id))
  }
}
