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

package config

import java.time.LocalDate

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.mvc.RequestHeader
import uk.gov.hmrc.play.bootstrap.binders.SafeRedirectUrl
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
class AppConfigImpl @Inject()(config: Configuration, servicesConfig: ServicesConfig) extends AppConfig {
  private val contactHost = servicesConfig.getString("contact-frontend.host")

  private def requestUri(implicit request: RequestHeader): String = SafeRedirectUrl(host + request.uri).encodedUrl

  val footerLinkItems: Seq[String] = config.get[Seq[String]]("footerLinkItems")
  val contactFormServiceIdentifier = servicesConfig.getString("contact-frontend.service-identifier")
  lazy val contactUrl = s"$contactHost/contact/contact-hmrc?service=$contactFormServiceIdentifier"
  lazy val host = servicesConfig.getString("urls.host")

  def feedbackUrl(implicit request: RequestHeader): String =
    s"$contactHost/contact/beta-feedback?service=$contactFormServiceIdentifier&backUrl=$requestUri"

  lazy val appName: String = servicesConfig.getString("appName")
  lazy val loginUrl: String = servicesConfig.getString("urls.login")
  lazy val signOutUrl: String = servicesConfig.getString("urls.signOut")
  lazy val loginContinueUrl: String = servicesConfig.getString("urls.loginContinue")
  lazy val addressLookupFrontend: String = servicesConfig.baseUrl("address-lookup-frontend")
  lazy val addressLookupInitialise: String = servicesConfig.getString("urls.addressLookupInitialiseUri")
  lazy val addressLookupConfirmed: String = servicesConfig.getString("urls.addressLookupConfirmedUri")
  val addressLookupFeedbackUrl: String =
    s"$contactHost/contact/beta-feedback?service=$contactFormServiceIdentifier"
  lazy val addressLookupCallbackUrl: String = servicesConfig.getString("urls.host") +
    controllers.routes.AddressLookupController.callback("").url
  lazy val timeoutPeriod: Int = servicesConfig.getInt("timeout.period")
  lazy val cacheTtl = servicesConfig.getInt("mongodb.timeToLiveInSeconds")
}

trait AppConfig extends FixedConfig {
  val footerLinkItems: Seq[String]
  val contactFormServiceIdentifier: String
  val contactUrl: String
  val host: String
  def feedbackUrl(implicit request: RequestHeader): String
  val appName: String
  val loginUrl: String
  val signOutUrl: String
  val loginContinueUrl: String
  val addressLookupFrontend: String
  val addressLookupInitialise: String
  val addressLookupConfirmed: String
  val addressLookupFeedbackUrl: String
  val addressLookupCallbackUrl: String
  val timeoutPeriod: Int
  val cacheTtl: Int
}

trait FixedConfig {
  val euExitDate: LocalDate = LocalDate.of(2021, 1, 1)
}
