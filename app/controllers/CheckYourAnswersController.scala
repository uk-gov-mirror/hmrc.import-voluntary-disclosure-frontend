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

package controllers

import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import play.api.i18n.I18nSupport
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import viewmodels.{CYASummaryList, CYASummaryListHelper}
import views.html.CheckYourAnswersView
import javax.inject.{Inject, Singleton}

import scala.concurrent.Future

@Singleton
class CheckYourAnswersController @Inject()(identify: IdentifierAction,
                                           getData: DataRetrievalAction,
                                           requireData: DataRequiredAction,
                                           mcc: MessagesControllerComponents,
                                           view: CheckYourAnswersView)
  extends FrontendController(mcc) with I18nSupport with CYASummaryListHelper {

  val onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    val disclosureDetails: CYASummaryList = buildDisclosureDetailsSummaryList(request.userAnswers).get
    val underpaymentDetails: CYASummaryList = buildUnderpaymentDetailsSummaryList(request.userAnswers).get
    val amendmentDetails: CYASummaryList = buildAmendmentDetailsSummaryList(request.userAnswers).get
    val supportingDocuments: CYASummaryList = buildSupportingDocumentsSummaryList(request.userAnswers).get
    val yourDetailsDocuments: CYASummaryList = buildYourDetailsSummaryList(request.userAnswers).get
    val paymentInformation: CYASummaryList = buildPaymentInformationSummaryList(request.userAnswers).get


    Future.successful(Ok(view(Seq(disclosureDetails, underpaymentDetails, amendmentDetails, supportingDocuments, yourDetailsDocuments, paymentInformation), controllers.routes.CheckYourAnswersController.onLoad)))
  }
}
