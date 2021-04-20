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
import forms.AnyOtherSupportingDocsFormProvider
import messages.{AnyOtherSupportingDocsMessages, BaseMessages}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.AnyOtherSupportingDocsView

class AnyOtherSupportingDocsViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: AnyOtherSupportingDocsView = app.injector.instanceOf[AnyOtherSupportingDocsView]

  val formProvider: AnyOtherSupportingDocsFormProvider = injector.instanceOf[AnyOtherSupportingDocsFormProvider]

  val backLink: Call = Call("GET", "url")

  "Rendering the AnyOtherSupportDocs page" when {
    "no errors exist" should {
      val form: Form[Boolean] = formProvider.apply()
      lazy val view: Html = injectedView(form, backLink)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title of '${AnyOtherSupportingDocsMessages.pageTitle}'" in {
        document.title mustBe AnyOtherSupportingDocsMessages.pageTitle
      }

      "it" should {
        val form: Form[Boolean] = formProvider.apply()
        lazy val view: Html = injectedView(form, backLink)(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)
        s"have the correct page heading of '${AnyOtherSupportingDocsMessages.heading}'" in {
          elementText("h1") mustBe AnyOtherSupportingDocsMessages.heading
        }

        s"have the correct page text of '${AnyOtherSupportingDocsMessages.p1}'" in {
          elementText("#main-content p:nth-of-type(1)") mustBe AnyOtherSupportingDocsMessages.p1
        }

        s"have the correct page text of '${AnyOtherSupportingDocsMessages.bullet1}'" in {
          elementText("#main-content li:nth-of-type(1)") mustBe AnyOtherSupportingDocsMessages.bullet1
        }

        s"have the correct page text of '${AnyOtherSupportingDocsMessages.bullet2}'" in {
          elementText("#main-content li:nth-of-type(2)") mustBe AnyOtherSupportingDocsMessages.bullet2
        }

        s"have the correct page text of '${AnyOtherSupportingDocsMessages.bullet3}'" in {
          elementText("#main-content li:nth-of-type(3)") mustBe AnyOtherSupportingDocsMessages.bullet3
        }

        s"have the correct page text of '${AnyOtherSupportingDocsMessages.bullet4}'" in {
          elementText("#main-content li:nth-of-type(4)") mustBe AnyOtherSupportingDocsMessages.bullet4
        }

        s"have the correct value for the first radio button of '${AnyOtherSupportingDocsMessages.yes}'" in {
          elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(1) > label") mustBe AnyOtherSupportingDocsMessages.yes
        }

        s"have the correct value for the second radio button of '${AnyOtherSupportingDocsMessages.no}'" in {
          elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(2) > label") mustBe AnyOtherSupportingDocsMessages.no
        }

        "render a back link with the correct URL" in {
          elementAttributes("#back-link") must contain("href" -> "url")
        }

        s"have the correct Continue button" in {
          elementText(".govuk-button") mustBe continue
        }
      }
    }
  }
}