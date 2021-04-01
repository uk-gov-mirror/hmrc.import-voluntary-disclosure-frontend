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
import models.SelectedDutyTypes._
import models.underpayments.{UnderpaymentAmount, UnderpaymentDetail}
import pages._
import play.api.libs.json._

import java.time.{LocalDate, LocalDateTime}

class IvdSubmissionSpec extends ModelSpecBase {

  private val currentTimestamp = LocalDateTime.now()

  private val contactDetails = ContactDetails("John Smith", "test@test.com", "0123456789")
  private val address = ContactAddress("99 Avenue Road", None, "Any Old Town", Some("99JZ 1AA"), "GB")

  val submission: IvdSubmission = IvdSubmission(
    userType = UserType.Importer,
    knownDetails = EoriDetails("GB0000000000001", "Importer Inc.", address),
    numEntries = NumberOfEntries.OneEntry,
    acceptedBeforeBrexit = true,
    entryDetails = EntryDetails("123", "123456Q", LocalDate.parse("2020-12-12")),
    originalCpc = "4000C09",
    declarantContactDetails = contactDetails,
    traderContactDetails = ContactDetails("Importer Inc.", contactDetails.email, contactDetails.phoneNumber),
    traderAddress = address,
    paymentByDeferment = true,
    defermentType = None,
    defermentAccountNumber = Some("1234567"),
    additionalDefermentAccountNumber = None,
    supportingDocuments = Seq(
      FileUploadInfo(
        fileName = "TestDocument.pdf",
        downloadUrl = "http://some/location",
        uploadTimestamp = currentTimestamp,
        checksum = "the file checksum",
        fileMimeType = "application/pdf"
      )
    ),
    additionalInfo = "some text",
    amendedItems = Seq(UnderpaymentReason(1, 0, "GBP100", "GBP200"))
  )

  val userAnswers: UserAnswers = (for {
    answers <- new UserAnswers("some-cred-id").set(UserTypePage, submission.userType)
    answers <- answers.set(EntryDetailsPage, submission.entryDetails)
    answers <- answers.set(KnownEoriDetails, submission.knownDetails)
    answers <- answers.set(NumberOfEntriesPage, submission.numEntries)
    answers <- answers.set(AcceptanceDatePage, submission.acceptedBeforeBrexit)
    answers <- answers.set(TraderAddressCorrectPage, true)
    answers <- answers.set(DeclarantContactDetailsPage, submission.declarantContactDetails)
    answers <- answers.set(TraderAddressPage, submission.traderAddress)
    answers <- answers.set(EnterCustomsProcedureCodePage, submission.originalCpc)
    answers <- answers.set(FileUploadPage, submission.supportingDocuments)
    answers <- answers.set(DefermentPage, true)
    answers <- answers.set(DefermentAccountPage, "1234567")
    answers <- answers.set(MoreInformationPage, "some text")
    answers <- answers.set(UnderpaymentReasonsPage, submission.amendedItems)
    answers <- answers.set(SplitPaymentPage, false)
  } yield answers).getOrElse(new UserAnswers("some-cred-id"))

  val userAnswersJson: JsValue = userAnswers.data

  def data(path: String)(implicit json: JsValue): JsValue = json \ path match {
    case JsDefined(value) => value
    case _ => fail(s"data expected at path '$path' not found")
  }

  "IVD Submission model representing an importer journey" when {
    "building a model from user answers" should {
      "result in a valid submission model" in {
        userAnswersJson.validate[IvdSubmission] match {
          case JsSuccess(result, _) => result shouldBe submission
          case JsError(errors) => fail(s"Failed to parse JSON with: $errors")
        }
      }
    }

    "building a submission payload" should {
      implicit lazy val result: JsValue = Json.toJson(submission)

      "generate the correct JSON for the userType" in {
        data("userType") shouldBe JsString("importer")
      }

      "generate the correct JSON for the isBulkEntry" in {
        data("isBulkEntry") shouldBe JsBoolean(false)
      }

      "generate the correct JSON for the isEuropeanUnionDuty" in {
        data("isEuropeanUnionDuty") shouldBe JsBoolean(true)
      }

      "generate the correct JSON for the additionalInfo" in {
        data("additionalInfo") shouldBe JsString("some text")
      }

      "generate the correct JSON for the entryDetails" in {
        data("entryDetails") shouldBe Json.obj(
          "epu" -> "123",
          "entryNumber" -> "123456Q",
          "entryDate" -> "2020-12-12"
        )
      }

      "generate the correct JSON for the customsProcessingCode" in {
        data("customsProcessingCode") shouldBe JsString("4000C09")
      }

      "generate the correct JSON for the declarantContactDetails" in {
        data("declarantContactDetails") shouldBe Json.obj(
          "fullName" -> "John Smith",
          "email" -> "test@test.com",
          "phoneNumber" -> "0123456789"
        )
      }

      "generate the correct JSON for the supportingDocumentTypes" in {
        data("supportingDocumentTypes") shouldBe Json.arr()
      }

      "generate the correct JSON for the defermentType" in {
        data("defermentType") shouldBe JsString("D")
      }

      "generate the correct JSON for the defermentAccountNumber" in {
        data("defermentAccountNumber") shouldBe JsString("D1234567")
      }

      "generate the correct JSON for the amendedItems" in {
        val item = submission.amendedItems.head

        data("amendedItems") shouldBe Json.arr(
          Json.obj(
            "boxNumber" -> item.boxNumber,
            "itemNumber" -> item.itemNumber,
            "original" -> item.original,
            "amended" -> item.amended
          )
        )
      }

      "generate the correct JSON for the supportingDocuments" in {
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

      "generate the correct JSON for the importer" in {
        data("importer") shouldBe Json.obj(
          "eori" -> submission.knownDetails.eori,
          "contactDetails" -> submission.traderContactDetails,
          "address" -> submission.traderAddress
        )
      }

      "not generate representative details" in {
        result.as[JsObject].keys shouldNot contain("representative")
      }

    }

  }

  "IVD Submission model representing a representative journey" when {

    "the correct answers are supplied" should {
      val repSubmission = submission.copy(
        userType = UserType.Representative,
        knownDetails = EoriDetails("GB1234567890", "Representative Inc.", address),
        importerName = Some("Importer Inc."),
        importerAddress = Some(address),
        defermentType = Some("B"),
        additionalDefermentAccountNumber = Some("1234567"),
        additionalDefermentType = Some("C"),
        splitDeferment = true
      )

      implicit lazy val result: JsValue = Json.toJson(repSubmission)

      "generate the correct JSON for the representative" in {
        data("representative") shouldBe Json.obj(
          "eori" -> repSubmission.knownDetails.eori,
          "contactDetails" -> repSubmission.declarantContactDetails,
          "address" -> repSubmission.traderAddress
        )
      }

      "generate the correct JSON for the defermentType" in {
        data("defermentType") shouldBe JsString("B")
      }

      "generate the correct JSON for the defermentAccountNumber" in {
        data("defermentAccountNumber") shouldBe JsString("B1234567")
      }

      "generate the correct JSON for the additionalDefermentAccountNumber" in {
        data("additionalDefermentAccountNumber") shouldBe JsString("C1234567")
      }

      "render a property for an importer" in {
        result.as[JsObject].keys should contain("importer")
      }

    }

    "the importer name has not been supplied" should {
      val repSubmission = submission.copy(
        userType = UserType.Representative,
        knownDetails = EoriDetails("GB1234567890", "Representative Inc.", address),
        importerAddress = Some(address),
        defermentType = Some("B")
      )

      "throw an exception" in {
        val error = intercept[RuntimeException](Json.toJson(repSubmission))
        error.getMessage shouldBe "Importer details not captured in representative flow"
      }
    }

    "the importer address has not been supplied" should {
      val repSubmission = submission.copy(
        userType = UserType.Representative,
        knownDetails = EoriDetails("GB1234567890", "Representative Inc.", address),
        importerName = Some("Importer Inc."),
        defermentType = Some("B")
      )

      "throw an exception" in {
        val error = intercept[RuntimeException](Json.toJson(repSubmission))
        error.getMessage shouldBe "Importer details not captured in representative flow"
      }
    }

    "the importer's EORI is supplied" should {
      val repSubmission = submission.copy(
        userType = UserType.Representative,
        knownDetails = EoriDetails("GB1234567890", "Representative Inc.", address),
        importerEori = Some("GB01"),
        importerName = Some("Importer Inc."),
        importerAddress = Some(address),
        defermentType = Some("B")
      )

      implicit lazy val result: JsValue = Json.toJson(repSubmission)

      "generate the correct JSON for the importer" in {
        data("importer") shouldBe Json.obj(
          "eori" -> "GB01",
          "contactDetails" -> ContactDetails(repSubmission.importerName.get),
          "address" -> repSubmission.importerAddress
        )
      }

    }

    "the importer's EORI is not supplied" should {
      val repSubmission = submission.copy(
        userType = UserType.Representative,
        knownDetails = EoriDetails("GB1234567890", "Representative Inc.", address),
        importerName = Some("Importer Inc."),
        importerAddress = Some(address),
        defermentType = Some("B")
      )

      implicit lazy val result: JsValue = Json.toJson(repSubmission)

      "generate the correct JSON for the importer using the default EORI" in {
        data("importer") shouldBe Json.obj(
          "eori" -> "GBPR",
          "contactDetails" -> ContactDetails(repSubmission.importerName.get),
          "address" -> repSubmission.importerAddress
        )
      }

    }

    "no deferment option required" should {
      val repSubmission = submission.copy(
        userType = UserType.Representative,
        knownDetails = EoriDetails("GB1234567890", "Representative Inc.", address),
        importerName = Some("Importer Inc."),
        importerAddress = Some(address),
        paymentByDeferment = false,
        defermentAccountNumber = None
      )

      implicit lazy val result: JsValue = Json.toJson(repSubmission)

      "generate the correct JSON without defermentType details" in {
        (result \ "defermentType").toOption shouldBe None
      }

      "generate the correct JSON without defermentAccountNumber details" in {
        (result \ "defermentAccountNumber").toOption shouldBe None
      }

      "generate the correct JSON without additionalDefermentNumber details" in {
        (result \ "additionalDefermentNumber").toOption shouldBe None
      }

    }

    "deferment without split option required" should {
      val repSubmission = submission.copy(
        userType = UserType.Representative,
        knownDetails = EoriDetails("GB1234567890", "Representative Inc.", address),
        importerName = Some("Importer Inc."),
        importerAddress = Some(address),
        paymentByDeferment = true,
        splitDeferment = false,
        defermentAccountNumber = Some("1234567"),
        defermentType = Some("A")
      )

      implicit lazy val result: JsValue = Json.toJson(repSubmission)

      "generate the correct JSON with defermentType details" in {
        data("defermentType") shouldBe JsString("A")
      }

      "generate the correct JSON with defermentAccountNumber details" in {
        data("defermentAccountNumber") shouldBe JsString("A1234567")
      }

      "generate the correct JSON without additionalDefermentNumber details" in {
        (result \ "additionalDefermentNumber").toOption shouldBe None
      }

      "generate the correct JSON without authority document type details" in {
        data("supportingDocumentTypes") shouldBe Json.arr()
      }

      "generate the correct JSON without additional supporting documents for authority" in {
        data("supportingDocuments").as[Seq[FileUploadInfo]].size shouldBe submission.supportingDocuments.size
      }
    }

    "split deferment option with both proof of authority required" should {
      val repSubmission = submission.copy(
        userType = UserType.Representative,
        knownDetails = EoriDetails("GB1234567890", "Representative Inc.", address),
        importerName = Some("Importer Inc."),
        importerAddress = Some(address),
        paymentByDeferment = true,
        splitDeferment = true,
        defermentAccountNumber = Some("1234567"),
        defermentType = Some("B"),
        additionalDefermentAccountNumber = Some("7654321"),
        additionalDefermentType = Some("B"),
        authorityDocuments = Seq(
          UploadAuthority("1234567", Duty,
            FileUploadInfo(
              fileName = "TestDocument.pdf",
              downloadUrl = "http://some/location",
              uploadTimestamp = currentTimestamp,
              checksum = "the file checksum",
              fileMimeType = "application/pdf"
            )),
          UploadAuthority("7654321", Vat,
            FileUploadInfo(
              fileName = "TestDocument.pdf",
              downloadUrl = "http://some/location",
              uploadTimestamp = currentTimestamp,
              checksum = "the file checksum",
              fileMimeType = "application/pdf"
            ))
        )
      )

      implicit lazy val result: JsValue = Json.toJson(repSubmission)

      "generate the correct JSON with defermentType details" in {
        data("defermentType") shouldBe JsString("B")
      }

      "generate the correct JSON with defermentAccountNumber details" in {
        data("defermentAccountNumber") shouldBe JsString("B1234567")
      }

      "generate the correct JSON with additional defermentAccountNumber details" in {
        data("additionalDefermentAccountNumber") shouldBe JsString("B7654321")
      }

      "generate the correct JSON with authority document type details" in {
        data("supportingDocumentTypes") shouldBe Json.arr(JsString(DocumentTypes.DefermentAuthorisation))
      }

      "generate the correct JSON with additional supporting documents for authority" in {
        data("supportingDocuments") shouldBe Json.arr(
          Json.obj(
            "fileName" -> "TestDocument.pdf",
            "downloadUrl" -> "http://some/location",
            "uploadTimestamp" -> currentTimestamp,
            "checksum" -> "the file checksum",
            "fileMimeType" -> "application/pdf"
          ),
          Json.obj(
            "fileName" -> "TestDocument.pdf",
            "downloadUrl" -> "http://some/location",
            "uploadTimestamp" -> currentTimestamp,
            "checksum" -> "the file checksum",
            "fileMimeType" -> "application/pdf"
          ),
          Json.obj(
            "fileName" -> "TestDocument.pdf",
            "downloadUrl" -> "http://some/location",
            "uploadTimestamp" -> currentTimestamp,
            "checksum" -> "the file checksum",
            "fileMimeType" -> "application/pdf"
          )
        )
      }

    }

    "split deferment option with duty proof of authority required" should {
      val repSubmission = submission.copy(
        userType = UserType.Representative,
        knownDetails = EoriDetails("GB1234567890", "Representative Inc.", address),
        importerName = Some("Importer Inc."),
        importerAddress = Some(address),
        paymentByDeferment = true,
        splitDeferment = true,
        defermentAccountNumber = Some("1234567"),
        defermentType = Some("B"),
        additionalDefermentAccountNumber = Some("7654321"),
        additionalDefermentType = Some("C"),
        authorityDocuments = Seq(
          UploadAuthority("1234567", Duty,
            FileUploadInfo(
              fileName = "TestDocument.pdf",
              downloadUrl = "http://some/location",
              uploadTimestamp = currentTimestamp,
              checksum = "the file checksum",
              fileMimeType = "application/pdf"
            ))
        )
      )

      implicit lazy val result: JsValue = Json.toJson(repSubmission)

      "generate the correct JSON with defermentType details" in {
        data("defermentType") shouldBe JsString("B")
      }

      "generate the correct JSON with defermentAccountNumber details" in {
        data("defermentAccountNumber") shouldBe JsString("B1234567")
      }

      "generate the correct JSON with additional defermentAccountNumber details" in {
        data("additionalDefermentAccountNumber") shouldBe JsString("C7654321")
      }

      "generate the correct JSON with authority document type details" in {
        data("supportingDocumentTypes") shouldBe Json.arr(JsString(DocumentTypes.DefermentAuthorisation))
      }

      "generate the correct JSON with additional supporting documents for authority" in {
        data("supportingDocuments") shouldBe Json.arr(
          Json.obj(
            "fileName" -> "TestDocument.pdf",
            "downloadUrl" -> "http://some/location",
            "uploadTimestamp" -> currentTimestamp,
            "checksum" -> "the file checksum",
            "fileMimeType" -> "application/pdf"
          ),
          Json.obj(
            "fileName" -> "TestDocument.pdf",
            "downloadUrl" -> "http://some/location",
            "uploadTimestamp" -> currentTimestamp,
            "checksum" -> "the file checksum",
            "fileMimeType" -> "application/pdf"
          )
        )
      }

    }

    "split deferment option with Vat proof of authority required" should {
      val repSubmission = submission.copy(
        userType = UserType.Representative,
        knownDetails = EoriDetails("GB1234567890", "Representative Inc.", address),
        importerName = Some("Importer Inc."),
        importerAddress = Some(address),
        paymentByDeferment = true,
        splitDeferment = true,
        defermentAccountNumber = Some("1234567"),
        defermentType = Some("A"),
        additionalDefermentAccountNumber = Some("7654321"),
        additionalDefermentType = Some("B"),
        authorityDocuments = Seq(
          UploadAuthority("7654321", Vat,
            FileUploadInfo(
              fileName = "TestDocument.pdf",
              downloadUrl = "http://some/location",
              uploadTimestamp = currentTimestamp,
              checksum = "the file checksum",
              fileMimeType = "application/pdf"
            ))
        )
      )

      implicit lazy val result: JsValue = Json.toJson(repSubmission)

      "generate the correct JSON with defermentType details" in {
        data("defermentType") shouldBe JsString("A")
      }

      "generate the correct JSON with defermentAccountNumber details" in {
        data("defermentAccountNumber") shouldBe JsString("A1234567")
      }

      "generate the correct JSON with additional defermentAccountNumber details" in {
        data("additionalDefermentAccountNumber") shouldBe JsString("B7654321")
      }

      "generate the correct JSON with authority document type details" in {
        data("supportingDocumentTypes") shouldBe Json.arr(JsString(DocumentTypes.DefermentAuthorisation))
      }

      "generate the correct JSON with additional supporting documents for authority" in {
        data("supportingDocuments") shouldBe Json.arr(
          Json.obj(
            "fileName" -> "TestDocument.pdf",
            "downloadUrl" -> "http://some/location",
            "uploadTimestamp" -> currentTimestamp,
            "checksum" -> "the file checksum",
            "fileMimeType" -> "application/pdf"
          ),
          Json.obj(
            "fileName" -> "TestDocument.pdf",
            "downloadUrl" -> "http://some/location",
            "uploadTimestamp" -> currentTimestamp,
            "checksum" -> "the file checksum",
            "fileMimeType" -> "application/pdf"
          )
        )
      }

    }

  }
}
