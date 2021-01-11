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
import forms.AcceptanceDateFormProvider
import messages.{AcceptanceDateMessages, BaseMessages}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.Html
import views.html.AcceptanceDateView

class AcceptanceDateViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: AcceptanceDateView = app.injector.instanceOf[AcceptanceDateView]

  val formProvider: AcceptanceDateFormProvider = injector.instanceOf[AcceptanceDateFormProvider]

  "Rendering the AcceptanceDate page" when {
    "no errors exist" should {

      val form: Form[Boolean] = formProvider.apply()
      lazy val view: Html = injectedView(form)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe AcceptanceDateMessages.title
      }

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }
    }

    "an error exists (no option has been selected)" should {
      lazy val form: Form[Boolean] = formProvider().bind(Map("value" -> ""))
      lazy val view: Html = injectedView(form)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "update the page title to include the error prefix" in {
        document.title mustBe AcceptanceDateMessages.errorPrefix + AcceptanceDateMessages.title
      }

      "render an error summary with the correct message" in {
        elementText("div.govuk-error-summary > div") mustBe AcceptanceDateMessages.requiredError
      }

      "render an error message against the field" in {
        elementText("#value-error") mustBe AcceptanceDateMessages.errorPrefix + AcceptanceDateMessages.requiredError
      }

    }
  }

  it should {

    val form: Form[Boolean] = formProvider.apply()
    lazy val view: Html = injectedView(form)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct h1 of '${AcceptanceDateMessages.h1}'" in {
      elementText("h1") mustBe AcceptanceDateMessages.h1
    }

    s"have the correct value for the first radio button of '${AcceptanceDateMessages.radioButtonOne}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(1)") mustBe AcceptanceDateMessages.radioButtonOne
    }

    s"have the correct value for the second radio button of '${AcceptanceDateMessages.radioButtonTwo}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(2)") mustBe AcceptanceDateMessages.radioButtonTwo
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> controllers.routes.EntryDetailsController.onLoad().url)
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }

  }
}
