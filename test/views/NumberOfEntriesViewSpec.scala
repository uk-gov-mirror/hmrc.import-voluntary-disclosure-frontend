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

import java.time.LocalDateTime

import forms.NumberOfEntriesFormProvider
import messages.{BaseMessages, NumberOfEntriesMessages}
import models.UserAnswers
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.libs.json.Json
import play.twirl.api.Html
import views.html.NumberOfEntriesView

class NumberOfEntriesViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: NumberOfEntriesView = app.injector.instanceOf[NumberOfEntriesView]

  val userAnswers: UserAnswers = UserAnswers(
    "123456",
    Json.obj("value" -> "oneEntry"),
    LocalDateTime.now()
  )
  val formProvider = injector.instanceOf[NumberOfEntriesFormProvider]
  val form = formProvider.apply()

  "Rendering the NumberOfEntries page" when {

    lazy val view: Html = injectedView(form, userAnswers)(fakeRequest, messages)

    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct page heading of '${NumberOfEntriesMessages.title}'" in {
      document.title mustBe NumberOfEntriesMessages.title
    }

    s"have the correct h1 of '${NumberOfEntriesMessages.h1}'" in {
      elementText("h1") mustBe NumberOfEntriesMessages.h1
    }

    s"have the correct value for the first radio button of '${NumberOfEntriesMessages.radioButtonOne}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(1)") mustBe NumberOfEntriesMessages.radioButtonOne
    }

    s"have the correct value for the second radio button of '${NumberOfEntriesMessages.radioButtonTwo}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(2)") mustBe
        s"${NumberOfEntriesMessages.radioButtonTwo} ${NumberOfEntriesMessages.hint}"
    }
  }
}
