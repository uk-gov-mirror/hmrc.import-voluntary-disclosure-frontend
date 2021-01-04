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

package services

import base.SpecBase
import org.scalamock.scalatest.MockFactory
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.audit.model.ExtendedDataEvent

import scala.concurrent.ExecutionContext

class AuditServiceSpec extends SpecBase with MockFactory {

  "audit" must {
    "call the audit connector" in {
      val mockAuditConnector = mock[AuditConnector]
      (mockAuditConnector.sendExtendedEvent(_: ExtendedDataEvent)(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *)
        .once()

      val service = new AuditService(appConfig, mockAuditConnector)

      val auditModel: JsonAuditModel = new JsonAuditModel {
        override val auditType: String = "SomeEvent"
        override val transactionName: String = "some-event"
        override val detail: JsObject = Json.obj("name" -> "test name")
      }

      service.audit(auditModel)(implicitly, implicitly, fakeRequest)
    }
  }

  "toExtendedDataEvent" must {
    "return a data event with credId and affinityGroup appended to the data event" in {
      val mockAuditConnector = mock[AuditConnector]

      val service = new AuditService(appConfig, mockAuditConnector)

      val auditModel: JsonAuditModel = new JsonAuditModel {
        override val auditType: String = "SomeEvent"
        override val transactionName: String = "some-event"
        override val detail: JsObject = Json.obj("name" -> "test name")
      }

      implicit val hc: HeaderCarrier = HeaderCarrier()

      val result = service.toExtendedDataEvent(auditModel, "/some-path")

      result.detail mustBe Json.obj("name" -> "test name")
    }
  }
}
