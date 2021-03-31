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
import messages.UploadAuthoritySuccessMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.UploadAuthoritySuccessView

class UploadAuthoritySuccessViewSpec extends ViewBaseSpec {

  private lazy val injectedView: UploadAuthoritySuccessView = app.injector.instanceOf[UploadAuthoritySuccessView]
  private val filename: String = UploadAuthoritySuccessMessages.filename
  private val action: String = "action/url"

  "Rendering the Upload Progress page" when {
    lazy val view: Html = injectedView(filename, action)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "called normally" should {
      "have the correct button link" in {
        elementAttributes("#main-content .govuk-button").get("href").get mustBe action
      }
    }
  }

  it should {
    lazy val view: Html = injectedView(filename, action)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct page title" in {
      document.title mustBe UploadAuthoritySuccessMessages.title
    }

    "not have a back link" in {
      elementExtinct("#back-link")
    }

    s"have the correct h1 of '${UploadAuthoritySuccessMessages.h1}'" in {
      elementText("h1") mustBe UploadAuthoritySuccessMessages.h1
    }

    s"have the correct text of '${UploadAuthoritySuccessMessages.bodyText}'" in {
      elementText("#main-content p:nth-of-type(1)") mustBe UploadAuthoritySuccessMessages.bodyText
    }

     s"have the correct Continue button text" in {
      elementText("#main-content .govuk-button") mustBe UploadAuthoritySuccessMessages.continue
    }

  }
}
