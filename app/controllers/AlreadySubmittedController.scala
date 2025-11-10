/*
 * Copyright 2025 HM Revenue & Customs
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

import controllers.actions._
import models.SubmissionType
import play.api.i18n.I18nSupport
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.shared.AlreadySubmittedView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AlreadySubmittedController @Inject() (
  identify: IdentifierAction,
  mcc: MessagesControllerComponents,
  view: AlreadySubmittedView,
  implicit val ec: ExecutionContext
) extends FrontendController(mcc)
    with I18nSupport {

  def createSubmitted: Action[AnyContent] = identify.async { implicit request =>
    Future.successful(Ok(view(SubmissionType.CreateCase.toString)))
  }

  def amendSubmitted: Action[AnyContent] = identify.async { implicit request =>
    Future.successful(Ok(view(SubmissionType.UpdateCase.toString)))
  }

  def cancelSubmitted: Action[AnyContent] = identify.async { implicit request =>
    Future.successful(Ok(view(SubmissionType.CancelCase.toString)))
  }

}
