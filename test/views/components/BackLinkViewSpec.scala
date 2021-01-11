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

package views.components

import base.ViewBaseSpec
import messages.BaseMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.components.back_link

class BackLinkViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val target: back_link = app.injector.instanceOf[back_link]

  "Rendering the back link" when {
    "no URL is specified" should {

      lazy val markup: Html = target(None)
      lazy implicit val document: Document = Jsoup.parse(markup.toString)

      s"have the default URL" in {
        elementAttributes("a") must contain("href" -> "#")
      }
    }

    "a URL is specified" should {

      val url: String = "/some-relative-url/here"
      lazy val markup: Html = target(Some(Call("GET", url)))
      lazy implicit val document: Document = Jsoup.parse(markup.toString)

      s"have the include the specified URL" in {
        elementAttributes("a") must contain("href" -> url)
      }
    }
  }

  it should {
    lazy val markup: Html = target(None)
    lazy implicit val document: Document = Jsoup.parse(markup.toString)

    "render the correct link text" in {
      elementText("a") mustBe "Back"
    }

    "have the correct element ID" in {
      elementAttributes("a") must contain("id" -> "back-link")
    }

    "have the correct GDS CSS class" in {
      elementAttributes("a") must contain("class" -> "govuk-back-link")
    }
  }
}
