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
import messages.{BaseMessages, BoxGuidanceMessages}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.html.BoxGuidanceView

class BoxGuidanceViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: BoxGuidanceView = app.injector.instanceOf[BoxGuidanceView]

  "Rendering the Supportdoc page" when {
    "no errors exist" should {
      lazy val view: Html = injectedView(controllers.routes.BoxGuidanceController.onLoad())(fakeRequest, appConfig, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title of '${BoxGuidanceMessages.title}'" in {
        document.title mustBe BoxGuidanceMessages.title
      }

      "it" should {
        lazy val view: Html = injectedView(controllers.routes.BoxGuidanceController.onLoad())(fakeRequest, appConfig, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)
        s"have the correct page heading of '${BoxGuidanceMessages.heading}'" in {
          elementText("h1") mustBe BoxGuidanceMessages.heading
        }

        s"have the correct p1 page text of '${BoxGuidanceMessages.p1}'" in {
          elementText("#main-content p:nth-of-type(1)") mustBe BoxGuidanceMessages.p1
        }

        s"have the correct p2 page text of '${BoxGuidanceMessages.p2}'" in {
          elementText("#main-content p:nth-of-type(2)") mustBe BoxGuidanceMessages.p2
        }

        s"have the correct page text of '${BoxGuidanceMessages.bullet1}'" in {
          elementText("#main-content li:nth-of-type(1)") mustBe BoxGuidanceMessages.bullet1
        }

        s"have the correct page text of '${BoxGuidanceMessages.bullet2}'" in {
          elementText("#main-content li:nth-of-type(2)") mustBe BoxGuidanceMessages.bullet2
        }

        s"have the correct page text of '${BoxGuidanceMessages.bullet3}'" in {
          elementText("#main-content li:nth-of-type(3)") mustBe BoxGuidanceMessages.bullet3
        }

        s"have the correct p2 page text of '${BoxGuidanceMessages.p3}'" in {
          elementText("#main-content p:nth-of-type(3)") mustBe BoxGuidanceMessages.p3
        }

        "render a continue button with the correct URL " in {
          elementAttributes(".govuk-button") must contain("href" -> controllers.routes.BoxGuidanceController.onLoad().url)
        }

        "render a back link with the correct URL" in {
          elementAttributes("#back-link") must contain("href" -> controllers.routes.BoxGuidanceController.onLoad().url)
        }
      }
    }
  }
}
