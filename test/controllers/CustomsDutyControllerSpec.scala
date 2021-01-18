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
import controllers.Assets.Redirect
import controllers.actions.FakeDataRetrievalAction
import forms.CustomsDutyFormProvider
import mocks.repositories.MockSessionRepository
import models.{UnderpaymentAmount, UnderpaymentType, UserAnswers}
import pages.{CustomsDutyPage, UnderpaymentTypePage}
import play.api.http.Status
import play.api.mvc.{AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, status}
import views.html.CustomsDutyView

import scala.concurrent.Future

class CustomsDutyControllerSpec extends ControllerSpecBase {

  val userAnswersWithUnderpayment: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
    .set(
      UnderpaymentTypePage,
      UnderpaymentType(true, false, false)
    ).success.value
  )

  private def fakeRequestGenerator(original: String, amended: String): FakeRequest[AnyContentAsFormUrlEncoded] =
    fakeRequest.withFormUrlEncodedBody(
      "original" -> original,
      "amended" -> amended
    )

  trait Test extends MockSessionRepository {
    lazy val controller = new CustomsDutyController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      mockSessionRepository,
      messagesControllerComponents,
      customsDutyView,
      form
    )
    private lazy val customsDutyView = app.injector.instanceOf[CustomsDutyView]
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)
    val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id"))
    val formProvider: CustomsDutyFormProvider = injector.instanceOf[CustomsDutyFormProvider]
    MockedSessionRepository.set(Future.successful(true))
    val form: CustomsDutyFormProvider = formProvider
  }

  "GET /" when {

    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      override val userAnswers: Option[UserAnswers] = Option(
        UserAnswers("some-cred-id").set(
          CustomsDutyPage,
          UnderpaymentAmount(BigDecimal("40"), BigDecimal(40))
        ).success.value
      )
      val result: Future[Result] = controller.onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

  }

  "POST /" when {

    "Underpayment type has Import VAT selected" should {
      "redirect to Import VAT page" in new Test {
        controller.redirect(Some(UnderpaymentType(true, true, false))) mustBe Redirect(controllers.routes.ImportVATController.onLoad())
      }
    }

    "Underpayment type has Excise Duty selected" should {
      "redirect to Excise Duty page" in new Test {
        controller.redirect(Some(UnderpaymentType(true, false, true))) mustBe Redirect(controllers.routes.ExciseDutyController.onLoad())
      }
    }

    "Underpayment type doesn't have Import VAT or Excise Duty selected" should {
      "redirect to Summary page" in new Test {
        controller.redirect(Some(UnderpaymentType(true, false, false))) mustBe Redirect(controllers.routes.CustomsDutyController.onLoad())
      }
    }

    "payload contains valid data" should {

      "return a SEE OTHER response when correct data is sent" in new Test {
        override val userAnswers: Option[UserAnswers] = userAnswersWithUnderpayment
        lazy val result: Future[Result] = controller.onSubmit(fakeRequestGenerator("50", "60"))
        status(result) mustBe Status.SEE_OTHER
      }

      "update the UserAnswers in session" in new Test {
        override val userAnswers: Option[UserAnswers] = userAnswersWithUnderpayment
        await(controller.onSubmit(fakeRequestGenerator(original = "40", amended = "50")))
        MockedSessionRepository.verifyCalls()
      }

    }

    "payload contains invalid data" should {

      "return BAD REQUEST when original amount is exceeded" in new Test {
        override val userAnswers: Option[UserAnswers] = userAnswersWithUnderpayment
        val result: Future[Result] = controller.onSubmit(fakeRequestGenerator("10000000000", "60"))
        status(result) mustBe Status.BAD_REQUEST
      }

      "return BAD REQUEST" in new Test {
        val result: Future[Result] = controller.onSubmit(fakeRequest)
        status(result) mustBe Status.BAD_REQUEST
      }

    }

  }

}
