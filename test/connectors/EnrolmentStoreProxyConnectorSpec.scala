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

package connectors

import base.SpecBase
import config.SessionKeys
import controllers.connectors.EnrolmentStoreProxyConnector
import org.scalamock.scalatest.MockFactory
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse}
import play.api.http.Status.{NO_CONTENT, OK}
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.{ExecutionContext, Future}

class EnrolmentStoreProxyConnectorSpec extends SpecBase with MockFactory {

  trait Test {
    val request: FakeRequest[AnyContentAsEmpty.type]
    val mockHttpClient: HttpClient = mock[HttpClient]
    val target: EnrolmentStoreProxyConnector = new EnrolmentStoreProxyConnector(mockHttpClient)
  }

  "Calling isGroupEnrolledForPaye" when {
    "the PAYE enrolment exists in session and fetchFreshData is false" must {
      "return what is stored in session" in new Test {
        override val request: FakeRequest[AnyContentAsEmpty.type] = fakeRequest.withSession(SessionKeys.groupEnrolledForPAYE -> "true")
        private val result = await(target.isGroupEnrolledForPaye("groupId", fetchFreshData = false)(implicitly, request))
        result mustBe true
      }

      "not call EnrolmentStoreProxy" in new Test {
        override val request: FakeRequest[AnyContentAsEmpty.type] = fakeRequest.withSession(SessionKeys.groupEnrolledForPAYE -> "true")
        (mockHttpClient.GET[HttpResponse](_: String)(_: HttpReads[HttpResponse], _: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *, *)
          .never()

        private val result = await(target.isGroupEnrolledForPaye("groupId", fetchFreshData = false)(implicitly, request))
        result mustBe true
      }
    }

    "the PAYE enrolment exists in session and fetchFreshData is true" must {
      "call EnrolmentStoreProxy" in new Test {
        override val request: FakeRequest[AnyContentAsEmpty.type] = fakeRequest.withSession(SessionKeys.groupEnrolledForPAYE -> "true")
        (mockHttpClient.GET[HttpResponse](_: String)(_: HttpReads[HttpResponse], _: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *, *)
          .returns(Future.successful(HttpResponse(NO_CONTENT, "")))
          .once()

        private val result = await(target.isGroupEnrolledForPaye("groupId", fetchFreshData = true)(implicitly, request))
        result mustBe false
      }
    }

    "the PAYE enrolment does NOT exists in session" must {
      "call EnrolmentStoreProxy" in new Test {
        (mockHttpClient.GET[HttpResponse](_: String)(_: HttpReads[HttpResponse], _: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *, *)
          .returns(Future.successful(HttpResponse(NO_CONTENT, "")))
          .once()

        override val request: FakeRequest[AnyContentAsEmpty.type] = fakeRequest
        private val result = await(target.isGroupEnrolledForPaye("groupId", fetchFreshData = false)(implicitly, request))
        result mustBe false
      }
    }
  }

  "Calling isGroupEnrolledForPayeCall" when {

    "a NO_CONTENT response is received from EnrolmentStoreProxy" must {

      "treat the response as false" in new Test {
        (mockHttpClient.GET[HttpResponse](_: String)(_: HttpReads[HttpResponse], _: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *, *)
          .returns(Future.successful(HttpResponse(NO_CONTENT, "")))
          .once()

        override val request: FakeRequest[AnyContentAsEmpty.type] = fakeRequest
        private val result = await(target.isGroupEnrolledForPayeCall("groupId"))
        result mustBe false
      }
    }

    "an OK response is received from EnrolmentStoreProxy" must {

      "treat a body with an IR-PAYE enrolment as true" in new Test {
        val bodyWithPayeEnrolment: JsValue = Json.parse("""{"services":["IR-SA", "IR-PAYE"]}""")
        (mockHttpClient.GET[HttpResponse](_: String)(_: HttpReads[HttpResponse], _: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *, *)
          .returns(Future.successful(HttpResponse(OK, bodyWithPayeEnrolment, Map("contentType" -> Seq("application/json")))))
          .once()

        override val request: FakeRequest[AnyContentAsEmpty.type] = fakeRequest
        private val result = await(target.isGroupEnrolledForPayeCall("groupId"))
        result mustBe true
      }

      "treat a body without an IR-PAYE enrolment as false" in new Test {
        val bodyWithPayeEnrolment: JsValue = Json.parse("""{"services":["IR-SA"]}""")
        (mockHttpClient.GET[HttpResponse](_: String)(_: HttpReads[HttpResponse], _: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *, *)
          .returns(Future.successful(HttpResponse(OK, bodyWithPayeEnrolment, Map("contentType" -> Seq("application/json")))))
          .once()

        override val request: FakeRequest[AnyContentAsEmpty.type] = fakeRequest
        private val result = await(target.isGroupEnrolledForPayeCall("groupId"))
        result mustBe false
      }
    }

    "en exception is thrown" must {

      "return false" in new Test {
        (mockHttpClient.GET[HttpResponse](_: String)(_: HttpReads[HttpResponse], _: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *, *)
          .returns(Future.failed(new RuntimeException("Oops")))
          .once()

        override val request: FakeRequest[AnyContentAsEmpty.type] = fakeRequest
        private val result = await(target.isGroupEnrolledForPayeCall("groupId"))
        result mustBe false
      }

    }
  }
}
