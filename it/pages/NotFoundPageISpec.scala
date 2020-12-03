/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import play.api.http.Status
import play.api.libs.ws.{WSRequest, WSResponse}
import stubs.AuditStub
import support.IntegrationSpec

class NotFoundPageISpec extends IntegrationSpec {

  "calling an unknown route" should {

    "return an Not Found response" in {

      AuditStub.audit()

      val request: WSRequest = buildRequest("/some-path-that-does-not-exist")

      val response: WSResponse = await(request.get())

      response.status shouldBe Status.NOT_FOUND

    }

  }

}
