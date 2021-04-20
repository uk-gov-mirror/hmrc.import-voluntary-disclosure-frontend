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

package views.underpayments

import base.ViewBaseSpec
import messages.BaseMessages
import messages.underpayments.UnderpaymentDetailConfirmMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.mvc.Call
import play.twirl.api.Html
import views.data.underpayments.UnderpaymentDetailConfirmData
import views.html.underpayments.UnderpaymentDetailConfirmView

class UnderpaymentDetailConfirmViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: UnderpaymentDetailConfirmView = app.injector.instanceOf[UnderpaymentDetailConfirmView]

  val underpaymentType = "B00"

  val backLink: Call = controllers.underpayments.routes.UnderpaymentDetailsController.onLoad(underpaymentType)

  val submitCall: Call = controllers.underpayments.routes.UnderpaymentDetailConfirmController.onSubmit(underpaymentType, change = true)


  "Rendering the Underpayment Detail Summary page" when {
    "no errors exist" should {

      lazy val view: Html = injectedView(
        underpaymentType,
        UnderpaymentDetailConfirmData.underpaymentDetailSummaryList(
          underpaymentType,
          UnderpaymentDetailConfirmMessages.underpaymentTypeContent(underpaymentType).body.get
        ),
        backLink,
        submitCall
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe UnderpaymentDetailConfirmMessages.underpaymentTypeContent(underpaymentType).title
      }

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }
    }
  }

  "The Underpayment Detail Summary page" when {
    Seq("B00", "A00", "E00", "A20", "A30", "A35", "A40", "A45", "A10", "D10").foreach { testType =>
      checkContent(testType)
    }

    def checkContent(underpaymentType: String): Unit = {
      s"rendered for type $underpaymentType" should {
        lazy val view: Html = injectedView(
          underpaymentType,
          UnderpaymentDetailConfirmData.underpaymentDetailSummaryList(
            underpaymentType,
            UnderpaymentDetailConfirmMessages.underpaymentTypeContent(underpaymentType).body.get
          ),
          backLink,
          submitCall
        )(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "have the correct page title" in {
          document.title mustBe
            UnderpaymentDetailConfirmMessages.underpaymentTypeContent(underpaymentType).title
        }

        "have the correct page heading" in {
          elementText("h1") mustBe
            UnderpaymentDetailConfirmMessages.underpaymentTypeContent(underpaymentType).heading
        }

        "have the correct body text" in {
          elementText("#main-content > div > div > dl > div:nth-child(3) > dt") mustBe
            UnderpaymentDetailConfirmMessages.underpaymentTypeContent(underpaymentType).body.get
        }
      }
    }
  }

  it should {

    lazy val view: Html = injectedView(
      underpaymentType,
      UnderpaymentDetailConfirmData.underpaymentDetailSummaryList(
        underpaymentType,
        UnderpaymentDetailConfirmMessages.underpaymentTypeContent(underpaymentType).body.get
      ),
      backLink,
      submitCall
    )(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct value for Amount that was paid to HMRC" in {
      elementText("#main-content > div > div > dl > div:nth-child(1) > dt") mustBe UnderpaymentDetailConfirmMessages.originalAmount
    }

    "have the correct value for Amount that should have been paid" in {
      elementText("#main-content > div > div > dl > div:nth-child(2) > dt") mustBe UnderpaymentDetailConfirmMessages.amendedAmount
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }
  }

}
