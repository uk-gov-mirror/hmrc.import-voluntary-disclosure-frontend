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

package assets

import messages.BaseMessages
import models._
import play.api.libs.json.{JsValue, Json}

import java.time.LocalDate

trait IVDSubmissionTestData extends BaseMessages {

  val ivdSubmission = IVDSubmission(
    userType = UserType.Importer,
    numEntries = NumberOfEntries.OneEntry,
    acceptanceDate = Some(true),
    additionalInfo = Some("Not Applicable"),
    entryDetails = EntryDetails("123", "123456Q", LocalDate.of(2020,12,12)),
    originalCpc = "4000C09",
    amendedCpc = None,
    traderContactDetails = TraderContactDetails("Joe Bloggs", "test@test.com", "0123456789"),
    traderAddress = TraderAddress("99 Avenue Road", "Anyold Town", Some("99JZ 1AA"), "United Kingdom"),
    defermentType = None,
    defermentAccountNumber = None,
    additionalDefermentNumber = None,
    underpaymentReasons = None,
    underpaymentDetails = Some(Seq(
      UnderpaymentDetail("customsDuty", BigDecimal(123.0), BigDecimal(233.33)),
      UnderpaymentDetail("importVat", BigDecimal(111.11), BigDecimal(1234.0)),
      UnderpaymentDetail("exciseDuty", BigDecimal(123.22), BigDecimal(4409.55))
    )),
    documentList = None
  )

  val ivdSubmissionJson: JsValue = Json.parse(
    """
      |{
      |  "userType": "importer",
      |  "numEntries": "oneEntry",
      |  "acceptanceDate": true,
      |  "additionalInfo": "Not Applicable",
      |  "entryDetails": {
      |    "epu": "123",
      |    "entryNumber": "123456Q",
      |    "entryDate": "2020-12-12"
      |  },
      |  "originalCpc": "4000C09",
      |  "traderContactDetails": {
      |    "fullName": "Joe Bloggs",
      |    "email": "test@test.com",
      |    "phoneNumber": "0123456789"
      |  },
      |  "traderAddress": {
      |    "streetAndNumber": "99 Avenue Road",
      |    "city": "Anyold Town",
      |    "postalCode": "99JZ 1AA",
      |    "countryCode": "United Kingdom"
      |  },
      |  "underpaymentDetails": [
      |    {
      |      "duty": "customsDuty",
      |      "original": 123,
      |      "amended": 233.33
      |    },
      |    {
      |      "duty": "importVat",
      |      "original": 111.11,
      |      "amended": 1234
      |    },
      |    {
      |      "duty": "exciseDuty",
      |      "original": 123.22,
      |      "amended": 4409.55
      |    }
      |  ]
      |}""".stripMargin
  )

  val userAnswersJson: JsValue = Json.parse("""
      |{
      |        "user-type" : "importer",
      |        "entry-details" : {
      |            "epu" : "123",
      |            "entryNumber" : "123456Q",
      |            "entryDate" : "2020-12-12"
      |        },
      |        "customs-duty" : {
      |            "original" : 123.0,
      |            "amended" : 233.33
      |        },
      |        "trader-contact-details" : {
      |            "fullName" : "Joe Bloggs",
      |            "email" : "test@test.com",
      |            "phoneNumber" : "0123456789"
      |        },
      |        "final-importer-address" : {
      |            "streetAndNumber" : "99 Avenue Road",
      |            "city" : "Anyold Town",
      |            "postalCode" : "99JZ 1AA",
      |            "countryCode" : "United Kingdom"
      |        },
      |        "importer-address" : true,
      |        "deferment" : false,
      |        "excise-duty" : {
      |            "original" : 123.22,
      |            "amended" : 4409.55
      |        },
      |        "number-of-entries" : "oneEntry",
      |        "uploaded-files" : [
      |            {
      |                "fileName" : "TestDocument.pdf",
      |                "downloadUrl" : "http://localhost:9570/upscan/download/dd76a36a-f6d9-49d4-830c-968566a33f00",
      |                "uploadTimestamp" : "2021-02-09T10:22:33.466",
      |                "checksum" : "e50a70a5d79253c7ecaf48421eb688c708c872e4639b3e3a0cc089600ffe0fde",
      |                "fileMimeType" : "application/pdf"
      |            }
      |        ],
      |        "cpc" : {
      |            "cpc-changed" : false,
      |            "original-cpc" : "4000C09"
      |        },
      |        "underpaymentType" : {
      |            "customsDuty" : true,
      |            "importVAT" : true,
      |            "exciseDuty" : true
      |        },
      |        "import-vat" : {
      |            "original" : 111.11,
      |            "amended" : 1234.0
      |        },
      |        "acceptance-date" : true
      |    }""".stripMargin)
}
