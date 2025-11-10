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

package views.updateCase

import base.ViewBaseSpec
import forms.shared.UploadFileFormProvider
import messages.updateCase.UploadSupportingDocumentationMessages
import mocks.config.MockAppConfig
import models.upscan._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.updateCase.UploadSupportingDocumentationView

class UploadSupportingDocumentationViewSpec extends ViewBaseSpec {

  private lazy val injectedView: UploadSupportingDocumentationView =
    app.injector.instanceOf[UploadSupportingDocumentationView]
  private lazy val initiateResponse: UpScanInitiateResponse =
    UpScanInitiateResponse(Reference("Upscan Ref"), UploadFormTemplate("url", Map.empty))
  private val backLink: Call = Call("GET", "url")

  val zeroFilesUploaded = 0
  val oneFileUploaded   = 1

  val formProvider: UploadFileFormProvider = injector.instanceOf[UploadFileFormProvider]

  "Rendering the UploadSupportingDocumentation page" when {
    val form: Form[String] = formProvider.apply()
    lazy val view: Html =
      injectedView(form, initiateResponse, backLink, oneFileUploaded, checkMode = false)(
        fakeRequest,
        MockAppConfig.appConfig,
        messages
      )
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> "url")
    }

    s"have the correct form action" in {
      elementAttributes("form").get("action") mustBe Some(initiateResponse.uploadFormTemplate.href)
    }

    s"have the correct file upload control" in {
      element(".govuk-file-upload").attr("id") mustBe UploadSupportingDocumentationMessages.fileUploadId
    }

    s"have the correct file upload control file types" in {
      element(".govuk-file-upload").attr("accept") mustBe MockAppConfig.appConfig.upScanAcceptedFileTypes
    }

    "an error exists (no file has been uploaded)" should {
      lazy val form: Form[String] = formProvider().withError("file", UploadSupportingDocumentationMessages.fileUnknown)
      lazy val view: Html =
        injectedView(form, initiateResponse, backLink, zeroFilesUploaded, checkMode = false)(
          fakeRequest,
          MockAppConfig.appConfig,
          messages
        )
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(UploadSupportingDocumentationMessages.errorPrefix + UploadSupportingDocumentationMessages.title)

      "render an error summary with the correct message" in {
        elementText(
          "div.govuk-error-summary > div"
        ) mustBe UploadSupportingDocumentationMessages.thereIsAProblemPrefix + UploadSupportingDocumentationMessages.fileUnknown
      }

      "render an error message against the field" in {
        elementText(
          "#file-error"
        ) mustBe UploadSupportingDocumentationMessages.errorPrefix + UploadSupportingDocumentationMessages.fileUnknown
      }

    }

  }

  it should {
    val form: Form[String] = formProvider.apply()
    lazy val view: Html =
      injectedView(form, initiateResponse, backLink, oneFileUploaded, checkMode = true)(
        fakeRequest,
        MockAppConfig.appConfig,
        messages
      )
    lazy implicit val document: Document = Jsoup.parse(view.body)

    checkPageTitle(UploadSupportingDocumentationMessages.title)

    s"have the correct h1 of '${UploadSupportingDocumentationMessages.title}'" in {
      elementText("h1") mustBe UploadSupportingDocumentationMessages.title
    }

    s"have the correct text of '${UploadSupportingDocumentationMessages.fileSize}'" in {
      elementText("#main-content p:nth-of-type(1)") mustBe UploadSupportingDocumentationMessages.fileSize
    }

    s"have the correct list of allowed file types" in {
      elementText(".govuk-heading-s") mustBe UploadSupportingDocumentationMessages.typesOfFile
      elementText(".govuk-list > li:nth-of-type(1)") mustBe UploadSupportingDocumentationMessages.filePdf
      elementText(".govuk-list > li:nth-of-type(2)") mustBe UploadSupportingDocumentationMessages.fileCsv
      elementText(".govuk-list > li:nth-of-type(3)") mustBe UploadSupportingDocumentationMessages.fileMs
      elementText(".govuk-list > li:nth-of-type(4)") mustBe UploadSupportingDocumentationMessages.fileOd
      elementText(".govuk-list > li:nth-of-type(5)") mustBe UploadSupportingDocumentationMessages.fileImage
    }

    s"have the '${UploadSupportingDocumentationMessages.uploadAFile}' upload label" in {
      elementText(".govuk-form-group > label") mustBe UploadSupportingDocumentationMessages.uploadAFile
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe UploadSupportingDocumentationMessages.uploadChosenFile
    }

  }

}
