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

import play.api.libs.json.{Json, Reads, __}

case class IVDSubmission(
                        userType: UserType,
                        numEntries: NumberOfEntries,
                        acceptanceDate: Option[Boolean],
                        additionalInfo: Option[String] = Some("Not Applicable"), // TODO: Not implemented yet. Maps to amendmentReason
                        entryDetails: EntryDetails,
                        originalCpc: String,
                        amendedCpc: Option[String] = None, // TODO: Not yet implemented
                        traderContactDetails: TraderContactDetails, // TODO: This should be Declarant details
                        traderAddress: TraderAddress,
                        defermentType: Option[String] = None, // TODO: Not captured yet
                        defermentAccountNumber: Option[String] = None, // TODO: Not captured yet
                        additionalDefermentNumber: Option[String] = None, // TODO: Not captured yet
                        underpaymentReasons: Option[Seq[UnderpaymentReason]] = None, // TODO: Not captured yet (box changes)
                        underpaymentDetails: Option[Seq[UnderpaymentDetail]] = None, // TODO: Other duties will need to be refactored into this
                        documentList: Option[Seq[String]] = None // TODO: List of documents the user claims to have uploaded (not the actual docs)
                        )

object IVDSubmission {
  implicit val writes = Json.writes[IVDSubmission]

  implicit val ivdSubmissionReads: Reads[IVDSubmission] =
    for {
      userType <- (__ \ "user-type").read[UserType]
      numEntries <- (__ \ "number-of-entries").read[NumberOfEntries]
      acceptanceDate <- (__ \ "acceptance-date").readNullable[Boolean]
      entryDetails <- (__ \ "entry-details").read[EntryDetails]
      originalCpc <- (__ \ "cpc" \ "original-cpc").read[String]
      traderContactDetails <- (__ \ "trader-contact-details").read[TraderContactDetails]
      traderAddress <- (__ \ "final-importer-address").read[TraderAddress]
      customsDuty <- (__ \ "customs-duty").readNullable[UnderpaymentAmount]
      importVat <- (__ \ "import-vat").readNullable[UnderpaymentAmount]
      exciseDuty <- (__ \ "excise-duty").readNullable[UnderpaymentAmount]
    } yield {
      val customsDutyUnderpayment = customsDuty.map( x => Seq(UnderpaymentDetail("customsDuty", x.original, x.amended))).getOrElse(Seq.empty)
      val importVatUnderpayment = importVat.map( x => Seq(UnderpaymentDetail("importVat", x.original, x.amended))).getOrElse(Seq.empty)
      val exciseDutyUnderpayment = exciseDuty.map( x => Seq(UnderpaymentDetail("exciseDuty", x.original, x.amended))).getOrElse(Seq.empty)

      IVDSubmission(
        userType = userType,
        numEntries = numEntries,
        acceptanceDate = acceptanceDate,
        entryDetails = entryDetails,
        originalCpc = originalCpc,
        traderContactDetails = traderContactDetails,
        traderAddress = traderAddress,
        underpaymentDetails = Some(customsDutyUnderpayment ++ importVatUnderpayment ++ exciseDutyUnderpayment)
      )
    }
}
