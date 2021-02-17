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
import forms.UnderpaymentReasonAmendmentFormProvider
import mocks.repositories.MockSessionRepository
import models.{UnderpaymentReasonValue, UserAnswers}
import pages.{UnderpaymentReasonAmendmentPage, UnderpaymentReasonItemNumberPage}
import play.api.http.Status
import play.api.mvc.{AnyContentAsFormUrlEncoded, Call, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, status}
import views.html.TextAmendmentView

import scala.concurrent.Future

class UnderpaymentReasonAmendmentControllerSpec extends ControllerSpecBase {

  private final lazy val fifty: String = "GBP50"
  private final lazy val sixtyFive: String = "GBP65.01"

  private def fakeRequestGenerator(original: String, amended: String): FakeRequest[AnyContentAsFormUrlEncoded] =
    fakeRequest.withFormUrlEncodedBody(
      "original" -> original,
      "amended" -> amended
    )

  trait Test extends MockSessionRepository {
    lazy val controller = new UnderpaymentReasonAmendmentController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      mockSessionRepository,
      messagesControllerComponents,
      form,
      textAmendmentView,
      appConfig
    )
    private lazy val textAmendmentView = app.injector.instanceOf[TextAmendmentView]
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)
    val userAnswers: Option[UserAnswers] = Some(
      UserAnswers("some-cred-id")
        .set(UnderpaymentReasonItemNumberPage, 5).success.value
        .set(UnderpaymentReasonAmendmentPage, UnderpaymentReasonValue("5", "15")).success.value
    )
    val formProvider: UnderpaymentReasonAmendmentFormProvider = injector.instanceOf[UnderpaymentReasonAmendmentFormProvider]
    MockedSessionRepository.set(Future.successful(true))
    val form: UnderpaymentReasonAmendmentFormProvider = formProvider
  }

  "GET /" should {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad(22)(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML for valid box number" in new Test {
      override val userAnswers: Option[UserAnswers] = Option(UserAnswers("some-cred-id"))
      val result: Future[Result] = controller.onLoad(33)(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

    "return HTML for 62 valid box number" in new Test {
      val result: Future[Result] = controller.onLoad(62)(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

    "throw an exception for invalid box number" in new Test {
      val result: RuntimeException = intercept[RuntimeException](await(controller.onLoad(0)(fakeRequest)))
      assert(result.getMessage.contains("Invalid Box Number"))
    }

    "should redirect the back button to Box Number Controller" in new Test {
      controller.backLink(22) mustBe Call("GET", controllers.routes.BoxNumberController.onLoad().toString)
    }

    "should redirect the back button to Item Number Controller" in new Test {
      controller.backLink(33) mustBe Call("GET", controllers.routes.ItemNumberController.onLoad().toString)
    }

    "should redirect the back button to Box Number Controller when the box number is not in the list" in new Test {
      controller.backLink(0) mustBe Call("GET", controllers.routes.BoxNumberController.onLoad().toString)
    }

  }

  "POST /" when {

    "payload contains valid data" should {

      "return a SEE OTHER response when correct data is sent" in new Test {
        lazy val result: Future[Result] = controller.onSubmit(22)(fakeRequestGenerator(fifty, sixtyFive))
        status(result) mustBe Status.SEE_OTHER
      }

      "update the UserAnswers in session" in new Test {
        await(controller.onSubmit(22)(fakeRequestGenerator(fifty, sixtyFive)))
        verifyCalls()
      }

    }

    "payload contains invalid data" should {

      "return Ok form with errors when invalid data is sent" in new Test {
        val result: Future[Result] = controller.onSubmit(62)(fakeRequest)
        status(result) mustBe Status.BAD_REQUEST
      }

      "return Bad Request for form with errors when only original value is sent" in new Test {
        val result: Future[Result] = controller.onSubmit(22)(
          fakeRequest.withFormUrlEncodedBody(
            "original" -> fifty
          )
        )
        status(result) mustBe Status.BAD_REQUEST
      }

      "return RuntimeException for invalid box number" in new Test {
        val result: RuntimeException = intercept[RuntimeException](await(controller.onSubmit(0)(fakeRequest)))
        assert(result.getMessage.contains("Invalid Box Number"))
      }

    }

  }

}