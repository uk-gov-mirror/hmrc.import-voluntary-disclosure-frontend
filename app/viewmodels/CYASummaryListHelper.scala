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

import java.time.format.DateTimeFormatter

import models.{NumberOfEntries, UserAnswers}
import pages._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import views.ViewUtils.displayMoney

trait CYASummaryListHelper {

  def buildDisclosureDetailsSummaryList(answer: UserAnswers)(implicit messages: Messages): Option[CYASummaryList] = {

    val numberOfEntriesSummaryListRow: Option[Seq[SummaryListRow]] = answer.get(NumberOfEntriesPage) map { numberOfEntries =>
      val numberOfEntriesValue = if (numberOfEntries.equals(NumberOfEntries.OneEntry)) messages("cya.oneEntry") else messages("cya.bulkEntry")
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("cya.numberOfEntries")),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            content = HtmlContent(numberOfEntriesValue)
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem("Url", Text(messages("cya.change")))
            )
          )
          )
        )
      )
    }

    val epuSummaryListRow: Option[Seq[SummaryListRow]] = answer.get(EntryDetailsPage) map { entryDetails =>
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("cya.epu")),
            classes = "govuk-!-width-two-thirds govuk-!-padding-bottom-0"
          ),
          value = Value(
            content = HtmlContent(entryDetails.epu),
            classes = "govuk-!-padding-bottom-0"
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem("Url", Text(messages("cya.change")))
            ),
            classes = "govuk-!-padding-bottom-0")
          ),
          classes = "govuk-summary-list__row--no-border"
        )
      )
    }

    val entryNumberListRow: Option[Seq[SummaryListRow]] = answer.get(EntryDetailsPage) map { entryDetails =>
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("cya.entryNumber")),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0 govuk-!-padding-bottom-0"
          ),
          value = Value(
            content = HtmlContent(entryDetails.entryNumber),
            classes = "govuk-!-padding-top-0 govuk-!-padding-bottom-0"
          ),
          classes = "govuk-summary-list__row--no-border"
        )
      )
    }

    val entryDateListRow: Option[Seq[SummaryListRow]] = answer.get(EntryDetailsPage) map { entryDetails =>
      val entryDateFormat = entryDetails.entryDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("cya.entryDate")),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
          ),
          value = Value(
            content = HtmlContent(entryDateFormat),
            classes = "govuk-!-padding-top-0"
          )
        )
      )
    }

    val acceptanceDateListRow: Option[Seq[SummaryListRow]] = answer.get(AcceptanceDatePage) map { acceptanceDate =>
      val acceptanceDateValue = if (acceptanceDate) messages("site.yes") else messages("site.no")
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("cya.acceptanceDate")),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            content = HtmlContent(acceptanceDateValue)
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem("Url", Text(messages("cya.change")))
            )
          )
          )
        )
      )
    }

    val rows = numberOfEntriesSummaryListRow.getOrElse(Seq.empty) ++
      epuSummaryListRow.getOrElse(Seq.empty) ++
      entryNumberListRow.getOrElse(Seq.empty) ++
      entryDateListRow.getOrElse(Seq.empty) ++
      acceptanceDateListRow.getOrElse(Seq.empty)

    if (rows.nonEmpty) {
      Some(
        CYASummaryList(
          messages("cya.disclosureDetails"),
          SummaryList(
            classes = "govuk-!-margin-bottom-9",
            rows = rows
          )
        )
      )
    } else None
  }

  def buildUnderpaymentDetailsSummaryList(answer: UserAnswers)(implicit messages: Messages): Option[CYASummaryList] = {

    val customsDutySummaryListRow: Option[Seq[SummaryListRow]] = answer.get(CustomsDutyPage) map { underpaymentAmount =>
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("cya.customsDuty")),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            content = HtmlContent(displayMoney(underpaymentAmount.amended - underpaymentAmount.original))
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem("Url", Text(messages("cya.change")))
            )
          )
         )
        )
      )
    }

    val importVatSummaryListRow: Option[Seq[SummaryListRow]] = answer.get(ImportVATPage) map { underpaymentAmount =>
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("cya.importVat")),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            content = HtmlContent(displayMoney(underpaymentAmount.amended - underpaymentAmount.original))
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem("Url", Text(messages("cya.change")))
            )
          )
          )
        )
      )
    }

    val exciseDutySummaryListRow: Option[Seq[SummaryListRow]] = answer.get(ExciseDutyPage) map { underpaymentAmount =>
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("cya.exciseDuty")),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            content = HtmlContent(displayMoney(underpaymentAmount.amended - underpaymentAmount.original))
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem("Url", Text(messages("cya.change")))
            )
          )
          )
        )
      )
    }

    val rows = customsDutySummaryListRow.getOrElse(Seq.empty) ++
      importVatSummaryListRow.getOrElse(Seq.empty) ++
      exciseDutySummaryListRow.getOrElse(Seq.empty)

    if (rows.nonEmpty) {
      Some(
        CYASummaryList(
          messages("cya.underpaymentDetails"),
          SummaryList(
            classes = "govuk-!-margin-bottom-9",
            rows = rows
          )
        )
      )
    } else None
  }

  def buildAmendmentDetailsSummaryList(answer: UserAnswers)(implicit messages: Messages): Option[CYASummaryList] = {

    val customProcedureCodeSummaryListRow: Option[Seq[SummaryListRow]] = answer.get(EnterCustomsProcedureCodePage) map { customsProcedure =>
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("cya.customsProcedureCode")),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            content = HtmlContent(customsProcedure)
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem("Url", Text(messages("cya.change")))
            )
          )
          )
        )
      )
    }

//    val numberOfAmendmentsSummaryListRow: Option[Seq[SummaryListRow]] = answer.get(numberOfAmendmentsPage) map { numberOfAmendments =>
//      Seq(
//        SummaryListRow(
//          key = Key(
//            content = Text(messages("cya.numberOfAmendments")),
//            classes = "govuk-!-width-two-thirds"
//          ),
//          value = Value(
//            content = HtmlContent(numberOfAmendments)
//          ),
//          actions = Some(Actions(
//            items = Seq(
//              ActionItem("Url", Text(messages("cya.change")))
//            )
//          )
//          )
//        )
//      )
//    }
//
//val supportingInformationSummaryListRow: Option[Seq[SummaryListRow]] = answer.get(SupportingInformationPage) map { supportingInformation =>
//  Seq(
//    SummaryListRow(
//      key = Key(
//        content = Text(messages("cya.supportingInformation")),
//        classes = "govuk-!-width-two-thirds"
//      ),
//      value = Value(
//        content = HtmlContent(supportingInformation)
//      ),
//      actions = Some(Actions(
//        items = Seq(
//          ActionItem("Url", Text(messages("cya.change")))
//        )
//      )
//      )
//    )
//  )
//}

    val rows = customProcedureCodeSummaryListRow.getOrElse(Seq.empty)
//      numberOfAmendmentsSummaryListRow.getOrElse(Seq.empty) ++
//      supportingInformationSummaryListRow.getOrElse(Seq.empty)

    if (rows.nonEmpty) {
      Some(
        CYASummaryList(
          messages("cya.amendmentDetails"),
          SummaryList(
            classes = "govuk-!-margin-bottom-9",
            rows = rows
          )
        )
      )
    } else None
  }

  def buildSupportingDocumentsSummaryList(answer: UserAnswers)(implicit messages: Messages): Option[CYASummaryList] = {
    val uploadedFilesSummaryListRow: Option[Seq[SummaryListRow]] = answer.get(FileUploadPage) map { files =>
      val fileNames = files map (file => file.fileName)
      val whichFile = if(fileNames.length == 1) "file" else "files"
      Seq(
        SummaryListRow(
          key = Key(
              content = Text(messages("cya.filesUploaded", fileNames.length, whichFile)),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            content = HtmlContent(fileNames.mkString("\n"))
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem("Url", Text(messages("cya.change")))
            )
          )
          )
        )
      )
    }
    val rows = uploadedFilesSummaryListRow.getOrElse(Seq.empty)
    if (rows.nonEmpty) {
      Some(
        CYASummaryList(
          messages(messages("cya.supportingDocuments")),
          SummaryList(
            classes = "govuk-!-margin-bottom-9",
            rows = rows
          )
        )
      )
    } else None
  }

  def buildYourDetailsSummaryList(answer: UserAnswers)(implicit messages: Messages): Option[CYASummaryList] = {
    val detailsSummaryListRow: Option[Seq[SummaryListRow]] = answer.get(TraderContactDetailsPage) map { details =>
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("cya.name")),
            classes = "govuk-!-width-two-thirds govuk-!-padding-bottom-0"
          ),
          value = Value(
            content = HtmlContent(details.fullName),
            classes = "govuk-!-padding-bottom-0"
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem("Url", Text(messages("cya.change")))
            ),
            classes = "govuk-!-padding-bottom-0")
          ),
          classes = "govuk-summary-list__row--no-border"
        ),
        SummaryListRow(
          key = Key(
            content = Text(messages("cya.email")),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0 govuk-!-padding-bottom-0"
          ),
          value = Value(
            content = HtmlContent(details.email),
            classes = "govuk-!-padding-top-0 govuk-!-padding-bottom-0"
          ),
          classes = "govuk-summary-list__row--no-border"
        ),
        SummaryListRow(
          key = Key(
            content = Text(messages("cya.phone")),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
          ),
          value = Value(
            content = HtmlContent(details.phoneNumber),
            classes = "govuk-!-padding-top-0"
          ),
          classes = "govuk-!-padding-top-0"
        )
      )
    }

    val addressSummaryListRow: Option[Seq[SummaryListRow]] = answer.get(ImporterAddressFinalPage) map { address =>
      val addressString = address.postalCode match {
        case Some(value) => address.addressLine1 + "<br/>" +
          address.city + "<br/>" +
          address.postalCode.get + "<br/>" +
          address.countryCode
        case None => address.addressLine1 + "<br/>" +
          address.city + "<br/>" +
          address.countryCode
      }
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("cya.address")),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            content = HtmlContent(addressString)
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem("Url", Text(messages("cya.change")))
            )
          )
          )
        )
      )
    }

    val rows = detailsSummaryListRow.getOrElse(Seq.empty) ++ addressSummaryListRow.getOrElse(Seq.empty)
    if (rows.nonEmpty) {
      Some(
        CYASummaryList(
          messages(messages("cya.yourDetails")),
          SummaryList(
            classes = "govuk-!-margin-bottom-9",
            rows = rows
          )
        )
      )
    } else None
  }

  def buildPaymentInformationSummaryList(answer: UserAnswers)(implicit messages: Messages): Option[CYASummaryList] = {
    val paymentInformationSummaryListRow: Option[Seq[SummaryListRow]] = answer.get(DefermentPage) map { deferment =>
      val payingByDeferment = if (deferment) messages("site.yes") else messages("site.no")
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("cya.payingByDeferment")),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            content = HtmlContent(payingByDeferment)
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem("Url", Text(messages("cya.change")))
            ))
          )
        )
      )
    }
    if (paymentInformationSummaryListRow.nonEmpty) {
      Some(
        CYASummaryList(
          messages(messages("cya.paymentInformation")),
          SummaryList(
            classes = "govuk-!-margin-bottom-9",
            rows = paymentInformationSummaryListRow.get
          )
        )
      )
    } else None
  }

}
