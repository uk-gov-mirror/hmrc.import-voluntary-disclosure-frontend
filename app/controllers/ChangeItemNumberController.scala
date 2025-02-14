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
import forms.ItemNumberFormProvider
import pages.{ChangeUnderpaymentReasonPage, UnderpaymentReasonsPage}
import play.api.data.FormError
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.ItemNumberView

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ChangeItemNumberController @Inject()(identify: IdentifierAction,
                                           getData: DataRetrievalAction,
                                           requireData: DataRequiredAction,
                                           sessionRepository: SessionRepository,
                                           mcc: MessagesControllerComponents,
                                           view: ItemNumberView,
                                           formProvider: ItemNumberFormProvider)
  extends FrontendController(mcc) with I18nSupport {

  private lazy val backLink: Call = controllers.routes.ChangeUnderpaymentReasonController.onLoad()

  private lazy val formAction: Call = controllers.routes.ChangeItemNumberController.onSubmit()

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val form = request.userAnswers.get(ChangeUnderpaymentReasonPage).fold(formProvider()) { reason =>
      formProvider().fill(reason.changed.itemNumber)
    }
    Future.successful(Ok(view(form, formAction, backLink)))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors, formAction, backLink))),
      value => {
        request.userAnswers.get(ChangeUnderpaymentReasonPage) match {
          case Some(data) =>
            val currentUnderpayments = request.userAnswers.get(UnderpaymentReasonsPage).getOrElse(Seq.empty)
            if (currentUnderpayments.nonEmpty) {
              val alreadyExistsBoxAndItem = currentUnderpayments.exists(underpayment =>
                underpayment.boxNumber == data.changed.boxNumber &&
                  underpayment.itemNumber == value &&
                  data.original.itemNumber != value
              )
              if (alreadyExistsBoxAndItem) {
                val form = formProvider().fill(data.changed.itemNumber)
                val formError = FormError("itemNumber", "itemNo.error.notTheSameNumber")
                Future.successful(Ok(view(form.copy(errors = Seq(formError)), formAction, backLink)))
              } else {
                val changed = data.changed.copy(itemNumber = value)
                val reason = data.copy(changed = changed)
                for {
                  updatedAnswers <- Future.fromTry(request.userAnswers.set(ChangeUnderpaymentReasonPage, reason))
                  _ <- sessionRepository.set(updatedAnswers)
                } yield {
                  Redirect(controllers.routes.ChangeUnderpaymentReasonDetailsController.onLoad(data.original.boxNumber))
                }
              }
            } else {
              Future.successful(InternalServerError("List of underpayment reasons is empty"))
            }
          case _ => Future.successful(InternalServerError("Changed item number not found"))
        }
      }
    )
  }
}
