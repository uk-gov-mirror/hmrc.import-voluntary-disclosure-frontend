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
import forms.EntryDetailsFormProvider
import messages.{BaseMessages, EntryDetailsMessages}
import models.EntryDetails
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.Html
import views.html.EntryDetailsView

import java.time.LocalDate

class EntryDetailsViewSpec extends ViewBaseSpec with BaseMessages {

  val currDay: Int = LocalDate.now().getDayOfMonth
  val currMonth: Int = LocalDate.now().getMonthValue
  val currYear: Int = LocalDate.now().getYear

  def formDataSetup(epu: Option[String] = Some("123"),
                    entryNumber: Option[String] = Some("123456Q"),
                    day: Option[String] = Some(s"$currDay"),
                    month: Option[String] = Some(s"$currMonth"),
                    year: Option[String] = Some(s"$currYear")): Map[String, String] =
    (
      epu.map(_ => "epu" -> epu.get) ++
        entryNumber.map(_ => "entryNumber" -> entryNumber.get) ++
        day.map(_ => "entryDate.day" -> day.get) ++
        month.map(_ => "entryDate.month" -> month.get) ++
        year.map(_ => "entryDate.year" -> year.get)
      ).toMap

  private lazy val injectedView: EntryDetailsView = app.injector.instanceOf[EntryDetailsView]

  val formProvider: EntryDetailsFormProvider = injector.instanceOf[EntryDetailsFormProvider]

  "Rendering the Entry Details view" when {

    val missingEpu = formDataSetup(epu = None)
    val missingEntryNumber = formDataSetup(entryNumber = None)
    val missingEntryDate = formDataSetup(day = None, month = None, year = None)
    val missingEntryDateDay = formDataSetup(day = None)
    val missingEntryDateDayAndMonth = formDataSetup(day = None, month = None)
    val missingEntryDateDayAndYear = formDataSetup(day = None, year = None)
    val missingEntryDateMonth = formDataSetup(month = None)
    val missingEntryDateMonthAndYear = formDataSetup(month = None, year = None)
    val missingEntryDateYear = formDataSetup(year = None)
    val formatEpu = formDataSetup(epu = Some("12"))
    val formatEntryNumber = formDataSetup(entryNumber = Some("12"))
    val realEntryDateError = formDataSetup(day = Some("34"))
    val pastEntryDateError = formDataSetup(day = Some(s"${currDay + 1}"))
    val twoDigitEntryDateYearError = formDataSetup(year = Some("20"))

    // represents error scenario description, data and expected error message
    val testScenarios: Map[String, (Map[String, String], String)] = Map(
      "EPU is missing" -> (missingEpu -> EntryDetailsMessages.epuRequiredError),
      "Entry number is missing" -> (missingEntryNumber -> EntryDetailsMessages.entryNumberRequiredError),
      "Entry date is missing" -> (missingEntryDate -> EntryDetailsMessages.entryDateAllRequiredError),
      "Entry date day is missing" -> (missingEntryDateDay -> EntryDetailsMessages.entryDateDayRequiredError),
      "Entry date day and month are missing" -> (missingEntryDateDayAndMonth -> EntryDetailsMessages.entryDateDayMonthRequiredError),
      "Entry date day and year are missing" -> (missingEntryDateDayAndYear -> EntryDetailsMessages.entryDateDayYearRequiredError),
      "Entry date month is missing" -> (missingEntryDateMonth -> EntryDetailsMessages.entryDateMonthRequiredError),
      "Entry date month and year are missing" -> (missingEntryDateMonthAndYear -> EntryDetailsMessages.entryDateMonthYearRequiredError),
      "Entry date year is missing" -> (missingEntryDateYear -> EntryDetailsMessages.entryDateYearRequiredError),
      "EPU format is incorrect" -> (formatEpu -> EntryDetailsMessages.epuFormatError),
      "Entry number format is incorrect" -> (formatEntryNumber -> EntryDetailsMessages.entryNumberFormatError),
      "Entry date is not a real date" -> (realEntryDateError -> EntryDetailsMessages.entryDateRealError),
      "Entry date is in the future" -> (pastEntryDateError -> EntryDetailsMessages.entryDatePastError),
      "Entry date has too many digits in the year" -> (twoDigitEntryDateYearError -> EntryDetailsMessages.entryDateTwoDigitYearError)
    )

    testScenarios.foreach { scenario =>
      val (description, (formData, errorMessage)) = scenario

      description should {
        lazy val form: Form[EntryDetails] = formProvider().bind(formData)
        lazy val view: Html = injectedView(form)(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "update the page title to include the error prefix" in {
          document.title mustBe EntryDetailsMessages.errorPrefix + EntryDetailsMessages.title
        }

        s"have correct message in the error summary" in {
          elementText(".govuk-error-summary__list") mustBe errorMessage
        }

        s"have correct error message against the field in error" in {
          // Note: the error message includes a visually hidden "Error:" prompt to accessibility
          elementText(".govuk-error-message") mustBe "Error: " + errorMessage
        }
      }
    }

    "multiple errors" should {

      lazy val form: Form[EntryDetails] = formProvider().bind(Map("entryNumber" -> "123456Q"))
      lazy val view: Html = injectedView(form)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "update the page title to include the error prefix" in {
        document.title mustBe EntryDetailsMessages.errorPrefix + EntryDetailsMessages.title
      }

      "produce correct error summary" in {
        elementText(".govuk-error-summary__list") mustBe
          EntryDetailsMessages.epuRequiredError + " " + EntryDetailsMessages.entryDateAllRequiredError

      }
    }
  }

  it should {
    lazy val form: Form[EntryDetails] = formProvider()
    lazy val view: Html = injectedView(form)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct page heading of '${EntryDetailsMessages.title}'" in {
      document.title mustBe EntryDetailsMessages.title
    }

    s"have the correct h1 of '${EntryDetailsMessages.h1}'" in {
      elementText("h1") mustBe EntryDetailsMessages.h1
    }

    s"have the correct EPU label of '${EntryDetailsMessages.epuLabel}'" in {
      elementText(".govuk-label[for=\"epu\"]") mustBe EntryDetailsMessages.epuLabel
    }

    s"have the correct Entry Number label of '${EntryDetailsMessages.entryNumberLabel}'" in {
      elementText(".govuk-label[for=\"entryNumber\"]") mustBe EntryDetailsMessages.entryNumberLabel
    }

    s"have the correct Entry Date label of '${EntryDetailsMessages.entryDateLabel}'" in {
      elementText(".govuk-fieldset__legend") mustBe EntryDetailsMessages.entryDateLabel
    }

    s"have the correct Entry Date Day label of '${EntryDetailsMessages.entryDateDayLabel}'" in {
      elementText(".govuk-label[for=\"entryDate.day\"]") mustBe EntryDetailsMessages.entryDateDayLabel
    }

    s"have the correct Entry Date Month label of '${EntryDetailsMessages.entryDateMonthLabel}'" in {
      elementText(".govuk-label[for=\"entryDate.month\"]") mustBe EntryDetailsMessages.entryDateMonthLabel
    }

    s"have the correct Entry Date Year label of '${EntryDetailsMessages.entryDateYearLabel}'" in {
      elementText(".govuk-label[for=\"entryDate.year\"]") mustBe EntryDetailsMessages.entryDateYearLabel
    }

    s"have the correct EPU hint of '${EntryDetailsMessages.epuHint}'" in {
      elementText("#epu-hint") mustBe EntryDetailsMessages.epuHint
    }

    s"have the correct Entry Number hint of '${EntryDetailsMessages.entryNumberHint}'" in {
      elementText("#entryNumber-hint") mustBe EntryDetailsMessages.entryNumberHint
    }

    s"have the correct Entry Date hint of '${EntryDetailsMessages.entryDateHint}'" in {
      elementText("#entryDate-hint") mustBe EntryDetailsMessages.entryDateHint
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }
  }
}
