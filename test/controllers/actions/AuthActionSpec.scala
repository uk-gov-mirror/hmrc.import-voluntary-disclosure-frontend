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

import base.SpecBase
import com.google.inject.Inject
import config.{EnrolmentKeys, SessionKeys}
import controllers.errors.routes
import data.BaseConstants
import mocks.connectors.MockEnrolmentStoreProxyConnector
import mocks.services.MockAuditService
import models.audit._
import play.api.libs.json.Json
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Individual, Organisation}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Credentials, Retrieval, ~}
import uk.gov.hmrc.http.HeaderCarrier
import utils.SessionUtils._

import scala.concurrent.{ExecutionContext, Future}

class AuthActionSpec extends SpecBase
  with BaseConstants {

  trait Test extends MockEnrolmentStoreProxyConnector with MockAuditService {
    lazy val bodyParsers: BodyParsers.Default = app.injector.instanceOf[BodyParsers.Default]

    class Harness(authAction: IdentifierAction) {
      def onPageLoad(): Action[AnyContent] = authAction { _ => Results.Ok }
    }

    def authIdentifierAction(authConnector: AuthConnector): AuthenticatedIdentifierAction =
      new AuthenticatedIdentifierAction(authConnector, appConfig, mockAuditService, bodyParsers, mockConnector)

    type agentRetrieval = ~[~[Option[String], Option[Credentials]], Enrolments]
    type enrolmentDetails = ~[Option[AffinityGroup], Enrolments]
    type internalId = ~[enrolmentDetails, Option[String]]
    type groupId = ~[internalId, Option[String]]
    type role = ~[groupId, Option[CredentialRole]]
    type credId = ~[role, Option[Credentials]]

    val testCredId = "1234567891"
    val testArn = "654321"
    val testAgentEnrolment: Enrolment = Enrolment("IR-PAYE-AGENT") withIdentifier(testArn, testArn)

    def agentRetrievalResponse(internalId: Option[String] = Some("internalId"),
                               credId: Option[Credentials] = Some(Credentials(testCredId, "gg")),
                               enrolments: Enrolments = Enrolments(Set.empty)): Option[String] ~ Option[Credentials] ~ Enrolments =
      new ~(new ~(internalId, credId), enrolments)

    def data(enrolments: Enrolments = Enrolments(Set.empty),
             credentialRole: Option[CredentialRole] = Some(User),
             groupId: String = "groupId",
             affinityGroup: AffinityGroup = Agent,
             internalId: Option[String] = Some("internalId"),
             credId: Option[Credentials] = Some(Credentials(testCredId, "gg"))): credId = {
      new ~(new ~(new ~(new ~(new ~(Some(affinityGroup),
        enrolments),
        internalId),
        Some(groupId)),
        credentialRole),
        credId)
    }

    def agentNotAuthorisedRetrievalResponse(credId: Option[Credentials] = Some(Credentials(testCredId, "gg")),
                                            enrolments: Enrolments = Enrolments(Set.empty)): Option[Credentials] ~ Enrolments =
      new ~(credId, enrolments)

    val agentRequestWithEmpref: FakeRequest[AnyContentAsEmpty.type] = fakeRequest.withSession(
      SessionKeys.userType -> Json.toJson(AffinityGroup.Agent).toString(),
      SessionKeys.empref -> empref
    )

    val agentRequestNoEmpref: FakeRequest[AnyContentAsEmpty.type] = fakeRequest.withSession(SessionKeys.userType -> Json.toJson(AffinityGroup.Agent).toString())

  }

  "Auth Action" when {

    "Affinity Group of user is not held in session" when {

      "user is not logged in" must {

        "redirect to sign-in" in new Test {

          val authAction: AuthenticatedIdentifierAction =
            authIdentifierAction(new FakeFailingAuthConnector(new BearerTokenExpired))
          val controller = new Harness(authAction)
          val result: Future[Result] = controller.onPageLoad()(fakeRequest)

          status(result) mustBe SEE_OTHER

          redirectLocation(result).get must startWith(appConfig.loginUrl)
        }
      }

      "user is logged in" when {

        "affinity group is returned from auth" must {

          "redirect to the requested URI adding to session the affinity group returned" in new Test {

            val authAction: AuthenticatedIdentifierAction =
              authIdentifierAction(new FakeSuccessAuthConnector[Option[AffinityGroup]](Some(AffinityGroup.Agent)))
            val controller = new Harness(authAction)
            val result: Future[Result] = controller.onPageLoad()(fakeRequest)

            status(result) mustBe SEE_OTHER
            redirectLocation(result) mustBe Some(fakeRequest.uri)
            session(result).getModel[AffinityGroup](SessionKeys.userType) mustBe Some(AffinityGroup.Agent)
          }
        }

        "affinity group is NOT returned from auth" must {

          "redirect to the requested URI adding to session the affinity group returned" in new Test {

            val authAction: AuthenticatedIdentifierAction =
              authIdentifierAction(new FakeSuccessAuthConnector[Option[AffinityGroup]](None))
            val controller = new Harness(authAction)
            val result: Future[Result] = controller.onPageLoad()(fakeRequest)

            status(result) mustBe SEE_OTHER
            redirectLocation(result) mustBe Some(controllers.errors.routes.UnauthorisedController.onPageLoad().url)
          }
        }
      }
    }

    "Affinity Group is held in session" when {

      "user type is Agent" when {

        "no empref held in session" must {

          "redirect to agent services select client page" in new Test {

            val authAction: AuthenticatedIdentifierAction =
              authIdentifierAction(new FakeSuccessAuthConnector[credId](data(enrolments = Enrolments(Set(testAgentEnrolment)))))
            val controller = new Harness(authAction)
            val result: Future[Result] = controller.onPageLoad()(agentRequestNoEmpref)

            status(result) mustBe SEE_OTHER

            redirectLocation(result) mustBe Some(appConfig.agentServicesNoClientUrl)
          }
        }

        "an empref is held in session" when {

          "agent has IR-PAYE enrolment" when {

            val enrolments = Enrolments(Set(Enrolment(
              key = EnrolmentKeys.agentPAYE,
              identifiers = Seq(
                EnrolmentIdentifier(EnrolmentKeys.irAgentReference, "agentRef")
              ),
              state = EnrolmentKeys.activated
            )))

            "agent is not delegated to act on behalf of client" must {

              "throw unauthorised page" in new Test {

                stubAudit()

                val authAction: AuthenticatedIdentifierAction =
                  authIdentifierAction(new FakeFailingAuthConnector(InsufficientEnrolments()))
                val controller = new Harness(authAction)
                val result: Future[Result] = controller.onPageLoad()(agentRequestWithEmpref)

                status(result) mustBe SEE_OTHER
                redirectLocation(result) mustBe Some(controllers.errors.routes.IneligibleController.fileOnlyAgent().url)
              }

              "auditAgentNotAuthorised" when {

                "ARN is retrieved from second auth call" must {

                  "create an audit event" in new Test {
                    verifyAudit(AgentNotAuthorisedAuditEvent(Some(testCredId), Some("agentRef")))

                    val authAction: AuthenticatedIdentifierAction = authIdentifierAction(
                      new FakeSuccessAuthConnector[~[Option[Credentials], Enrolments]](agentNotAuthorisedRetrievalResponse(enrolments = enrolments))
                    )
                    val result: Future[Result] = authAction.auditAgentNotAuthorised(fakeRequest, hc)

                    status(result) mustBe SEE_OTHER
                    redirectLocation(result) mustBe Some(controllers.errors.routes.IneligibleController.fileOnlyAgent().url)
                  }
                }

                "error from second auth call" must {

                  "create an audit event" in new Test {
                    verifyAudit(AgentNotAuthorisedAuditEvent(None, None))

                    val authAction: AuthenticatedIdentifierAction =
                      authIdentifierAction(new FakeFailingAuthConnector(MissingBearerToken()))
                    val result: Future[Result] = authAction.auditAgentNotAuthorised(fakeRequest, hc)

                    status(result) mustBe SEE_OTHER
                    redirectLocation(result) mustBe Some(controllers.errors.routes.IneligibleController.fileOnlyAgent().url)
                  }

                }
              }
            }

            "agent is delegated to act on behalf of client" must {

              "execute supplied block in test harness" in new Test {

                val authAction: AuthenticatedIdentifierAction =
                  authIdentifierAction(new FakeSuccessAuthConnector[agentRetrieval](agentRetrievalResponse(enrolments = enrolments)))
                val controller = new Harness(authAction)
                val result: Future[Result] = controller.onPageLoad()(agentRequestWithEmpref)

                status(result) mustBe OK
              }
            }

            "the agents internalID cannot be retrieved" must {

              "redirect to the unauthorised route" in new Test {
                val authAction: AuthenticatedIdentifierAction =
                  authIdentifierAction(new FakeSuccessAuthConnector[agentRetrieval](agentRetrievalResponse(internalId = None)))

                val controller = new Harness(authAction)
                val result: Future[Result] = controller.onPageLoad()(agentRequestWithEmpref)

                status(result) mustBe SEE_OTHER
                redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad().url)
              }
            }

            "the agents credentials cannot be retrieved" must {

              "redirect to the unauthorised route" in new Test {
                val authAction: AuthenticatedIdentifierAction =
                  authIdentifierAction(new FakeSuccessAuthConnector[agentRetrieval](agentRetrievalResponse(credId = None)))

                val controller = new Harness(authAction)
                val result: Future[Result] = controller.onPageLoad()(agentRequestWithEmpref)

                status(result) mustBe SEE_OTHER
                redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad().url)
              }
            }

            "the auth session has expired" must {

              "redirect to the unauthorised route" in new Test {
                val authAction: AuthenticatedIdentifierAction =
                  authIdentifierAction(new FakeFailingAuthConnector(new MissingBearerToken))

                val controller = new Harness(authAction)
                val result: Future[Result] = controller.onPageLoad()(agentRequestWithEmpref)

                status(result) mustBe SEE_OTHER
                redirectLocation(result).get must startWith(appConfig.loginUrl)
              }
            }

            "there is an UnsupportedAuthProvider error" must {

              "redirect to the unauthorised route" in new Test {
                val authAction: AuthenticatedIdentifierAction =
                  authIdentifierAction(new FakeFailingAuthConnector(new UnsupportedAuthProvider))

                val controller = new Harness(authAction)
                val result: Future[Result] = controller.onPageLoad()(agentRequestWithEmpref)

                status(result) mustBe SEE_OTHER
                redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad().url)
              }
            }
          }

          "agent does not have IR-PAYE enrolment" when {

            "throw insufficient enrolment page in" in new Test {

              val authAction: AuthenticatedIdentifierAction =
                authIdentifierAction(new FakeSuccessAuthConnector[agentRetrieval](agentRetrievalResponse()))
              val controller = new Harness(authAction)
              val result: Future[Result] = controller.onPageLoad()(agentRequestWithEmpref)

              status(result) mustBe SEE_OTHER

              redirectLocation(result) mustBe Some(controllers.errors.routes.IneligibleController.fileOnlyAgent().url)
            }
          }
        }
      }

      "the user is logged in with an Individual affinity group" must {

        "redirect to the Individual Unauthorised route" in new Test {

          verifyAudit(IndividualIneligibleAuditEvent(testCredId))

          lazy val individualRequest: FakeRequest[AnyContentAsEmpty.type] =
            fakeRequest.withSession(SessionKeys.userType -> Json.toJson(AffinityGroup.Individual).toString())

          val authAction: AuthenticatedIdentifierAction =
            authIdentifierAction(new FakeSuccessAuthConnector[credId](data(affinityGroup = Individual)))

          val controller = new Harness(authAction)
          val result: Future[Result] = controller.onPageLoad()(individualRequest)

          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(controllers.errors.routes.UnauthorisedController.individual().url)
        }
      }

      "the user is an Organisation affinity group" when {

        lazy val organisationRequest = fakeRequest.withSession(SessionKeys.userType -> Json.toJson(AffinityGroup.Organisation).toString())

        "user is NOT logged in" must {

          "redirect to sign-in" in new Test {

            val authAction: AuthenticatedIdentifierAction =
              authIdentifierAction(new FakeFailingAuthConnector(new BearerTokenExpired))
            val controller = new Harness(authAction)
            val result: Future[Result] = controller.onPageLoad()(organisationRequest)

            status(result) mustBe SEE_OTHER

            redirectLocation(result).get must startWith(appConfig.loginUrl)
          }
        }

        "user is logged in" must {

          "the user does NOT have the ePAYE enrolment" must {

            "redirect to the Unauthorised missing enrolment route" in new Test {
              stubAudit()
              mockCheckGroup("groupId")(response = false)

              val authAction: AuthenticatedIdentifierAction =
                authIdentifierAction(new FakeSuccessAuthConnector[credId](data(affinityGroup = Organisation)))

              val controller = new Harness(authAction)
              val result: Future[Result] = controller.onPageLoad()(organisationRequest)

              status(result) mustBe SEE_OTHER
              redirectLocation(result) mustBe Some(controllers.errors.routes.UnauthorisedController.missingEnrolment().url)
            }

          }

          "the group does have the ePAYE enrolment" must {

            "redirect to the Unauthorised missing enrolment route" in new Test {
              mockCheckGroup("groupId")(response = true)

              val authAction: AuthenticatedIdentifierAction =
                authIdentifierAction(new FakeSuccessAuthConnector[credId](data(affinityGroup = Organisation)))

              val controller = new Harness(authAction)
              val result: Future[Result] = controller.onPageLoad()(organisationRequest)

              status(result) mustBe SEE_OTHER
              redirectLocation(result) mustBe Some(controllers.routes.GroupEnrolledForPayeController.onPageLoad().url)
            }

          }

          "the user does NOT have the ePAYE enrolment and is an assistant" must {

            "redirect to the Unauthorised route" in new Test {
              val authAction: AuthenticatedIdentifierAction =
                authIdentifierAction(new FakeSuccessAuthConnector[credId](data(affinityGroup = Organisation, credentialRole = Some(Assistant))))

              val controller = new Harness(authAction)
              val result: Future[Result] = controller.onPageLoad()(organisationRequest)

              status(result) mustBe SEE_OTHER
              redirectLocation(result) mustBe Some(controllers.errors.routes.UnauthorisedController.missingEnrolment().url)
            }
          }

          "the user does have the ePAYE enrolment" when {

            "the enrolment is NOT activated" must {

              "redirect to the generic Unauthorised route" in new Test {

                verifyAudit(MissingEnrolmentAuditEvent(testCredId))

                val enrolments: Enrolments = Enrolments(Set(Enrolment(
                  key = EnrolmentKeys.ePAYE,
                  identifiers = Seq(
                    EnrolmentIdentifier(EnrolmentKeys.taxOfficeNumber, taxOfficeNumber),
                    EnrolmentIdentifier(EnrolmentKeys.taxOfficeReference, taxOfficeReference)
                  ),
                  state = EnrolmentKeys.inactive
                )))

                val authAction: AuthenticatedIdentifierAction =
                  authIdentifierAction(new FakeSuccessAuthConnector[credId](data(affinityGroup = Organisation, enrolments = enrolments)))

                val controller = new Harness(authAction)
                val result: Future[Result] = controller.onPageLoad()(organisationRequest)

                status(result) mustBe SEE_OTHER
                redirectLocation(result) mustBe Some(controllers.errors.routes.UnauthorisedController.missingEnrolment().url)
              }

            }

            "the enrolment is activated" when {

              "an internal ID cannot be retrieved" must {

                "redirect to the generic Unauthorised route" in new Test {

                  val enrolments: Enrolments = Enrolments(Set(Enrolment(
                    key = EnrolmentKeys.ePAYE,
                    identifiers = Seq(
                      EnrolmentIdentifier(EnrolmentKeys.taxOfficeNumber, taxOfficeNumber),
                      EnrolmentIdentifier(EnrolmentKeys.taxOfficeReference, taxOfficeReference)
                    ),
                    state = EnrolmentKeys.activated
                  )))

                  val authAction: AuthenticatedIdentifierAction = authIdentifierAction(
                    new FakeSuccessAuthConnector[credId](data(affinityGroup = Organisation, enrolments = enrolments, internalId = None))
                  )

                  val controller = new Harness(authAction)
                  val result: Future[Result] = controller.onPageLoad()(organisationRequest)

                  status(result) mustBe SEE_OTHER
                  redirectLocation(result) mustBe Some(controllers.errors.routes.UnauthorisedController.onPageLoad().url)
                }

              }

              "an internal ID is retrieved" when {

                "a credentialId is NOT retrieved" must {

                  "redirect to unauthorised page" in new Test {

                    val enrolments: Enrolments = Enrolments(Set(Enrolment(
                      key = EnrolmentKeys.ePAYE,
                      identifiers = Seq(
                        EnrolmentIdentifier(EnrolmentKeys.taxOfficeNumber, taxOfficeNumber),
                        EnrolmentIdentifier(EnrolmentKeys.taxOfficeReference, taxOfficeReference)
                      ),
                      state = EnrolmentKeys.activated
                    )))

                    val authAction: AuthenticatedIdentifierAction = authIdentifierAction(
                      new FakeSuccessAuthConnector[credId](data(affinityGroup = Organisation, enrolments = enrolments, credId = None))
                    )

                    val controller = new Harness(authAction)
                    val result: Future[Result] = controller.onPageLoad()(organisationRequest)

                    status(result) mustBe SEE_OTHER
                    redirectLocation(result) mustBe Some(controllers.errors.routes.UnauthorisedController.onPageLoad().url)

                  }
                }

                "a credentialId is retrieved" must {

                  "the empref cannot be constructed as Identifiers are missing" must {

                    "redirect to the ISE page as this should not happen" in new Test {

                      verifyAudit(MissingEnrolmentAuditEvent(testCredId))

                      val enrolments: Enrolments = Enrolments(Set(Enrolment(
                        key = EnrolmentKeys.ePAYE,
                        identifiers = Seq(
                          EnrolmentIdentifier(EnrolmentKeys.taxOfficeReference, taxOfficeReference)
                        ),
                        state = EnrolmentKeys.activated
                      )))

                      val authAction: AuthenticatedIdentifierAction =
                        authIdentifierAction(new FakeSuccessAuthConnector[credId](data(affinityGroup = Organisation, enrolments = enrolments)))

                      val controller = new Harness(authAction)
                      val result: Future[Result] = controller.onPageLoad()(organisationRequest)

                      status(result) mustBe SEE_OTHER
                      redirectLocation(result) mustBe Some(controllers.errors.routes.UnauthorisedController.onPageLoad().url)
                    }
                  }

                  "the empref is constructed successfully from identifiers" must {

                    "successful cary out request" in new Test {

                      val enrolments: Enrolments = Enrolments(Set(Enrolment(
                        key = EnrolmentKeys.ePAYE,
                        identifiers = Seq(
                          EnrolmentIdentifier(EnrolmentKeys.taxOfficeNumber, taxOfficeNumber),
                          EnrolmentIdentifier(EnrolmentKeys.taxOfficeReference, taxOfficeReference)
                        ),
                        state = EnrolmentKeys.activated
                      )))

                      val authAction: AuthenticatedIdentifierAction =
                        authIdentifierAction(new FakeSuccessAuthConnector[credId](data(affinityGroup = Organisation, enrolments = enrolments)))

                      val controller = new Harness(authAction)
                      val result: Future[Result] = controller.onPageLoad()(organisationRequest)

                      status(result) mustBe OK
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

}

class FakeSuccessAuthConnector[B] @Inject()(response: B) extends AuthConnector {
  override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] =
    Future.successful(response.asInstanceOf[A])
}

class FakeFailingAuthConnector @Inject()(exceptionToReturn: Throwable) extends AuthConnector {
  override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] =
    Future.failed(exceptionToReturn)
}
