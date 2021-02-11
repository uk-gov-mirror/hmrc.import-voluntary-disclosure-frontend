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
import messages.CYAMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.mvc.Call
import play.twirl.api.Html
import views.data.CheckYourAnswersData
import views.data.CheckYourAnswersData._
import views.html.CheckYourAnswersView

class CheckYourAnswersViewSpec extends ViewBaseSpec {

  private lazy val injectedView: CheckYourAnswersView = app.injector.instanceOf[CheckYourAnswersView]

  private val backLink: Call = Call("GET", "url")

  "Rendering the Check Your Answers page" when {
    "multiple answers provided" should {
      lazy val view: Html = injectedView(answers, backLink)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have ${answers.size + 1} sub-headings" in {
        document.select("main h2").size mustBe answers.size + 1
      }

      "have correct sub-headings" in {
        val subHeadings = document.select("main h2")
        answers.zipWithIndex.map {
          case (answer, index) => subHeadings.get(index).text mustBe answer.heading
        }
      }

      s"have ${answers.size} Summary Lists" in {
        document.select(".govuk-summary-list").size mustBe answers.size
      }
    }

    "single answer provided" should {
      lazy val view: Html = injectedView(Seq(underpaymentAnswers), backLink)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have ${CYAMessages.underpaymentDetails} sub-heading" in {
        document.select("main h2").first.text mustBe CYAMessages.underpaymentDetails
      }

      s"have 1 Summary List" in {
        document.select(".govuk-summary-list").size mustBe 1
      }

      "have 3 Summary List Rows" in {
        document.select(".govuk-summary-list__row").size mustBe 3
      }

      "have correct customs duty key" in {
        document.select(".govuk-summary-list__key").eachText.get(0) mustBe CYAMessages.customsDuty
      }

      "have correct customs duty value" in {
        document.select(".govuk-summary-list__value").eachText.get(0) mustBe "£5,000.00"
      }

      "have correct customs duty Change link " in {
        document.select(".govuk-summary-list__actions").eachText.get(0).trim mustBe CYAMessages.change.trim
        document.select(".govuk-summary-list__actions > a").eachAttr("href").get(0) mustBe changeUrl
      }

      "have correct Import VAT key" in {
        document.select(".govuk-summary-list__key").eachText.get(1) mustBe CYAMessages.importVAT
      }

      "have correct Import VAT value" in {
        document.select(".govuk-summary-list__value").eachText.get(1) mustBe "£900.00"
      }

      "have correct Import VAT Change link " in {
        document.select(".govuk-summary-list__actions").eachText.get(1).trim mustBe CYAMessages.change.trim
        document.select(".govuk-summary-list__actions > a").eachAttr("href").get(1) mustBe changeUrl
      }

      "have correct excise duty key" in {
        document.select(".govuk-summary-list__key").eachText.get(2) mustBe CYAMessages.exciseDuty
      }

      "have correct excise duty value" in {
        document.select(".govuk-summary-list__value").eachText.get(2) mustBe "£140.00"
      }

      "have correct excise duty Change link " in {
        document.select(".govuk-summary-list__actions").eachText.get(2).trim mustBe CYAMessages.change.trim
        document.select(".govuk-summary-list__actions > a").eachAttr("href").get(2) mustBe changeUrl
      }
    }
  }

  it should {

    lazy val view: Html = injectedView(answers, backLink)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct page title" in {
      document.title mustBe CYAMessages.title
    }

    s"have the correct h1 of '${CYAMessages.heading}'" in {
      elementText("h1") mustBe CYAMessages.heading
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> "url")
    }

    "have Now Send Disclosure sub-heading " in {
      document.select("main h2").last.text mustBe CYAMessages.sendDisclosure
    }

    "have Now Send Disclosure message " in {
      document.select("main p").text mustBe CYAMessages.disclosureConfirmation
    }

    s"have the correct Accept button" in {
      elementText(".govuk-button") mustBe CYAMessages.acceptAndSend
    }

  }
}
