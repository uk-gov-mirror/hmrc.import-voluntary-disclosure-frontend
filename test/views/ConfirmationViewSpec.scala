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
import messages.ConfirmationMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.html.ConfirmationView

class ConfirmationViewSpec extends ViewBaseSpec {

  private lazy val injectedView: ConfirmationView = app.injector.instanceOf[ConfirmationView]
  private val entryNumber: String = "C18-101"

  "Rendering the Confirmation page" when {

    "no errors exist" should {
      lazy val view: Html = injectedView(entryNumber)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title of '${ConfirmationMessages.pageTitle}'" in {
        document.title mustBe ConfirmationMessages.pageTitle
      }

      s"have the correct Entry number value" in {
        elementText(".govuk-panel__body > strong") mustBe entryNumber
      }
    }

    "it" should {
      lazy val view: Html = injectedView()(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)
      s"have the correct page heading of '${ConfirmationMessages.heading}'" in {
        elementText("h1") mustBe ConfirmationMessages.heading
      }

      s"have the correct entry number message of '${ConfirmationMessages.entryNumber}'" in {
        elementText("#entryNumber") mustBe ConfirmationMessages.entryNumber
      }

      s"have the p1 message of '${ConfirmationMessages.p1}'" in {
        elementText("#p1") mustBe ConfirmationMessages.p1
      }

      s"have the correct icon displayed " in {
        document.select("#icon").size mustBe 1
      }

      s"have the correct Print and Save message of '${ConfirmationMessages.printSave}'" in {
        elementText("#printSave") mustBe ConfirmationMessages.printSave
      }

      s"have the correct Print and Save Rest of Message '${ConfirmationMessages.printSaveRestOfMessage}'" in {
        elementText("#printSaveRestOfMessage") mustBe ConfirmationMessages.printSaveRestOfMessage
      }

      s"have the h2 message of '${ConfirmationMessages.whatHappensNext}'" in {
        elementText("#whatHappensNext") mustBe ConfirmationMessages.whatHappensNext
      }

      s"have the p2 message of '${ConfirmationMessages.p2}'" in {
        elementText("#p2") mustBe ConfirmationMessages.p2
      }

      s"have the p3 message of '${ConfirmationMessages.p3}'" in {
        elementText("#p3") mustBe ConfirmationMessages.p3
      }

    }

  }

}
