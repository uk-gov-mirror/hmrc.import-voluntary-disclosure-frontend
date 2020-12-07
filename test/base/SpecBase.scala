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

package base

import config.{AppConfig, ErrorHandler}
import controllers.actions.{FakeIdentifierAction, IdentifierAction}
import models.requests.IdentifierRequest
import org.jsoup.Jsoup
import org.scalatest.TryValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.Application
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.Injector
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import uk.gov.hmrc.http.{HeaderCarrier, SessionKeys}

import scala.concurrent.duration.{Duration, FiniteDuration, _}
import scala.concurrent.{Await, ExecutionContext, Future}

trait SpecBase extends PlaySpec
  with GuiceOneAppPerSuite
  with TryValues
  with ScalaFutures
  with IntegrationPatience
  with MaterializerSupport {

  override lazy val app: Application = GuiceApplicationBuilder()
    .build()

  val internalId = "id"
  val empref = "840-GZ00064"
  val credId = "1234567891"

  lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest("GET", "/foo").withSession(SessionKeys.sessionId -> "foo").withCSRFToken.asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  lazy val fakeIdentifierRequest: IdentifierRequest[_] =
    IdentifierRequest(fakeRequest, internalId, empref, credId, None)

  implicit val defaultTimeout: FiniteDuration = 5.seconds

  def await[A](future: Future[A])(implicit timeout: Duration): A = Await.result(future, timeout)

  def title(heading: String, section: Option[String] = None)(implicit messages: Messages): String =
    s"$heading - ${section.fold("")(_ + " - ")}${messages("service.name")} - ${messages("site.govuk")}"

  def titleOf(result: String): String = Jsoup.parse(result).title

  lazy val injector: Injector = app.injector

  implicit lazy val appConfig: AppConfig = injector.instanceOf[AppConfig]

  implicit lazy val ec: ExecutionContext = injector.instanceOf[ExecutionContext]

  lazy val messagesControllerComponents: MessagesControllerComponents = injector.instanceOf[MessagesControllerComponents]

  implicit lazy val messagesApi: MessagesApi = injector.instanceOf[MessagesApi]
  implicit val messages: Messages = messagesApi.preferred(fakeRequest)

  implicit lazy val errorHandler: ErrorHandler = injector.instanceOf[ErrorHandler]

  implicit val hc: HeaderCarrier = HeaderCarrier()

  lazy val authenticatedAction: IdentifierAction =
    FakeIdentifierAction.identifierAction(messagesControllerComponents.parsers.anyContent,internalId, empref, credId)

}
