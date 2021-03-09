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

package views

import base.ViewBaseSpec
import messages.{ConfirmEORIDetailsMessages, ConfirmReasonDetailMessages}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.data.ConfirmEORIDetailsData.details
import views.data.ConfirmReasonData.reasons
import views.html.ConfirmEORIDetailsView

class ConfirmEORIDetailsViewSpec extends ViewBaseSpec {

  private lazy val injectedView: ConfirmEORIDetailsView = app.injector.instanceOf[ConfirmEORIDetailsView]


  "Rendering the Confirm EORI Details page" should {

    lazy val view: Html = injectedView(details("GB987654321000", "Fast Food ltd."))(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have only 1 Summary List" in {
      document.select(".govuk-summary-list").size mustBe 1
    }

    "have 2 Summary List Rows" in {
      document.select(".govuk-summary-list__row").size mustBe 2
    }


    "have correct EORI number value" in {
      elementText("#main-content > div > div > dl > div:nth-child(1) > dd.govuk-summary-list__value") mustBe "GB987654321000"
    }

    "have correct name value" in {
      elementText("#main-content > div > div > dl > div:nth-child(2) > dd.govuk-summary-list__value") mustBe "Fast Food ltd."
    }

  }




  it should {

    lazy val view: Html = injectedView(details("GB987654321000","Fast Food ltd."))(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct page title" in {
      document.title mustBe ConfirmEORIDetailsMessages.title
    }

    s"have the correct h1 of '${ConfirmEORIDetailsMessages.h1}'" in {
      elementText("h1") mustBe ConfirmEORIDetailsMessages.h1
    }

    s"have correct EORI number title of '${ConfirmEORIDetailsMessages.eoriNumber}'" in {
      elementText("#main-content > div > div > dl > div:nth-child(1) > dt") mustBe ConfirmEORIDetailsMessages.eoriNumber
    }

    s"have correct name title of '${ConfirmEORIDetailsMessages.name}'" in {
      elementText("#main-content > div > div > dl > div:nth-child(2) > dt") mustBe ConfirmEORIDetailsMessages.name
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe ConfirmEORIDetailsMessages.continue
    }

    "render a continue button with the correct URL " in {
      elementAttributes(".govuk-button")  must contain("href" -> controllers.routes.UserTypeController.onLoad().url)
    }

  }

}
