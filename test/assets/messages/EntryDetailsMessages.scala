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

package messages

object EntryDetailsMessages extends BaseMessages {

  val title: String = "Entry details"
  val h1: String = "Entry details"

  val epuLabel: String = "EPU number"
  val epuHint: String = "This is 3 numbers, for example ‘121’"

  val entryNumberLabel: String = "Entry number"
  val entryNumberHint: String = "This is 6 numbers and a letter, for example ‘123456Q’"

  val entryDateLabel: String = "Entry date"
  val entryDateHint: String = "For example, 27 3 2020"
  val entryDateDayLabel: String = "Day"
  val entryDateMonthLabel: String = "Month"
  val entryDateYearLabel: String = "Year"

  val epuRequiredError: String = "Enter an EPU number"
  val entryNumberRequiredError: String = "Enter an entry number"
  val entryDateAllRequiredError: String = "Enter an entry date and include a day, month and year"
  val entryDateDayRequiredError: String = "Entry date must include a day"
  val entryDateMonthRequiredError: String = "Entry date must include a month"
  val entryDateYearRequiredError: String = "Entry date must include a year"
  val entryDateDayMonthRequiredError: String = "Entry date must include a day and month"
  val entryDateDayYearRequiredError: String = "Entry date must include a day and year"
  val entryDateMonthYearRequiredError: String = "Entry date must include a month and year"

  val epuFormatError: String = "Enter an EPU number in the correct format"
  val entryNumberFormatError: String = "Enter an entry number in the correct format"

  val entryDateTwoDigitYearError: String = "Entry date year must be 4 numbers"
  val entryDatePastError: String = "Entry date must be today or in the past"
  val entryDateRealError: String = "Entry date must be a real date"

}
