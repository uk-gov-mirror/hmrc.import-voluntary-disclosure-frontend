/*
 * Copyright 2020 HM Revenue & Customs
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
import play.api.http.Status
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.libs.ws.{WSRequest, WSResponse}
import stubs.{AddressLookupStub, AuditStub, AuthStub}
import support.IntegrationSpec

class AddressLookupControllerISpec extends IntegrationSpec {

  lazy val mockAppConfig: AppConfig = app.injector.instanceOf[AppConfig]

  "Calling AddressLookupController.initialiseJourney" when {

        "handoff to address lookup frontend with the correct english and welsh messages" in {

          AuthStub.authorised()
          AuditStub.audit()

          AddressLookupStub.postInitV2Journey(ACCEPTED, AddressLookupOnRampModel("redirect/url"))

          val request: WSRequest = buildRequest("/initialise")

          val response: WSResponse = await(request.get())

          response.status shouldBe Status.SEE_OTHER

        }

    "Address Lookup returns UNAUTHORISED" in {

      AuthStub.authorised()
      AuditStub.audit()

      AddressLookupStub.postInitV2Journey(UNAUTHORIZED, AddressLookupOnRampModel("redirect/url"))

      val request: WSRequest = buildRequest("/initialise")

      val response: WSResponse = await(request.get())

      response.status shouldBe Status.INTERNAL_SERVER_ERROR

    }
  }

  "Calling AddressLookupController.callback" when {

      "Status OK received" in {

        AuthStub.authorised()
        AuditStub.audit()

        AddressLookupStub.getAddress(OK, Json.obj(
          "lines" -> Json.arr("line1", "line2"),
          "country" -> Json.obj(
            "name" -> "United Kingdom",
            "code" -> "GB"
          )
        ))

        val request: WSRequest = buildRequest("/callback?id=9999999")

        val response: WSResponse = await(request.get())

        response.status shouldBe Status.OK

      }
  }
}
