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

import assets.AddressLookupTestConstants.customerAddressMax
import assets.BaseTestConstants.errorModel
import base.ControllerSpecBase
import mocks.services.MockAddressLookupService
import models.addressLookup.AddressLookupOnRampModel
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers.{redirectLocation, _}

import scala.concurrent.Future

class AddressLookupControllerSpec extends ControllerSpecBase with MockAddressLookupService {

  "Calling .callback" must {

    def setup(addressLookupResponse: RetrieveAddressResponse): AddressLookupController = {

      setupMockRetrieveAddress(addressLookupResponse)

      new AddressLookupController(
        authenticatedAction,
        mockAddressLookupService,
        errorHandler,
        messagesControllerComponents,
        appConfig,
        ec)
    }

    "address lookup service returns success" when {

        def controller: AddressLookupController = setup(
          addressLookupResponse = Right(customerAddressMax),
        )

        "for an Individual" should {

          lazy val result = controller.callback("12345")(fakeRequest)

          "return OK (200)" in {
            status(result) mustBe Status.OK
          }
        }
    }

      "and business address lookup service returns an error" should {

        lazy val controller = setup(
          addressLookupResponse = Left(errorModel))
        lazy val result = controller.callback("12345")(fakeRequest)

        "return InternalServerError" in {
          status(result) mustBe Status.INTERNAL_SERVER_ERROR
        }
      }
  }

      "Calling .initialiseJourney" when {

        def setup(addressLookupResponse: InitialiseJourneyResponse): AddressLookupController = {

          setupMockInitialiseJourney(addressLookupResponse)

          new AddressLookupController(
            authenticatedAction,
            mockAddressLookupService,
            errorHandler,
            messagesControllerComponents,
            appConfig,
            ec)
        }

        "address lookup service returns success" when {

          lazy val controller = setup(addressLookupResponse = Right(AddressLookupOnRampModel("redirect-url")))

          lazy val result: Future[Result] = controller.initialiseJourney(fakeRequest)

            "return redirect to the url returned" in {
              status(result) mustBe Status.SEE_OTHER
            }

            "redirect to url returned" in {
              redirectLocation(result) mustBe Some("redirect-url")
            }
        }

        "address lookup service returns an error" should {

          lazy val controller = setup(addressLookupResponse = Left(errorModel))
          lazy val result = controller.initialiseJourney(fakeRequest)

          "return InternalServerError" in {
            status(result) mustBe Status.INTERNAL_SERVER_ERROR
          }
        }
      }
}
