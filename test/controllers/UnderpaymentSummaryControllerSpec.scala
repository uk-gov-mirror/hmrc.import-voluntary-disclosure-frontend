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
import messages.UnderpaymentSummaryMessages
import models.UserAnswers
import pages._
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, status}
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import views.data.UnderpaymentSummaryData._
import views.html.UnderpaymentSummaryView

import scala.concurrent.Future


class UnderpaymentSummaryControllerSpec extends ControllerSpecBase {

  trait Test {
    private lazy val view: UnderpaymentSummaryView = app.injector.instanceOf[UnderpaymentSummaryView]

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    lazy val controller = new UnderpaymentSummaryController(authenticatedAction, dataRetrievalAction, dataRequiredAction,
      messagesControllerComponents, view)
  }

  "GET /" should {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
        .set(CustomsDutyPage, cdUnderpayment).success.value
        .set(ImportVATPage, ivUnderpayment).success.value
        .set(ExciseDutyPage, edUnderpayment).success.value
      )
      val result: Future[Result] = controller.onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

    "produce correct summary list for customs duty" in new Test {
      val result: SummaryList = controller.summaryList(
        cdUnderpayment,
        UnderpaymentSummaryMessages.customsDutyTitle,
        controllers.routes.UnderpaymentSummaryController.onLoad()
      )

      result mustBe customsDuty.get
    }
  }

}



