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
import forms.ItemNumberFormProvider
import messages.{BaseMessages, ItemNumberMessages}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.ItemNumberView

class ItemNumberViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: ItemNumberView = app.injector.instanceOf[ItemNumberView]

  val formProvider: ItemNumberFormProvider = injector.instanceOf[ItemNumberFormProvider]

  "Rendering the Item Number page" when {
    "no errors exist" should {

      val form: Form[Int] = formProvider.apply()
      lazy val view: Html = injectedView(form, Call("GET", controllers.routes.BoxNumberController.onLoad().url) )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe ItemNumberMessages.title
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
        lazy val form: Form[Int] = formProvider().bind(Map("itemNumber" -> ""))
        lazy val view: Html = injectedView(form, Call("GET", controllers.routes.BoxNumberController.onLoad().url) )(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "update the page title to include the error prefix" in {
          document.title mustBe ItemNumberMessages.errorPrefix + ItemNumberMessages.title
        }

        "render an error summary with the correct message" in {
          elementText("div.govuk-error-summary > div") mustBe ItemNumberMessages.requiredError
        }

        "render an error message against the field" in {
          elementText("#itemNumber-error") mustBe ItemNumberMessages.errorPrefix + ItemNumberMessages.requiredError
        }

      }
    }
  }

  it should {

    val form: Form[Int] = formProvider.apply()
    lazy val view: Html = injectedView(form, Call("GET", controllers.routes.BoxNumberController.onLoad().url) )(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct h1 of '${ItemNumberMessages.h1}'" in {
      elementText("h1") mustBe ItemNumberMessages.h1
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> controllers.routes.BoxNumberController.onLoad().url)
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }

  }
}
