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

import models.BoxType
import play.api.Configuration
import play.api.mvc.RequestHeader
import uk.gov.hmrc.play.bootstrap.binders.SafeRedirectUrl
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.time.LocalDate
import javax.inject.{Inject, Singleton}

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
  val addressLookupFeedbackUrl: String =
    s"$contactHost/contact/beta-feedback?service=$contactFormServiceIdentifier"
  lazy val addressLookupCallbackUrl: String = servicesConfig.getString("urls.host") +
    controllers.routes.AddressLookupController.callback("").url
  lazy val timeoutPeriod: Int = servicesConfig.getInt("timeout.period")
  lazy val cacheTtl = servicesConfig.getInt("mongodb.timeToLiveInSeconds")
  lazy val allowedUploadFileTypes: Seq[String] = config.get[Seq[String]]("uploads.allowedFileTypes")
  lazy val mustIncludeFiles: Seq[String] = config.get[Seq[String]]("uploads.mustIncludeFiles")
  lazy val mayIncludeFiles: Seq[String] = config.get[Seq[String]]("uploads.mayIncludeFiles")
  lazy val fileSize = config.get[Int]("uploads.maxFileSize")

  lazy val upScanCallbackUrlForSuccessOrFailureOfFileUpload: String = servicesConfig.getString("upscan.callbackUrlForSuccessOrFailureOfFileUpload")
  lazy val upScanSuccessRedirectForUser: String = host + servicesConfig.getString("upscan.successRedirectForUser")
  lazy val upScanErrorRedirectForUser: String = host + servicesConfig.getString("upscan.errorRedirectForUser")
  lazy val upScanMinFileSize: Int = servicesConfig.getInt("upscan.minFileSize")
  lazy val upScanMaxFileSize: Int = servicesConfig.getInt("upscan.maxFileSize")
  lazy val upScanPollingDelayMilliSeconds: Int = servicesConfig.getInt("upscan.upScanPollingDelayMilliSeconds")
  lazy val upScanInitiateBaseUrl: String = servicesConfig.baseUrl("upscan-initiate")
  lazy val upScanAcceptedFileTypes: String = allowedUploadFileTypes.map(x=>"."+x).mkString(",").toLowerCase

  lazy val fileRepositoryTtl: Int = servicesConfig.getInt("upscan.fileRepositoryTtl")

  lazy val importVoluntaryDisclosureSubmission: String = servicesConfig.baseUrl("import-voluntary-disclosure-submission")

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
  val addressLookupFeedbackUrl: String
  val addressLookupCallbackUrl: String
  val timeoutPeriod: Int
  val cacheTtl: Int
  val allowedUploadFileTypes: Seq[String]
  val mustIncludeFiles: Seq[String]
  val mayIncludeFiles: Seq[String]
  val fileSize: Int
  val upScanCallbackUrlForSuccessOrFailureOfFileUpload: String
  val upScanSuccessRedirectForUser: String
  val upScanErrorRedirectForUser: String
  val upScanMinFileSize: Int
  val upScanMaxFileSize: Int
  val upScanPollingDelayMilliSeconds: Int
  val upScanInitiateBaseUrl: String
  val upScanAcceptedFileTypes: String

  val fileRepositoryTtl: Int
  val importVoluntaryDisclosureSubmission: String
}

trait FixedConfig {
  val euExitDate: LocalDate = LocalDate.of(2021, 1, 1)

  val boxNumberTypes: Map[Int, BoxType] = Map(
    22 -> BoxType(22, "entry","text", 20),
    33 -> BoxType(33, "item","commodity", 20),
    34 -> BoxType(34, "item","text", 20),
    35 -> BoxType(35, "item","text", 20),
    36 -> BoxType(36, "item","text", 20),
    37 -> BoxType(37, "item","text", 20),
    38 -> BoxType(38, "item","text", 20),
    39 -> BoxType(39, "item","text", 20),
    41 -> BoxType(41, "item","text", 20),
    42 -> BoxType(42, "item","text", 20),
    43 -> BoxType(43, "item","text", 20),
    45 -> BoxType(45, "item","text", 20),
    46 -> BoxType(46, "item","text", 20),
    62 -> BoxType(62, "entry","text", 20),
    63 -> BoxType(63, "entry","text", 20),
    66 -> BoxType(66, "entry","text", 20),
    67 -> BoxType(67, "entry","text", 20),
    68 -> BoxType(68, "entry","text", 20)
  )
  val invalidBox = BoxType(-1, "invalid", "invalid", -1)

}
