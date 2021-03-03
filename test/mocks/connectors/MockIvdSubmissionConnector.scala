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

package mocks.connectors

import connectors.IvdSubmissionConnector
import models.{EoriDetails, ErrorModel, IvdSubmission, SubmissionResponse}
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockIvdSubmissionConnector extends MockFactory {

  val mockIVDSubmissionConnector: IvdSubmissionConnector = mock[IvdSubmissionConnector]

  type EoriDetailsResponse = Either[ErrorModel, EoriDetails]

  def setupMockGetEoriDetails(response: Either[ErrorModel, EoriDetails]): Unit = {
    (mockIVDSubmissionConnector.getEoriDetails(_: String)(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *)
      .returns(Future.successful(response))
  }

  def setupMockPostSubmission(response: Either[ErrorModel, SubmissionResponse]): Unit = {
    (mockIVDSubmissionConnector.postSubmission(_: IvdSubmission)(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *)
      .returns(Future.successful(response))
  }

}
