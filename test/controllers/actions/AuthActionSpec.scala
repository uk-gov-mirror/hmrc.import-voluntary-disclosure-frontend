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

package controllers.actions

import base.SpecBase
import mocks.connectors.MockAuthConnector
import play.api.http.Status
import play.api.mvc.{Action, AnyContent, BodyParsers, Results}
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core._
import views.html.errors.UnauthorisedView

import scala.concurrent.Future

class AuthActionSpec extends SpecBase {

  trait Test extends MockAuthConnector {

    class Harness(authAction: IdentifierAction) {
      def onPageLoad(): Action[AnyContent] = authAction { _ => Results.Ok }
    }

    lazy val bodyParsers: BodyParsers.Default = injector.instanceOf[BodyParsers.Default]

    lazy val unauthorisedView: UnauthorisedView = injector.instanceOf[views.html.errors.UnauthorisedView]

    lazy val action = new AuthenticatedIdentifierAction(mockAuthConnector, unauthorisedView, appConfig, bodyParsers, messagesApi)

    val target = new Harness(action)
  }

  "Auth Action" when {

    "user is not logged in" must {

      "redirect to sign-in" in new Test {
        MockedAuthConnector.authorise(Future.failed(SessionRecordNotFound()))
        private val response = target.onPageLoad()(fakeRequest)
        status(response) mustBe Status.SEE_OTHER
      }
    }


    "user is logged in and has an external ID" must {

      "execute the action block" in new Test {
        MockedAuthConnector.authorise(Future.successful(Some("a")))
        private val response = target.onPageLoad()(fakeRequest)
        status(response) mustBe Status.OK
      }
    }

    "user is logged in and has no external ID" must {

      "receive an authorised response" in new Test {
        MockedAuthConnector.authorise(Future.successful(None))
        private val response = target.onPageLoad()(fakeRequest)
        status(response) mustBe Status.UNAUTHORIZED
      }
    }

    "authorisation exception occurs" must {

      "receive an authorised response" in new Test {
        MockedAuthConnector.authorise(Future.failed(InternalError()))
        private val response = target.onPageLoad()(fakeRequest)
        status(response) mustBe Status.UNAUTHORIZED
      }
    }
  }

}
