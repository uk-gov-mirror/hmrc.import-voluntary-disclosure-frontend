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

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import mocks.repositories.MockSessionRepository
import models.{UnderpaymentReason, UnderpaymentReasonValue, UserAnswers}
import pages._
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, redirectLocation, status}
import views.data.ConfirmReasonData
import views.html.ConfirmReasonDetailView

import scala.concurrent.Future


class ConfirmReasonDetailControllerSpec extends ControllerSpecBase {

  trait Test extends MockSessionRepository {
    private lazy val view: ConfirmReasonDetailView = app.injector.instanceOf[ConfirmReasonDetailView]

    MockedSessionRepository.set(Future.successful(true))

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId")
      .set(UnderpaymentReasonBoxNumberPage, 22).success.value
      .set(UnderpaymentReasonAmendmentPage, UnderpaymentReasonValue("1806321000", "2204109400X411")).success.value
    )
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    lazy val controller = new ConfirmReasonDetailController(authenticatedAction, dataRetrievalAction, dataRequiredAction,
      mockSessionRepository, messagesControllerComponents, view)
  }

  "GET onLoad " should {

    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

    "produce correct summary list" in new Test {
      val result = controller.summaryList(UserAnswers("some-cred-id")
        .set(UnderpaymentReasonBoxNumberPage, 33).success.value
        .set(UnderpaymentReasonItemNumberPage, 1).success.value
        .set(UnderpaymentReasonAmendmentPage, UnderpaymentReasonValue("1806321000", "2204109400X411")).success.value,
        boxNumber = 33
      )

      val expectedResult = Some(ConfirmReasonData.reasons(33, Some(1), "1806321000", "2204109400X411"))
      result mustBe expectedResult
    }
  }

  "GET onSubmit" when {

    "payload contains valid data" should {

      "return a SEE OTHER entry level response when correct data is sent" in new Test {
        lazy val result: Future[Result] = controller.onSubmit()(fakeRequest)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.UnderpaymentReasonSummaryController.onLoad().url)
      }

      "return a SEE OTHER item level response when correct data is sent" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId")
            .set(UnderpaymentReasonBoxNumberPage, 33).success.value
            .set(UnderpaymentReasonItemNumberPage, 1).success.value
            .set(UnderpaymentReasonAmendmentPage, UnderpaymentReasonValue("1806321000", "2204109400X411")).success.value
        )
        lazy val result: Future[Result] = controller.onSubmit()(fakeRequest)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.UnderpaymentReasonSummaryController.onLoad().url)
        verifyCalls()
      }

      "return a SEE OTHER when existing reason are present" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId")
            .set(UnderpaymentReasonBoxNumberPage, 33).success.value
            .set(UnderpaymentReasonItemNumberPage, 1).success.value
            .set(UnderpaymentReasonAmendmentPage, UnderpaymentReasonValue("1806321000", "2204109400X411")).success.value
            .set(UnderpaymentReasonsPage, Seq(UnderpaymentReason(22, 0, "GBP871.12", "EUR2908946"))).success.value
        )
        lazy val result: Future[Result] = controller.onSubmit()(fakeRequest)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.UnderpaymentReasonSummaryController.onLoad().url)
        verifyCalls()
      }

      "payload contains no data" should {
        "produce no summary list" in new Test {
          val result = controller.summaryList(UserAnswers("some-cred-id"), 22)
          result mustBe None
        }
      }
    }
  }
}
