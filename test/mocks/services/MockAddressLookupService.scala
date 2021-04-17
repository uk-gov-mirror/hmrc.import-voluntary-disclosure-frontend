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

package mocks.services

import models.ErrorModel
import models.addressLookup.{AddressLookupOnRampModel, AddressModel}
import org.scalamock.scalatest.MockFactory
import play.api.mvc.{AnyContent, Request}
import services.AddressLookupService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockAddressLookupService extends MockFactory {

  val mockAddressLookupService: AddressLookupService = mock[AddressLookupService]

  type RetrieveAddressResponse = Either[ErrorModel, AddressModel]
  type InitialiseJourneyResponse = Either[ErrorModel, AddressLookupOnRampModel]

  def setupMockRetrieveAddress(response: RetrieveAddressResponse): Unit = {
    (mockAddressLookupService.retrieveAddress(_: String)(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *)
      .returns(Future.successful(response))
  }

  def setupMockInitialiseJourney(response: InitialiseJourneyResponse): Unit = {
    (mockAddressLookupService.initialiseJourney(_: HeaderCarrier, _: ExecutionContext, _: Request[AnyContent]))
      .expects(*, *, *)
      .returns(Future.successful(response))
  }

  def setupMockInitialiseImporterJourney(response: InitialiseJourneyResponse): Unit = {
    (mockAddressLookupService.initialiseImporterJourney(_: HeaderCarrier, _: ExecutionContext, _: Request[AnyContent]))
      .expects(*, *, *)
      .returns(Future.successful(response))
  }
}
