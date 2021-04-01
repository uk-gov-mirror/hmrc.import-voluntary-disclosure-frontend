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
import forms.RemoveUnderpaymentReasonFormProvider
import messages.RemoveUnderpaymentReasonMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.RemoveUnderpaymentReasonView

class RemoveUnderpaymentReasonViewSpec extends ViewBaseSpec {

  private lazy val injectedView: RemoveUnderpaymentReasonView = app.injector.instanceOf[RemoveUnderpaymentReasonView]
  val backLink = Call("GET", "backLink/url")

  val boxNumber = 35
  val itemNumber = 1

  val formProvider: RemoveUnderpaymentReasonFormProvider = injector.instanceOf[RemoveUnderpaymentReasonFormProvider]

  "Rendering the Remove Underpayment Reason page" when {

    "no errors exist" should {
      val form: Form[Boolean] = formProvider.apply()
      lazy val view: Html = injectedView(
        form,
        backLink,
        boxNumber,
        itemNumber
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe RemoveUnderpaymentReasonMessages.title
      }

      s"have the correct paragraph" in {
        document.select("#main-content > div > div > p").text() mustBe RemoveUnderpaymentReasonMessages.box35Paragraph(itemNumber)
      }

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }
    }

    "an error exists" should {
      lazy val form: Form[Boolean] = formProvider().bind(Map("value" -> ""))
      lazy val view: Html = injectedView(
        form,
        backLink,
        boxNumber,
        itemNumber
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "update the page title to include the error prefix" in {
        document.title mustBe RemoveUnderpaymentReasonMessages.errorPrefix + RemoveUnderpaymentReasonMessages.title
      }

      "render an error summary with the correct message" in {
        elementText("div.govuk-error-summary > div") mustBe RemoveUnderpaymentReasonMessages.requiredError
      }

      "render an error message against the field" in {
        elementText("#value-error") mustBe RemoveUnderpaymentReasonMessages.errorPrefix + RemoveUnderpaymentReasonMessages.requiredError
      }
    }

  }

  it should {

    val form: Form[Boolean] = formProvider.apply()
    lazy val view: Html = injectedView(
      form,
      backLink,
      boxNumber,
      itemNumber
    )(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct value for the first radio button of '${RemoveUnderpaymentReasonMessages.radioYes}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(1) > label") mustBe RemoveUnderpaymentReasonMessages.radioYes
    }

    s"have the correct value for the second radio button of '${RemoveUnderpaymentReasonMessages.radioNo}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(2) > label") mustBe RemoveUnderpaymentReasonMessages.radioNo
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> backLink.url)
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe RemoveUnderpaymentReasonMessages.continue
    }

  }
}
