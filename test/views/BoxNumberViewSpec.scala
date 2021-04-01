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
import forms.BoxNumberFormProvider
import messages.{BaseMessages, BoxNumberMessages}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.Html
import utils.ReusableValues
import views.html.BoxNumberView

class BoxNumberViewSpec extends ViewBaseSpec with BaseMessages with ReusableValues {

  private lazy val injectedView: BoxNumberView = app.injector.instanceOf[BoxNumberView]

  val formProvider: BoxNumberFormProvider = injector.instanceOf[BoxNumberFormProvider]


  "Rendering the Box number page" when {
    "no errors exist" should {

      val form: Form[Int] = formProvider.apply()
      lazy val view: Html = injectedView(form, controllers.routes.BoxGuidanceController.onLoad(),boxNumberRadioButtons)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe BoxNumberMessages.pageTitle
      }

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }
    }

    "an error exists (no option has been selected)" should {
      val form: Form[Int] = formProvider().bind(Map("value" -> ""))
      lazy val view: Html = injectedView(form, controllers.routes.BoxGuidanceController.onLoad(),boxNumberRadioButtons)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "update the page title to include the error prefix" in {
        document.title mustBe BoxNumberMessages.errorPrefix + BoxNumberMessages.pageTitle
      }

      "render an error summary with the correct message" in {
        elementText("div.govuk-error-summary > div") mustBe BoxNumberMessages.errorRequired
      }

      "render an error message against the field" in {
        elementText("#value-error") mustBe BoxNumberMessages.errorPrefix + BoxNumberMessages.errorRequired
      }

    }

  }

  it should {

    val form: Form[Int] = formProvider.apply()
    lazy val view: Html = injectedView(form, controllers.routes.BoxGuidanceController.onLoad(),boxNumberRadioButtons)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct h1 of '${BoxNumberMessages.heading}'" in {
      elementText("h1") mustBe BoxNumberMessages.heading
    }

    s"have the correct value for the first radio button of '${BoxNumberMessages.radioButton22}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(1)") mustBe
        BoxNumberMessages.radioButton22
    }

    s"have the correct value for the second radio button of '${BoxNumberMessages.radioButton33}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(2)") mustBe
        BoxNumberMessages.radioButton33
    }

    s"have the correct value for the third radio button of '${BoxNumberMessages.radioButton34}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(3)") mustBe
        BoxNumberMessages.radioButton34
    }

    s"have the correct value for the fourth radio button of '${BoxNumberMessages.radioButton35}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(4)") mustBe
        BoxNumberMessages.radioButton35
    }

    s"have the correct value for the fifth radio button of '${BoxNumberMessages.radioButton36}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(5)") mustBe
        BoxNumberMessages.radioButton36
    }

    s"have the correct value for the sixth radio button of '${BoxNumberMessages.radioButton37}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(6)") mustBe
        BoxNumberMessages.radioButton37
    }

    s"have the correct value for the seventh radio button of '${BoxNumberMessages.radioButton38}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(7)") mustBe
        BoxNumberMessages.radioButton38
    }

    s"have the correct value for the eighth radio button of '${BoxNumberMessages.radioButton39}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(8)") mustBe
        BoxNumberMessages.radioButton39
    }

    s"have the correct value for the ninth radio button of '${BoxNumberMessages.radioButton41}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(9)") mustBe
        BoxNumberMessages.radioButton41
    }

    s"have the correct value for the tenth radio button of '${BoxNumberMessages.radioButton42}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(10)") mustBe
        BoxNumberMessages.radioButton42
    }

    s"have the correct value for the eleventh radio button of '${BoxNumberMessages.radioButton43}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(11)") mustBe
        BoxNumberMessages.radioButton43
    }

    s"have the correct value for the twelfth radio button of '${BoxNumberMessages.radioButton45}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(12)") mustBe
        BoxNumberMessages.radioButton45
    }

    s"have the correct value for the thirteenth radio button of '${BoxNumberMessages.radioButton46}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(13)") mustBe
        BoxNumberMessages.radioButton46
    }

    s"have the correct value for the fourteenth radio button of '${BoxNumberMessages.radioButton62}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(14)") mustBe
        BoxNumberMessages.radioButton62
    }

    s"have the correct value for the fifteenth radio button of '${BoxNumberMessages.radioButton63}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(15)") mustBe
        BoxNumberMessages.radioButton63
    }

    s"have the correct value for the sixteenth radio button of '${BoxNumberMessages.radioButton66}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(16)") mustBe
        BoxNumberMessages.radioButton66
    }

    s"have the correct value for the seventeenth radio button of '${BoxNumberMessages.radioButton67}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(17)") mustBe
        BoxNumberMessages.radioButton67
    }

    s"have the correct value for the eighteenth radio button of '${BoxNumberMessages.radioButton68}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(18)") mustBe
        BoxNumberMessages.radioButton68
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> controllers.routes.BoxGuidanceController.onLoad().url)
    }

    s"the input field is rendered" in {
      document.select("#value").size mustBe 1
    }

  }

}
