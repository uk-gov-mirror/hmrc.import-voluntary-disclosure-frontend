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

package mocks.config

import config.AppConfig
import play.api.mvc.RequestHeader

object MockAppConfig extends AppConfig {
  override val footerLinkItems: Seq[String] = Seq("TBC")
  override val contactFormServiceIdentifier: String = "TBC"
  override val contactUrl: String = "TBC"
  override val host: String = "TBC"

  override def feedbackUrl(implicit request: RequestHeader): String = "TBC"

  override val appName: String = "import-voluntary-disclosure-frontend"
  override val loginUrl: String = "TBC"
  override val signOutUrl: String = "TBC"
  override val loginContinueUrl: String = "TBC"
  override val addressLookupFrontend: String = "TBC"
  override val addressLookupInitialise: String = "TBC"
  override val addressLookupConfirmed: String = "TBC"
  override val addressLookupFeedbackUrl: String = "TBC"
  override val addressLookupCallbackUrl: String = "TBC"
  override val timeoutPeriod: Int = 900
  override val cacheTtl: Int = 500
}