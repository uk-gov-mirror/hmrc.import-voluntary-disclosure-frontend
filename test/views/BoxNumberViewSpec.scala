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
import forms.BoxNumberFormProvider
import messages.{BaseMessages, BoxNumberMessages}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.Html
import views.html.BoxNumberView

class BoxNumberViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: BoxNumberView = app.injector.instanceOf[BoxNumberView]

  val formProvider: BoxNumberFormProvider = injector.instanceOf[BoxNumberFormProvider]


  "Rendering the Box number page" when {
    "no errors exist" should {

      val form: Form[Int] = formProvider.apply()
      lazy val view: Html = injectedView(form, controllers.routes.BoxGuidanceController.onLoad())(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe BoxNumberMessages.pageTitle
      }

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }
    }

    "an error exists (no option has been selected)" should {
      val form: Form[Int] = formProvider().bind(Map("value" -> ""))
      lazy val view: Html = injectedView(form, controllers.routes.BoxGuidanceController.onLoad())(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "update the page title to include the error prefix" in {
        document.title mustBe BoxNumberMessages.errorPrefix + BoxNumberMessages.pageTitle
      }

      "render an error summary with the correct message" in {
        elementText("div.govuk-error-summary > div") mustBe BoxNumberMessages.errorRequired
      }

      "render an error message against the field" in {
        elementText("#value-error") mustBe BoxNumberMessages.errorPrefix + BoxNumberMessages.errorRequired
      }

    }

  }

  it should {

    val form: Form[Int] = formProvider.apply()
    lazy val view: Html = injectedView(form, controllers.routes.BoxGuidanceController.onLoad())(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct h1 of '${BoxNumberMessages.heading}'" in {
      elementText("h1") mustBe BoxNumberMessages.heading
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> controllers.routes.BoxGuidanceController.onLoad().url)
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }

    s"the input field is rendered" in {
      document.select("#value").size mustBe 1
    }

  }

}
