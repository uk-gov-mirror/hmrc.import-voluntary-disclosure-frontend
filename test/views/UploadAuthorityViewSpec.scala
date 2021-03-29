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
import messages.UploadAuthorityMessages
import mocks.config.MockAppConfig
import models.upscan.{Reference, UpScanInitiateResponse, UploadFormTemplate}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.UploadAuthorityView

class UploadAuthorityViewSpec extends ViewBaseSpec {

  private val dan: String = "1234567"
  private val dutyTypeKey = "both"
  private lazy val injectedView: UploadAuthorityView = app.injector.instanceOf[UploadAuthorityView]
  private lazy val initiateResponse: UpScanInitiateResponse =
    UpScanInitiateResponse(Reference("Upscan Ref"), UploadFormTemplate("url", Map.empty))
  private val backLink: Call = Call("GET", "url")

  "Rendering the UploadFile page" when {
    lazy val view: Html = injectedView(initiateResponse, backLink, dan, dutyTypeKey)(fakeRequest, MockAppConfig, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct form action" in {
      elementAttributes("form").get("action").get mustBe initiateResponse.uploadFormTemplate.href
    }

    s"have the correct file upload control" in {
      element(".govuk-file-upload").attr("id") mustBe UploadAuthorityMessages.fileUploadId
    }

    s"have the correct file upload control file types" in {
      element(".govuk-file-upload").attr("accept") mustBe MockAppConfig.upScanAcceptedFileTypes
    }
    
  }



  it should {
    lazy val view: Html = injectedView(initiateResponse, backLink, dan, dutyTypeKey)(fakeRequest, MockAppConfig, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct page title" in {
      document.title mustBe UploadAuthorityMessages.title
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> "url")
    }

    s"have the correct h1 of '${UploadAuthorityMessages.h1}'" in {
      elementText("h1") mustBe UploadAuthorityMessages.h1
    }

    s"have the correct text of '${UploadAuthorityMessages.para2}'" in {
      elementText("#main-content p:nth-of-type(2)") mustBe UploadAuthorityMessages.para2
    }

    s"have the correct h1 of '${UploadAuthorityMessages.h2}'" in {
      elementText("h2") mustBe UploadAuthorityMessages.h2
    }

    s"have the correct text of '${UploadAuthorityMessages.para3}'" in {
      elementText("#main-content p:nth-of-type(3)") mustBe UploadAuthorityMessages.para3
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe UploadAuthorityMessages.button
    }

  }

  Seq("duty", "vat", "both").map { dutyType =>
    lazy val view: Html = injectedView(initiateResponse, backLink, dan, dutyType)(fakeRequest, MockAppConfig, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct text of '${UploadAuthorityMessages.para1(dan, dutyType)}'" in {
      elementText("#main-content p:nth-of-type(1)") mustBe UploadAuthorityMessages.para1(dan, dutyType)
    }
  }

}
