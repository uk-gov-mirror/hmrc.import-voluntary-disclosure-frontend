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
import forms.underpayments.UnderpaymentDetailsFormProvider
import messages.BaseMessages
import messages.underpayments.UnderpaymentDetailsMessages
import models.underpayments.UnderpaymentAmount
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.{Form, FormError}
import play.twirl.api.Html
import views.html.underpayments.UnderpaymentDetailsView

class UnderpaymentDetailsViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: UnderpaymentDetailsView = app.injector.instanceOf[UnderpaymentDetailsView]
  val formProvider: UnderpaymentDetailsFormProvider = injector.instanceOf[UnderpaymentDetailsFormProvider]
  private lazy val backLink = controllers.underpayments.routes.UnderpaymentTypeController.onLoad()
  private final val validValue = "871.12"
  private final val invalidValue = "11.111"
  private final val underpaymentType = "B00"
  private final val originalErrorId = "#original-error"
  private final val amendedErrorId = "#amended-error"
  private final val invalidValue2 = "3adp4"

  def underpaymentReasonFormWithValues(originalValue: String, amendedValue: String): Form[UnderpaymentAmount] =
    formProvider().bind(Map("original" -> originalValue, "amended" -> amendedValue))

  "Rendering the Underpayment Reason Amendment page" when {

    "no errors exist" should {
      val form: Form[UnderpaymentAmount] = formProvider.apply()
      lazy val view: Html = injectedView(form, underpaymentType, backLink)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe UnderpaymentDetailsMessages.B00pageTitle
      }

      s"have the correct H1 text of '${UnderpaymentDetailsMessages.B00pageHeader}'" in {
        elementText("h1") mustBe UnderpaymentDetailsMessages.B00pageHeader
      }

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }
    }

    "an error exists (no value has been specified for original amount)" should {
      lazy val form: Form[UnderpaymentAmount] = underpaymentReasonFormWithValues(emptyString, validValue)
      lazy val view: Html = injectedView(form, underpaymentType, backLink)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe UnderpaymentDetailsMessages.errorPrefix + UnderpaymentDetailsMessages.B00pageTitle
      }

      "render an error summary with the correct message " in {
        elementText(govErrorSummaryListClass) mustBe UnderpaymentDetailsMessages.originalNonEmpty
      }

      "render an error message against the field" in {
        elementText(originalErrorId) mustBe UnderpaymentDetailsMessages.errorPrefix + UnderpaymentDetailsMessages.originalNonEmpty
      }
    }

    "an error exists (no value has been specified for amended amount)" should {
      lazy val form: Form[UnderpaymentAmount] = underpaymentReasonFormWithValues(validValue, emptyString)
      lazy val view: Html = injectedView(form, underpaymentType, backLink)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe UnderpaymentDetailsMessages.errorPrefix + UnderpaymentDetailsMessages.B00pageTitle
      }

      "render an error summary with the correct message " in {
        elementText(govErrorSummaryListClass) mustBe UnderpaymentDetailsMessages.amendedNonEmpty
      }

      "render an error message against the field" in {
        elementText(amendedErrorId) mustBe UnderpaymentDetailsMessages.errorPrefix + UnderpaymentDetailsMessages.amendedNonEmpty
      }
    }

    "an error exists (same value has been entered for original and amended amount)" should {
      lazy val form: Form[UnderpaymentAmount] = underpaymentReasonFormWithValues(validValue, validValue)
        .discardingErrors
        .withError(FormError("amended", UnderpaymentDetailsMessages.amendedDifferent))
      lazy val view: Html = injectedView(form, underpaymentType, backLink)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe UnderpaymentDetailsMessages.errorPrefix + UnderpaymentDetailsMessages.B00pageTitle
      }

      "render an error summary with the correct message " in {
        elementText(govErrorSummaryListClass) mustBe UnderpaymentDetailsMessages.amendedDifferent
      }

      "render an error message against the field" in {
        elementText(amendedErrorId) mustBe UnderpaymentDetailsMessages.errorPrefix + UnderpaymentDetailsMessages.amendedDifferent
      }
    }

    "an error exists (value has been entered in an invalid format for both original and amended)" should {
      lazy val form: Form[UnderpaymentAmount] = underpaymentReasonFormWithValues(invalidValue, invalidValue2)
      lazy val view: Html = injectedView(form, underpaymentType, backLink)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe UnderpaymentDetailsMessages.errorPrefix + UnderpaymentDetailsMessages.B00pageTitle
      }

      "render an error summary with the correct message " in {
        elementText(govErrorSummaryListClass) mustBe UnderpaymentDetailsMessages.originalNonNumber + " " + UnderpaymentDetailsMessages.amendedNonNumber
      }

      "render an error message against the original field" in {
        elementText(originalErrorId) mustBe UnderpaymentDetailsMessages.errorPrefix + UnderpaymentDetailsMessages.originalNonNumber
      }

      "render an error message against the amended field" in {
        elementText(amendedErrorId) mustBe UnderpaymentDetailsMessages.errorPrefix + UnderpaymentDetailsMessages.amendedNonNumber
      }
    }

  }

  "The Underpayment Reason Amendment page" when {
    Seq("B00", "A00", "E00", "A20", "A30", "A35", "A40", "A45", "A10", "D10").foreach { testType =>
      checkContent(testType)
    }

    def checkContent(underpaymentType: String): Unit = {
      s"rendered for type $underpaymentType" should {
        val form: Form[UnderpaymentAmount] = formProvider.apply()
        lazy val view: Html = injectedView(form, underpaymentType, backLink)(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "have the correct page title" in {
          document.title mustBe UnderpaymentDetailsMessages.underpaymentTypeContent(underpaymentType).title
        }

        "have the correct page heading" in {
          elementText("h1") mustBe UnderpaymentDetailsMessages.underpaymentTypeContent(underpaymentType).heading
        }

      }
    }

  }

  it should {

    lazy val form: Form[UnderpaymentAmount] = formProvider.apply()
    lazy val view: Html = injectedView(form, "A00", backLink)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have correct legend for the original amount" in {
      elementText("#original-fieldset-legend") mustBe UnderpaymentDetailsMessages.originalAmount
    }

    s"have correct legend for the amended amount" in {
      elementText("#amended-fieldset-legend") mustBe UnderpaymentDetailsMessages.amendedAmount
    }

    "have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> backLink.url)
    }

  }

}
