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

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import mocks.repositories.MockSessionRepository
import models.UserAnswers
import models.underpayments.UnderpaymentAmount
import pages.underpayments.{UnderpaymentDetailsPage, UnderpaymentTypePage}
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, status}
import views.data.ConfirmReasonData
import views.data.underpayments.UnderpaymentDetailSummaryData
import views.html.underpayments.UnderpaymentDetailSummaryView

import scala.concurrent.Future

class UnderpaymentDetailSummaryControllerSpec extends ControllerSpecBase {

  trait Test extends MockSessionRepository {
    private lazy val underpaymentDetailSummaryView: UnderpaymentDetailSummaryView = app.injector.instanceOf[UnderpaymentDetailSummaryView]

    val userAnswers: Option[UserAnswers] = Some(
      UserAnswers("credId")
      .set(UnderpaymentTypePage, "B00").success.value
      .set(UnderpaymentDetailsPage, UnderpaymentAmount(0,1)).success.value
    )

    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    MockedSessionRepository.set(Future.successful(true))

    lazy val controller = new UnderpaymentDetailSummaryController(authenticatedAction, dataRetrievalAction, dataRequiredAction,
      mockSessionRepository, messagesControllerComponents, underpaymentDetailSummaryView)
  }

  "GET onLoad " should {

    "return OK" in new Test {
     override val userAnswers = Some(UserAnswers("some-cred-id"))
      val result: Future[Result] = controller.onLoad("B00")(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      val result: Future[Result] = controller.onLoad("B00")(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

    "produce correct summary list" in new Test {
      controller.summaryList("B00", UnderpaymentAmount(0,1)) mustBe UnderpaymentDetailSummaryData.underpaymentDetailSummaryList
    }
  }

}
