/*
 * Copyright 2025 HM Revenue & Customs
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

package mocks

import izumi.reflect.Tag
import org.scalamock.scalatest.MockFactory
import play.api.libs.ws.BodyWritable
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads}

import java.net.{URI, URL}
import scala.concurrent.{ExecutionContext, Future}

import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers.any

import uk.gov.hmrc.http.client.RequestBuilder

trait MockHttp extends MockFactory {

  val mockHttp: HttpClientV2 = mock[HttpClientV2]

  def setupMockHttpGet[T](url: String)(response: T): Unit = {
    val mockRequestBuilder: RequestBuilder = mock[RequestBuilder]
    when(mockHttp.get(any())(any())).thenReturn(mockRequestBuilder)
    when(mockRequestBuilder.execute(any(), any())).thenReturn(Future.successful(response))
  }

  def setupMockHttpPost[I, O](url: String)(response: O): Unit = {
    val mockRequestBuilder: RequestBuilder = mock[RequestBuilder]
    when(mockRequestBuilder.withBody(any())(any(), any(), any())).thenReturn(mockRequestBuilder)
    when(mockRequestBuilder.execute(any(), any())).thenReturn(Future.successful(response))
    when(mockHttp.post(any())(any())).thenReturn(mockRequestBuilder)
  }

  def setupMockHttpPostWithBody[I, O](url: String, body: I)(response: O): Unit =
    val mockRequestBuilder: RequestBuilder = mock[RequestBuilder]
    when(mockRequestBuilder.withBody(any())(any(), any(), any())).thenReturn(mockRequestBuilder)
    when(mockRequestBuilder.execute(any(), any())).thenReturn(Future.successful(response))
    when(mockHttp.post(any())(any())).thenReturn(mockRequestBuilder)
}
