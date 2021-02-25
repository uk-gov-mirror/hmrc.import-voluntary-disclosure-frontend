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
import forms.EnterCustomsProcedureCodeFormProvider
import messages.{BaseMessages, EnterCustomsProcedureCodeMessages}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.EnterCustomsProcedureCodeView

class EnterCustomsProcedureCodeViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: EnterCustomsProcedureCodeView = app.injector.instanceOf[EnterCustomsProcedureCodeView]

  val formProvider: EnterCustomsProcedureCodeFormProvider = injector.instanceOf[EnterCustomsProcedureCodeFormProvider]

  "Rendering the Customs Procedure Code page" when {
    "no errors exist" should {

      val form: Form[String] = formProvider.apply()
      lazy val view: Html = injectedView(form, Call("GET", controllers.routes.EntryDetailsController.onLoad().url))(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe EnterCustomsProcedureCodeMessages.title
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
        lazy val form: Form[String] = formProvider().bind(Map("cpc" -> ""))
        lazy val view: Html = injectedView(form, Call("GET", controllers.routes.EntryDetailsController.onLoad().url))(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "update the page title to include the error prefix" in {
          document.title mustBe EnterCustomsProcedureCodeMessages.errorPrefix + EnterCustomsProcedureCodeMessages.title
        }

        "render an error summary with the correct message" in {
          elementText("div.govuk-error-summary > div") mustBe EnterCustomsProcedureCodeMessages.requiredError
        }

        "render an error message against the field" in {
          elementText("#cpc-error") mustBe EnterCustomsProcedureCodeMessages.errorPrefix + EnterCustomsProcedureCodeMessages.requiredError
        }

      }
    }

    "invalid data supplied" should {

      "an error exists" should {
        lazy val form: Form[String] = formProvider().bind(Map("cpc" -> "A1234567"))
        lazy val view: Html = injectedView(form, Call("GET", controllers.routes.EntryDetailsController.onLoad().url))(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "update the page title to include the error prefix" in {
          document.title mustBe EnterCustomsProcedureCodeMessages.errorPrefix + EnterCustomsProcedureCodeMessages.title
        }

        "render an error summary with the correct message" in {
          elementText("div.govuk-error-summary > div") mustBe EnterCustomsProcedureCodeMessages.formatError
        }

        "render an error message against the field" in {
          elementText("#cpc-error") mustBe EnterCustomsProcedureCodeMessages.errorPrefix + EnterCustomsProcedureCodeMessages.formatError
        }

      }
    }
  }

  it should {

    val form: Form[String] = formProvider.apply()
    lazy val view: Html = injectedView(form, Call("GET", controllers.routes.EntryDetailsController.onLoad().url))(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct h1 of '${EnterCustomsProcedureCodeMessages.h1}'" in {
      elementText("h1") mustBe EnterCustomsProcedureCodeMessages.h1
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> controllers.routes.EntryDetailsController.onLoad().url)
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }

  }
}
