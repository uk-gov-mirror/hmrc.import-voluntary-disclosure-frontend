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
import messages.UnderpaymentSummaryMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.mvc.Call
import play.twirl.api.Html
import views.data.UnderpaymentSummaryData._
import views.html.UnderpaymentSummaryView

class UnderpaymentSummaryViewSpec extends ViewBaseSpec {

  private lazy val injectedView: UnderpaymentSummaryView = app.injector.instanceOf[UnderpaymentSummaryView]

  private val backLink: Call = Call("GET", "url")

  "Rendering the UnderpaymentSummary page" when {
    "only 1 underpayment made" should {

      lazy val view: Html = injectedView(None, importVat, None, backLink)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have only 1 sub-heading" in {
        document.select("main h2").size mustBe 1
      }

      "have correct single sub-heading" in {
        document.select("main h2").text mustBe UnderpaymentSummaryMessages.importVatTitle
      }

      "have only 1 Summary List" in {
        document.select(".govuk-summary-list").size mustBe 1
      }

      "have 3 Summary List Rows" in {
        document.select(".govuk-summary-list__row").size mustBe 3
      }

      "have correct original amount title" in {
        document.select(".govuk-summary-list__key").eachText.get(0) mustBe UnderpaymentSummaryMessages.originalAmount
      }

      "have correct original amount value" in {
        document.select(".govuk-summary-list__value").eachText.get(0) mustBe "£100.00"
      }

      "have correct amended amount title" in {
        document.select(".govuk-summary-list__key").eachText.get(1) mustBe UnderpaymentSummaryMessages.amendedAmount
      }

      "have correct amended amount value" in {
        document.select(".govuk-summary-list__value").eachText.get(1) mustBe "£1,000.00"
      }

      "have correct due amount title" in {
        document.select(".govuk-summary-list__key").eachText.get(2) mustBe
          UnderpaymentSummaryMessages.importVatTitle + UnderpaymentSummaryMessages.dueToHmrc
      }

      "have correct due amount value" in {
        document.select(".govuk-summary-list__value").eachText.get(2) mustBe "£900.00"
      }

      "have correct Change link " in {
        document.select(".govuk-summary-list__actions").text.trim mustBe
          (UnderpaymentSummaryMessages.change + " " + UnderpaymentSummaryMessages.importVatTitle).trim

        document.select(".govuk-summary-list__actions > a").attr("href") mustBe
          controllers.routes.UnderpaymentSummaryController.onLoad().url
      }
    }

    "all 3 underpayment made" should {

      lazy val view: Html = injectedView(customsDuty, importVat, exciseDuty, backLink)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have 3 sub-headings" in {
        document.select("main h2").size mustBe 3
      }

      "have correct sub-headings" in {
        document.select("main h2").eachText.get(0) mustBe UnderpaymentSummaryMessages.customsDutyTitle
        document.select("main h2").eachText.get(1) mustBe UnderpaymentSummaryMessages.importVatTitle
        document.select("main h2").eachText.get(2) mustBe UnderpaymentSummaryMessages.exciseDutyTitle
      }

      "have 3 Summary Lists" in {
        document.select(".govuk-summary-list").size mustBe 3
      }

      "have 9 Summary List Rows" in {
        document.select(".govuk-summary-list__row").size mustBe 9
      }

      "have correct Change link " in {
        document.select(".govuk-summary-list__actions").eachText.get(0).trim mustBe
          (UnderpaymentSummaryMessages.change + " " + UnderpaymentSummaryMessages.customsDutyTitle).trim

        document.select(".govuk-summary-list__actions").eachText.get(1).trim mustBe
          (UnderpaymentSummaryMessages.change + " " + UnderpaymentSummaryMessages.importVatTitle).trim

        document.select(".govuk-summary-list__actions").eachText.get(2).trim mustBe
          (UnderpaymentSummaryMessages.change + " " + UnderpaymentSummaryMessages.exciseDutyTitle).trim

        document.select(".govuk-summary-list__actions > a").eachAttr("href").get(0) mustBe
          controllers.routes.UnderpaymentSummaryController.onLoad().url

        document.select(".govuk-summary-list__actions > a").eachAttr("href").get(1) mustBe
          controllers.routes.UnderpaymentSummaryController.onLoad().url

        document.select(".govuk-summary-list__actions > a").eachAttr("href").get(2) mustBe
          controllers.routes.UnderpaymentSummaryController.onLoad().url
      }
    }
  }

  it should {

    lazy val view: Html = injectedView(customsDuty, importVat, exciseDuty, backLink)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct page title" in {
      document.title mustBe UnderpaymentSummaryMessages.title
    }

    s"have the correct h1 of '${UnderpaymentSummaryMessages.h1}'" in {
      elementText("h1") mustBe UnderpaymentSummaryMessages.h1
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> "url")
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe UnderpaymentSummaryMessages.continue
    }

  }
}
