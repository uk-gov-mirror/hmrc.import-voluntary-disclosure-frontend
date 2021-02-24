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

package pages

import config.AppConfig
import models.addressLookup.AddressLookupOnRampModel
import play.api.http.Status._
import play.api.http.{HeaderNames, Status}
import play.api.libs.json.Json
import play.api.libs.ws.{WSRequest, WSResponse}
import stubs.{AddressLookupStub, AuditStub, AuthStub, UserAnswersStub}
import support.IntegrationSpec

class AddressLookupControllerISpec extends IntegrationSpec {

  lazy val mockAppConfig: AppConfig = app.injector.instanceOf[AppConfig]

  "Calling AddressLookupController.initialiseJourney" when {

    "Address lookup frontend returns a ACCEPTED response" should {

      "handoff to address lookup frontend with the correct english and welsh messages" in {

        AuthStub.authorised()
        AuditStub.audit()

        AddressLookupStub.postInitV2Journey(ACCEPTED, AddressLookupOnRampModel("redirect/url"))

        val request: WSRequest = buildRequest("/address-initialise")

        val response: WSResponse = await(request.get())

        response.status shouldBe Status.SEE_OTHER

      }
    }

    "Address lookup frontend returns a INTERNAL_SERVER_ERROR response" should {

      "return an internal server error" in {

        AuthStub.authorised()
        AuditStub.audit()
        UserAnswersStub.createUserAnswers("some_external_id")

        AddressLookupStub.postInitV2Journey(INTERNAL_SERVER_ERROR, AddressLookupOnRampModel("redirect/url"))

        val request: WSRequest = buildRequest("/address-initialise")

        val response: WSResponse = await(request.get())

        println("\n\n\n\n\n\n\n\nLocation: " + response.header(HeaderNames.LOCATION).getOrElse("UNKNOWN"))
        response.status shouldBe Status.INTERNAL_SERVER_ERROR

      }
    }
  }

  "Calling AddressLookupController.callback" when {

    "Address lookup frontend returns an OK response" should {
      "redirect the user to the deferment page" in {

        AuthStub.authorised()
        AuditStub.audit()
        UserAnswersStub.createUserAnswers("some_external_id")

        AddressLookupStub.getAddress(OK, Json.obj(
          "lines" -> Json.arr("line1", "line2"),
          "country" -> Json.obj(
            "name" -> "United Kingdom",
            "code" -> "GB"
          )
        ))

        val request: WSRequest = buildRequest("/address-callback?id=9999999")

        val response: WSResponse = await(request.get())

        response.status shouldBe Status.SEE_OTHER
        response.header(HeaderNames.LOCATION) shouldBe Some(controllers.routes.DefermentController.onLoad().url)
      }
    }
  }
}
