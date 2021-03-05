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
import forms.MoreInformationFormProvider
import messages.{BaseMessages, MoreInformationMessages}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.MoreInformationView

class MoreInformationViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: MoreInformationView = app.injector.instanceOf[MoreInformationView]

  val formProvider: MoreInformationFormProvider = injector.instanceOf[MoreInformationFormProvider]

  "Rendering the More Information page" when {
    "no errors exist" should {

      val form: Form[String] = formProvider.apply()
      lazy val view: Html = injectedView(form, Call("GET", "url"))(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe MoreInformationMessages.title
      }

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }
    }

    "no data supplied" should {

      "an error exists" should {
        lazy val form: Form[String] = formProvider().bind(Map("value" -> ""))
        lazy val view: Html = injectedView(form, Call("GET", "url"))(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "update the page title to include the error prefix" in {
          document.title mustBe MoreInformationMessages.errorPrefix + MoreInformationMessages.title
        }

        "render an error summary with the correct message" in {
          elementText("div.govuk-error-summary > div") mustBe MoreInformationMessages.requiredError
        }

        "render an error message against the field" in {
          elementText("#value-error") mustBe MoreInformationMessages.errorPrefix + MoreInformationMessages.requiredError
        }

      }
    }

  }

  it should {

    val form: Form[String] = formProvider.apply()
    lazy val view: Html = injectedView(form, Call("GET", "url"))(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct h1 of '${MoreInformationMessages.h1}'" in {
      elementText("h1") mustBe MoreInformationMessages.h1
    }

    "render a text area" in {
      document.select("#value").size() mustBe 1
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> "url")
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }

  }
}
