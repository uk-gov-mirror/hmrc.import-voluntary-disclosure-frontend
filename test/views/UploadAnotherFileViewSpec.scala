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
import forms.UploadAnotherFileFormProvider
import messages.{BaseMessages, UploadAnotherFileMessages}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import viewmodels.AddToListRow
import views.html.UploadAnotherFileView
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, Value}

class UploadAnotherFileViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: UploadAnotherFileView = app.injector.instanceOf[UploadAnotherFileView]

  val formProvider: UploadAnotherFileFormProvider = injector.instanceOf[UploadAnotherFileFormProvider]

  val answers: Seq[AddToListRow] = Seq(AddToListRow(
    value = Value(HtmlContent("")),
    removeAction = Some(ActionItem(href = "", content = Text("Remove"), visuallyHiddenText = Some("")))))

  val answersTwoFiles: Seq[AddToListRow] = Seq(AddToListRow(
    value = Value(HtmlContent("")),
    removeAction = Some(ActionItem(href = "", content = Text("Remove"), visuallyHiddenText = Some("")))),
    AddToListRow(
    value = Value(HtmlContent("")),
    removeAction = Some(ActionItem(href = "", content = Text("Remove"), visuallyHiddenText = Some("")))
  ))


  "Rendering the UploadAnotherFile page" when {
    "no errors exist when one file is present" should {

      val form: Form[Boolean] = formProvider.apply()
      lazy val view: Html = injectedView(form,answers)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe UploadAnotherFileMessages.title("1","file")
      }

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }

      "remove link is present" in {
        document.select("#main-content > div > div > form > ul > li > span.hmrc-add-to-a-list__remove > a").size mustBe 1
      }

      "first remove contains the correct text" in {
        document.select("#main-content > div > div > form > ul > li:nth-child(1) > span.hmrc-add-to-a-list__remove > a > span:nth-child(1)").text mustBe UploadAnotherFileMessages.remove
      }

    }

    "no errors exist when two files are present" should {

      val form: Form[Boolean] = formProvider.apply()
      lazy val view: Html = injectedView(form,answersTwoFiles)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe UploadAnotherFileMessages.title("2","files")
      }

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }

      "remove link is present" in {
        document.select("#main-content > div > div > form > ul > li > span.hmrc-add-to-a-list__remove > a").size mustBe 2
      }

      "first remove contains the correct text" in {
        document.select("#main-content > div > div > form > ul > li:nth-child(1) > span.hmrc-add-to-a-list__remove > a > span:nth-child(1)").text mustBe UploadAnotherFileMessages.remove
      }

      "second remove contains the correct text" in {
        document.select("#main-content > div > div > form > ul > li:nth-child(2) > span.hmrc-add-to-a-list__remove > a > span:nth-child(1)").text mustBe UploadAnotherFileMessages.remove
      }
    }

    "an error exists (no option has been selected)" should {
      lazy val form: Form[Boolean] = formProvider().bind(Map("value" -> ""))
      lazy val view: Html = injectedView(form,answers)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "update the page title to include the error prefix" in {
        document.title mustBe UploadAnotherFileMessages.errorPrefix + UploadAnotherFileMessages.title("1","file")
      }

      "render an error summary with the correct message" in {
        elementText("div.govuk-error-summary > div") mustBe UploadAnotherFileMessages.requiredError
      }

      "render an error message against the field" in {
        elementText("#value-error") mustBe UploadAnotherFileMessages.errorPrefix + UploadAnotherFileMessages.requiredError
      }

    }
  }

  it should {

    val form: Form[Boolean] = formProvider.apply()
    lazy val view: Html = injectedView(form,answers)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct h1 of '${UploadAnotherFileMessages.h1("1","file")}'" in {
      elementText("h1") mustBe UploadAnotherFileMessages.h1("1","file")
    }

    s"have the correct value for the first radio button of '${UploadAnotherFileMessages.siteYes}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(1)") mustBe UploadAnotherFileMessages.siteYes
    }

    s"have the correct value for the second radio button of '${UploadAnotherFileMessages.siteNo}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(2)") mustBe UploadAnotherFileMessages.siteNo
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> controllers.routes.SupportingDocController.onLoad().url)
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }

  }
}
