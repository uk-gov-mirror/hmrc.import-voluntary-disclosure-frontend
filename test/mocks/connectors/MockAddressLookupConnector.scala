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

package mocks.connectors

import models.ErrorModel
import models.addressLookup.{AddressLookupOnRampModel, AddressModel}
import org.scalatestplus.play.PlaySpec

import scala.concurrent.{ExecutionContext, Future}
import connectors.AddressLookupConnector
import org.scalamock.scalatest.MockFactory
import play.api.libs.json.JsValue
import uk.gov.hmrc.http.HeaderCarrier

trait MockAddressLookupConnector extends PlaySpec with MockFactory {

  val mockAddressLookupConnector: AddressLookupConnector = mock[AddressLookupConnector]

  type AddressLookupGetAddressResponse = Either[ErrorModel, AddressModel]

  type AddressLookupInitialiseResponse = Either[ErrorModel, AddressLookupOnRampModel]

  def setupMockGetAddress(response: Either[ErrorModel, AddressModel]): Unit = {
    (mockAddressLookupConnector.getAddress(_: String)(_: HeaderCarrier, _: ExecutionContext))
      .expects(*,*,*)
      .returns(Future.successful(response))
  }

  def setupMockInitialiseJourney(response: Either[ErrorModel, AddressLookupOnRampModel]): Unit = {
    (mockAddressLookupConnector.initialiseJourney(_: JsValue)(_: HeaderCarrier, _: ExecutionContext))
      .expects(*,*,*)
      .returns(Future.successful(response))
  }
}

