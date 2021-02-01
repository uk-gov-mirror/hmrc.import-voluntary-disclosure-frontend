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
import messages.UploadFileMessages
import mocks.config.MockAppConfig
import models.upscan.{Reference, UpScanInitiateResponse, UploadFormTemplate}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.UploadFileView

class UploadFileViewSpec extends ViewBaseSpec {

  private lazy val injectedView: UploadFileView = app.injector.instanceOf[UploadFileView]
  private lazy val initiateResponse: UpScanInitiateResponse =
    UpScanInitiateResponse(Reference("Upscan Ref"), UploadFormTemplate("url", Map.empty))
  private val backLink: Call = Call("GET", "url")

  "Rendering the UploadFile page" when {
    lazy val view: Html = injectedView(initiateResponse, backLink)(fakeRequest, MockAppConfig, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct form action" in {
      elementAttributes("form").get("action").get mustBe initiateResponse.uploadFormTemplate.href
    }

    s"have the correct file upload control" in {
      element(".govuk-file-upload").attr("id") mustBe UploadFileMessages.fileUploadId
    }

    s"have the correct file upload control file types" in {
      element(".govuk-file-upload").attr("accept") mustBe MockAppConfig.upScanAcceptedFileTypes
    }
  }

  it should {
    lazy val view: Html = injectedView(initiateResponse, backLink)(fakeRequest, MockAppConfig, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct page title" in {
      document.title mustBe UploadFileMessages.title
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> "url")
    }

    s"have the correct h1 of '${UploadFileMessages.h1}'" in {
      elementText("h1") mustBe UploadFileMessages.h1
    }

    s"have the correct text of '${UploadFileMessages.mustInclude}'" in {
      elementText("#main-content p:nth-of-type(1)") mustBe UploadFileMessages.mustInclude
    }

    s"have the correct text for mandatory file bullet 1" in {
      elementText("#main-content ul:nth-of-type(1) li:nth-of-type(1)") mustBe UploadFileMessages.mustIncludeFile1
    }

    s"have the correct text for mandatory file bullet 2" in {
      elementText("#main-content ul:nth-of-type(1) li:nth-of-type(2)") mustBe UploadFileMessages.mustIncludeFile2
    }

    s"have the correct text of '${UploadFileMessages.mayInclude}'" in {
      elementText("#main-content p:nth-of-type(2)") mustBe UploadFileMessages.mayInclude
    }

    s"have the correct text for optional file bullet 1" in {
      elementText("#main-content ul:nth-of-type(2) li:nth-of-type(1)") mustBe UploadFileMessages.mayIncludeFile1
    }

    s"have the correct text for optional file bullet 2" in {
      elementText("#main-content ul:nth-of-type(2) li:nth-of-type(2)") mustBe UploadFileMessages.mayIncludeFile2
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe UploadFileMessages.continue
    }

  }
}
