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
import messages.underpayments.ChangeUnderpaymentDetailsMessages
import models.underpayments.UnderpaymentAmount
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.underpayments.ChangeUnderpaymentDetailsView
import play.api.data.{Form, FormError}

class ChangeUnderpaymentDetailsViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: ChangeUnderpaymentDetailsView = app.injector.instanceOf[ChangeUnderpaymentDetailsView]

  private val backLink: Call = Call("GET", "url")


  val formProvider: UnderpaymentDetailsFormProvider = injector.instanceOf[UnderpaymentDetailsFormProvider]

  private final val validValue = "871.12"
  private final val invalidValue = "11.111"
  private final val underpaymentType = "B00"
  private final val originalErrorId = "#original-error"
  private final val amendedErrorId = "#amended-error"
  private final val invalidValue2 = "3adp4"

  def underpaymentReasonFormWithValues(originalValue: String, amendedValue: String): Form[UnderpaymentAmount] =
    formProvider().bind(Map("original" -> originalValue, "amended" -> amendedValue))

  "Rendering the ChangeUnderpaymentDetailsView page" when {
    "no errors exist" should {
      val form = formProvider.apply()
      lazy val view: Html = injectedView(form, underpaymentType, backLink)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe ChangeUnderpaymentDetailsMessages.underpaymentTypeContent(underpaymentType).title
      }

      "have correct heading" in {
        document.select("h1").text mustBe ChangeUnderpaymentDetailsMessages.underpaymentTypeContent(underpaymentType).title
      }

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }

      s"have correct legend for the original amount" in {
        elementText("#main-content > div > div > form > div:nth-child(2) > label") mustBe ChangeUnderpaymentDetailsMessages.originalAmount
      }

      s"have correct legend for the amended amount" in {
        elementText("#main-content > div > div > form > div:nth-child(3) > label") mustBe ChangeUnderpaymentDetailsMessages.amendedAmount
      }
    }

      "an error exists (no value has been specified for original amount)" should {
        lazy val form: Form[UnderpaymentAmount] = underpaymentReasonFormWithValues(emptyString, validValue)
        lazy val view: Html = injectedView(form, underpaymentType, backLink)(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        s"have the correct page title" in {
          document.title mustBe ChangeUnderpaymentDetailsMessages.errorPrefix + ChangeUnderpaymentDetailsMessages.B00pageTitle
        }

        "render an error summary with the correct message " in {
          elementText(govErrorSummaryListClass) mustBe ChangeUnderpaymentDetailsMessages.originalNonEmpty
        }

        "render an error message against the field" in {
          elementText(originalErrorId) mustBe ChangeUnderpaymentDetailsMessages.errorPrefix + ChangeUnderpaymentDetailsMessages.originalNonEmpty
        }
      }

      "an error exists (no value has been specified for amended amount)" should {
        lazy val form: Form[UnderpaymentAmount] = underpaymentReasonFormWithValues(validValue, emptyString)
        lazy val view: Html = injectedView(form, underpaymentType, backLink)(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        s"have the correct page title" in {
          document.title mustBe ChangeUnderpaymentDetailsMessages.errorPrefix + ChangeUnderpaymentDetailsMessages.B00pageTitle
        }

        "render an error summary with the correct message " in {
          elementText(govErrorSummaryListClass) mustBe ChangeUnderpaymentDetailsMessages.amendedNonEmpty
        }

        "render an error message against the field" in {
          elementText(amendedErrorId) mustBe ChangeUnderpaymentDetailsMessages.errorPrefix + ChangeUnderpaymentDetailsMessages.amendedNonEmpty
        }
      }

      "an error exists (same value has been entered for original and amended amount)" should {
        lazy val form: Form[UnderpaymentAmount] = underpaymentReasonFormWithValues(validValue, validValue)
          .discardingErrors
          .withError(FormError("amended", ChangeUnderpaymentDetailsMessages.amendedDifferent))
        lazy val view: Html = injectedView(form, underpaymentType, backLink)(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        s"have the correct page title" in {
          document.title mustBe ChangeUnderpaymentDetailsMessages.errorPrefix + ChangeUnderpaymentDetailsMessages.B00pageTitle
        }

        "render an error summary with the correct message " in {
          elementText(govErrorSummaryListClass) mustBe ChangeUnderpaymentDetailsMessages.amendedDifferent
        }

        "render an error message against the field" in {
          elementText(amendedErrorId) mustBe ChangeUnderpaymentDetailsMessages.errorPrefix + ChangeUnderpaymentDetailsMessages.amendedDifferent
        }
      }

      "an error exists (value has been entered in an invalid format for both original and amended)" should {
        lazy val form: Form[UnderpaymentAmount] = underpaymentReasonFormWithValues(invalidValue, invalidValue2)
        lazy val view: Html = injectedView(form, underpaymentType, backLink)(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        s"have the correct page title" in {
          document.title mustBe ChangeUnderpaymentDetailsMessages.errorPrefix + ChangeUnderpaymentDetailsMessages.B00pageTitle
        }

        "render an error summary with the correct message " in {
          elementText(govErrorSummaryListClass) mustBe ChangeUnderpaymentDetailsMessages.originalNonNumber + " " + ChangeUnderpaymentDetailsMessages.amendedNonNumber
        }

        "render an error message against the original field" in {
          elementText(originalErrorId) mustBe ChangeUnderpaymentDetailsMessages.errorPrefix + ChangeUnderpaymentDetailsMessages.originalNonNumber
        }

        "render an error message against the amended field" in {
          elementText(amendedErrorId) mustBe ChangeUnderpaymentDetailsMessages.errorPrefix + ChangeUnderpaymentDetailsMessages.amendedNonNumber
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
          document.title mustBe ChangeUnderpaymentDetailsMessages.underpaymentTypeContent(underpaymentType).title
        }

        "have the correct page heading" in {
          elementText("h1") mustBe ChangeUnderpaymentDetailsMessages.underpaymentTypeContent(underpaymentType).heading
        }

        "have the correct remove link heading" in {
          elementText("#remove-link") mustBe ChangeUnderpaymentDetailsMessages.underpaymentTypeContent(underpaymentType).remove

        }

      }
    }
  }

  it should {

    lazy val form: Form[UnderpaymentAmount] = formProvider.apply()
    lazy val view: Html = injectedView(form, "A00", backLink)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have correct legend for the original amount" in {
      elementText("#original-fieldset-legend") mustBe ChangeUnderpaymentDetailsMessages.originalAmount
    }

    s"have correct legend for the amended amount" in {
      elementText("#amended-fieldset-legend") mustBe ChangeUnderpaymentDetailsMessages.amendedAmount
    }

    "have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> backLink.url)
    }

  }

}
