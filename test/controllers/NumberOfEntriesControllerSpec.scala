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
import forms.NumberOfEntriesFormProvider
import mocks.repositories.MockSessionRepository
import models.UserType.Representative
import models.{NumberOfEntries, UserAnswers}
import pages.{ImporterEORIExistsPage, ImporterEORINumberPage, NumberOfEntriesPage, UserTypePage}
import play.api.http.Status
import play.api.mvc.{AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.FlowService
import views.html.NumberOfEntriesView

import scala.concurrent.Future

class NumberOfEntriesControllerSpec extends ControllerSpecBase {

  trait Test extends MockSessionRepository {
    private lazy val numberOfEntriesPage: NumberOfEntriesView = app.injector.instanceOf[NumberOfEntriesView]

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
      .set(UserTypePage, Representative).success.value
      .set(ImporterEORIExistsPage, false).success.value)
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    val formProvider: NumberOfEntriesFormProvider = injector.instanceOf[NumberOfEntriesFormProvider]
    val form: NumberOfEntriesFormProvider = formProvider
    val flowService: FlowService = app.injector.instanceOf[FlowService]

    MockedSessionRepository.set(Future.successful(true))

    lazy val controller = new NumberOfEntriesController(authenticatedAction, dataRetrievalAction, dataRequiredAction,
      mockSessionRepository, appConfig, messagesControllerComponents, flowService, form, numberOfEntriesPage)
  }

  "GET /" should {
    "return OK" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
        .set(UserTypePage, Representative).success.value
        .set(ImporterEORIExistsPage, true).success.value
        .set(ImporterEORINumberPage, "GB345834921000").success.value)
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id").set(NumberOfEntriesPage, NumberOfEntries.OneEntry).success.value)
      val result: Future[Result] = controller.onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "POST oneEntry" when {
    "payload contains valid data" should {

      "return a SEE OTHER response" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> NumberOfEntries.OneEntry.toString)
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "return the correct location header" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> NumberOfEntries.OneEntry.toString)
        lazy val result: Future[Result] = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(controllers.routes.EntryDetailsController.onLoad().url)
      }

      "update the UserAnswers in session" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody("value" -> NumberOfEntries.OneEntry.toString)
        await(controller.onSubmit(request))
        verifyCalls()
      }
    }

    "payload contains invalid data" should {
      "return a BAD REQUEST" in new Test {
        val result: Future[Result] = controller.onSubmit(fakeRequest)
        status(result) mustBe Status.BAD_REQUEST
      }
    }
  }

  "POST moreThanOneEntry" when {
    "payload contains valid data" should {

      "return a SEE OTHER response" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> NumberOfEntries.MoreThanOneEntry.toString)
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "return the correct location header" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> NumberOfEntries.MoreThanOneEntry.toString)
        lazy val result: Future[Result] = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(controllers.routes.NumberOfEntriesController.onLoad().url)
      }

      "update the UserAnswers in session" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody("value" -> NumberOfEntries.MoreThanOneEntry.toString)
        await(controller.onSubmit(request))
        verifyCalls()
      }

    }

    "payload contains invalid data" should {
      "return a BAD REQUEST" in new Test {
        val result: Future[Result] = controller.onSubmit(fakeRequest)
        status(result) mustBe Status.BAD_REQUEST
      }
    }
  }

}
