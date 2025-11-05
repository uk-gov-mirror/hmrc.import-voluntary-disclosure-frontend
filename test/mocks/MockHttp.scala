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

trait MockHttp extends MockFactory {

  val mockHttp: HttpClientV2 = mock[HttpClientV2]

  def setupMockHttpGet[T](url: String)(response: T): Unit =
    (mockHttp.get(_: URL)(_: HeaderCarrier).execute[T](
      _: HttpReads[T],
      _: ExecutionContext
    ))
      .expects(new URI(url).toURL, *, *, *)
      .returns(Future.successful(response))

  def setupMockHttpPost[I, O](url: String)(response: O): Unit =
    (mockHttp.post(_: URL)(_: HeaderCarrier).withBody(_: I)(_: BodyWritable[I], _: Tag[I], _: ExecutionContext).execute[O](
      _: HttpReads[O],
      _: ExecutionContext
    ))
      .expects(new URI(url).toURL, *, *, *, *, *, *, *)
      .returns(Future.successful(response))

  def setupMockHttpPostWithBody[I, O](url: String, body: I)(response: O): Unit =
    (mockHttp.post(_: URL)(_: HeaderCarrier).withBody(body)(_: BodyWritable[I], _: Tag[I], _: ExecutionContext).execute[O](
      _: HttpReads[O],
      _: ExecutionContext
    ))
      .expects(new URI(url).toURL, *, *, *, *, *, *)
      .returns(Future.successful(response))
}
