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

package mocks.config

import config.AppConfig
import org.mockito.Mockito.when
import org.scalamock.scalatest.MockFactory
import play.api.i18n.Lang
import play.api.mvc.RequestHeader

import org.mockito.ArgumentMatchers.any

trait MockAppConfig extends MockFactory {

  val appConfig: AppConfig = mock[AppConfig]

  when(appConfig.footerLinkItems).thenReturn(Seq("TBC"))
  when(appConfig.contactFormServiceIdentifier).thenReturn("TBC")
  when(appConfig.surveyUrl).thenReturn("TBC")
  when(appConfig.host).thenReturn("TBC")
  when(appConfig.feedbackUrl(any())).thenReturn("/contact/beta-feedback?service=import-voluntary-disclosure")
  when(appConfig.appName).thenReturn("import-voluntary-disclosure-frontend")
  when(appConfig.loginUrl).thenReturn("TBC")
  when(appConfig.signOutUrl).thenReturn("TBC")
  when(appConfig.loginContinueUrl).thenReturn("TBC")
  when(appConfig.addressLookupFrontend).thenReturn("TBC")
  when(appConfig.addressLookupCallback).thenReturn("/disclose-import-taxes-underpayment/address-callback")
  when(appConfig.importerAddressLookupCallback).thenReturn("/disclose-import-taxes-underpayment/importer-address-callback")
  when(appConfig.addressLookupInitialise).thenReturn("TBC")
  when(appConfig.addressLookupFeedbackUrl).thenReturn("TBC")
  when(appConfig.addressLookupCallbackUrl).thenReturn("TBC")
  when(appConfig.importerAddressLookupCallbackUrl).thenReturn("TBC")
  when(appConfig.timeoutPeriod).thenReturn(900)
  when(appConfig.countdown).thenReturn(120)
  when(appConfig.cacheTtl).thenReturn(500)
  when(appConfig.upScanCallbackUrlForSuccessOrFailureOfFileUpload).thenReturn("TBC")
  when(appConfig.upScanSuccessRedirectForUser).thenReturn("TBC")
  when(appConfig.upScanSuccessRedirectForBulk).thenReturn("TBC")
  when(appConfig.upScanErrorRedirectForUser).thenReturn("TBC")
  when(appConfig.upScanMinFileSize).thenReturn(1)
  when(appConfig.upScanMaxFileSize).thenReturn(10485760)
  when(appConfig.upScanPollingDelayMilliSeconds).thenReturn(10)
  when(appConfig.upScanInitiateBaseUrl).thenReturn("TBC")
  when(appConfig.upScanAcceptedFileTypes).thenReturn("TBC")
  when(appConfig.fileRepositoryTtl).thenReturn(86400)
  when(appConfig.upScanAuthoritySuccessRedirectForUser).thenReturn("TBC")
  when(appConfig.upScanAuthorityErrorRedirectForUser).thenReturn("TBC")
  when(appConfig.upScanSupportingDocSuccessRedirectForUser).thenReturn("TBC")
  when(appConfig.upScanSupportingDocErrorRedirectForUser).thenReturn("TBC")
  when(appConfig.upScanCancelCaseRedirectForUser).thenReturn("TBC")
  when(appConfig.upScanCancelCaseDocErrorRedirectForUser).thenReturn("TBC")
  when(appConfig.importVoluntaryDisclosureSubmission).thenReturn("TBC")
  when(appConfig.eccSubscribeUrl).thenReturn("TBC")
  when(appConfig.c2001Url).thenReturn("TBC")
  when(appConfig.en).thenReturn(Lang("en"))
  when(appConfig.cy).thenReturn(Lang("cy"))
  when(appConfig.defaultLanguage).thenReturn(Lang("en"))
  when(appConfig.vatReturnAdjustmentsUrl).thenReturn("url")
  when(appConfig.pvaHandoffUrl).thenReturn("url")
  when(appConfig.c18EmailAddress).thenReturn("npcc@hmrc.gov.uk")
}

object MockAppConfig extends MockAppConfig
