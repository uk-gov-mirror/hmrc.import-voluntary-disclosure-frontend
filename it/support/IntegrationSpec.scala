/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package support

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec._
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import play.api.{Application, Environment, Mode}

trait IntegrationSpec
  extends AnyWordSpec
    with EitherValues
    with Matchers
    with FutureAwaits
    with DefaultAwaitTimeout
    with WireMockHelper
    with GuiceOneServerPerSuite
    with BeforeAndAfterEach
    with BeforeAndAfterAll {

  val mockHost: String = WireMockHelper.host
  val mockPort: String = WireMockHelper.wireMockPort.toString

  lazy val client: WSClient = app.injector.instanceOf[WSClient]

  private val servicesPath = "microservice.services"

  def overriddenConfig: Map[String, Any] = Map(
    s"$servicesPath.auth.host" -> mockHost,
    s"$servicesPath.auth.port" -> mockPort,
    "auditing.consumer.baseUri.port" -> mockPort
  )

  override implicit lazy val app: Application = new GuiceApplicationBuilder()
    .in(Environment.simple(mode = Mode.Dev))
    .configure(overriddenConfig)
    .build()

  override def beforeAll(): Unit = {
    super.beforeAll()
    startWireMock()
  }

  override def afterAll(): Unit = {
    stopWireMock()
    super.afterAll()
  }

  def buildRequest(path: String): WSRequest = client.url(s"http://localhost:$port/import-voluntary-disclosure-frontend$path").withFollowRedirects(false)

  def document(response: WSResponse): JsValue = Json.parse(response.body)
}
