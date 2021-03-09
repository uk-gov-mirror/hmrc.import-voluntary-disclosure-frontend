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
import forms.DeclarantContactDetailsFormProvider
import messages.{BaseMessages, TraderContactDetailsMessages}
import models.ContactDetails
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.Html
import views.html.DeclarantContactDetailsView

class DeclarantContactDetailsViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: DeclarantContactDetailsView = app.injector.instanceOf[DeclarantContactDetailsView]

  val formProvider: DeclarantContactDetailsFormProvider = injector.instanceOf[DeclarantContactDetailsFormProvider]

  "Rendering the TraderContactDetails page" when {
    "no errors exist" should {

      val form: Form[ContactDetails] = formProvider.apply()
      lazy val view: Html = injectedView(form)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe TraderContactDetailsMessages.title
      }

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }
    }

    "an error exists (no values have been entered)" should {
      lazy val form: Form[ContactDetails] = formProvider().bind(
        Map(
          "fullName" -> "",
          "email" -> "",
          "phoneNumber" -> ""
        )
      )
      lazy val view: Html = injectedView(form)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "update the page title to include the error prefix" in {
        document.title mustBe TraderContactDetailsMessages.errorPrefix + TraderContactDetailsMessages.title
      }

      "render an error summary with the correct message for full name" in {
        elementText("#main-content > div > div > div > div > ul > li:nth-child(1) > a") mustBe TraderContactDetailsMessages.errorNameNonEmpty
      }

      "render an error message against the full name field" in {
        elementText("#fullName-error") mustBe TraderContactDetailsMessages.errorPrefix + TraderContactDetailsMessages.errorNameNonEmpty
      }

      "render an error summary with the correct message for email" in {
        elementText("#main-content > div > div > div > div > ul > li:nth-child(2) > a") mustBe TraderContactDetailsMessages.errorEmailNonEmpty
      }

      "render an error message against the email field" in {
        elementText("#email-error") mustBe TraderContactDetailsMessages.errorPrefix + TraderContactDetailsMessages.errorEmailNonEmpty
      }

      "render an error summary with the correct message for phone number" in {
        elementText("#main-content > div > div > div > div > ul > li:nth-child(3) > a") mustBe TraderContactDetailsMessages.errorPhoneNumberNonEmpty
      }

      "render an error message against the phone number field" in {
        elementText("#phoneNumber-error") mustBe TraderContactDetailsMessages.errorPrefix + TraderContactDetailsMessages.errorPhoneNumberNonEmpty
      }

    }

    "an error exists (no value have been entered for full name)" should {
      lazy val form: Form[ContactDetails] = formProvider().bind(
        Map(
          "fullName" -> "",
          "email" -> "email@email.com",
          "phoneNumber" -> "0123456789"
        )
      )
      lazy val view: Html = injectedView(form)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "update the page title to include the error prefix" in {
        document.title mustBe TraderContactDetailsMessages.errorPrefix + TraderContactDetailsMessages.title
      }

      "render an error summary with the correct message for full name" in {
        elementText("#main-content > div > div > div > div > ul > li > a") mustBe TraderContactDetailsMessages.errorNameNonEmpty
      }

      "render an error message against the full name field" in {
        elementText("#fullName-error") mustBe TraderContactDetailsMessages.errorPrefix + TraderContactDetailsMessages.errorNameNonEmpty
      }

    }

    "an error exists (no value have been entered for email)" should {
      lazy val form: Form[ContactDetails] = formProvider().bind(
        Map(
          "fullName" -> "first second",
          "email" -> "",
          "phoneNumber" -> "0123456789"
        )
      )
      lazy val view: Html = injectedView(form)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "update the page title to include the error prefix" in {
        document.title mustBe TraderContactDetailsMessages.errorPrefix + TraderContactDetailsMessages.title
      }

      "render an error summary with the correct message for email" in {
        elementText("#main-content > div > div > div > div > ul > li > a") mustBe TraderContactDetailsMessages.errorEmailNonEmpty
      }

      "render an error message against the email field" in {
        elementText("#email-error") mustBe TraderContactDetailsMessages.errorPrefix + TraderContactDetailsMessages.errorEmailNonEmpty
      }

    }

    "an error exists (no value have been entered for phone number)" should {
      lazy val form: Form[ContactDetails] = formProvider().bind(
        Map(
          "fullName" -> "first second",
          "email" -> "email@email.com",
          "phoneNumber" -> ""
        )
      )
      lazy val view: Html = injectedView(form)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "update the page title to include the error prefix" in {
        document.title mustBe TraderContactDetailsMessages.errorPrefix + TraderContactDetailsMessages.title
      }

      "render an error summary with the correct message for phone number" in {
        elementText("#main-content > div > div > div > div > ul > li > a") mustBe TraderContactDetailsMessages.errorPhoneNumberNonEmpty
      }

      "render an error message against the phone number field" in {
        elementText("#phoneNumber-error") mustBe TraderContactDetailsMessages.errorPrefix + TraderContactDetailsMessages.errorPhoneNumberNonEmpty
      }

    }

    "an error exists (full name entered too short)" should {
      lazy val form: Form[ContactDetails] = formProvider().bind(
        Map(
          "fullName" -> "a",
          "email" -> "email@email.com",
          "phoneNumber" -> "0123456789"
        )
      )
      lazy val view: Html = injectedView(form)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "update the page title to include the error prefix" in {
        document.title mustBe TraderContactDetailsMessages.errorPrefix + TraderContactDetailsMessages.title
      }

      "render an error summary with the correct message for full name" in {
        elementText("#main-content > div > div > div > div > ul > li > a") mustBe TraderContactDetailsMessages.errorNameMinLength
      }

      "render an error message against the full name field" in {
        elementText("#fullName-error") mustBe TraderContactDetailsMessages.errorPrefix + TraderContactDetailsMessages.errorNameMinLength
      }

    }

    "an error exists (full name entered too long)" should {
      lazy val form: Form[ContactDetails] = formProvider().bind(
        Map(
          "fullName" -> "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
          "email" -> "email@email.com",
          "phoneNumber" -> "0123456789"
        )
      )
      lazy val view: Html = injectedView(form)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "update the page title to include the error prefix" in {
        document.title mustBe TraderContactDetailsMessages.errorPrefix + TraderContactDetailsMessages.title
      }

      "render an error summary with the correct message for full name" in {
        elementText("#main-content > div > div > div > div > ul > li > a") mustBe TraderContactDetailsMessages.errorNameMaxLength
      }

      "render an error message against the full name field" in {
        elementText("#fullName-error") mustBe TraderContactDetailsMessages.errorPrefix + TraderContactDetailsMessages.errorNameMaxLength
      }

    }

    "an error exists (full name entered format invalid)" should {
      lazy val form: Form[ContactDetails] = formProvider().bind(
        Map(
          "fullName" -> "first second*",
          "email" -> "email@email.com",
          "phoneNumber" -> "0123456789"
        )
      )
      lazy val view: Html = injectedView(form)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "update the page title to include the error prefix" in {
        document.title mustBe TraderContactDetailsMessages.errorPrefix + TraderContactDetailsMessages.title
      }

      "render an error summary with the correct message for full name" in {
        elementText("#main-content > div > div > div > div > ul > li > a") mustBe TraderContactDetailsMessages.errorNameAllowableCharacters
      }

      "render an error message against the full name field" in {
        elementText("#fullName-error") mustBe TraderContactDetailsMessages.errorPrefix + TraderContactDetailsMessages.errorNameAllowableCharacters
      }

    }

    "an error exists (email address entered format invalid)" should {
      lazy val form: Form[ContactDetails] = formProvider().bind(
        Map(
          "fullName" -> "first second",
          "email" -> "email",
          "phoneNumber" -> "0123456789"
        )
      )
      lazy val view: Html = injectedView(form)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "update the page title to include the error prefix" in {
        document.title mustBe TraderContactDetailsMessages.errorPrefix + TraderContactDetailsMessages.title
      }

      "render an error summary with the correct message for email" in {
        elementText("#main-content > div > div > div > div > ul > li > a") mustBe TraderContactDetailsMessages.errorEmailInvalidFormat
      }

      "render an error message against the email field" in {
        elementText("#email-error") mustBe TraderContactDetailsMessages.errorPrefix + TraderContactDetailsMessages.errorEmailInvalidFormat
      }

    }

    "an error exists (phone number entered format invalid)" should {
      lazy val form: Form[ContactDetails] = formProvider().bind(
        Map(
          "fullName" -> "first second",
          "email" -> "email@email.com",
          "phoneNumber" -> "++0123456789"
        )
      )
      lazy val view: Html = injectedView(form)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "update the page title to include the error prefix" in {
        document.title mustBe TraderContactDetailsMessages.errorPrefix + TraderContactDetailsMessages.title
      }

      "render an error summary with the correct message for phone number" in {
        elementText("#main-content > div > div > div > div > ul > li > a") mustBe TraderContactDetailsMessages.errorPhoneNumberInvalidFormat
      }

      "render an error message against the phone number field" in {
        elementText("#phoneNumber-error") mustBe TraderContactDetailsMessages.errorPrefix + TraderContactDetailsMessages.errorPhoneNumberInvalidFormat
      }

    }

  }

  it should {

    val form: Form[ContactDetails] = formProvider.apply()
    lazy val view: Html = injectedView(form)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct h1 of '${TraderContactDetailsMessages.h1}'" in {
      elementText("h1") mustBe TraderContactDetailsMessages.h1
    }

    s"have the correct value for the text field of Name" in {
      elementText("#main-content > div > div > form > div:nth-child(2) > label") mustBe "Name"
    }

    s"have the correct value for the text field of Email Address" in {
      elementText("#main-content > div > div > form > div:nth-child(3) > label") mustBe "Email address"
    }

    s"have the correct value for the text field of Phone number" in {
      elementText("#main-content > div > div > form > div:nth-child(4) > label") mustBe "UK telephone number"
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> controllers.routes.UploadAnotherFileController.onLoad().url)
    }

  }

}
