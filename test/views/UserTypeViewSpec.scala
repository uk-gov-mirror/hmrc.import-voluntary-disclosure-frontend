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
import forms.UserTypeFormProvider
import messages.{BaseMessages, UserTypeMessages}
import models.{UserAnswers, UserType}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.libs.json.Json
import play.twirl.api.Html
import views.html.UserTypeView

import java.time.LocalDateTime

class UserTypeViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: UserTypeView = app.injector.instanceOf[UserTypeView]

  val userAnswers: UserAnswers = UserAnswers(
    "123456",
    Json.obj("value" -> "importer"),
    LocalDateTime.now()
  )
  val formProvider: UserTypeFormProvider = injector.instanceOf[UserTypeFormProvider]

  "Rendering the UserType page" when {

    "no errors exist" should {
      lazy val form: Form[UserType] = formProvider()
      lazy val view: Html = injectedView(form, userAnswers)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page heading of '${UserTypeMessages.title}'" in {
        document.title mustBe UserTypeMessages.title
      }

      s"have the correct h1 of '${UserTypeMessages.h1}'" in {
        elementText("h1") mustBe UserTypeMessages.h1
      }

      s"have the correct value for the first radio button of '${UserTypeMessages.radioButtonOne}'" in {
        elementText("#main-content div.govuk-radios__item:nth-child(1)") mustBe UserTypeMessages.radioButtonOne
      }

      s"have the correct value for the second radio button of '${UserTypeMessages.radioButtonTwo}'" in {
        elementText("#main-content div.govuk-radios__item:nth-child(2)") mustBe UserTypeMessages.radioButtonTwo
      }

      "not render a back link" in {
        document.select("#back-link").size mustBe 0
      }
    }

    "an error exists (no option has been selected)" should {
      lazy val form: Form[UserType] = formProvider().bind(Map("value" -> ""))
      lazy val view: Html = injectedView(form, userAnswers)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "render an error summary with the correct message" in {
        elementText("div.govuk-error-summary > div") mustBe UserTypeMessages.requiredError
      }

      "render an error message against the field" in {
        elementText("#value-error") mustBe UserTypeMessages.errorPrefix + UserTypeMessages.requiredError
      }

    }
  }
}