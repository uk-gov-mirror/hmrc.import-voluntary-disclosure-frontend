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
import messages.ConfirmReasonDetailMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.mvc.Call
import play.twirl.api.Html
import views.data.ConfirmReasonData.reasons
import views.html.ConfirmReasonDetailView

class ConfirmReasonDetailViewSpec extends ViewBaseSpec {

  private lazy val injectedView: ConfirmReasonDetailView = app.injector.instanceOf[ConfirmReasonDetailView]

  private val backLink: Call = Call("GET", "url")


  "Rendering the Confirm Reason Detail page" when {
    "when an item level box is selected" should {

      lazy val view: Html = injectedView(reasons(33, Some(1), "1806321000", "2204109400X411"), backLink)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have only 1 Summary List" in {
        document.select(".govuk-summary-list").size mustBe 1
      }

      "have 4 Summary List Rows" in {
        document.select(".govuk-summary-list__row").size mustBe 4
      }

      "have correct box number title" in {
        elementText("#main-content > div > div > dl > div:nth-child(1) > dt") mustBe ConfirmReasonDetailMessages.boxNumber
      }

      "have correct item number title" in {
        elementText("#main-content > div > div > dl > div:nth-child(2) > dt") mustBe ConfirmReasonDetailMessages.itemNumber
      }

      "have correct original value title" in {
        elementText("#main-content > div > div > dl > div:nth-child(3) > dt") mustBe ConfirmReasonDetailMessages.originalValue
      }

      "have correct amended value title" in {
        elementText("#main-content > div > div > dl > div:nth-child(4) > dt") mustBe ConfirmReasonDetailMessages.amendedValue
      }

      "have correct box number value" in {
        elementText("#main-content > div > div > dl > div:nth-child(1) > dd.govuk-summary-list__value") mustBe "33"
      }

      "have correct item number value" in {
        elementText("#main-content > div > div > dl > div:nth-child(2) > dd.govuk-summary-list__value") mustBe "1"
      }

      "have correct original value" in {
        elementText("#main-content > div > div > dl > div:nth-child(3) > dd.govuk-summary-list__value") mustBe "1806321000"
      }

      "have correct amended value" in {
        elementText("#main-content > div > div > dl > div:nth-child(4) > dd.govuk-summary-list__value") mustBe "2204109400X411"
      }

      "have correct Change link for Box Number " in {
        elementText("#main-content > div > div > dl > div:nth-child(1) > dd.govuk-summary-list__actions > a") mustBe
          (ConfirmReasonDetailMessages.change).trim

        document.select("#main-content > div > div > dl > div:nth-child(1) > dd.govuk-summary-list__actions > a").attr("href") mustBe
          controllers.routes.BoxNumberController.onLoad().url
      }
      "have correct Change link for Item Number " in {
        elementText("#main-content > div > div > dl > div:nth-child(2) > dd.govuk-summary-list__actions > a") mustBe
          (ConfirmReasonDetailMessages.change).trim

        document.select("#main-content > div > div > dl > div:nth-child(2) > dd.govuk-summary-list__actions > a").attr("href") mustBe
          controllers.routes.ItemNumberController.onLoad().url
      }

      "have correct Change link for original/amended value " in {
        elementText("#main-content > div > div > dl > div:nth-child(3) > dd.govuk-summary-list__actions > a") mustBe
          (ConfirmReasonDetailMessages.change).trim

        document.select("#main-content > div > div > dl > div:nth-child(3) > dd.govuk-summary-list__actions > a").attr("href") mustBe
          controllers.routes.UnderpaymentReasonAmendmentController.onLoad(33).url
      }

    }
  }

  "Rendering the Confirm Reason Detail page" when {
    "when an entry level box is selected" should {

      lazy val view: Html = injectedView(reasons(22, None, "EUR125.00", "GBP190.50"), backLink)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have only 1 Summary List" in {
        document.select(".govuk-summary-list").size mustBe 1
      }

      "have 3 Summary List Rows" in {
        document.select(".govuk-summary-list__row").size mustBe 3
      }

      "have correct box number title" in {
        elementText("#main-content > div > div > dl > div:nth-child(1) > dt") mustBe ConfirmReasonDetailMessages.boxNumber
      }

      "have correct original value title" in {
        elementText("#main-content > div > div > dl > div:nth-child(2) > dt") mustBe ConfirmReasonDetailMessages.originalValue
      }

      "have correct amended value title" in {
        elementText("#main-content > div > div > dl > div:nth-child(3) > dt") mustBe ConfirmReasonDetailMessages.amendedValue
      }

      "have correct box number value" in {
        elementText("#main-content > div > div > dl > div:nth-child(1) > dd.govuk-summary-list__value") mustBe "22"
      }

      "have correct original value" in {
        elementText("#main-content > div > div > dl > div:nth-child(2) > dd.govuk-summary-list__value") mustBe "EUR125.00"
      }

      "have correct amended value" in {
        elementText("#main-content > div > div > dl > div:nth-child(3) > dd.govuk-summary-list__value") mustBe "GBP190.50"
      }

      "have correct Change link for Box Number " in {
        elementText("#main-content > div > div > dl > div:nth-child(1) > dd.govuk-summary-list__actions > a") mustBe
          (ConfirmReasonDetailMessages.change).trim

        document.select("#main-content > div > div > dl > div:nth-child(1) > dd.govuk-summary-list__actions > a").attr("href") mustBe
          controllers.routes.BoxNumberController.onLoad().url
      }

      "have correct Change link for original/amended value " in {
        elementText("#main-content > div > div > dl > div:nth-child(2) > dd.govuk-summary-list__actions > a") mustBe
          (ConfirmReasonDetailMessages.change).trim

        document.select("#main-content > div > div > dl > div:nth-child(2) > dd.govuk-summary-list__actions > a").attr("href") mustBe
          controllers.routes.UnderpaymentReasonAmendmentController.onLoad(22).url
      }
    }
  }

  it should {

    lazy val view: Html = injectedView(reasons(22, None, "EUR125.00", "GBP190.50"), backLink)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct page title" in {
      document.title mustBe ConfirmReasonDetailMessages.title
    }

    s"have the correct h1 of '${ConfirmReasonDetailMessages.h1}'" in {
      elementText("h1") mustBe ConfirmReasonDetailMessages.h1
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> "url")
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe ConfirmReasonDetailMessages.continue
    }

  }

}
