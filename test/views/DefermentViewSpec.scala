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
import forms.DefermentFormProvider
import messages.{BaseMessages, DefermentMessages}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.DefermentView

class DefermentViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: DefermentView = app.injector.instanceOf[DefermentView]

  val formProvider: DefermentFormProvider = injector.instanceOf[DefermentFormProvider]

  "Rendering the Deferment page" when {

    "no errors exist VAT only" should {
      val form: Form[Boolean] = formProvider.apply()
      lazy val view: Html = injectedView(
        form,
        controllers.routes.DeclarantContactDetailsController.onLoad(),
        "deferment.headingOnlyVAT"
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe DefermentMessages.headingOnlyVAT
      }

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }
    }

    "no errors exist duty only" should {
      val form: Form[Boolean] = formProvider.apply()
      lazy val view: Html = injectedView(
        form,
        controllers.routes.DeclarantContactDetailsController.onLoad(),
        "deferment.headingDutyOnly"
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe DefermentMessages.headingDutyOnly
      }

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }
    }

    "no errors exist duty and VAT" should {
      val form: Form[Boolean] = formProvider.apply()
      lazy val view: Html = injectedView(
        form,
        controllers.routes.DeclarantContactDetailsController.onLoad(),
        "deferment.headingVATandDuty"
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe DefermentMessages.headingVATandDuty
      }

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }
    }

    "an error exists (no option has been selected) duty only" should {
      lazy val form: Form[Boolean] = formProvider().bind(Map("value" -> ""))
      lazy val view: Html = injectedView(
        form,
        controllers.routes.DeclarantContactDetailsController.onLoad(),
        "deferment.headingDutyOnly"
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "update the page title to include the error prefix" in {
        document.title mustBe DefermentMessages.errorPrefix + DefermentMessages.headingDutyOnly
      }

      "render an error summary with the correct message" in {
        elementText("div.govuk-error-summary > div") mustBe DefermentMessages.requiredError
      }

      "render an error message against the field" in {
        elementText("#value-error") mustBe DefermentMessages.errorPrefix + DefermentMessages.requiredError
      }
    }

    "an error exists (no option has been selected) VAT only" should {
      lazy val form: Form[Boolean] = formProvider().bind(Map("value" -> ""))
      lazy val view: Html = injectedView(
        form,
        controllers.routes.DeclarantContactDetailsController.onLoad(),
        "deferment.headingOnlyVAT"
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "update the page title to include the error prefix" in {
        document.title mustBe DefermentMessages.errorPrefix + DefermentMessages.headingOnlyVAT
      }

      "render an error summary with the correct message" in {
        elementText("div.govuk-error-summary > div") mustBe DefermentMessages.requiredError
      }

      "render an error message against the field" in {
        elementText("#value-error") mustBe DefermentMessages.errorPrefix + DefermentMessages.requiredError
      }
    }

    "an error exists (no option has been selected) VAT and duty" should {
      lazy val form: Form[Boolean] = formProvider().bind(Map("value" -> ""))
      lazy val view: Html = injectedView(
        form,
        controllers.routes.DeclarantContactDetailsController.onLoad(),
        "deferment.headingVATandDuty"
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "update the page title to include the error prefix" in {
        document.title mustBe DefermentMessages.errorPrefix + DefermentMessages.headingVATandDuty
      }

      "render an error summary with the correct message" in {
        elementText("div.govuk-error-summary > div") mustBe DefermentMessages.requiredError
      }

      "render an error message against the field" in {
        elementText("#value-error") mustBe DefermentMessages.errorPrefix + DefermentMessages.requiredError
      }
    }

  }

  it should {

    val form: Form[Boolean] = formProvider.apply()
    lazy val view: Html = injectedView(
      form,
      Call("GET", controllers.routes.DeclarantContactDetailsController.onLoad().url),
      "deferment.headingDutyOnly"
    )(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct value for the first radio button of '${DefermentMessages.payingByDeferment}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(1) > label") mustBe DefermentMessages.payingByDeferment
    }

    s"have the correct value for the second radio button of '${DefermentMessages.payingByOther}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(2) > label") mustBe DefermentMessages.payingByOther
    }

    s"have the correct value for the second radio button of '${DefermentMessages.payingByOther}' hint" in {
      elementText("#value-2-item-hint") mustBe DefermentMessages.hint
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> controllers.routes.DeclarantContactDetailsController.onLoad().url)
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }

  }
}
