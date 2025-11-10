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

package views.paymentInfo

import base.ViewBaseSpec
import forms.shared.UploadFileFormProvider
import messages.paymentInfo.UploadAuthorityMessages
import mocks.config.MockAppConfig
import models.upscan.{Reference, UpScanInitiateResponse, UploadFormTemplate}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.paymentInfo.UploadAuthorityView

class UploadAuthorityViewSpec extends ViewBaseSpec {

  private val dutyTypeKey                            = "both"
  private lazy val injectedView: UploadAuthorityView = app.injector.instanceOf[UploadAuthorityView]
  private lazy val initiateResponse: UpScanInitiateResponse =
    UpScanInitiateResponse(Reference("Upscan Ref"), UploadFormTemplate("url", Map.empty))
  private val backLink: Call = Call("GET", "url")

  val formProvider: UploadFileFormProvider = injector.instanceOf[UploadFileFormProvider]

  "Rendering the UploadAuthorityFile page" when {
    val form: Form[String] = formProvider.apply()
    lazy val view: Html =
      injectedView(form, initiateResponse, backLink, "importer", dutyTypeKey)(fakeRequest, MockAppConfig.appConfig, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct form action" in {
      elementAttributes("form").get("action").get mustBe initiateResponse.uploadFormTemplate.href
    }

    s"have the correct file upload control" in {
      element(".govuk-file-upload").attr("id") mustBe UploadAuthorityMessages.fileUploadId
    }

    s"have the correct file upload control file types" in {
      element(".govuk-file-upload").attr("accept") mustBe MockAppConfig.appConfig.upScanAcceptedFileTypes
    }

    s"have the correct text for duty only" in {
      val dutyType           = "duty"
      val form: Form[String] = formProvider.apply()
      lazy val view: Html =
        injectedView(form, initiateResponse, backLink, "importer", dutyType)(fakeRequest, MockAppConfig.appConfig, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      elementText("#main-content p:nth-of-type(1)") mustBe UploadAuthorityMessages.para1(dutyType)
    }

    s"have the correct text for vat only" in {
      val dutyType           = "vat"
      val form: Form[String] = formProvider.apply()
      lazy val view: Html =
        injectedView(form, initiateResponse, backLink, "importer", dutyType)(fakeRequest, MockAppConfig.appConfig, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      elementText("#main-content p:nth-of-type(1)") mustBe UploadAuthorityMessages.para1(dutyType)
    }

    s"have the correct text for both duty and vat" in {
      val dutyType           = "both"
      val form: Form[String] = formProvider.apply()
      lazy val view: Html =
        injectedView(form, initiateResponse, backLink, "importer", dutyType)(fakeRequest, MockAppConfig.appConfig, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      elementText("#main-content p:nth-of-type(1)") mustBe UploadAuthorityMessages.para1(dutyType)
    }

    "an error exists (no file has been uploaded)" should {
      val dutyType                = "vat"
      lazy val form: Form[String] = formProvider().withError("file", UploadAuthorityMessages.fileUnknown)
      lazy val view: Html =
        injectedView(form, initiateResponse, backLink, "importer", dutyType)(fakeRequest, MockAppConfig.appConfig, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(UploadAuthorityMessages.errorPrefix + UploadAuthorityMessages.title)

      "render an error summary with the correct message" in {
        elementText(
          "div.govuk-error-summary > div"
        ) mustBe UploadAuthorityMessages.thereIsAProblemPrefix + UploadAuthorityMessages.fileUnknown
      }

      "render an error message against the field" in {
        elementText("#file-error") mustBe UploadAuthorityMessages.errorPrefix + UploadAuthorityMessages.fileUnknown
      }

    }

  }

  it should {
    val form: Form[String] = formProvider.apply()
    lazy val view: Html =
      injectedView(form, initiateResponse, backLink, "importer", dutyTypeKey)(fakeRequest, MockAppConfig.appConfig, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    checkPageTitle(UploadAuthorityMessages.title)

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> "url")
    }

    s"have the correct h1 of '${UploadAuthorityMessages.title}'" in {
      elementText("h1") mustBe UploadAuthorityMessages.title
    }

    s"have the correct text of '${UploadAuthorityMessages.para2}'" in {
      elementText("#main-content p:nth-of-type(2)") mustBe UploadAuthorityMessages.para2
    }

    s"have the correct text of '${UploadAuthorityMessages.fileSize}'" in {
      elementText("#main-content p:nth-of-type(3)") mustBe UploadAuthorityMessages.fileSize
    }

    s"have the correct list of allowed file types" in {
      elementText("details > summary > span") mustBe UploadAuthorityMessages.typesOfFile
      elementText("details li:nth-of-type(1)") mustBe UploadAuthorityMessages.filePdf
      elementText("details li:nth-of-type(2)") mustBe UploadAuthorityMessages.fileCsv
      elementText("details li:nth-of-type(3)") mustBe UploadAuthorityMessages.fileMs
      elementText("details li:nth-of-type(4)") mustBe UploadAuthorityMessages.fileOd
      elementText("details li:nth-of-type(5)") mustBe UploadAuthorityMessages.fileImage
    }

    s"have the '${UploadAuthorityMessages.uploadFile}' upload label" in {
      elementText(".govuk-form-group > label") mustBe UploadAuthorityMessages.uploadFile
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe UploadAuthorityMessages.uploadChosenFile
    }

  }

}
