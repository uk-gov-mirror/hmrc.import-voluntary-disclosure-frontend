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
import models.UserAnswers
import models.requests.{IdentifierRequest, OptionalDataRequest}
import repositories.SessionRepository

import scala.concurrent.{ExecutionContext, Future}

class DataRetrievalActionSpec extends SpecBase {

  trait Test {

    class Harness(sessionRepository: SessionRepository) extends DataRetrievalActionImpl(sessionRepository) {
      def callTransform[A](request: IdentifierRequest[A]): Future[OptionalDataRequest[A]] = transform(request)
    }

    val mockSessionRepository: SessionRepository = mock[SessionRepository]
    val action = new Harness(mockSessionRepository)

    val request: IdentifierRequest[_] = IdentifierRequest(fakeRequest, "some cred ID")
  }

  "Data Retrieval Action" when {

    "there is no data in the cache" must {

      "set userAnswers to 'None' in the request" in new Test {

        (mockSessionRepository.get(_: String)(_: ExecutionContext))
          .expects(*, *)
          .returns(Future.successful(None))

        private val futureResult = action.callTransform(request)

        whenReady(futureResult) { result =>
          result.userAnswers mustBe None
        }
      }
    }

    "there is data in the cache" must {

      "build a userAnswers object and add it to the request" in new Test {

        private val answers = Some(new UserAnswers("id"))
        (mockSessionRepository.get(_: String)(_: ExecutionContext))
          .expects(*, *)
          .returns(Future.successful(answers))

        private val futureResult = action.callTransform(request)

        whenReady(futureResult) { result =>
          result.userAnswers mustBe answers
        }
      }
    }
  }
}
