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
import forms.ImportVATFormProvider
import messages.{BaseMessages, ImportVATMessages}
import models.underpayments.UnderpaymentAmount
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.ImportVATView

class ImportVATViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: ImportVATView = app.injector.instanceOf[ImportVATView]

  val formProvider: ImportVATFormProvider = injector.instanceOf[ImportVATFormProvider]

  private final val fifty = "50"
  private final val nonNumericInput = "!@JdsJgbnmL"
  private final val originalErrorId = "#original-error"
  private final val amendedErrorId = "#amended-error"

  def underpaymentFormWithValues(originalValue: String, amendedValue: String): Form[UnderpaymentAmount] =
    formProvider().bind(Map("original" -> originalValue, "amended" -> amendedValue))

  "Rendering the Import VAT page" when {

    "no errors exist" should {
      val form: Form[UnderpaymentAmount] = formProvider.apply()
      lazy val view: Html = injectedView(
        form,
        Call("GET", controllers.routes.UnderpaymentTypeController.onLoad().toString)
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe ImportVATMessages.pageTitle
      }

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }
    }

    "an error exists (no value has been specified for original amount)" should {
      lazy val form: Form[UnderpaymentAmount] = underpaymentFormWithValues(emptyString, fifty)
      lazy val view: Html = injectedView(
        form,
        Call("GET", controllers.routes.UnderpaymentTypeController.onLoad().toString)
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe ImportVATMessages.errorPrefix + ImportVATMessages.pageTitle
      }

      "render an error summary with the correct message " in {
        elementText(govErrorSummaryListClass) mustBe ImportVATMessages.originalNonEmpty
      }

      "render an error message against the field" in {
        elementText(originalErrorId) mustBe ImportVATMessages.errorPrefix + ImportVATMessages.originalNonEmpty
      }
    }

    "an error exists (no value has been specified for amended amount)" should {
      lazy val form: Form[UnderpaymentAmount] = underpaymentFormWithValues(fifty, emptyString)
      lazy val view: Html = injectedView(
        form,
        Call("GET", controllers.routes.UnderpaymentTypeController.onLoad().toString)
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe ImportVATMessages.errorPrefix + ImportVATMessages.pageTitle
      }

      "render an error summary with the correct message " in {
        elementText(govErrorSummaryListClass) mustBe ImportVATMessages.amendedNonEmpty
      }

      "render an error message against the field" in {
        elementText(amendedErrorId) mustBe ImportVATMessages.errorPrefix + ImportVATMessages.amendedNonEmpty
      }
    }

    "an error exists (not a numeric value has been specified for original amount)" should {
      lazy val form: Form[UnderpaymentAmount] = underpaymentFormWithValues(nonNumericInput, fifty)
      lazy val view: Html = injectedView(
        form,
        Call("GET", controllers.routes.UnderpaymentTypeController.onLoad().toString)
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe ImportVATMessages.errorPrefix + ImportVATMessages.pageTitle
      }

      "render an error summary with the correct message " in {
        elementText(govErrorSummaryListClass) mustBe ImportVATMessages.originalNonNumber
      }

      "render an error message against the field" in {
        elementText(originalErrorId) mustBe ImportVATMessages.errorPrefix + ImportVATMessages.originalNonNumber
      }
    }

    "an error exists (not a numeric value has been specified for amended amount)" should {
      lazy val form: Form[UnderpaymentAmount] = underpaymentFormWithValues(fifty, nonNumericInput)
      lazy val view: Html = injectedView(
        form,
        Call("GET", controllers.routes.UnderpaymentTypeController.onLoad().toString)
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe ImportVATMessages.errorPrefix + ImportVATMessages.pageTitle
      }

      "render an error summary with the correct message " in {
        elementText(govErrorSummaryListClass) mustBe ImportVATMessages.amendedNonNumber
      }

      "render an error message against the field" in {
        elementText(amendedErrorId) mustBe ImportVATMessages.errorPrefix + ImportVATMessages.amendedNonNumber
      }
    }

    "an error exists (the value for original amount exceeds the limit)" should {
      lazy val form: Form[UnderpaymentAmount] = underpaymentFormWithValues("10000000000", fifty)
      lazy val view: Html = injectedView(
        form,
        Call("GET", controllers.routes.UnderpaymentTypeController.onLoad().toString)
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe ImportVATMessages.errorPrefix + ImportVATMessages.pageTitle
      }

      "render an error summary with the correct message " in {
        elementText(govErrorSummaryListClass) mustBe ImportVATMessages.originalUpperLimit
      }

      "render an error message against the field" in {
        elementText(originalErrorId) mustBe ImportVATMessages.errorPrefix + ImportVATMessages.originalUpperLimit
      }
    }

  }

  it should {

    lazy val form: Form[UnderpaymentAmount] = formProvider()
    lazy val view: Html = injectedView(form,
      Call("GET", controllers.routes.UnderpaymentTypeController.onLoad().toString)
    )(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct page title of '${ImportVATMessages.pageTitle}'" in {
      document.title mustBe ImportVATMessages.pageTitle
    }

    s"have the correct h1 of '${ImportVATMessages.pageHeader}'" in {
      elementText("h1") mustBe ImportVATMessages.pageHeader
    }

    s"have correct legend for the original amount" in {
      elementText("#original-fieldset-legend") mustBe ImportVATMessages.originalAmount
    }

    s"have correct legend for the amended amount" in {
      elementText("#amended-fieldset-legend") mustBe ImportVATMessages.amendedAmount
    }

    "have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }

  }
}