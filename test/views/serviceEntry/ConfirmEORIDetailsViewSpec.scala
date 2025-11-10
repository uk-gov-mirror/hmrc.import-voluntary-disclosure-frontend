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

package views.serviceEntry

import base.ViewBaseSpec
import messages.serviceEntry.ConfirmEORIDetailsMessages
import mocks.config.MockAppConfig
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.data.ConfirmEORIDetailsData.details
import views.html.serviceEntry.ConfirmEORIDetailsView

class ConfirmEORIDetailsViewSpec extends ViewBaseSpec {

  private lazy val injectedView: ConfirmEORIDetailsView = app.injector.instanceOf[ConfirmEORIDetailsView]

  "Rendering the Confirm EORI Details page without the vatNumber" should {

    lazy val view: Html = injectedView(
      details(
        "GB987654321000",
        "Fast Food ltd.",
        "Not VAT registered"
      )
    )(fakeRequest, messages, MockAppConfig.appConfig)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have only 1 Summary List" in {
      document.select(".govuk-summary-list").size mustBe 1
    }

    "have 2 Summary List Rows" in {
      document.select(".govuk-summary-list__row").size mustBe 3
    }

    "have correct EORI number value" in {
      elementText(
        "#main-content > div > div > dl > div:nth-child(1) > dd.govuk-summary-list__value"
      ) mustBe "GB987654321000"
    }

    "have correct name value" in {
      elementText(
        "#main-content > div > div > dl > div:nth-child(2) > dd.govuk-summary-list__value"
      ) mustBe "Fast Food ltd."
    }

    "have correct vatNumber name value" in {
      elementText("#main-content > div > div > dl > div:nth-child(3) > dt") mustBe ConfirmEORIDetailsMessages.vatNumber
    }

    "have correct vatNumber value when it's not present" in {
      elementText(
        "#main-content > div > div > dl > div:nth-child(3) > dd.govuk-summary-list__value"
      ) mustBe ConfirmEORIDetailsMessages.vatNumberNotPresent
    }

    "render a continue button with the correct URL " in {
      elementAttributes(".govuk-button") must contain(
        "href" -> controllers.serviceEntry.routes.WhatDoYouWantToDoController.onLoad().url
      )
    }

  }

  "Rendering the Confirm EORI Details page with the vatNumber" should {

    lazy val view: Html = injectedView(
      details(
        "GB987654321000",
        "Fast Food ltd.",
        "987654321000"
      )
    )(fakeRequest, messages, MockAppConfig.appConfig)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have only 1 Summary List" in {
      document.select(".govuk-summary-list").size mustBe 1
    }

    "have 2 Summary List Rows" in {
      document.select(".govuk-summary-list__row").size mustBe 3
    }

    "have correct EORI number value" in {
      elementText(
        "#main-content > div > div > dl > div:nth-child(1) > dd.govuk-summary-list__value"
      ) mustBe "GB987654321000"
    }

    "have correct name value" in {
      elementText(
        "#main-content > div > div > dl > div:nth-child(2) > dd.govuk-summary-list__value"
      ) mustBe "Fast Food ltd."
    }

    "have correct vatNumber name value" in {
      elementText("#main-content > div > div > dl > div:nth-child(3) > dt") mustBe ConfirmEORIDetailsMessages.vatNumber
    }

    "have correct vatNumber value when it's not present" in {
      elementText(
        "#main-content > div > div > dl > div:nth-child(3) > dd.govuk-summary-list__value"
      ) mustBe "987654321000"
    }

    "render a continue button with the correct URL " in {
      elementAttributes(".govuk-button") must contain(
        "href" -> controllers.serviceEntry.routes.WhatDoYouWantToDoController.onLoad().url
      )
    }

  }

  it should {

    lazy val view: Html =
      injectedView(details("GB987654321000", "Fast Food ltd.", "987654321000"))(fakeRequest, messages, MockAppConfig.appConfig)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    checkPageTitle(ConfirmEORIDetailsMessages.title)

    s"have the correct h1 of '${ConfirmEORIDetailsMessages.title}'" in {
      elementText("h1") mustBe ConfirmEORIDetailsMessages.title
    }

    s"have correct EORI number title of '${ConfirmEORIDetailsMessages.eoriNumber}'" in {
      elementText("#main-content > div > div > dl > div:nth-child(1) > dt") mustBe ConfirmEORIDetailsMessages.eoriNumber
    }

    s"have correct name title of '${ConfirmEORIDetailsMessages.name}'" in {
      elementText("#main-content > div > div > dl > div:nth-child(2) > dt") mustBe ConfirmEORIDetailsMessages.name
    }

    s"have correct expandable text '${ConfirmEORIDetailsMessages.notThisEoriText}'" in {
      elementText(
        "#main-content > div > div > details > summary > span"
      ) mustBe ConfirmEORIDetailsMessages.notThisEoriText
    }

    s"have correct link within the expandable text '${ConfirmEORIDetailsMessages.notThisEoriExpandedLinkText}'" in {
      elementText(
        "#main-content > div > div > details > div > a"
      ) mustBe ConfirmEORIDetailsMessages.notThisEoriExpandedLinkText
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe ConfirmEORIDetailsMessages.continue
    }

  }

}
