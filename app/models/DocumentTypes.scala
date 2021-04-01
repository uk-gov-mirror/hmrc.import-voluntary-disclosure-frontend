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

import play.api.libs.json.{Format, Json}

object DocumentTypes extends Enumeration {

  type DocumentType = Value

  val OriginalC88: DocumentType = Value("OriginalC88")
  val OriginalC2: DocumentType = Value("OriginalC2")
  val AmendedSubstituteEntryWorksheet: DocumentType = Value("AmendedSubstituteEntryWorksheet")
  val AmendedC88: DocumentType = Value("AmendedC88")
  val AmendedC2: DocumentType = Value("AmendedC2")
  val InvoiceAirwayBillPreferenceCertificate: DocumentType = Value("InvoiceAirwayBillPreferenceCertificate")
  val DefermentAuthorisation: DocumentType = Value("DefermentAuthorisation")
  val Other: DocumentType = Value("Other")

  implicit val format: Format[DocumentType] = Json.formatEnum(this)

}