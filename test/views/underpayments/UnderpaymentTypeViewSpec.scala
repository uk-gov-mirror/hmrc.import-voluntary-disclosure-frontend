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
import forms.underpayments.UnderpaymentTypeFormProvider
import messages.BaseMessages
import messages.underpayments.UnderpaymentTypeMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.Html
import utils.ReusableValues
import views.html.underpayments.UnderpaymentTypeView

class UnderpaymentTypeViewSpec extends ViewBaseSpec with BaseMessages with ReusableValues {

  private lazy val injectedView: UnderpaymentTypeView = app.injector.instanceOf[UnderpaymentTypeView]

  val formProvider: UnderpaymentTypeFormProvider = injector.instanceOf[UnderpaymentTypeFormProvider]

  private lazy val backLink = controllers.underpayments.routes.UnderpaymentStartController.onLoad()

  "Rendering the UnderpaymentType page" when {
    "no errors exist" should {

      val form: Form[String] = formProvider.apply()
      lazy val view: Html = injectedView(form, backLink, underpaymentTypeRadioButtons)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe UnderpaymentTypeMessages.pageTitle
      }

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }
    }

    "an error exists (no option has been selected)" should {
      lazy val form: Form[String] = formProvider().bind(Map("value" -> ""))
      lazy val view: Html = injectedView(form, backLink, underpaymentTypeRadioButtons)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "update the page title to include the error prefix" in {
        document.title mustBe UnderpaymentTypeMessages.errorPrefix + UnderpaymentTypeMessages.pageTitle
      }

      "render an error summary with the correct message" in {
        elementText("div.govuk-error-summary > div") mustBe UnderpaymentTypeMessages.errorRequired
      }

      "render an error message against the field" in {
        elementText("#value-error") mustBe UnderpaymentTypeMessages.errorPrefix + UnderpaymentTypeMessages.errorRequired
      }

    }
  }

  it should {

    val form: Form[String] = formProvider.apply()
    lazy val view: Html = injectedView(form, backLink, underpaymentTypeRadioButtons)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct h1 of '${UnderpaymentTypeMessages.pageHeader}'" in {
      elementText("h1") mustBe UnderpaymentTypeMessages.pageHeader
    }

    s"have the correct value for the first radio button of '${UnderpaymentTypeMessages.importVAT}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(1)") mustBe
        UnderpaymentTypeMessages.importVAT
    }

    s"have the correct value for the second radio button of '${UnderpaymentTypeMessages.customsDuty}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(2)") mustBe
        UnderpaymentTypeMessages.customsDuty
    }

    s"have the correct value for the third radio button of '${UnderpaymentTypeMessages.exciseDuty}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(3)") mustBe
        UnderpaymentTypeMessages.exciseDuty
    }

    s"have the correct value for the fourth radio button of '${UnderpaymentTypeMessages.additionalDuty}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(4)") mustBe
        UnderpaymentTypeMessages.additionalDuty
    }

    s"have the correct value for the fifth radio button of '${UnderpaymentTypeMessages.definitiveAntiDumpingDuty}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(5)") mustBe
        UnderpaymentTypeMessages.definitiveAntiDumpingDuty
    }

    s"have the correct value for the sixth radio button of '${UnderpaymentTypeMessages.provisionalAntiDumpingDuty}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(6)") mustBe
        UnderpaymentTypeMessages.provisionalAntiDumpingDuty
    }

    s"have the correct value for the seventh radio button of '${UnderpaymentTypeMessages.definitiveCountervailingDuty}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(7)") mustBe
        UnderpaymentTypeMessages.definitiveCountervailingDuty
    }

    s"have the correct value for the eighth radio button of '${UnderpaymentTypeMessages.provisionalCountervailingDuty}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(8)") mustBe
        UnderpaymentTypeMessages.provisionalCountervailingDuty
    }

    s"have the correct value for the ninth radio button of '${UnderpaymentTypeMessages.agriculturalDuty}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(9)") mustBe
        UnderpaymentTypeMessages.agriculturalDuty
    }

    s"have the correct value for the tenth radio button of '${UnderpaymentTypeMessages.compensatoryDuty}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(10)") mustBe
        UnderpaymentTypeMessages.compensatoryDuty
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> backLink.url)
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }

  }

}
