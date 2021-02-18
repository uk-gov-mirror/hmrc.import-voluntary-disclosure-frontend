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

package viewmodels

import java.time.LocalDateTime

import base.SpecBase
import models.{EntryDetails, FileUploadInfo, NumberOfEntries, ContactAddress, ContactDetails, UserAnswers}
import org.scalatest.{MustMatchers, OptionValues, TryValues}
import pages._
import views.data.CheckYourAnswersData._
import views.data.UnderpaymentSummaryData.{cdUnderpayment, edUnderpayment, ivUnderpayment}
import java.time.LocalDate


class CYASummaryListHelperSpec extends SpecBase with MustMatchers with TryValues with OptionValues with CYASummaryListHelper {

  trait Test  {

    val userAnswers: UserAnswers = UserAnswers("some-cred-id")
      .set(NumberOfEntriesPage, NumberOfEntries.OneEntry).success.value
      .set(EntryDetailsPage, EntryDetails("123", "123456Q", LocalDate.of(2020, 12, 1))).success.value
      .set(AcceptanceDatePage, true).success.value
      .set(CustomsDutyPage, cdUnderpayment).success.value
      .set(ImportVATPage, ivUnderpayment).success.value
      .set(ExciseDutyPage, edUnderpayment).success.value
      .set(FileUploadPage, Seq(FileUploadInfo(
        "Example.pdf",
        "https://bucketName.s3.eu-west-2.amazonaws.com?1235676",
        LocalDateTime.now,
        "396f101dd52e8b2ace0dcf5ed09b1d1f030e608938510ce46e7a5c7a4e775100",
        "application/pdf"))).success.value
      .set(TraderContactDetailsPage, ContactDetails(
        "First Second",
        "email@email.com",
        "1234567890")).success.value
      .set(ImporterAddressFinalPage, ContactAddress("21 Street", "London", Some("SN6PY"), "UK")).success.value
      .set(EnterCustomsProcedureCodePage, "4000C09").success.value
      .set(CPCChangedPage, false).success.value
      .set(DefermentPage, false).success.value

  }

  "buildDisclosureDetails" should {

    "produce a valid model when all answers are provided" in new Test{
      buildDisclosureDetailsSummaryList(userAnswers) mustBe Some(disclosureDetailsAnswers)
    }

    "produce a valid model when no answers are provided" in new Test {
      override val userAnswers: UserAnswers = UserAnswers("")
      buildDisclosureDetailsSummaryList(userAnswers) mustBe None
    }

  }

  "buildUnderpaymentDetails" should {

    "produce a valid model when all answers are provided" in new Test {
      buildUnderpaymentDetailsSummaryList(userAnswers) mustBe Some(underpaymentAnswers)
    }

    "produce a valid model when no answers are provided" in new Test {
      override val userAnswers: UserAnswers = UserAnswers("")
      buildUnderpaymentDetailsSummaryList(userAnswers) mustBe None
    }

  }

  "buildAmendmentDetails" should {

    "produce a valid model when all answers are provided" in new Test {
      buildAmendmentDetailsSummaryList(userAnswers) mustBe Some(amendmentDetailsAnswers)
    }

    "produce a valid model when no answers are provided" in new Test {
      override val userAnswers: UserAnswers = UserAnswers("")
      buildAmendmentDetailsSummaryList(userAnswers) mustBe None
    }

  }

  "buildSupportingDocuments" should {

    "produce a valid model when all answers are provided" in new Test {
      buildSupportingDocumentsSummaryList(userAnswers) mustBe Some(uploadFilesAnswers)
    }

    "produce a valid model when no answers are provided" in new Test {
      override val userAnswers: UserAnswers = UserAnswers("")
      buildSupportingDocumentsSummaryList(userAnswers) mustBe None
    }

  }

  "buildYourDetails" should {

    "produce a valid model when all answers are provided" in new Test {
      buildYourDetailsSummaryList(userAnswers) mustBe Some(yourDetailsAnswers)
    }

    "produce a valid model when no answers are provided" in new Test {
      override val userAnswers: UserAnswers = UserAnswers("")
      buildYourDetailsSummaryList(userAnswers) mustBe None
    }

  }

  "buildPaymentInformation" should {

    "produce a valid model when all answers are provided" in new Test {
      buildPaymentInformationSummaryList(userAnswers) mustBe Some(defermentAnswers)
    }

    "produce a valid model when no answers are provided" in new Test {
      override val userAnswers: UserAnswers = UserAnswers("")
      buildPaymentInformationSummaryList(userAnswers) mustBe None
    }

  }
}
