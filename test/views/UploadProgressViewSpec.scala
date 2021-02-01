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
import messages.UploadProgressMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.UploadProgressView

class UploadProgressViewSpec extends ViewBaseSpec {

  private lazy val injectedView: UploadProgressView = app.injector.instanceOf[UploadProgressView]
  private val reference: String = "11370e18-6e24-453e-b45a-76d3e32ea33d"
  private val backLink: Call = Call("GET", "url")

  "Rendering the Upload Progress page" when {
    lazy val view: Html = injectedView(reference, backLink)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "called normally" should {
      "have the correct button link" in {
        elementAttributes("#main-content .govuk-button").get("href").get mustBe
          controllers.routes.UploadFileController.uploadProgress(reference).url
      }
    }
  }

  it should {
    lazy val view: Html = injectedView(reference, backLink)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct page title" in {
      document.title mustBe UploadProgressMessages.title
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> "url")
    }

    s"have the correct h1 of '${UploadProgressMessages.h1}'" in {
      elementText("h1") mustBe UploadProgressMessages.h1
    }

    s"have the correct text of '${UploadProgressMessages.waiting}'" in {
      elementText("#main-content p:nth-of-type(1)") mustBe UploadProgressMessages.waiting
    }

    s"have the correct text of '${UploadProgressMessages.request}'" in {
      elementText("#main-content p:nth-of-type(2)") mustBe UploadProgressMessages.request
    }

    s"have the correct Refresh button text" in {
      elementText("#main-content .govuk-button") mustBe UploadProgressMessages.refresh
    }

  }
}
