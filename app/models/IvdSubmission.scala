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

import config.FixedConfig
import models.DocumentTypes.{DefermentAuthorisation, DocumentType}
import models.SelectedDutyTypes._
import models.underpayments.UnderpaymentDetail
import pages._
import pages.underpayments.UnderpaymentDetailSummaryPage
import play.api.libs.json.{JsObject, Json, Reads, Writes}

case class IvdSubmission(userType: UserType,
                         knownDetails: EoriDetails,
                         numEntries: NumberOfEntries,
                         acceptedBeforeBrexit: Boolean,
                         additionalInfo: String = "Not Applicable",
                         entryDetails: EntryDetails,
                         originalCpc: String,
                         declarantContactDetails: ContactDetails,
                         traderContactDetails: ContactDetails,
                         traderAddress: ContactAddress,
                         importerEori: Option[String] = None,
                         importerName: Option[String] = None,
                         importerAddress: Option[ContactAddress] = None,
                         paymentByDeferment: Boolean,
                         defermentType: Option[String] = None,
                         defermentAccountNumber: Option[String] = None,
                         additionalDefermentAccountNumber: Option[String] = None,
                         additionalDefermentType: Option[String] = None,
                         amendedItems: Seq[UnderpaymentReason] = Seq.empty,
                         underpaymentDetails: Seq[UnderpaymentDetail] = Seq.empty,
                         documentsSupplied: Seq[DocumentType] = Seq.empty, // mandatory docs
                         optionalDocumentsSupplied: Seq[DocumentType] = Seq.empty, // optional docs
                         supportingDocuments: Seq[FileUploadInfo] = Seq.empty,
                         splitDeferment: Boolean = false,
                         authorityDocuments: Seq[UploadAuthority] = Seq.empty
                        )

object IvdSubmission extends FixedConfig {
  implicit val writes: Writes[IvdSubmission] = (data: IvdSubmission) => {

    val DEFAULT_EORI: String = "GBPR"
    val isEuropeanUnionDuty: Boolean = data.entryDetails.entryDate.isBefore(euExitDate) && data.acceptedBeforeBrexit
    val isBulkEntry = data.numEntries == NumberOfEntries.MoreThanOneEntry

    val importerDetails: JsObject = if (data.userType == UserType.Importer) {
      Json.obj(
        "importer" -> Json.obj(
          "eori" -> data.knownDetails.eori,
          "contactDetails" -> data.declarantContactDetails.copy(fullName = data.knownDetails.name),
          "address" -> data.traderAddress
        )
      )
    } else {
      val details = for {
        eori <- data.importerEori.orElse(Some(DEFAULT_EORI))
        name <- data.importerName
        address <- data.importerAddress
      } yield {
        Json.obj(
          "importer" -> Json.obj(
            "eori" -> eori,
            "contactDetails" -> ContactDetails(name),
            "address" -> address
          )
        )
      }
      details.getOrElse(throw new RuntimeException("Importer details not captured in representative flow"))
    }

    val representativeDetails = if (data.userType == UserType.Representative) {
      Json.obj(
        "representative" -> Json.obj(
          "eori" -> data.knownDetails.eori,
          "contactDetails" -> data.declarantContactDetails,
          "address" -> data.traderAddress
        )
      )
    } else {
      Json.obj()
    }

    val defermentDetails = if (data.paymentByDeferment) {
      (data.defermentType, data.defermentAccountNumber, data.additionalDefermentAccountNumber, data.additionalDefermentType) match {
        case (Some(dt), Some(dan), Some(addDan), Some(addDT)) if data.userType == UserType.Representative && data.splitDeferment =>
          Json.obj(
            "defermentType" -> dt,
            "defermentAccountNumber" -> s"$dt$dan",
            "additionalDefermentAccountNumber" -> s"$addDT$addDan"
          )
        case (Some(dt), Some(dan), _, _) if data.userType == UserType.Representative =>
          Json.obj(
            "defermentType" -> dt,
            "defermentAccountNumber" -> s"$dt$dan"
          )
        case (_, Some(dan), _, _) if data.userType == UserType.Importer =>
          Json.obj(
            "defermentType" -> "D",
            "defermentAccountNumber" -> s"D$dan"
          )
        case _ => Json.obj()
      }
    } else {
      Json.obj()
    }

    val supportingDocuments = if (data.paymentByDeferment) {
      (data.splitDeferment, data.defermentType, data.additionalDefermentType) match {
        case (true, Some("B"), Some("B")) =>
          data.authorityDocuments.filter(x => Seq(Duty, Vat).contains(x.dutyType)).map(_.file) ++ data.supportingDocuments
        case (true, Some("B"), _) =>
          data.authorityDocuments.filter(_.dutyType == Duty).map(_.file) ++ data.supportingDocuments
        case (true, _, Some("B")) =>
          data.authorityDocuments.filter(_.dutyType == Vat).map(_.file) ++ data.supportingDocuments
        case (false, Some("B"), _) =>
          data.authorityDocuments.map(_.file) ++ data.supportingDocuments
        case _ => data.supportingDocuments
      }
    } else {
      data.supportingDocuments
    }

    val supportingDocumentTypes = if (data.paymentByDeferment) {
      (data.splitDeferment, data.defermentType, data.additionalDefermentType) match {
        case (true, Some(dt), Some(addDt)) if dt != "B" && addDt != "B" => data.documentsSupplied
        case (true, _, _) => data.documentsSupplied ++ Seq(DefermentAuthorisation)
        case (false, Some("B"), _) => data.documentsSupplied ++ Seq(DefermentAuthorisation)
        case _ => data.documentsSupplied
      }
    } else {
      data.documentsSupplied
    }

    val payload = Json.obj(
      "userType" -> data.userType,
      "isBulkEntry" -> isBulkEntry,
      "isEuropeanUnionDuty" -> isEuropeanUnionDuty,
      "additionalInfo" -> data.additionalInfo,
      "entryDetails" -> data.entryDetails,
      "customsProcessingCode" -> data.originalCpc,
      "declarantContactDetails" -> data.declarantContactDetails,
      "underpaymentDetails" -> data.underpaymentDetails,
      "supportingDocumentTypes" -> supportingDocumentTypes,
      "optionalDocumentTypes" -> data.optionalDocumentsSupplied,
      "amendedItems" -> data.amendedItems,
      "supportingDocuments" -> supportingDocuments
    )

    payload ++ defermentDetails ++ importerDetails ++ representativeDetails
  }

  implicit val reads: Reads[IvdSubmission] =
    for {
      userType <- UserTypePage.path.read[UserType]
      knownDetails <- KnownEoriDetails.path.read[EoriDetails]
      numEntries <- NumberOfEntriesPage.path.read[NumberOfEntries]
      acceptanceDate <- AcceptanceDatePage.path.readNullable[Boolean]
      entryDetails <- EntryDetailsPage.path.read[EntryDetails]
      originalCpc <- EnterCustomsProcedureCodePage.path.read[String]
      declarantContactDetails <- DeclarantContactDetailsPage.path.read[ContactDetails]
      traderAddress <- TraderAddressPage.path.read[ContactAddress]
      importerEori <- ImporterEORINumberPage.path.readNullable[String]
      importerName <- ImporterNamePage.path.readNullable[String]
      importerAddress <- ImporterAddressPage.path.readNullable[ContactAddress]
      underpaymentDetails <- UnderpaymentDetailSummaryPage.path.readNullable[Seq[UnderpaymentDetail]]
      supportingDocuments <- FileUploadPage.path.read[Seq[FileUploadInfo]]
      paymentByDeferment <- DefermentPage.path.read[Boolean]
      defermentType <- DefermentTypePage.path.readNullable[String]
      defermentAccountNumber <- DefermentAccountPage.path.readNullable[String]
      additionalDefermentNumber <- AdditionalDefermentNumberPage.path.readNullable[String]
      additionalDefermentType <- AdditionalDefermentTypePage.path.readNullable[String]
      additionalInfo <- MoreInformationPage.path.readNullable[String]
      amendedItems <- UnderpaymentReasonsPage.path.read[Seq[UnderpaymentReason]]
      splitDeferment <- SplitPaymentPage.path.readNullable[Boolean]
      authorityDocuments <- UploadAuthorityPage.path.readNullable[Seq[UploadAuthority]]
      optionalDocumentsSupplied <- WhichDocumentsPage.path.readNullable[WhichDocuments]
    } yield {

      val traderContactDetails = ContactDetails(
        knownDetails.name,
        declarantContactDetails.email,
        declarantContactDetails.phoneNumber
      )

      val optionalDocuments = optionalDocumentsSupplied.getOrElse(WhichDocuments())
      val optionalDocumentsList: Option[Seq[DocumentType]] = Some(Map(
        "importAndEntry" -> optionalDocuments.importAndEntry,
        "airwayBill" -> optionalDocuments.airwayBill,
        "originProof" -> optionalDocuments.originProof,
        "other" -> optionalDocuments.other
      ).flatMap( document => document match {
        case ("importAndEntry", true) => Some(Seq(DocumentTypes.AmendedC88, DocumentTypes.AmendedC2))
        case ("airwayBill", true) => Some(Seq(DocumentTypes.InvoiceAirwayBillPreferenceCertificate))
        case ("originProof", true) => Some(Seq(DocumentTypes.InvoiceAirwayBillPreferenceCertificate))
        case ("other", true) => Some(Seq(DocumentTypes.Other))
        case _ => None
      }).flatten.toSeq)

      IvdSubmission(
        userType = userType,
        knownDetails = knownDetails,
        numEntries = numEntries,
        acceptedBeforeBrexit = acceptanceDate.getOrElse(false),
        entryDetails = entryDetails,
        originalCpc = originalCpc,
        declarantContactDetails = declarantContactDetails,
        traderContactDetails = traderContactDetails,
        traderAddress = traderAddress,
        importerEori = importerEori,
        importerName = importerName,
        importerAddress = importerAddress,
        underpaymentDetails = underpaymentDetails.getOrElse(Seq.empty),
        supportingDocuments = supportingDocuments,
        paymentByDeferment = paymentByDeferment,
        defermentType = defermentType,
        defermentAccountNumber = defermentAccountNumber,
        additionalDefermentAccountNumber = additionalDefermentNumber,
        additionalDefermentType = additionalDefermentType,
        additionalInfo = additionalInfo.getOrElse("Not Applicable"),
        amendedItems = amendedItems,
        splitDeferment = splitDeferment.getOrElse(false),
        authorityDocuments = authorityDocuments.getOrElse(Seq.empty),
        optionalDocumentsSupplied = optionalDocumentsList.getOrElse(Seq.empty)
      )
    }
}
