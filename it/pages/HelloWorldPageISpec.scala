/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import play.api.http.Status
import play.api.libs.ws.{WSRequest, WSResponse}
import stubs.{AuditStub, AuthStub}
import support.IntegrationSpec

class HelloWorldPageISpec extends IntegrationSpec {

  "calling the hello-world route" should {

    "return an OK response" in {

      AuditStub.audit()
      AuthStub.authorised()

      val request: WSRequest = buildRequest("/hello-world")

      val response: WSResponse = await(request.get())

      response.status shouldBe Status.OK

    }

  }

}
