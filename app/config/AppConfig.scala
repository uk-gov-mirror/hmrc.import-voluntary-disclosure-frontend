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

package config

import play.api.Configuration
import play.api.i18n.Lang
import play.api.mvc.RequestHeader
import uk.gov.hmrc.play.bootstrap.binders.SafeRedirectUrl
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.time.LocalDate
import javax.inject.{Inject, Singleton}

@Singleton
class AppConfig @Inject() (config: Configuration, servicesConfig: ServicesConfig) {
  private val contactHost  = servicesConfig.getString("contact-frontend.host")
  private val feedbackHost = servicesConfig.getString("feedback-frontend.host")
  lazy val surveyUrl: String = feedbackHost + servicesConfig.getString("feedback-frontend.url")

  val footerLinkItems: Seq[String] = config.get[Seq[String]]("footerLinkItems")

  val contactFormServiceIdentifier: String = servicesConfig.getString("contact-frontend.serviceId")
  lazy val host: String = servicesConfig.getString("urls.host")

  def feedbackUrl(implicit request: RequestHeader): String =
    s"$contactHost/contact/beta-feedback?service=$contactFormServiceIdentifier&backUrl=${SafeRedirectUrl(host + request.uri).encodedUrl}"

  lazy val appName: String                       = servicesConfig.getString("appName")
  lazy val loginUrl: String                      = servicesConfig.getString("urls.login")
  lazy val signOutUrl: String                    = servicesConfig.getString("urls.signOut")
  lazy val loginContinueUrl: String              = servicesConfig.getString("urls.loginContinue")
  lazy val addressLookupFrontend: String         = servicesConfig.baseUrl("address-lookup-frontend")
  lazy val addressLookupCallback: String         = servicesConfig.getString("urls.addressCallbackUrl")
  lazy val importerAddressLookupCallback: String = servicesConfig.getString("urls.importerAddressCallbackUrl")
  lazy val c2001Url: String                      = servicesConfig.getString("urls.c2001Url")

  lazy val addressLookupInitialise: String = servicesConfig.getString("urls.addressLookupInitialiseUri")
  val addressLookupFeedbackUrl: String =
    s"$contactHost/contact/beta-feedback?service=$contactFormServiceIdentifier"
  lazy val addressLookupCallbackUrl: String = servicesConfig.getString("urls.host") +
    addressLookupCallback
  lazy val importerAddressLookupCallbackUrl: String = servicesConfig.getString("urls.host") +
    importerAddressLookupCallback
  lazy val cacheTtl: Int = servicesConfig.getInt("mongodb.timeToLiveInSeconds")

  lazy val timeoutPeriod: Int = servicesConfig.getInt("timeout.period")
  lazy val countdown: Int     = servicesConfig.getInt("timeout.countdown")

  lazy val upScanCallbackUrlForSuccessOrFailureOfFileUpload: String =
    servicesConfig.getString("upscan.callbackUrlForSuccessOrFailureOfFileUpload")
  lazy val upScanSuccessRedirectForUser: String = host + servicesConfig.getString("upscan.successRedirectForUser")
  lazy val upScanErrorRedirectForUser: String   = host + servicesConfig.getString("upscan.errorRedirectForUser")
  lazy val upScanSuccessRedirectForBulk: String = host + servicesConfig.getString("upscan.successRedirectForBulk")
  lazy val upScanErrorRedirectForBulk: String   = host + servicesConfig.getString("upscan.errorRedirectForBulk")
  lazy val upScanSupportingDocSuccessRedirectForUser: String =
    host + servicesConfig.getString("upscan.supportingDocSuccessRedirectForUser")
  lazy val upScanSupportingDocErrorRedirectForUser: String =
    host + servicesConfig.getString("upscan.supportingDocErrorRedirectForUser")
  lazy val upScanMinFileSize: Int              = servicesConfig.getInt("upscan.minFileSize")
  lazy val upScanMaxFileSize: Int              = servicesConfig.getInt("upscan.maxFileSize")
  lazy val upScanPollingDelayMilliSeconds: Int = servicesConfig.getInt("upscan.upScanPollingDelayMilliSeconds")
  lazy val upScanInitiateBaseUrl: String       = servicesConfig.baseUrl("upscan-initiate")
  lazy val upScanAcceptedFileTypes: String     = servicesConfig.getString("upscan.acceptedFileTypes")

  lazy val upScanAuthoritySuccessRedirectForUser: String =
    host + servicesConfig.getString("upscan.authoritySuccessRedirectForUser")
  lazy val upScanAuthorityErrorRedirectForUser: String =
    host + servicesConfig.getString("upscan.authorityErrorRedirectForUser")

  lazy val upScanCancelCaseRedirectForUser: String =
    host + servicesConfig.getString("upscan.cancelCaseDocSuccessRedirectForUser")
  lazy val upScanCancelCaseDocErrorRedirectForUser: String =
    host + servicesConfig.getString("upscan.cancelCaseDocErrorRedirectForUser")

  lazy val fileRepositoryTtl: Int = servicesConfig.getInt("upscan.fileRepositoryTtl")

  lazy val importVoluntaryDisclosureSubmission: String =
    servicesConfig.baseUrl("import-voluntary-disclosure-submission")

  lazy val eccSubscribeUrl: String = servicesConfig.getString("urls.eccSubscribeUrl")

  val en: Lang              = Lang("en")
  val cy: Lang              = Lang("cy")
  val defaultLanguage: Lang = en

  lazy val vatReturnAdjustmentsUrl: String = servicesConfig.getString("urls.vatReturnAdjustmentsUrl")

  lazy val pvaHandoffUrl: String = servicesConfig.getString("urls.pvaHandoffUrl")

  val c18EmailAddress: String = servicesConfig.getString("emails.c18Support")

}

