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
import forms.EntryDetailsFormProvider
import mocks.config.MockAppConfig
import mocks.repositories.MockSessionRepository
import models.{EntryDetails, UserAnswers}
import pages.EntryDetailsPage
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers._
import views.html.EntryDetailsView
import java.time.LocalDate

import scala.concurrent.Future

class EntryDetailsControllerSpec extends ControllerSpecBase {

  def buildForm(epu: Option[String] = Some("123"),
                    entryNumber: Option[String] = Some("123456Q"),
                    day: Option[String] = Some("31"),
                    month: Option[String] = Some("12"),
                    year: Option[String] = Some("2020")): Seq[(String, String)] =
    (
      (epu.map(_ => "epu" -> epu.get) ++
        entryNumber.map(_ => "entryNumber" -> entryNumber.get) ++
        day.map(_ => "entryDate.day" -> day.get) ++
        month.map(_ => "entryDate.month" -> month.get) ++
        year.map(_ => "entryDate.year" -> year.get)).toSeq
      )

  trait Test extends MockSessionRepository {
    private lazy val entryDetailsView: EntryDetailsView = app.injector.instanceOf[EntryDetailsView]

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    val formProvider: EntryDetailsFormProvider = injector.instanceOf[EntryDetailsFormProvider]
    val form: EntryDetailsFormProvider = formProvider

    MockedSessionRepository.set(Future.successful(true))

    lazy val controller = new EntryDetailsController(authenticatedAction, dataRetrievalAction, dataRequiredAction,
      mockSessionRepository, MockAppConfig, messagesControllerComponents, form, entryDetailsView)
  }

  "GET /" should {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      override val userAnswers: Option[UserAnswers] =
        Some(UserAnswers("some-cred-id").set(EntryDetailsPage, EntryDetails("123","123456Q",LocalDate.now)).success.value)
      val result: Future[Result] = controller.onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "POST /" when {
    "payload contains valid data" should {

      "return a SEE OTHER response and redirect to correct location for date BEFORE EU exit" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody(buildForm(day=Some("31"),month=Some("12"),year=Some("2020")):_*)
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.AcceptanceDateController.onLoad().url)
      }

      "return a SEE OTHER response and redirect to correct location for date AFTER EU exit" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody(buildForm(day=Some("02"),month=Some("01"),year=Some("2021")):_*)
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.UnderpaymentTypeController.onLoad().url)
      }

      "update the UserAnswers in session" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody(buildForm():_*)
        await(controller.onSubmit(request))
        MockedSessionRepository.verifyCalls()
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
