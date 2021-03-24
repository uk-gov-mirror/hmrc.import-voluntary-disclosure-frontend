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
import forms.RepresentativeDanFormProvider
import messages.{BaseMessages, RepresentativeDanMessages}
import models.RepresentativeDan
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.RepresentativeDanView

class RepresentativeDanViewSpec extends ViewBaseSpec with BaseMessages {

  val backLink = Call("GET","backLinkUrl")

  private lazy val injectedView: RepresentativeDanView = app.injector.instanceOf[RepresentativeDanView]

  val formProvider: RepresentativeDanFormProvider = injector.instanceOf[RepresentativeDanFormProvider]

  def repDanFormWithValues(accountNumber: String, danType: String): Form[RepresentativeDan] =
    formProvider().bind(Map("accountNumber" -> accountNumber, "value" -> danType))

  "Rendering the RepresentativeDan page" when {

    "no errors exist" should {
      val form: Form[RepresentativeDan] = formProvider.apply()
      lazy val view: Html = injectedView(
        form,
        backLink
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe RepresentativeDanMessages.title
      }

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the input field" in {
        document.select("#accountNumber-error").size mustBe 0
      }

      "not render an error message against the radio field" in {
        document.select("#value-error").size mustBe 0
      }
    }

    "an error exists (no value has been specified for the account number)" should {
      lazy val form: Form[RepresentativeDan] = repDanFormWithValues(emptyString, "A")
      lazy val view: Html = injectedView(
        form,
        backLink
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe RepresentativeDanMessages.errorPrefix + RepresentativeDanMessages.title
      }

      "render an error summary with the correct message " in {
        elementText("div.govuk-error-summary > div") mustBe RepresentativeDanMessages.accountNumberRequiredError
      }

      "render an error message against the field" in {
        elementText("#accountNumber-error") mustBe RepresentativeDanMessages.errorPrefix + RepresentativeDanMessages.accountNumberRequiredError
      }
    }

    "an error exists (account number value is an invalid format)" should {
      lazy val form: Form[RepresentativeDan] = repDanFormWithValues("!234567", "A")
      lazy val view: Html = injectedView(
        form,
        backLink
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe RepresentativeDanMessages.errorPrefix + RepresentativeDanMessages.title
      }

      "render an error summary with the correct message " in {
        elementText("div.govuk-error-summary > div") mustBe RepresentativeDanMessages.accountNumberFormatError
      }

      "render an error message against the field" in {
        elementText("#accountNumber-error") mustBe RepresentativeDanMessages.errorPrefix + RepresentativeDanMessages.accountNumberFormatError
      }
    }

    "an error exists (dan type radio selection has not been provided)" should {
      lazy val form: Form[RepresentativeDan] = repDanFormWithValues("1234567", emptyString)
      lazy val view: Html = injectedView(
        form,
        backLink
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe RepresentativeDanMessages.errorPrefix + RepresentativeDanMessages.title
      }

      "render an error summary with the correct message " in {
        elementText("div.govuk-error-summary > div") mustBe RepresentativeDanMessages.danTypeRequiredError
      }

      "render an error message against the field" in {
        elementText("#value-error") mustBe RepresentativeDanMessages.errorPrefix + RepresentativeDanMessages.danTypeRequiredError
      }
    }

  }

  it should {
    lazy val form: Form[RepresentativeDan] = formProvider()
    lazy val view: Html = injectedView(form, backLink)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct page title of '${RepresentativeDanMessages.title}'" in {
      document.title mustBe RepresentativeDanMessages.title
    }

    s"have the correct h1 of '${RepresentativeDanMessages.h1}'" in {
      elementText("h1") mustBe RepresentativeDanMessages.h1
    }

    s"have the correct accountNumber label of '${RepresentativeDanMessages.accountNumberLabel}'" in {
      elementText(".govuk-label[for=\"accountNumber\"]") mustBe RepresentativeDanMessages.accountNumberLabel
    }

    s"have the correct danType label of '${RepresentativeDanMessages.radioButtonLabel}'" in {
      elementText("#main-content > div > div > form > div:nth-child(3) > fieldset > legend") mustBe RepresentativeDanMessages.radioButtonLabel
    }

    s"have the correct value for the first radio button of '${RepresentativeDanMessages.radio1}'" in {
      elementText("#main-content > div > div > form > div:nth-child(3) > fieldset > div > div:nth-child(1) > label") mustBe RepresentativeDanMessages.radio1
    }

    s"have the correct value for the second radio button of '${RepresentativeDanMessages.radio2}'" in {
      elementText("#main-content > div > div > form > div:nth-child(3) > fieldset > div > div:nth-child(2) > label") mustBe RepresentativeDanMessages.radio2
    }

    s"have the correct value for the third radio button of '${RepresentativeDanMessages.radio3}'" in {
      elementText("#main-content > div > div > form > div:nth-child(3) > fieldset > div > div:nth-child(3) > label") mustBe RepresentativeDanMessages.radio3
    }

    s"have the correct radio 2 hint of '${RepresentativeDanMessages.radio2Hint}'" in {
      elementText("#value-2-item-hint") mustBe RepresentativeDanMessages.radio2Hint
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> backLink.url)
    }
  }
}
