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
import messages.{ConfirmChangeReasonDetailMessages, ConfirmReasonDetailMessages}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.mvc.Call
import play.twirl.api.Html
import views.data.ConfirmChangeReasonData.reasons
import views.html.ConfirmChangeReasonDetailView

class ConfirmChangeReasonDetailViewSpec extends ViewBaseSpec {

  private lazy val injectedView: ConfirmChangeReasonDetailView = app.injector.instanceOf[ConfirmChangeReasonDetailView]

  private val backLink: Call = Call("GET", "url")


  "Rendering the Confirm Change Reason Detail page" when {
    "when an item level box is selected" should {

      lazy val view: Html = injectedView(reasons(33, Some(1), "1806321000", "2204109400X411"), 33, backLink)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have only 1 Summary List" in {
        document.select(".govuk-summary-list").size mustBe 1
      }

      "have 3 Summary List Rows" in {
        document.select(".govuk-summary-list__row").size mustBe 3
      }

      "have correct item number title" in {
        elementText("#main-content > div > div > dl > div:nth-child(1) > dt") mustBe ConfirmReasonDetailMessages.itemNumber
      }

      "have correct original value title" in {
        elementText("#main-content > div > div > dl > div:nth-child(2) > dt") mustBe ConfirmReasonDetailMessages.originalValue
      }

      "have correct amended value title" in {
        elementText("#main-content > div > div > dl > div:nth-child(3) > dt") mustBe ConfirmReasonDetailMessages.amendedValue
      }

      "have correct item number value" in {
        elementText("#main-content > div > div > dl > div:nth-child(1) > dd.govuk-summary-list__value") mustBe "1"
      }

      "have correct original value" in {
        elementText("#main-content > div > div > dl > div:nth-child(2) > dd.govuk-summary-list__value") mustBe "1806321000"
      }

      "have correct amended value" in {
        elementText("#main-content > div > div > dl > div:nth-child(3) > dd.govuk-summary-list__value") mustBe "2204109400X411"
      }

      "have correct Change link for Item Number " in {
        elementText("#main-content > div > div > dl > div:nth-child(1) > dd.govuk-summary-list__actions > a") mustBe
          (ConfirmReasonDetailMessages.change).trim

        document.select("#main-content > div > div > dl > div:nth-child(1) > dd.govuk-summary-list__actions > a").attr("href") mustBe
          controllers.routes.ChangeItemNumberController.onLoad().url
      }

      "have correct Change link for original/amended value " in {
        elementText("#main-content > div > div > dl > div:nth-child(2) > dd.govuk-summary-list__actions > a") mustBe
          (ConfirmReasonDetailMessages.change).trim

        document.select("#main-content > div > div > dl > div:nth-child(2) > dd.govuk-summary-list__actions > a").attr("href") mustBe
          controllers.routes.ChangeUnderpaymentReasonDetailsController.onLoad(33).url
      }

    }
  }

  "Rendering the Confirm Change Reason Detail page" when {
    "when an entry level box is selected" should {

      lazy val view: Html = injectedView(reasons(22, None, "EUR125.00", "GBP190.50"), 22, backLink)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have only 1 Summary List" in {
        document.select(".govuk-summary-list").size mustBe 1
      }

      "have 2 Summary List Rows" in {
        document.select(".govuk-summary-list__row").size mustBe 2
      }

      "have correct original value title" in {
        elementText("#main-content > div > div > dl > div:nth-child(1) > dt") mustBe ConfirmReasonDetailMessages.originalValue
      }

      "have correct amended value title" in {
        elementText("#main-content > div > div > dl > div:nth-child(2) > dt") mustBe ConfirmReasonDetailMessages.amendedValue
      }

      "have correct original value" in {
        elementText("#main-content > div > div > dl > div:nth-child(1) > dd.govuk-summary-list__value") mustBe "EUR125.00"
      }

      "have correct amended value" in {
        elementText("#main-content > div > div > dl > div:nth-child(2) > dd.govuk-summary-list__value") mustBe "GBP190.50"
      }

      "have correct Change link for original/amended value " in {
        elementText("#main-content > div > div > dl > div:nth-child(1) > dd.govuk-summary-list__actions > a") mustBe
          (ConfirmReasonDetailMessages.change).trim

        document.select("#main-content > div > div > dl > div:nth-child(1) > dd.govuk-summary-list__actions > a").attr("href") mustBe
          controllers.routes.ChangeUnderpaymentReasonDetailsController.onLoad(22).url
      }
    }
  }

  it should {

    lazy val view: Html = injectedView(reasons(22, None, "EUR125.00", "GBP190.50"), 22, backLink)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct page title" in {
      document.title mustBe ConfirmChangeReasonDetailMessages.title(22)
    }

    s"have the correct h1 of '${ConfirmChangeReasonDetailMessages.h1(22)}'" in {
      elementText("h1") mustBe ConfirmChangeReasonDetailMessages.h1(22)
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> "url")
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe ConfirmChangeReasonDetailMessages.continue
    }

  }

}
