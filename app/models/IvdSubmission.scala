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
import pages._
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
                         additionalDefermentNumber: Option[String] = None,
                         amendedItems: Seq[UnderpaymentReason] = Seq.empty,
                         underpaymentDetails: Seq[UnderpaymentDetail] = Seq.empty,
                         documentsSupplied: Seq[String] = Seq.empty,
                         supportingDocuments: Seq[FileUploadInfo] = Seq.empty)

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
      (data.defermentType, data.defermentAccountNumber, data.additionalDefermentNumber) match {
        case (Some(dt), Some(dan), Some(add)) if data.userType == UserType.Representative => // TODO: Needs guard to check user has selected split
          Json.obj(
            "defermentType" -> dt,
            "defermentAccountNumber" -> s"$dt$dan",
            "additionalDefermentAccountNumber" -> add // TODO: This needs to include the additionalDefermentType on the front
          )
        case (Some(dt), Some(dan), _) if data.userType == UserType.Representative =>
          Json.obj(
            "defermentType" -> dt,
            "defermentAccountNumber" -> s"$dt$dan"
          )
        case (_, Some(dan), _) if data.userType == UserType.Importer =>
          Json.obj(
            "defermentType" -> "D",
            "defermentAccountNumber" -> s"D$dan"
          )
        case _ => Json.obj()
      }
    } else {
      Json.obj()
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
      "supportingDocumentTypes" -> data.documentsSupplied,
      "amendedItems" -> data.amendedItems,
      "supportingDocuments" -> data.supportingDocuments
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
      customsDuty <- CustomsDutyPage.path.readNullable[UnderpaymentAmount]
      importVat <- ImportVATPage.path.readNullable[UnderpaymentAmount]
      exciseDuty <- ExciseDutyPage.path.readNullable[UnderpaymentAmount]
      supportingDocuments <- FileUploadPage.path.read[Seq[FileUploadInfo]]
      paymentByDeferment <- DefermentPage.path.read[Boolean]
      defermentType <- DefermentTypePage.path.readNullable[String]
      defermentAccountNumber <- DefermentAccountPage.path.readNullable[String]
      additionalDefermentNumber <- AdditionalDefermentNumberPage.path.readNullable[String]
      additionalInfo <- MoreInformationPage.path.readNullable[String]
      amendedItems <- UnderpaymentReasonsPage.path.read[Seq[UnderpaymentReason]]
    } yield {

      val underpaymentDetails = Seq(
        "customsDuty" -> customsDuty,
        "importVat" -> importVat,
        "exciseDuty" -> exciseDuty
      ).collect {
        case (key, Some(details)) => UnderpaymentDetail(key, details.original, details.amended)
      }

      val traderContactDetails = ContactDetails(
        knownDetails.name,
        declarantContactDetails.email,
        declarantContactDetails.phoneNumber
      )

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
        underpaymentDetails = underpaymentDetails,
        supportingDocuments = supportingDocuments,
        paymentByDeferment = paymentByDeferment,
        defermentType = defermentType,
        defermentAccountNumber = defermentAccountNumber,
        additionalDefermentNumber = additionalDefermentNumber,
        additionalInfo = additionalInfo.getOrElse("Not Applicable"),
        amendedItems = amendedItems
      )
    }
}
