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
import forms.{DefermentFormProvider, RemoveUploadedFileFormProvider}
import messages.{BaseMessages, DefermentMessages, RemoveUploadedFileMessages}
import models.Index
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.{DefermentView, RemoveUploadedFileView}

class RemoveUploadedFileViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: RemoveUploadedFileView = app.injector.instanceOf[RemoveUploadedFileView]

  val formProvider: RemoveUploadedFileFormProvider = injector.instanceOf[RemoveUploadedFileFormProvider]

  val index: Index = Index.apply(1)


  "Rendering the Remove Uploaded File page" when {
    "no errors exist" should {

      val form: Form[Boolean] = formProvider.apply()
      lazy val view: Html = injectedView(form,index)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe RemoveUploadedFileMessages.title
      }

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }
    }

    "an error exists (no option has been selected)" should {
      lazy val form: Form[Boolean] = formProvider().bind(Map("value" -> ""))
      lazy val view: Html = injectedView(form,index)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "update the page title to include the error prefix" in {
        document.title mustBe DefermentMessages.errorPrefix + RemoveUploadedFileMessages.title
      }

      "render an error summary with the correct message" in {
        elementText("div.govuk-error-summary > div") mustBe RemoveUploadedFileMessages.requiredError
      }

      "render an error message against the field" in {
        elementText("#value-error") mustBe RemoveUploadedFileMessages.errorPrefix + RemoveUploadedFileMessages.requiredError
      }

    }
  }

  it should {

    val form: Form[Boolean] = formProvider.apply()
    lazy val view: Html = injectedView(form, index)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct h1 of '${DefermentMessages.h1}'" in {
      elementText("h1") mustBe RemoveUploadedFileMessages.h1
    }

    s"have the correct value for the first radio button of '${RemoveUploadedFileMessages.siteYes}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(1)") mustBe RemoveUploadedFileMessages.siteYes
    }

    s"have the correct value for the second radio button of '${RemoveUploadedFileMessages.siteNo}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(2)") mustBe RemoveUploadedFileMessages.siteNo
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> controllers.routes.UploadAnotherFileController.onLoad().url)
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }

  }
}
