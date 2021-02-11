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

import java.time.LocalDateTime

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import mocks.repositories.MockSessionRepository
import models.{FileUploadInfo, NumberOfEntries, TraderContactDetails, UserAnswers}
import pages._
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, status}
import viewmodels.CYASummaryListHelper
import views.data.UnderpaymentSummaryData.{cdUnderpayment, edUnderpayment, ivUnderpayment}
import views.html.CheckYourAnswersView

import scala.concurrent.Future


class CheckYourAnswersControllerSpec extends ControllerSpecBase {

  trait Test extends MockSessionRepository {


    private lazy val checkYourAnswersView: CheckYourAnswersView = app.injector.instanceOf[CheckYourAnswersView]

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
      .set(NumberOfEntriesPage,NumberOfEntries.OneEntry).success.value
      .set(CustomsDutyPage, cdUnderpayment).success.value
      .set(ImportVATPage, ivUnderpayment).success.value
      .set(ExciseDutyPage, edUnderpayment).success.value
      .set(FileUploadPage,Seq(FileUploadInfo(
        "test.pdf",
        "https://bucketName.s3.eu-west-2.amazonaws.com?1235676",
        LocalDateTime.now,
        "396f101dd52e8b2ace0dcf5ed09b1d1f030e608938510ce46e7a5c7a4e775100",
        "application/pdf"))).success.value
      .set(TraderContactDetailsPage,TraderContactDetails(
        "f",
        "fefewfew@gmail.com",
        "07485939292")).success.value
      .set(EnterCustomsProcedureCodePage,"3333333").success.value
      .set(DefermentPage,true).success.value
    )
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    MockedSessionRepository.set(Future.successful(true))

    lazy val controller = new CheckYourAnswersController(authenticatedAction, dataRetrievalAction, dataRequiredAction,
       messagesControllerComponents, checkYourAnswersView)
  }


  "GET /" should {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }
}



