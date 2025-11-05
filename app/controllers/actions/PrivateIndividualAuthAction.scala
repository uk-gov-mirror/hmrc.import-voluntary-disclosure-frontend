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

package controllers.actions

import com.google.inject.Inject
import config.AppConfig
import models.requests.IdentifierRequest
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Results.{Redirect, Unauthorized}
import play.api.mvc._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals._
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

trait PrivateIndividualAuthAction
    extends ActionBuilder[IdentifierRequest, AnyContent]
    with ActionFunction[Request, IdentifierRequest]

class PrivateIndividualAuthenticationAction @Inject() (
  override val authConnector: AuthConnector,
  unauthorisedView: views.html.errors.UnauthorisedView,
  config: AppConfig,
  val parser: BodyParsers.Default,
  val messagesApi: MessagesApi
)(implicit val executionContext: ExecutionContext)
    extends PrivateIndividualAuthAction
    with AuthorisedFunctions
    with I18nSupport {

  private val logger = Logger("application." + getClass.getCanonicalName)

  private def isValidUser(enrolments: Enrolments) = enrolments.getEnrolment("HMRC-CTS-ORG") match {
    case Some(enrolment) => enrolment.isActivated
    case None            => false
  }

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
    authorised().retrieve(externalId and allEnrolments and affinityGroup) {
      case Some(userId) ~ enrolments ~ Some(AffinityGroup.Individual) if !isValidUser(enrolments) =>
        val req = IdentifierRequest(request, userId, "NoEORI")
        block(req)
      case _ =>
        logger.warn("Non-Individual trying to access Private Individual journey")
        Future.successful(Unauthorized(unauthorisedView()(request, request2Messages(request))))
    } recover {
      case _: NoActiveSession =>
        Redirect(config.loginUrl, Map("continue" -> Seq(config.loginContinueUrl)))
      case x: AuthorisationException =>
        logger.warn(s"Authorisation Exception ${x.reason}")
        Unauthorized(unauthorisedView()(request, request2Messages(request)))
    }
  }

}
