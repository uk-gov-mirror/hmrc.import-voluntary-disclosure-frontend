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

package models.addressLookup

import config.AppConfig
import play.api.i18n.{Lang, Messages, MessagesApi, MessagesImpl}
import play.api.libs.json._
import play.api.mvc.{AnyContent, Request}


case class ImporterAddressLookupJsonBuilder(continueUrl: String)(implicit request: Request[AnyContent], messagesApi: MessagesApi, config: AppConfig) {

  // general journey overrides
  val showPhaseBanner: Boolean = true
  val ukMode: Boolean = false
  val conf: AppConfig = config
  val deskproServiceName: String = "TBC" //TODO: Needs to contain name if we need it
  val accessibilityFooterUrl: String = "TBC" //TODO: Needs to point somewhere

  object Version2 {

    val eng: Messages = MessagesImpl(Lang("en"), messagesApi)
    val wel: Messages = MessagesImpl(Lang("cy"), messagesApi)

    val version: Int = 2

    val navTitle: Messages => String = message => message("service.name")

    val confirmPageConfig: JsObject = Json.obj(
      "showSubHeadingAndInfo" -> true,
      "showSearchAgainLink" -> true
    )

    val timeoutConfig: JsObject = Json.obj(
      "timeoutAmount" -> conf.timeoutPeriod,
      "timeoutUrl" -> "TBC" //TODO: Needs to point somewhere
    )
    val selectPageLabels: Messages => JsObject = message => Json.obj(
      "title" -> message("importerAddress_lookupPage.selectPage.heading"),
      "heading" -> message("importerAddress_lookupPage.selectPage.heading"),
      "submitLabel" -> message("common.continue"),
      "editAddressLinkText" -> message("importerAddress_lookupPage.selectPage.editLink")
    )

    val lookupPageLabels: Messages => JsObject = message => Json.obj(
      "title" -> message("importerAddress_lookupPage.heading"),
      "heading" -> message("importerAddress_lookupPage.heading"),
      "filterLabel" -> message("importerAddress_lookupPage.filter"),
      "postcodeLabel" -> message("importerAddress_lookupPage.postcode"),
      "submitLabel" -> message("importerAddress_lookupPage.lookupPage.submit")
    )

    val confirmPageLabels: Messages => JsObject = message => Json.obj(
      "title" -> message("importerAddress_lookupPage.confirmPage.heading"),
      "heading" -> message("importerAddress_lookupPage.confirmPage.heading"),
      "infoMessage" -> message("importerAddress_lookupPage.confirmPage.infoMessage"),
      "showConfirmChangeText" -> false
    )

    val editPageLabels: Messages => JsObject = message => Json.obj(
      "heading" -> message("importerAddress_lookupPage.editPage.heading"),
      "townLabel" -> message("importerAddress_lookupPage.editPage.townOrCity"),
      "submitLabel" -> message("common.continue")
    )

    val phaseBannerHtml: Messages => String = message =>
      s"${message("feedback.before")}" +
        s" <a id='beta-banner-feedback' href='${config.addressLookupFeedbackUrl}'>${message("feedback.link")}</a>" +
        s" ${message("feedback.after")}"
  }

}

object ImporterAddressLookupJsonBuilder {

  implicit val writes: Writes[ImporterAddressLookupJsonBuilder] = new Writes[ImporterAddressLookupJsonBuilder] {
    def writes(data: ImporterAddressLookupJsonBuilder): JsObject =
    {
      Json.obj(fields =
        "version" -> 2,
        "options" -> Json.obj(
          "continueUrl" -> data.continueUrl,
          "accessibilityFooterUrl" -> data.accessibilityFooterUrl,
          "deskProServiceName" -> data.deskproServiceName,
          "showPhaseBanner" -> data.showPhaseBanner,
          "ukMode" -> data.ukMode,
          "timeoutConfig" -> data.Version2.timeoutConfig,
          "confirmPageConfig" -> data.Version2.confirmPageConfig
        ),
        "labels" -> Json.obj(
          "en" -> Json.obj(
            "appLevelLabels" -> Json.obj(
              "navTitle" -> data.Version2.navTitle(data.Version2.eng),
              "phaseBannerHtml" -> data.Version2.phaseBannerHtml(data.Version2.eng)
            ),
            "selectPageLabels" -> data.Version2.selectPageLabels(data.Version2.eng),
            "lookupPageLabels" -> data.Version2.lookupPageLabels(data.Version2.eng),
            "confirmPageLabels" -> data.Version2.confirmPageLabels(data.Version2.eng),
            "editPageLabels" -> data.Version2.editPageLabels(data.Version2.eng)
          ),
          "cy" -> Json.obj(
            "appLevelLabels" -> Json.obj(
              "navTitle" -> data.Version2.navTitle(data.Version2.wel),
              "phaseBannerHtml" -> data.Version2.phaseBannerHtml(data.Version2.wel)
            ),
            "selectPageLabels" -> data.Version2.selectPageLabels(data.Version2.wel),
            "lookupPageLabels" -> data.Version2.lookupPageLabels(data.Version2.wel),
            "confirmPageLabels" -> data.Version2.confirmPageLabels(data.Version2.wel),
            "editPageLabels" -> data.Version2.editPageLabels(data.Version2.wel)
          )
        )
      )
    }
  }
}