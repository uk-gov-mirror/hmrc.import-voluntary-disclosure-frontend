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

package models

import base.ModelSpecBase
import pages._
import play.api.libs.json._

import java.time.{LocalDate, LocalDateTime}

class IvdSubmissionSpec extends ModelSpecBase {

  private val currentTimestamp = LocalDateTime.now()

  val submission: IvdSubmission = IvdSubmission(
    userType = UserType.Importer,
    numEntries = NumberOfEntries.OneEntry,
    acceptedBeforeBrexit = true,
    entryDetails = EntryDetails("123", "123456Q", LocalDate.parse("2020-12-12")),
    originalCpc = "4000C09",
    declarantContactDetails = ContactDetails("John Smith", "test@test.com", "0123456789"),
    declarantAddress = ContactAddress("99 Avenue Road", None, "Any Old Town", Some("99JZ 1AA"), "United Kingdom"),
    defermentType = None,
    defermentAccountNumber = None,
    additionalDefermentNumber = None,
    underpaymentDetails = Seq(
      UnderpaymentDetail("customsDuty", BigDecimal(123.0), BigDecimal(233.33)),
      UnderpaymentDetail("importVat", BigDecimal(111.11), BigDecimal(1234.0)),
      UnderpaymentDetail("exciseDuty", BigDecimal(123.22), BigDecimal(4409.55))
    ),
    supportingDocuments = Seq(
      FileUploadInfo(
        fileName = "TestDocument.pdf",
        downloadUrl = "http://some/location",
        uploadTimestamp = currentTimestamp,
        checksum = "the file checksum",
        fileMimeType = "application/pdf"
      )
    )
  )

  val userAnswers: UserAnswers = (for {
    answers <- new UserAnswers("some-cred-id").set(UserTypePage, submission.userType)
    answers <- answers.set(EntryDetailsPage, submission.entryDetails)
    answers <- answers.set(NumberOfEntriesPage, submission.numEntries)
    answers <- answers.set(AcceptanceDatePage, submission.acceptedBeforeBrexit)
    answers <- answers.set(UnderpaymentTypePage, UnderpaymentType(customsDuty = true, importVAT = true, exciseDuty = true))
    answers <- answers.set(CustomsDutyPage, UnderpaymentAmount(BigDecimal("123.0"), BigDecimal("233.33")))
    answers <- answers.set(ImportVATPage, UnderpaymentAmount(BigDecimal("111.11"), BigDecimal("1234")))
    answers <- answers.set(ExciseDutyPage, UnderpaymentAmount(BigDecimal("123.22"), BigDecimal("4409.55")))
    answers <- answers.set(EoriDetailsPage, true)
    answers <- answers.set(TraderContactDetailsPage, submission.declarantContactDetails)
    answers <- answers.set(ImporterAddressFinalPage, submission.declarantAddress)
    answers <- answers.set(ImporterAddressFinalPage, submission.declarantAddress)
    answers <- answers.set(EnterCustomsProcedureCodePage, submission.originalCpc)
    answers <- answers.set(FileUploadPage, submission.supportingDocuments)
    answers <- answers.set(DefermentPage, false)
  } yield answers).getOrElse(new UserAnswers("some-cred-id"))

  val userAnswersJson: JsValue = userAnswers.data

  def data(path: String)(implicit json: JsValue): JsValue = json \ path match {
    case JsDefined(value) => value
    case _ => fail(s"data expected at path '$path' not found")
  }

  "IVD Submission model" when {
    "converting from a user answers" should {
      "produce a valid model" in {
        val result = Json.fromJson[IvdSubmission](userAnswersJson).get
        result shouldBe submission
      }
    }

    "serialising a model" should {
      implicit lazy val result: JsValue = Json.toJson(submission)

      "generate the correct json for the userType" in {
        data("userType") shouldBe JsString("importer")
      }

      "generate the correct json for the isBulkEntry" in {
        data("isBulkEntry") shouldBe JsBoolean(false)
      }

      "generate the correct json for the isEuropeanUnionDuty" in {
        data("isEuropeanUnionDuty") shouldBe JsBoolean(true)
      }

      "generate the correct json for the additionalInfo" in {
        data("additionalInfo") shouldBe JsString("Not Applicable")
      }

      "generate the correct json for the entryDetails" in {
        data("entryDetails") shouldBe Json.obj(
          "epu" -> "123",
          "entryNumber" -> "123456Q",
          "entryDate" -> "2020-12-12"
        )
      }

      "generate the correct json for the customsProcessingCode" in {
        data("customsProcessingCode") shouldBe JsString("4000C09")
      }

      "generate the correct json for the declarantContactDetails" in {
        data("declarantContactDetails") shouldBe Json.obj(
          "fullName" -> "John Smith",
          "email" -> "test@test.com",
          "phoneNumber" -> "0123456789"
        )
      }

      "generate the correct json for the declarantAddress" in {
        data("declarantAddress") shouldBe Json.obj(
          "addressLine1" -> "99 Avenue Road",
          "city" -> "Any Old Town",
          "postalCode" -> "99JZ 1AA",
          "countryCode" -> "United Kingdom"
        )
      }

      "generate the correct json for the underpaymentDetails" in {
        data("underpaymentDetails") shouldBe Json.arr(
          Json.obj(
            "duty" -> "customsDuty",
            "original" -> BigDecimal("123"),
            "amended" -> BigDecimal("233.33")
          ),
          Json.obj(
            "duty" -> "importVat",
            "original" -> BigDecimal("111.11"),
            "amended" -> BigDecimal("1234")
          ),
          Json.obj(
            "duty" -> "exciseDuty",
            "original" -> BigDecimal("123.22"),
            "amended" -> BigDecimal("4409.55")
          )
        )
      }

      "generate the correct json for the supportingDocumentTypes" in {
        data("supportingDocumentTypes") shouldBe Json.arr()
      }

      "generate the correct json for the amendedItems" in {
        data("amendedItems") shouldBe Json.arr()
      }

      "generate the correct json for the supportingDocuments" in {
        data("supportingDocuments") shouldBe Json.arr(
          Json.obj(
            "fileName" -> "TestDocument.pdf",
            "downloadUrl" -> "http://some/location",
            "uploadTimestamp" -> currentTimestamp,
            "checksum" -> "the file checksum",
            "fileMimeType" -> "application/pdf"
          )
        )
      }

      "generate the correct json for the importer" in {
        data("importer") shouldBe Json.obj(
          "eori" -> "GB000000000000001",
          "contactDetails" -> submission.declarantContactDetails,
          "address" -> submission.declarantAddress
        )
      }

    }
  }
}
