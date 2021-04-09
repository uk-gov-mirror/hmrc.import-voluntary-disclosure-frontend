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

package controllers.underpayments

import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.underpayments.UnderpaymentTypeFormProvider
import pages.underpayments.{UnderpaymentDetailSummaryPage, UnderpaymentTypePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.underpayments.UnderpaymentTypeView

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UnderpaymentTypeController @Inject()(identify: IdentifierAction,
                                           getData: DataRetrievalAction,
                                           requireData: DataRequiredAction,
                                           sessionRepository: SessionRepository,
                                           mcc: MessagesControllerComponents,
                                           underpaymentTypeView: UnderpaymentTypeView,
                                           formProvider: UnderpaymentTypeFormProvider)
  extends FrontendController(mcc) with I18nSupport {

  private lazy val backLink: Call = controllers.underpayments.routes.UnderpaymentStartController.onLoad()
  private val underpaymentTypes = Seq("B00", "A00", "E00", "A20", "A30", "A35", "A40", "A45", "A10", "D10")

  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val underpaymentDetails = request.userAnswers.get(UnderpaymentDetailSummaryPage)
    val existingUnderpaymentDetails = underpaymentDetails.getOrElse(Seq.empty).map(item => item.duty)
    if (existingUnderpaymentDetails.length == 10){
      Future.successful(Redirect(controllers.underpayments.routes.UnderpaymentDetailSummaryController.onLoad()))
    } else {
      val form = request.userAnswers.get(UnderpaymentTypePage).fold(formProvider()) {
        formProvider().fill
      }
      val availableUnderPaymentTypes = underpaymentTypes.filter(item => !existingUnderpaymentDetails.contains(item))
      val availableUnderPaymentTypesOptions = createRadioButton(form, availableUnderPaymentTypes)
      Future.successful(Ok(underpaymentTypeView(form, backLink, availableUnderPaymentTypesOptions)))
    }
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => {
        Future.successful(
          BadRequest(underpaymentTypeView(formWithErrors, backLink, createRadioButton(formWithErrors, underpaymentTypes)))
        )
      },
      value => {
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(UnderpaymentTypePage, value))
          _ <- sessionRepository.set(updatedAnswers)
        } yield {
          Redirect(controllers.underpayments.routes.UnderpaymentDetailsController.onLoad(value))
        }
      }
    )
  }

  private def createRadioButton(form: Form[_], values: Seq[String])(implicit messages: Messages): Seq[RadioItem] = {
    values.map(keyValue =>
      RadioItem(
        value = Some(keyValue),
        content = Text(messages(s"underpaymentType.$keyValue.radio")),
        checked = form("value").value.contains(keyValue),
        id = Some(keyValue)
      )
    )
  }

}
