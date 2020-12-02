/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.Status._
import support.WireMockMethods

object AuditStub extends WireMockMethods {

  private val auditUri: String = s"/write/audit.*"

  def audit(): StubMapping = {
    when(method = POST, uri = auditUri)
      .thenReturn(status = NO_CONTENT)
  }

}
