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

package stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import models.addressLookup.AddressLookupOnRampModel
import play.api.http.HeaderNames.LOCATION
import play.api.libs.json.JsValue
import support.WireMockMethods

object AddressLookupStub extends WireMockMethods {

  private val addressUri = "/api/confirmed.*"
  private val initUriV2 = "/api/v2/init"

  def postInitV2Journey(status: Int, response: AddressLookupOnRampModel, body: Option[String] = None): StubMapping = {
    when(method = POST, uri = initUriV2, body = body)
      .thenReturn(status = status, headers = Map(LOCATION -> response.redirectUrl))
  }

  def getAddress(status: Int, response: JsValue): StubMapping = {
    when(method = GET, uri = addressUri)
      .thenReturn(status = status, body = response)
  }
}
