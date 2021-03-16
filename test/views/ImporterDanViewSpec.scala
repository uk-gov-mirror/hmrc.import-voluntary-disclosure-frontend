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
import forms.ImporterDanFormProvider
import messages.{BaseMessages, ImporterDanMessages}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.ImporterDanView

class ImporterDanViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: ImporterDanView = app.injector.instanceOf[ImporterDanView]
  val backLinkUrl = "backLinkUrl"

  val formProvider: ImporterDanFormProvider = injector.instanceOf[ImporterDanFormProvider]

  "Rendering the Importer DAN page" when {
    "no errors exist" should {

      val form: Form[String] = formProvider.apply()
      lazy val view: Html = injectedView(form, Call("GET", backLinkUrl))(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe ImporterDanMessages.title
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
        lazy val view: Html = injectedView(form, Call("GET", backLinkUrl))(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "update the page title to include the error prefix" in {
          document.title mustBe ImporterDanMessages.errorPrefix + ImporterDanMessages.title
        }

        "render an error summary with the correct message" in {
          elementText("div.govuk-error-summary > div") mustBe ImporterDanMessages.requiredError
        }

        "render an error message against the field" in {
          elementText("#value-error") mustBe ImporterDanMessages.errorPrefix + ImporterDanMessages.requiredError
        }

      }
    }

    "invalid data supplied" should {

      "an error exists" should {
        lazy val form: Form[String] = formProvider().bind(Map("value" -> "A1234567"))
        lazy val view: Html = injectedView(form, Call("GET", backLinkUrl))(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "update the page title to include the error prefix" in {
          document.title mustBe ImporterDanMessages.errorPrefix + ImporterDanMessages.title
        }

        "render an error summary with the correct message" in {
          elementText("div.govuk-error-summary > div") mustBe ImporterDanMessages.formatError
        }

        "render an error message against the field" in {
          elementText("#value-error") mustBe ImporterDanMessages.errorPrefix + ImporterDanMessages.formatError
        }

      }
    }
  }

  it should {

    val form: Form[String] = formProvider.apply()
    lazy val view: Html = injectedView(form, Call("GET", backLinkUrl))(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct h1 of '${ImporterDanMessages.h1}'" in {
      elementText("h1") mustBe ImporterDanMessages.h1
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> backLinkUrl)
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }

  }
}
