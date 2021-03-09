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
import forms.TraderAddressCorrectFormProvider
import messages.{BaseMessages, ImporterAddressMessages}
import models.ContactAddress
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.Html
import utils.ReusableValues
import views.html.TraderAddressCorrectView

class TraderAddressCorrectViewSpec extends ViewBaseSpec with BaseMessages with ReusableValues {


  private lazy val injectedView: TraderAddressCorrectView = app.injector.instanceOf[TraderAddressCorrectView]

  val formProvider: TraderAddressCorrectFormProvider = injector.instanceOf[TraderAddressCorrectFormProvider]

  "Rendering the AcceptanceDate page" when {
    "no errors exist with full trader details" should {

      val form: Form[Boolean] = formProvider.apply()
      lazy val view: Html = injectedView(form, addressDetails)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe ImporterAddressMessages.pageTitle
      }

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }
    }

    "no errors exist with trader details without postcode" should {

      val form: Form[Boolean] = formProvider.apply()
      lazy val view: Html = injectedView(form, ContactAddress("first", None, "second", None, "fourth"))(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe ImporterAddressMessages.pageTitle
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
      lazy val view: Html = injectedView(form, addressDetails)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "update the page title to include the error prefix" in {
        document.title mustBe ImporterAddressMessages.errorPrefix + ImporterAddressMessages.pageTitle
      }

      "render an error summary with the correct message" in {
        elementText("div.govuk-error-summary > div") mustBe ImporterAddressMessages.errorRequired
      }

      "render an error message against the field" in {
        elementText("#value-error") mustBe
          ImporterAddressMessages.errorPrefix + ImporterAddressMessages.errorRequired
      }

    }
  }

  it should {

    val form: Form[Boolean] = formProvider.apply()
    lazy val view: Html = injectedView(form, addressDetails)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct h1 of '${ImporterAddressMessages.h1}'" in {
      elementText("h1") mustBe ImporterAddressMessages.h1
    }

    s"have the correct value for the first radio button of '${ImporterAddressMessages.siteYes}'" in {
      elementText(
        "#main-content > div > div > form > div > fieldset > div > div:nth-child(1) > label"
      ) mustBe ImporterAddressMessages.siteYes
    }

    s"have the correct value for the second radio button of '${ImporterAddressMessages.siteNo}'" in {
      elementText(
        "#main-content > div > div > form > div > fieldset > div > div:nth-child(2) > label"
      ) mustBe ImporterAddressMessages.siteNo
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain(
        "href" -> controllers.routes.DeclarantContactDetailsController.onLoad().url
      )
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }

  }


}
