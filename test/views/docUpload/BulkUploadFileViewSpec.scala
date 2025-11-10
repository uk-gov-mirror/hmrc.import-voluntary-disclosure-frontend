/*
 * Copyright 2025 HM Revenue & Customs
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

package views.docUpload

import base.ViewBaseSpec
import forms.shared.UploadFileFormProvider
import messages.docUpload.BulkUploadFileMessages
import mocks.config.MockAppConfig
import models.upscan.{Reference, UpScanInitiateResponse, UploadFormTemplate}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.docUpload.BulkUploadFileView

class BulkUploadFileViewSpec extends ViewBaseSpec {

  private lazy val injectedView: BulkUploadFileView = app.injector.instanceOf[BulkUploadFileView]
  private lazy val initiateResponse: UpScanInitiateResponse =
    UpScanInitiateResponse(Reference("Upscan Ref"), UploadFormTemplate("url", Map.empty))
  private val backLink: Call = Call("GET", "url")

  val formProvider: UploadFileFormProvider = injector.instanceOf[UploadFileFormProvider]

  "Rendering the UploadFile page" should {
    val form: Form[String] = formProvider.apply()
    lazy val view: Html    = injectedView(form, initiateResponse, backLink)(fakeRequest, MockAppConfig.appConfig, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct form action" in {
      elementAttributes("form").get("action").get mustBe initiateResponse.uploadFormTemplate.href
    }

    "have the correct file upload control" in {
      element(".govuk-file-upload").attr("id") mustBe BulkUploadFileMessages.fileUploadId
    }

    "have the correct file upload control file types" in {
      element(".govuk-file-upload").attr("accept") mustBe MockAppConfig.appConfig.upScanAcceptedFileTypes
    }

  }

  it should {
    val form: Form[String] = formProvider.apply()
    lazy val view: Html    = injectedView(form, initiateResponse, backLink)(fakeRequest, MockAppConfig.appConfig, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    checkPageTitle(BulkUploadFileMessages.title)

    s"have the correct h1 of '${BulkUploadFileMessages.title}'" in {
      elementText("h1") mustBe BulkUploadFileMessages.title
    }

    s"have the correct text of '${BulkUploadFileMessages.mustContain}'" in {
      elementText("#main-content p:nth-of-type(1)") mustBe BulkUploadFileMessages.mustContain
    }

    "have the correct text for mandatory file bullet 1" in {
      elementText("#main-content ul:nth-of-type(1) li:nth-of-type(1)") mustBe BulkUploadFileMessages.bullet1
    }

    "have the correct text for mandatory file bullet 2" in {
      elementText("#main-content ul:nth-of-type(1) li:nth-of-type(2)") mustBe BulkUploadFileMessages.bullet2
    }

    "have the correct text for mandatory file bullet 3" in {
      elementText("#main-content ul:nth-of-type(1) li:nth-of-type(3)") mustBe BulkUploadFileMessages.bullet3
    }

    "have the correct text for mandatory file bullet 4" in {
      elementText("#main-content ul:nth-of-type(1) li:nth-of-type(4)") mustBe BulkUploadFileMessages.bullet4
    }

    "have the correct text for mandatory file bullet 5" in {
      elementText("#main-content ul:nth-of-type(1) li:nth-of-type(5)") mustBe BulkUploadFileMessages.bullet5
    }

    s"have correct expandable text '${BulkUploadFileMessages.typesOfFile}'" in {
      elementText("#main-content > div > div > details > summary > span") mustBe BulkUploadFileMessages.typesOfFile
    }

    s"have correct link within the expandable text '${BulkUploadFileMessages.filePdf}'" in {
      elementText(
        "#main-content > div > div > details > div > ul:nth-of-type(1) li:nth-of-type(1)"
      ) mustBe BulkUploadFileMessages.filePdf
    }

    s"have correct link within the expandable text '${BulkUploadFileMessages.fileCsv}'" in {
      elementText(
        "#main-content > div > div > details > div > ul:nth-of-type(1) li:nth-of-type(2)"
      ) mustBe BulkUploadFileMessages.fileCsv
    }

    s"have correct link within the expandable text '${BulkUploadFileMessages.fileMs}'" in {
      elementText(
        "#main-content > div > div > details > div > ul:nth-of-type(1) li:nth-of-type(3)"
      ) mustBe BulkUploadFileMessages.fileMs
    }

    s"have correct link within the expandable text '${BulkUploadFileMessages.fileOd}'" in {
      elementText(
        "#main-content > div > div > details > div > ul:nth-of-type(1) li:nth-of-type(4)"
      ) mustBe BulkUploadFileMessages.fileOd
    }

    s"have correct link within the expandable text '${BulkUploadFileMessages.fileImage}'" in {
      elementText(
        "#main-content > div > div > details > div > ul:nth-of-type(1) li:nth-of-type(5)"
      ) mustBe BulkUploadFileMessages.fileImage
    }

    "have the correct Continue button" in {
      elementText(".govuk-button") mustBe BulkUploadFileMessages.uploadChosenFile
    }

  }
}
