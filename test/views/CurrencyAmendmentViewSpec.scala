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
import forms.UnderpaymentReasonAmendmentFormProvider
import messages.{AmendReasonValuesMessages, BaseMessages}
import models.UnderpaymentReasonValue
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.{Form, FormError}
import play.twirl.api.Html
import views.html.CurrencyAmendmentView

class CurrencyAmendmentViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: CurrencyAmendmentView = app.injector.instanceOf[CurrencyAmendmentView]

  val formProvider: UnderpaymentReasonAmendmentFormProvider = injector.instanceOf[UnderpaymentReasonAmendmentFormProvider]

  private final val boxNumber: Int = 46
  private final val itemNumber: Int = 1
  private final val validValue = "100.45"
  private final val originalErrorId = "#original-error"
  private final val amendedErrorId = "#amended-error"
  private final val invalidValue1 = "3adp4"
  private final val invalidValue2 = "50.837"
  private final val invalidValue3 = "1283749383429.50"


  def underpaymentReasonFormWithValues(originalValue: String, amendedValue: String): Form[UnderpaymentReasonValue] =
    formProvider(boxNumber).bind(Map("original" -> originalValue, "amended" -> amendedValue))

  "Rendering the Underpayment Reason Amendment page" when {

    "no errors exist" should {
      val form: Form[UnderpaymentReasonValue] = formProvider.apply(boxNumber)
      lazy val view: Html = injectedView(
        form, boxNumber, itemNumber, controllers.routes.BoxNumberController.onLoad()
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe AmendReasonValuesMessages.box46PageTitle
      }

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }
    }

    "an error exists (no value has been specified for original amount)" should {
      lazy val form: Form[UnderpaymentReasonValue] = underpaymentReasonFormWithValues(emptyString, validValue)
      lazy val view: Html = injectedView(
        form, boxNumber, itemNumber, controllers.routes.BoxNumberController.onLoad()
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe AmendReasonValuesMessages.errorPrefix + AmendReasonValuesMessages.box46PageTitle
      }

      "render an error summary with the correct message " in {
        elementText(govErrorSummaryListClass) mustBe AmendReasonValuesMessages.originalCurrencyNonEmpty
      }

      "render an error message against the field" in {
        elementText(originalErrorId) mustBe AmendReasonValuesMessages.errorPrefix + AmendReasonValuesMessages.originalCurrencyNonEmpty
      }
    }
  }

  "an error exists (no value has been specified for amended amount)" should {
    lazy val form: Form[UnderpaymentReasonValue] = underpaymentReasonFormWithValues(validValue, emptyString)
    lazy val view: Html = injectedView(
      form, boxNumber, itemNumber, controllers.routes.BoxNumberController.onLoad()
    )(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct page title" in {
      document.title mustBe AmendReasonValuesMessages.errorPrefix + AmendReasonValuesMessages.box46PageTitle
    }

    "render an error summary with the correct message " in {
      elementText(govErrorSummaryListClass) mustBe AmendReasonValuesMessages.amendedCurrencyNonEmpty
    }

    "render an error message against the field" in {
      elementText(amendedErrorId) mustBe AmendReasonValuesMessages.errorPrefix + AmendReasonValuesMessages.amendedCurrencyNonEmpty
    }
  }

  "an error exists (same value has been entered for original and amended amount)" should {
    lazy val form: Form[UnderpaymentReasonValue] = underpaymentReasonFormWithValues(validValue, validValue)
      .discardingErrors
      .withError(FormError("amended", AmendReasonValuesMessages.amendedDifferent))
    lazy val view: Html = injectedView(
      form, boxNumber, itemNumber, controllers.routes.BoxNumberController.onLoad()
    )(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct page title" in {
      document.title mustBe AmendReasonValuesMessages.errorPrefix + AmendReasonValuesMessages.box46PageTitle
    }

    "render an error summary with the correct message " in {
      elementText(govErrorSummaryListClass) mustBe AmendReasonValuesMessages.amendedDifferent
    }

    "render an error message against the field" in {
      elementText(amendedErrorId) mustBe AmendReasonValuesMessages.errorPrefix + AmendReasonValuesMessages.amendedDifferent
    }
  }

  "an error exists (value has been entered in an none numeric format for both original and amended)" should {
    lazy val form: Form[UnderpaymentReasonValue] = underpaymentReasonFormWithValues(invalidValue1, invalidValue1)
    lazy val view: Html = injectedView(
      form, boxNumber, itemNumber, controllers.routes.BoxNumberController.onLoad()
    )(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct page title" in {
      document.title mustBe AmendReasonValuesMessages.errorPrefix + AmendReasonValuesMessages.box46PageTitle
    }

    "render an error summary with the correct message " in {
      elementText(govErrorSummaryListClass) mustBe AmendReasonValuesMessages.originalInvalidCurrencyFormat + " " + AmendReasonValuesMessages.amendedInvalidCurrencyFormat
    }

    "render an error message against the original field" in {
      elementText(originalErrorId) mustBe AmendReasonValuesMessages.errorPrefix + AmendReasonValuesMessages.originalInvalidCurrencyFormat
    }

    "render an error message against the amended field" in {
      elementText(amendedErrorId) mustBe AmendReasonValuesMessages.errorPrefix + AmendReasonValuesMessages.amendedInvalidCurrencyFormat
    }
  }

  "an error exists (value has been entered with too many decimal points for both original and amended)" should {
    lazy val form: Form[UnderpaymentReasonValue] = underpaymentReasonFormWithValues(invalidValue2, invalidValue2)
    lazy val view: Html = injectedView(
      form, boxNumber, itemNumber, controllers.routes.BoxNumberController.onLoad()
    )(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct page title" in {
      document.title mustBe AmendReasonValuesMessages.errorPrefix + AmendReasonValuesMessages.box46PageTitle
    }

    "render an error summary with the correct message " in {
      elementText(govErrorSummaryListClass) mustBe AmendReasonValuesMessages.originalInvalidCurrencyDecimal + " " + AmendReasonValuesMessages.amendedInvalidCurrencyDecimal
    }

    "render an error message against the original field" in {
      elementText(originalErrorId) mustBe AmendReasonValuesMessages.errorPrefix + AmendReasonValuesMessages.originalInvalidCurrencyDecimal
    }

    "render an error message against the amended field" in {
      elementText(amendedErrorId) mustBe AmendReasonValuesMessages.errorPrefix + AmendReasonValuesMessages.amendedInvalidCurrencyDecimal
    }
  }

  "an error exists (value has been entered out of range for both original and amended)" should {
    lazy val form: Form[UnderpaymentReasonValue] = underpaymentReasonFormWithValues(invalidValue3, invalidValue3)
    lazy val view: Html = injectedView(
      form, boxNumber, itemNumber, controllers.routes.BoxNumberController.onLoad()
    )(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct page title" in {
      document.title mustBe AmendReasonValuesMessages.errorPrefix + AmendReasonValuesMessages.box46PageTitle
    }

    "render an error summary with the correct message " in {
      elementText(govErrorSummaryListClass) mustBe AmendReasonValuesMessages.originalInvalidCurrencyOutOfRange + " " + AmendReasonValuesMessages.amendedInvalidCurrencyOutOfRange
    }

    "render an error message against the original field" in {
      elementText(originalErrorId) mustBe AmendReasonValuesMessages.errorPrefix + AmendReasonValuesMessages.originalInvalidCurrencyOutOfRange
    }

    "render an error message against the amended field" in {
      elementText(amendedErrorId) mustBe AmendReasonValuesMessages.errorPrefix + AmendReasonValuesMessages.amendedInvalidCurrencyOutOfRange
    }
  }

  "The Underpayment Reason Amendment page" when {
    Seq(46).map { testBox =>
      checkContent(testBox)
    }

    def checkContent(boxNumber: Int) = {
      s"rendered for box ${boxNumber}" should {
        val form: Form[UnderpaymentReasonValue] = formProvider.apply(boxNumber)
        lazy val view: Html = injectedView(
          form, boxNumber, itemNumber, controllers.routes.BoxNumberController.onLoad()
        )(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "have the correct page title" in {
          document.title mustBe AmendReasonValuesMessages.boxContent.get(boxNumber).get.title
        }

        "have the correct page heading" in {
          elementText("h1") mustBe AmendReasonValuesMessages.boxContent.get(boxNumber).get.heading
        }
      }
    }

  }

  it should {

    lazy val form: Form[UnderpaymentReasonValue] = underpaymentReasonFormWithValues(validValue, emptyString)
    lazy val view: Html = injectedView(
      form, boxNumber, itemNumber, controllers.routes.BoxNumberController.onLoad()
    )(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have correct legend for the original amount" in {
      elementText("#main-content > div > div > form > div:nth-child(2) > label") mustBe AmendReasonValuesMessages.originalAmount
    }

    s"have correct legend for the amended amount" in {
      elementText("#main-content > div > div > form > div:nth-child(3) > label") mustBe AmendReasonValuesMessages.amendedAmount
    }

    s"the original input field is rendered" in {
      document.select("#original").size mustBe 1
    }

    s"the amended input field is rendered" in {
      document.select("#amended").size mustBe 1
    }

    "have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> controllers.routes.BoxNumberController.onLoad().url)
    }

  }

}

