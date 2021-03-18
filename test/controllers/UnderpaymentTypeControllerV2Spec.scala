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
import forms.UnderpaymentTypeFormProviderV2
import mocks.repositories.MockSessionRepository
import models.UserAnswers
import pages.UnderpaymentTypePageV2
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers._
import views.html.UnderpaymentTypeViewV2

import scala.concurrent.Future

class UnderpaymentTypeControllerV2Spec extends ControllerSpecBase {

  trait Test extends MockSessionRepository {
    private lazy val underpaymentTypeView: UnderpaymentTypeViewV2 = app.injector.instanceOf[UnderpaymentTypeViewV2]

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    val formProvider: UnderpaymentTypeFormProviderV2 = injector.instanceOf[UnderpaymentTypeFormProviderV2]
    val form: UnderpaymentTypeFormProviderV2 = formProvider

    MockedSessionRepository.set(Future.successful(true))

    lazy val controller = new UnderpaymentTypeControllerV2(authenticatedAction, dataRetrievalAction, dataRequiredAction,
      mockSessionRepository, messagesControllerComponents, underpaymentTypeView, form)
  }

  "GET onLoad" should {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("credId").set(UnderpaymentTypePageV2, "A00").success.value
      )
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "POST onSubmit" when {

    "payload contains valid data" should {

      "return a SEE OTHER entry level response when correct data is sent" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))
        lazy val result: Future[Result] = controller.onSubmit()(
          fakeRequest.withFormUrlEncodedBody("value" -> "A00")
        )
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.UnderpaymentTypeControllerV2.onLoad().url)
      }

      "update the UserAnswers in session" in new Test {
        await(controller.onSubmit()(fakeRequest.withFormUrlEncodedBody("value" -> "A00")))
        verifyCalls()
      }

    }

    "payload contains invalid data" should {
      "return BAD REQUEST when no value is sent" in new Test {
        val result: Future[Result] = controller.onSubmit()(fakeRequest.withFormUrlEncodedBody("" -> ""))
        status(result) mustBe Status.BAD_REQUEST
      }
    }

  }

}
