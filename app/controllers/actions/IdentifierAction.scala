/*
 * Copyright 2020 HM Revenue & Customs
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
import config.{AppConfig, EnrolmentKeys, SessionKeys}
import controllers.connectors.EnrolmentStoreProxyConnector
import models.audit.{AgentIneligibleAuditEvent, AgentNotAuthorisedAuditEvent, IndividualIneligibleAuditEvent, MissingEnrolmentAuditEvent}
import models.requests.IdentifierRequest
import play.api.Logger
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionBuilder, ActionFunction, AnyContent, BodyParsers, Request, Result}
import services.AuditService
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Individual}
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.{AffinityGroup, AuthConnector, AuthorisationException, AuthorisedFunctions, CredentialRole, Enrolment, Enrolments, InsufficientEnrolments, NoActiveSession, UnsupportedAuthProvider, User}
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.HeaderCarrierConverter
import utils.SessionUtils.{ResultUtils, SessionUtils}

import scala.concurrent.{ExecutionContext, Future}

trait IdentifierAction extends ActionBuilder[IdentifierRequest, AnyContent] with ActionFunction[Request, IdentifierRequest]


class AuthenticatedIdentifierAction @Inject()(override val authConnector: AuthConnector,
                                              config: AppConfig,
                                              auditService: AuditService,
                                              val parser: BodyParsers.Default,
                                              isGroupEnrolledForPAYEConnector: EnrolmentStoreProxyConnector
                                             )(implicit val executionContext: ExecutionContext)
  extends IdentifierAction with AuthorisedFunctions {

  private val logger = Logger("application." + getClass.getCanonicalName)

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSessionAndRequest(
      headers = request.headers,
      session = Some(request.session),
      request = Some(request)
    )

    implicit val req = request

    logger.debug("Agent Access enabled")
    req.session.getModel[AffinityGroup](SessionKeys.userType) match {
      case Some(AffinityGroup.Agent) =>
        logger.debug("Authorising as an Agent")
        authoriseAsAgent(block)
      case Some(_) =>
        logger.debug("Authorising as an Organisation")
        authoriseAsOrganisation(block)
      case None =>
        logger.debug("Determining user type")
        determineUserType
    }

  }

  private def determineUserType[A](implicit request: Request[A], hc: HeaderCarrier) = {
    authorised().retrieve(Retrievals.affinityGroup) {
      case Some(userType) => {
        Future.successful(Redirect(request.uri)
          .addingModelToSession[AffinityGroup](SessionKeys.userType -> userType))
      }
      case None => {
        logger.error("[invokeBlock] Could not retrieve affinity group")
        throw UnsupportedAuthProvider("Unable to retrieve credId")
      }
    } recover {
      case _: NoActiveSession =>
        Redirect(config.loginUrl, Map("continue" -> Seq(config.loginContinueUrl)))
      case x: AuthorisationException =>
        logger.warn(s"[determineUserType] Authorisation Exception ${x.reason}")
        Redirect(controllers.errors.routes.UnauthorisedController.onPageLoad())
    }
  }

  private def authoriseAsAgent[A](block: IdentifierRequest[A] => Future[Result])(implicit request: Request[A], hc: HeaderCarrier): Future[Result] = {
    request.session.get(SessionKeys.empref) match {
      case None =>
        logger.debug("[authoriseAsAgent] No empref in session, redirect to agent classic services")
        Future.successful(Redirect(config.agentServicesNoClientUrl))
      case Some(empref) =>
        val taxOfficeNumber = empref.split("-").head
        val taxOfficeReference = empref.split("-").last
        logger.debug(s"[authoriseAsAgent] TaxOfficeNumber: $taxOfficeNumber, TaxOfficeReference: $taxOfficeReference")
        authorised(
          Enrolment("IR-PAYE")
            .withIdentifier("TaxOfficeNumber", taxOfficeNumber)
            .withIdentifier("TaxOfficeReference", taxOfficeReference)
            .withDelegatedAuthRule("epaye-auth")
        ).retrieve(
          Retrievals.internalId and Retrievals.credentials and Retrievals.allEnrolments
        ) {
          case None ~ _ ~ _ => {
            logger.error("[authoriseAsAgent] Could not retrieve internalId")
            throw UnsupportedAuthProvider("Unable to retrieve internal Id")
          }
          case _ ~ None ~ _ => {
            logger.error("[authoriseAsAgent] Could not retrieve credId")
            throw UnsupportedAuthProvider("Unable to retrieve credId")
          }
          case Some(internalId) ~ Some(credentials) ~ enrolments =>
            enrolments.getEnrolment(EnrolmentKeys.agentPAYE).flatMap(_.identifiers.headOption.map(_.value)) match {
              case Some(arn) => block(IdentifierRequest(request, s"${internalId}_$empref", empref, credentials.providerId, Some(arn)))
              case None => Future.successful(Redirect(controllers.errors.routes.IneligibleController.fileOnlyAgent()))
            }
        } recoverWith {
          case _: InsufficientEnrolments =>
            auditAgentNotAuthorised
          case _: NoActiveSession =>
            Future.successful(Redirect(config.loginUrl, Map("continue" -> Seq(config.loginContinueUrl))))
          case x: AuthorisationException =>
            logger.debug(s"[authoriseAsAgent] Authorisation Exception ${x.reason}")
            Future.successful(Redirect(controllers.errors.routes.UnauthorisedController.onPageLoad()))
        }
    }
  }

  private[actions] def auditAgentNotAuthorised[A](implicit request: Request[A], hc: HeaderCarrier) = {
    authorised().retrieve(Retrievals.credentials and Retrievals.allEnrolments) {
      case credId ~ enrolments =>
        val arn = enrolments.getEnrolment(EnrolmentKeys.agentPAYE).flatMap(_.identifiers.headOption.map(_.value))
        auditService.audit(AgentNotAuthorisedAuditEvent(credId.map(_.providerId), arn))
        logger.warn(s"[authoriseAsAgent] Agent without delegated authority")
        Future.successful(Redirect(controllers.errors.routes.IneligibleController.fileOnlyAgent()))
    } recover {
      case _: Exception =>
        logger.error(s"[authoriseAsAgent] Unexpected Error in second call to auth")
        auditService.audit(AgentNotAuthorisedAuditEvent(None, None))
        Redirect(controllers.errors.routes.IneligibleController.fileOnlyAgent())
    }
  }

  private def authoriseAsOrganisation[A](block: IdentifierRequest[A] => Future[Result])(implicit request: Request[A], hc: HeaderCarrier) = {
    authorised().retrieve(
      Retrievals.affinityGroup and Retrievals.allEnrolments and Retrievals.internalId and
        Retrievals.groupIdentifier and Retrievals.credentialRole and Retrievals.credentials) {

      case Some(Agent) ~ enrolments ~ _ ~ _ ~ _ ~ Some(credentials) => {
        logger.debug("[authoriseAsOrganisation] Agent Affinity Group")
        auditService.audit(AgentIneligibleAuditEvent(
          credId = credentials.providerId,
          arn = enrolments.getEnrolment(EnrolmentKeys.agentPAYE).flatMap(_.identifiers.headOption.map(_.value))
        ))
        Future.successful(Redirect(controllers.errors.routes.UnauthorisedController.agent()))
      }

      case Some(Individual) ~ _ ~ _ ~ _ ~ _ ~ Some(credentials) => {
        logger.debug("[authoriseAsOrganisation] Individual Affinity Group")
        auditService.audit(IndividualIneligibleAuditEvent(credentials.providerId))
        Future.successful(Redirect(controllers.errors.routes.UnauthorisedController.individual()))
      }

      case _ ~ _ ~ None ~ _ ~ _ ~ _ => {
        logger.error("[authoriseAsOrganisation] Could not retrieve internalId")
        throw UnsupportedAuthProvider("Unable to retrieve internal Id")
      }

      case _ ~ _ ~ _ ~ _ ~ _ ~ None => {
        logger.error("[authoriseAsOrganisation] Could not retrieve credId")
        throw UnsupportedAuthProvider("Unable to retrieve credId")
      }

      case _ ~ enrolments ~ Some(internalId) ~ Some(groupId) ~ role ~ Some(credentials) =>
        checkOrganisationEpayeEnrolment(enrolments, internalId, groupId, role, credentials.providerId)(block)

    } recover {
      case _: NoActiveSession =>
        Redirect(config.loginUrl, Map("continue" -> Seq(config.loginContinueUrl)))
      case x: AuthorisationException =>
        logger.debug(s"[authoriseAsOrganisation] Authorisation Exception ${x.reason}")
        Redirect(controllers.errors.routes.UnauthorisedController.onPageLoad())
    }
  }

  private def checkOrganisationEpayeEnrolment[A](enrolments: Enrolments,
                                                 internalId: String,
                                                 groupId: String,
                                                 role: Option[CredentialRole],
                                                 credId: String
                                                )(block: IdentifierRequest[A] => Future[Result])
                                                (implicit hc: HeaderCarrier, request: Request[A]): Future[Result] = {

    enrolments.enrolments.find(_.key == EnrolmentKeys.ePAYE) match {
      case Some(enrolment) if enrolment.isActivated =>
        (for {
          officeNumber <- enrolment.identifiers.find(_.key == EnrolmentKeys.taxOfficeNumber).map(_.value)
          officeRef <- enrolment.identifiers.find(_.key == EnrolmentKeys.taxOfficeReference).map(_.value)
          empref = officeNumber + "-" + officeRef
        } yield empref) match {
          case Some(empref) => block(IdentifierRequest(request, internalId, empref, credId))
          case None =>
            logger.error("[checkOrganisationEpayeEnrolment] Could not find Identifiers for IR-PAYE enrolment")
            auditService.audit(MissingEnrolmentAuditEvent(credId))
            Future.successful(Redirect(controllers.errors.routes.UnauthorisedController.onPageLoad()))
        }
      case Some(enrolment) if !enrolment.isActivated =>
        //Could route to bespoke not activated enrolment page?
        logger.debug("[checkOrganisationEpayeEnrolment] IR-PAYE enrolment found but not activated")
        auditService.audit(MissingEnrolmentAuditEvent(credId))
        Future.successful(Redirect(controllers.errors.routes.UnauthorisedController.missingEnrolment()))
      case _ => enrolmentCheck(groupId, role, credId)
    }
  }

  def enrolmentCheck[A](groupId: String,
                        role: Option[CredentialRole],
                        credId: String)
                       (implicit hc: HeaderCarrier, request: Request[A]): Future[Result] = {

    if(role.contains(User)){

      val fetchUrl = controllers.routes.IndexController.onPageLoad().url
      val fetchFreshData = request.uri == fetchUrl

      isGroupEnrolledForPAYEConnector.isGroupEnrolledForPaye(groupId, fetchFreshData).map {
        case true =>
          logger.debug(s"[enrolmentCheck] Group is enrolled for PAYE")
          Redirect(controllers.routes.GroupEnrolledForPayeController.onPageLoad())
            .addingToSession(SessionKeys.groupEnrolledForPAYE -> "true")

        case false =>
          logger.debug(s"[enrolmentCheck] Group is not enrolled for PAYE")
          auditService.audit(MissingEnrolmentAuditEvent(credId))
          Redirect(controllers.errors.routes.UnauthorisedController.missingEnrolment())
            .addingToSession(SessionKeys.groupEnrolledForPAYE -> "false")
      }

    } else {
      logger.warn(s"[enrolmentCheck] None User account logged into service - Role: $role")
      //Need to be of type User to access tax and scheme management so routing to Unauthorised
      Future.successful(Redirect(controllers.errors.routes.UnauthorisedController.missingEnrolment()))
    }
  }
}
