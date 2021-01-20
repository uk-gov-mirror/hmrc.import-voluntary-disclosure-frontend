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
import forms.UnderpaymentTypeFormProvider
import mocks.repositories.MockSessionRepository
import models.{EntryDetails, UnderpaymentType, UserAnswers}
import pages.UnderpaymentTypePage
import play.api.http.Status
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.mvc.{Call, Result}
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, redirectLocation, status}
import views.html.UnderpaymentTypeView

import java.time.LocalDate
import scala.concurrent.Future

class UnderpaymentTypeControllerSpec extends ControllerSpecBase {

  private def fakeRequestGenerator(customsDuty: String = "false",
                                   importVAT: String = "false",
                                   exciseDuty: String = "false") =
    fakeRequest.withFormUrlEncodedBody(
      "customsDuty" -> customsDuty,
      "importVAT" -> importVAT,
      "exciseDuty" -> exciseDuty
    )

  trait Test extends MockSessionRepository {
    lazy val controller = new UnderpaymentTypeController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      mockSessionRepository,
      messagesControllerComponents,
      underpaymentTypeView,
      form,
      appConfig
    )
    private lazy val underpaymentTypeView = app.injector.instanceOf[UnderpaymentTypeView]
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)
    val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id"))
    val formProvider: UnderpaymentTypeFormProvider = injector.instanceOf[UnderpaymentTypeFormProvider]
    MockedSessionRepository.set(Future.successful(true))
    val form: UnderpaymentTypeFormProvider = formProvider
  }

  "GET /" should {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      override val userAnswers: Option[UserAnswers] = Option(
        UserAnswers("some-cred-id").set(
          UnderpaymentTypePage,
          UnderpaymentType(customsDuty = false, importVAT = false, exciseDuty = false)
        ).success.value
      )
      val result: Future[Result] = controller.onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

    "should redirect the back button to Customs Procedure Code page" in new Test {
      val dateBeforeExit: LocalDate = LocalDate of (2020, 1, 1)
      val entryDetails: EntryDetails = EntryDetails("123", "123456A", dateBeforeExit)
      controller.backLink mustBe Call("GET", controllers.routes.CustomsProcedureCodeController.onLoad().toString)
    }
  }

  "POST /" when {

    "payload contains valid data" should {
      "return a SEE OTHER response to to Customs Duty page when all boxes are ticked" in new Test {
        lazy val result: Future[Result] = controller.onSubmit(
          fakeRequestGenerator(customsDuty = "true", importVAT = "true", exciseDuty = "true")
        )
        redirectLocation(result) mustBe Some(controllers.routes.CustomsDutyController.onLoad().url) // Customs Duty
      }

      "return a SEE OTHER response to to Customs Duty page when Customs Duty is selected" in new Test {
        lazy val result: Future[Result] = controller.onSubmit(
          fakeRequestGenerator(customsDuty = "true")
        )
        redirectLocation(result) mustBe Some(controllers.routes.CustomsDutyController.onLoad().url) // Customs Duty
      }

      "return a SEE OTHER response to to Import VAT page when Import VAT is selected" in new Test {
        lazy val result: Future[Result] = controller.onSubmit(fakeRequestGenerator(importVAT = "true"))
        redirectLocation(result) mustBe Some(controllers.routes.ImportVATController.onLoad().url)
      }

      "return a SEE OTHER response to to Excise Duty page when Excise Duty is selected" in new Test {
        lazy val result: Future[Result] = controller.onSubmit(fakeRequestGenerator(exciseDuty = "true"))
        redirectLocation(result) mustBe Some(controllers.routes.ExciseDutyController.onLoad().url) // Excise Duty
      }

      "update the UserAnswers in session" in new Test {
        await(controller.onSubmit(fakeRequestGenerator(customsDuty = "true")))
        MockedSessionRepository.verifyCalls()
      }


    }

    "payload contains invalid data" should {
      "return BAD REQUEST" in new Test {
        val result: Future[Result] = controller.onSubmit(fakeRequest)
        status(result) mustBe Status.BAD_REQUEST
      }
    }

  }

}
