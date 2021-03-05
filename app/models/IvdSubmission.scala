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
import play.api.libs.json.{Json, Reads, Writes}

import java.time.LocalDate

case class IvdSubmission(userType: UserType,
                         numEntries: NumberOfEntries,
                         acceptedBeforeBrexit: Boolean,
                         additionalInfo: String = "Not Applicable",
                         entryDetails: EntryDetails,
                         originalCpc: String,
                         declarantContactDetails: ContactDetails,
                         declarantAddress: ContactAddress,
                         defermentType: Option[String] = None,
                         defermentAccountNumber: Option[String] = None,
                         additionalDefermentNumber: Option[String] = None,
                         amendedItems: Seq[UnderpaymentReason] = Seq.empty,
                         underpaymentDetails: Seq[UnderpaymentDetail] = Seq.empty,
                         documentsSupplied: Seq[String] = Seq.empty,
                         supportingDocuments: Seq[FileUploadInfo] = Seq.empty)

object IvdSubmission extends FixedConfig {
  implicit val writes: Writes[IvdSubmission] = (data: IvdSubmission) => {

    val isEuropeanUnionDuty: Boolean = data.entryDetails.entryDate.isBefore(euExitDate) && data.acceptedBeforeBrexit
    val isBulkEntry = data.numEntries == NumberOfEntries.MoreThanOneEntry

    Json.obj(
      "userType" -> data.userType,
      "isBulkEntry" -> isBulkEntry,
      "isEuropeanUnionDuty" -> isEuropeanUnionDuty,
      "additionalInfo" -> data.additionalInfo,
      "entryDetails" -> data.entryDetails,
      "customsProcessingCode" -> data.originalCpc,
      "declarantContactDetails" -> data.declarantContactDetails,
      "declarantAddress" -> data.declarantAddress,
      "underpaymentDetails" -> data.underpaymentDetails,
      "supportingDocumentTypes" -> data.documentsSupplied,
      "amendedItems" -> data.amendedItems,
      "supportingDocuments" -> data.supportingDocuments,
      "importer" -> Json.obj(
        "eori" -> "GB000000000000001",
        "contactDetails" -> data.declarantContactDetails,
        "address" -> data.declarantAddress
      )
    )
  }

  implicit val reads: Reads[IvdSubmission] =
    for {
      userType <- UserTypePage.path.read[UserType]
      numEntries <- NumberOfEntriesPage.path.read[NumberOfEntries]
      acceptanceDate <- AcceptanceDatePage.path.readNullable[Boolean]
      entryDetails <- EntryDetailsPage.path.read[EntryDetails]
      originalCpc <- EnterCustomsProcedureCodePage.path.read[String]
      traderContactDetails <- TraderContactDetailsPage.path.read[ContactDetails]
      traderAddress <- ImporterAddressFinalPage.path.read[ContactAddress]
      customsDuty <- CustomsDutyPage.path.readNullable[UnderpaymentAmount]
      importVat <- ImportVATPage.path.readNullable[UnderpaymentAmount]
      exciseDuty <- ExciseDutyPage.path.readNullable[UnderpaymentAmount]
      supportingDocuments <- FileUploadPage.path.read[Seq[FileUploadInfo]]
      additionalInfo <- MoreInformationPage.path.read[String]
    } yield {

      val underpaymentDetails = Seq(
        "customsDuty" -> customsDuty,
        "importVat" -> importVat,
        "exciseDuty" -> exciseDuty
      ).collect {
        case (key, Some(details)) => UnderpaymentDetail(key, details.original, details.amended)
      }

      IvdSubmission(
        userType = userType,
        numEntries = numEntries,
        acceptedBeforeBrexit = acceptanceDate.getOrElse(false),
        entryDetails = entryDetails,
        originalCpc = originalCpc,
        declarantContactDetails = traderContactDetails,
        declarantAddress = traderAddress,
        underpaymentDetails = underpaymentDetails,
        supportingDocuments = supportingDocuments,
        additionalInfo = additionalInfo
      )
    }
}
