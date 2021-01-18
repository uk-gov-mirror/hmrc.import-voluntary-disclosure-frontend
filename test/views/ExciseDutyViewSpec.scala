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
import forms.ExciseDutyFormProvider
import messages.{BaseMessages, CustomsDutyMessages, ExciseDutyMessages}
import models.UnderpaymentAmount
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.ExciseDutyView

class ExciseDutyViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: ExciseDutyView = app.injector.instanceOf[ExciseDutyView]

  val formProvider: ExciseDutyFormProvider = injector.instanceOf[ExciseDutyFormProvider]

  private final val fifty = "50"
  private final val nonNumericInput = "!@JdsJgbnmL"
  private final val originalErrorId = "#original-error"
  private final val amendedErrorId = "#amended-error"

  def underpaymentFormWithValues(originalValue: String, amendedValue: String): Form[UnderpaymentAmount] =
    formProvider().bind(Map("original" -> originalValue, "amended" -> amendedValue))

  "Rendering the ExciseDuty page" when {

    "no errors exist" should {
      val form: Form[UnderpaymentAmount] = formProvider.apply()
      lazy val view: Html = injectedView(
        form,
        Call("GET", controllers.routes.UnderpaymentTypeController.onLoad().toString)
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe ExciseDutyMessages.pageTitle
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
        document.title mustBe ExciseDutyMessages.errorPrefix + ExciseDutyMessages.pageTitle
      }

      "render an error summary with the correct message " in {
        elementText(govErrorSummaryListClass) mustBe ExciseDutyMessages.originalNonEmpty
      }

      "render an error message against the field" in {
        elementText(originalErrorId) mustBe ExciseDutyMessages.errorPrefix + ExciseDutyMessages.originalNonEmpty
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
        document.title mustBe CustomsDutyMessages.errorPrefix + ExciseDutyMessages.pageTitle
      }

      "render an error summary with the correct message " in {
        elementText(govErrorSummaryListClass) mustBe ExciseDutyMessages.amendedNonEmpty
      }

      "render an error message against the field" in {
        elementText(amendedErrorId) mustBe ExciseDutyMessages.errorPrefix + ExciseDutyMessages.amendedNonEmpty
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
        document.title mustBe ExciseDutyMessages.errorPrefix + ExciseDutyMessages.pageTitle
      }

      "render an error summary with the correct message " in {
        elementText(govErrorSummaryListClass) mustBe ExciseDutyMessages.originalNonNumber
      }

      "render an error message against the field" in {
        elementText(originalErrorId) mustBe ExciseDutyMessages.errorPrefix + ExciseDutyMessages.originalNonNumber
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
        document.title mustBe ExciseDutyMessages.errorPrefix + ExciseDutyMessages.pageTitle
      }

      "render an error summary with the correct message " in {
        elementText(govErrorSummaryListClass) mustBe ExciseDutyMessages.amendedNonNumber
      }

      "render an error message against the field" in {
        elementText(amendedErrorId) mustBe ExciseDutyMessages.errorPrefix + ExciseDutyMessages.amendedNonNumber
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
        document.title mustBe ExciseDutyMessages.errorPrefix + ExciseDutyMessages.pageTitle
      }

      "render an error summary with the correct message " in {
        elementText(govErrorSummaryListClass) mustBe ExciseDutyMessages.originalUpperLimit
      }

      "render an error message against the field" in {
        elementText(originalErrorId) mustBe ExciseDutyMessages.errorPrefix + ExciseDutyMessages.originalUpperLimit
      }
    }

  }

  it should {

    lazy val form: Form[UnderpaymentAmount] = formProvider()
    lazy val view: Html = injectedView(form, Call("GET", controllers.routes.UnderpaymentTypeController.onLoad().toString)
    )(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct page title of '${ExciseDutyMessages.pageTitle}'" in {
      document.title mustBe ExciseDutyMessages.pageTitle
    }

    s"have the correct h1 of '${ExciseDutyMessages.pageHeader}'" in {
      elementText("h1") mustBe ExciseDutyMessages.pageHeader
    }

    s"have correct legend for the original amount" in {
      elementText("#original-fieldset-legend") mustBe ExciseDutyMessages.originalAmount
    }

    s"have correct legend for the amended amount" in {
      elementText("#amended-fieldset-legend") mustBe ExciseDutyMessages.amendedAmount
    }

    "have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> controllers.routes.UnderpaymentTypeController.onLoad().url)
    }

  }

}
