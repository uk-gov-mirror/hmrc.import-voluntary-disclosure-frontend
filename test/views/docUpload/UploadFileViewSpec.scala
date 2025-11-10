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
import messages.docUpload.UploadFileMessages
import mocks.config.MockAppConfig
import models.OptionalDocument
import models.OptionalDocument._
import models.upscan.{Reference, UpScanInitiateResponse, UploadFormTemplate}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.docUpload.UploadFileView

class UploadFileViewSpec extends ViewBaseSpec {

  private lazy val injectedView: UploadFileView = app.injector.instanceOf[UploadFileView]
  private lazy val initiateResponse: UpScanInitiateResponse =
    UpScanInitiateResponse(Reference("Upscan Ref"), UploadFormTemplate("url", Map.empty))
  private val backLink: Call                    = Call("GET", "url")
  private val maxOptDocs: Seq[OptionalDocument] = Seq(ImportAndEntry, AirwayBill, OriginProof, Other)

  val formProvider: UploadFileFormProvider = injector.instanceOf[UploadFileFormProvider]

  "Rendering the UploadFile page" when {
    "Optional Documents have been selected" should {
      val form: Form[String] = formProvider.apply()
      lazy val view: Html =
        injectedView(form, initiateResponse, Some(backLink), maxOptDocs)(fakeRequest, MockAppConfig.appConfig, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct form action" in {
        elementAttributes("form").get("action").get mustBe initiateResponse.uploadFormTemplate.href
      }

      "have the correct file upload control" in {
        element(".govuk-file-upload").attr("id") mustBe UploadFileMessages.fileUploadId
      }

      "have the correct file upload control file types" in {
        element(".govuk-file-upload").attr("accept") mustBe MockAppConfig.appConfig.upScanAcceptedFileTypes
      }

      "have heading for optional docs" in {
        elementText("#main-content p:nth-of-type(2)") mustBe UploadFileMessages.mayInclude
      }

      "have the correct text for optional file bullet 1" in {
        elementText("#main-content ul:nth-of-type(2) li:nth-of-type(1)") mustBe UploadFileMessages.mayIncludeFile1
      }

      "have the correct text for optional file bullet 2" in {
        elementText("#main-content ul:nth-of-type(2) li:nth-of-type(2)") mustBe UploadFileMessages.mayIncludeFile2
      }

      "have the correct text for optional file bullet 3" in {
        elementText("#main-content ul:nth-of-type(2) li:nth-of-type(3)") mustBe UploadFileMessages.mayIncludeFile3
      }

      "have the correct text for optional file bullet 4" in {
        elementText("#main-content ul:nth-of-type(2) li:nth-of-type(4)") mustBe UploadFileMessages.mayIncludeFile4
      }

      "an error exists (no file has been uploaded)" should {
        lazy val form: Form[String] = formProvider().withError("file", UploadFileMessages.fileUnknown)
        lazy val view: Html =
          injectedView(form, initiateResponse, Some(backLink), maxOptDocs)(fakeRequest, MockAppConfig.appConfig, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        checkPageTitle(UploadFileMessages.errorPrefix + UploadFileMessages.title)

        "render an error summary with the correct message" in {
          elementText(
            "div.govuk-error-summary > div"
          ) mustBe UploadFileMessages.thereIsAProblemPrefix + UploadFileMessages.fileUnknown
        }

        "render an error message against the field" in {
          elementText("#file-error") mustBe UploadFileMessages.errorPrefix + UploadFileMessages.fileUnknown
        }

      }

    }

    "Optional Documents have not been selected" should {
      val form: Form[String] = formProvider.apply()
      lazy val view: Html =
        injectedView(form, initiateResponse, Some(backLink), Seq.empty)(fakeRequest, MockAppConfig.appConfig, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct form action" in {
        elementAttributes("form").get("action").get mustBe initiateResponse.uploadFormTemplate.href
      }

      "have the correct file upload control" in {
        element(".govuk-file-upload").attr("id") mustBe UploadFileMessages.fileUploadId
      }

      "have the correct file upload control file types" in {
        element(".govuk-file-upload").attr("accept") mustBe MockAppConfig.appConfig.upScanAcceptedFileTypes
      }

    }

    "In check mode and all supporting documents have been removed" should {
      val form: Form[String] = formProvider.apply()
      lazy val view: Html = injectedView(form, initiateResponse, None, Seq.empty)(fakeRequest, MockAppConfig.appConfig, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "no back button displayed" in {
        elementExtinct("#back-link")
      }
    }

  }

  it should {
    val form: Form[String] = formProvider.apply()
    lazy val view: Html =
      injectedView(form, initiateResponse, Some(backLink), maxOptDocs)(fakeRequest, MockAppConfig.appConfig, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    checkPageTitle(UploadFileMessages.title)

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> "url")
    }

    s"have the correct h1 of '${UploadFileMessages.title}'" in {
      elementText("h1") mustBe UploadFileMessages.title
    }

    s"have the correct text of '${UploadFileMessages.mustInclude}'" in {
      elementText("#main-content p:nth-of-type(1)") mustBe UploadFileMessages.mustInclude
    }

    "have the correct text for mandatory file bullet 1" in {
      elementText("#main-content ul:nth-of-type(1) li:nth-of-type(1)") mustBe UploadFileMessages.mustIncludeFile1
    }

    "have the correct text for mandatory file bullet 2" in {
      elementText("#main-content ul:nth-of-type(1) li:nth-of-type(2)") mustBe UploadFileMessages.mustIncludeFile2
    }

    "have the correct text for mandatory file bullet 3" in {
      elementText("#main-content ul:nth-of-type(1) li:nth-of-type(3)") mustBe UploadFileMessages.mustIncludeFile3
    }

    s"have correct expandable text '${UploadFileMessages.typesOfFile}'" in {
      elementText("#main-content > div > div > details > summary > span") mustBe UploadFileMessages.typesOfFile
    }

    s"have correct bullet points within the expandable text '${UploadFileMessages.filePdf}'" in {
      elementText(
        "#main-content > div > div > details > div > ul:nth-of-type(1) li:nth-of-type(1)"
      ) mustBe UploadFileMessages.filePdf
    }

    s"have correct bullet points within the expandable text '${UploadFileMessages.fileCsv}'" in {
      elementText(
        "#main-content > div > div > details > div > ul:nth-of-type(1) li:nth-of-type(2)"
      ) mustBe UploadFileMessages.fileCsv
    }

    s"have correct bullet points within the expandable text '${UploadFileMessages.fileMs}'" in {
      elementText(
        "#main-content > div > div > details > div > ul:nth-of-type(1) li:nth-of-type(3)"
      ) mustBe UploadFileMessages.fileMs
    }

    s"have correct bullet points within the expandable text '${UploadFileMessages.fileOd}'" in {
      elementText(
        "#main-content > div > div > details > div > ul:nth-of-type(1) li:nth-of-type(4)"
      ) mustBe UploadFileMessages.fileOd
    }

    s"have correct bullet points within the expandable text '${UploadFileMessages.fileImage}'" in {
      elementText(
        "#main-content > div > div > details > div > ul:nth-of-type(1) li:nth-of-type(5)"
      ) mustBe UploadFileMessages.fileImage
    }

    s"have the correct text of '${UploadFileMessages.fileSize}'" in {
      elementText("#main-content p:nth-of-type(3)") mustBe UploadFileMessages.fileSize
    }

    "have the correct Continue button" in {
      elementText(".govuk-button") mustBe UploadFileMessages.uploadChosenFile
    }

  }
}
