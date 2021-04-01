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
import messages.ChangeUnderpaymentReasonMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.mvc.Call
import play.twirl.api.Html
import views.data.ChangeUnderpaymentReasonData._
import views.html.{ChangeUnderpaymentReasonView, UnderpaymentSummaryView}

class ChangeUnderpaymentReasonViewSpec extends ViewBaseSpec {

  private lazy val injectedView: ChangeUnderpaymentReasonView = app.injector.instanceOf[ChangeUnderpaymentReasonView]

  private val backLink: Call = Call("GET", "url")

  "Rendering the ChangeUnderpaymentReasonView page" when {
    "showing underpayment with item number" should {

      lazy val view: Html = injectedView(backLink, summaryList, singleItemReason.original.boxNumber )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe ChangeUnderpaymentReasonMessages.title(singleItemReason.original.boxNumber)
      }

      "have correct heading" in {
        document.select("h1").text mustBe ChangeUnderpaymentReasonMessages.title(singleItemReason.original.boxNumber)
      }

      "have only 1 Summary List" in {
        document.select(".govuk-summary-list").size mustBe 1
      }

      "have correct item number title" in {
        document.select(".govuk-summary-list__key").eachText.get(0) mustBe ChangeUnderpaymentReasonMessages.itemNumber
      }

      "have correct item number value" in {
        document.select(".govuk-summary-list__value").eachText.get(0) mustBe singleItemReason.original.itemNumber.toString
      }

      "have correct original amount title" in {
        document.select(".govuk-summary-list__key").eachText.get(1) mustBe ChangeUnderpaymentReasonMessages.originalValue
      }

      "have correct original amount value" in {
        document.select(".govuk-summary-list__value").eachText.get(1) mustBe singleItemReason.original.original
      }

      "have correct amended amount title" in {
        document.select(".govuk-summary-list__key").eachText.get(2) mustBe ChangeUnderpaymentReasonMessages.amendedValue
      }

      "have correct amended amount value" in {
        document.select(".govuk-summary-list__value").eachText.get(2) mustBe singleItemReason.original.amended
      }

      "have correct Change links" in {
        document.select(".govuk-summary-list__actions").eachText.get(0).trim mustBe
          ChangeUnderpaymentReasonMessages.change.trim

        document.select(".govuk-summary-list__actions").eachText.get(1).trim mustBe
          ChangeUnderpaymentReasonMessages.change.trim

        document.select(".govuk-summary-list__actions > a").eachAttr("href").get(0) mustBe
          controllers.routes.ChangeUnderpaymentReasonController.onLoad().url

        document.select(".govuk-summary-list__actions > a").eachAttr("href").get(1) mustBe
          controllers.routes.ChangeUnderpaymentReasonController.onLoad().url
      }
    }

    "showing underpayment without item number" should {

      lazy val view: Html = injectedView(backLink, entryLevelSummaryList, singleEntryLevelReason.original.boxNumber )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe ChangeUnderpaymentReasonMessages.title(singleEntryLevelReason.original.boxNumber)
      }

      "have correct heading" in {
        document.select("h1").text mustBe ChangeUnderpaymentReasonMessages.title(singleEntryLevelReason.original.boxNumber)
      }

      "have only 1 Summary List" in {
        document.select(".govuk-summary-list").size mustBe 1
      }

      "have correct original amount title" in {
        document.select(".govuk-summary-list__key").eachText.get(0) mustBe ChangeUnderpaymentReasonMessages.originalValue
      }

      "have correct original amount value" in {
        document.select(".govuk-summary-list__value").eachText.get(0) mustBe singleEntryLevelReason.original.original
      }

      "have correct amended amount title" in {
        document.select(".govuk-summary-list__key").eachText.get(1) mustBe ChangeUnderpaymentReasonMessages.amendedValue
      }

      "have correct amended amount value" in {
        document.select(".govuk-summary-list__value").eachText.get(1) mustBe singleEntryLevelReason.original.amended
      }

      "have correct Change link" in {
        document.select(".govuk-summary-list__actions").eachText.get(0).trim mustBe
          ChangeUnderpaymentReasonMessages.change.trim

        document.select(".govuk-summary-list__actions > a").eachAttr("href").get(0) mustBe
          controllers.routes.ChangeUnderpaymentReasonController.onLoad().url
      }
    }

  }

  it should {

    lazy val view: Html = injectedView(backLink, summaryList, singleItemReason.original.boxNumber)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> "url")
    }

    "have the remove link with the correct URL" in {
      elementAttributes("#remove-link") must contain("href" -> "#")
    }

    "have the remove link with the correct messahe" in {
      elementText("#remove-link") mustBe ChangeUnderpaymentReasonMessages.removeLink
    }

    "have the correct Continue button link" in {
      elementAttributes(".govuk-button") must contain("href" -> controllers.routes.ChangeUnderpaymentReasonController.onLoad().url)
    }

    "have the correct Continue button message" in {
      elementText(".govuk-button") mustBe ChangeUnderpaymentReasonMessages.backToReasons
    }

  }
}
