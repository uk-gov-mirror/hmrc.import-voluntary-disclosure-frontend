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

package views.underpayments

import base.ViewBaseSpec
import forms.underpayments.RemoveUnderpaymentDetailsFormProvider
import messages.underpayments.RemoveUnderpaymentDetailsMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.underpayments.RemoveUnderpaymentDetailsView

class RemoveUnderpaymentDetailsViewSpec extends ViewBaseSpec {

  private lazy val injectedView: RemoveUnderpaymentDetailsView = app.injector.instanceOf[RemoveUnderpaymentDetailsView]

  val underpaymentType = "B00"

  val backLink: Call = controllers.underpayments.routes.ChangeUnderpaymentDetailsController.onLoad(underpaymentType)

  val formProvider: RemoveUnderpaymentDetailsFormProvider = injector.instanceOf[RemoveUnderpaymentDetailsFormProvider]

  "Rendering the Remove Underpayment Details page" when {

    "no errors exist" should {
      val form: Form[Boolean] = formProvider.apply(underpaymentType)
      lazy val view: Html = injectedView(
        form,
        underpaymentType,
        backLink
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe RemoveUnderpaymentDetailsMessages.underpaymentTypeContent(underpaymentType).title
      }

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }
    }

    "The Underpayment Detail Summary page" when {
      Seq("B00", "A00", "E00", "A20", "A30", "A35", "A40", "A45", "A10", "D10").foreach { testType =>
        checkContent(testType)
      }

      def checkContent(underpaymentType: String): Unit = {
        s"rendered for type $underpaymentType" should {
          val form: Form[Boolean] = formProvider.apply(underpaymentType)
          lazy val view: Html = injectedView(
            form,
            underpaymentType,
            backLink
          )(fakeRequest, messages)
          lazy implicit val document: Document = Jsoup.parse(view.body)

          "have the correct page title" in {
            document.title mustBe
              RemoveUnderpaymentDetailsMessages.underpaymentTypeContent(underpaymentType).title
          }

          "have the correct page heading" in {
            elementText("h1") mustBe
              RemoveUnderpaymentDetailsMessages.underpaymentTypeContent(underpaymentType).heading
          }
        }
      }

      "an error exists" should {
        lazy val form: Form[Boolean] = formProvider(underpaymentType).bind(Map("value" -> ""))
        lazy val view: Html = injectedView(
          form,
          underpaymentType,
          backLink
        )(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "update the page title to include the error prefix" in {
          document.title mustBe RemoveUnderpaymentDetailsMessages.errorPrefix + RemoveUnderpaymentDetailsMessages.underpaymentTypeContent(underpaymentType).title
        }

        "render an error summary with the correct message" in {
          elementText("div.govuk-error-summary > div") mustBe
            RemoveUnderpaymentDetailsMessages.underpaymentTypeContent(underpaymentType).body.get
        }

      }

    }
  }
  it should {

    val form: Form[Boolean] = formProvider.apply(underpaymentType)
    lazy val view: Html = injectedView(
      form,
      underpaymentType,
      backLink
    )(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct value for the first radio button of '${RemoveUnderpaymentDetailsMessages.radioYes}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(1) > label") mustBe RemoveUnderpaymentDetailsMessages.radioYes
    }

    s"have the correct value for the second radio button of '${RemoveUnderpaymentDetailsMessages.radioNo}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(2) > label") mustBe RemoveUnderpaymentDetailsMessages.radioNo
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> backLink.url)
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe RemoveUnderpaymentDetailsMessages.continue
    }

  }
}


