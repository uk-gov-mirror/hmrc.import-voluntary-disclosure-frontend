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
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, redirectLocation, status}
import views.html.{CurrencyAmendmentView, TextAmendmentView, WeightAmendmentView}

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
      weightAmendmentView,
      currencyAmendmentView
    )
    lazy val textAmendmentView = app.injector.instanceOf[TextAmendmentView]
    lazy val weightAmendmentView = app.injector.instanceOf[WeightAmendmentView]
    lazy val currencyAmendmentView = app.injector.instanceOf[CurrencyAmendmentView]
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

  "GET onLoad" should {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad(22)(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML for valid box number" in new Test {
      override val userAnswers: Option[UserAnswers] = Option(UserAnswers("some-cred-id"))
      val result: Future[Result] = controller.onLoad(22)(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

    "throw an exception for invalid box number" in new Test {
      val result: RuntimeException = intercept[RuntimeException](await(controller.onLoad(0)(fakeRequest)))
      assert(result.getMessage.contains("Invalid Box Number"))
    }

    "should redirect the back button to Box Number Controller" in new Test {
      controller.backLink(22) mustBe controllers.routes.BoxNumberController.onLoad()
    }

    "should redirect the back button to Item Number Controller" in new Test {
      controller.backLink(33) mustBe controllers.routes.ItemNumberController.onLoad()
    }

    "should redirect the back button to Box Number Controller when the box number is not in the list" in new Test {
      controller.backLink(0) mustBe controllers.routes.BoxNumberController.onLoad()
    }

  }

  "POST onSubmit" when {

    "payload contains valid data" should {
      "return a SEE OTHER response when correct data is sent" in new Test {
        lazy val result: Future[Result] = controller.onSubmit(22)(fakeRequestGenerator(fifty, sixtyFive))
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.ConfirmReasonDetailController.onLoad().url)
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

      "return Bad Request for form with errors when only key is empty" in new Test {
        val result: Future[Result] = controller.onSubmit(22)(
          fakeRequest.withFormUrlEncodedBody(
            "original" -> fifty, "amended" -> fifty
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

  "routeToView for Text Amendment" when {
    def checkRoute(boxNumber: Int, itemNumber: Int, back: Call, expectedInputClass: Option[String] = Some("govuk-input--width-10")) = {
      s"render the view using the textAmendmentView for box ${boxNumber}" in new Test {
        val result = controller.routeToView(boxNumber, itemNumber, form.apply(boxNumber))(fakeRequest)
        result mustBe textAmendmentView(form.apply(boxNumber), boxNumber, itemNumber, back, inputClass = expectedInputClass)(fakeRequest, messages)
      }
    }

    "called with entry level box 22" should {checkRoute(22, 0, controllers.routes.BoxNumberController.onLoad())}
    "called with entry level box 62" should {checkRoute(62, 0, controllers.routes.BoxNumberController.onLoad())}
    "called with entry level box 63" should {checkRoute(63, 0, controllers.routes.BoxNumberController.onLoad())}
    "called with entry level box 66" should {checkRoute(66, 0, controllers.routes.BoxNumberController.onLoad())}
    "called with entry level box 67" should {checkRoute(67, 0, controllers.routes.BoxNumberController.onLoad())}
    "called with entry level box 68" should {checkRoute(68, 0, controllers.routes.BoxNumberController.onLoad())}

    "called with item level box 33" should {checkRoute(33, 1, controllers.routes.ItemNumberController.onLoad(), Some("govuk-input--width-20"))}
    "called with item level box 34" should {checkRoute(34, 1, controllers.routes.ItemNumberController.onLoad(), Some("govuk-input--width-3"))}
    "called with item level box 36" should {checkRoute(36, 1, controllers.routes.ItemNumberController.onLoad(), Some("govuk-input--width-3"))}
    "called with item level box 37" should {checkRoute(37, 1, controllers.routes.ItemNumberController.onLoad())}
    "called with item level box 39" should {checkRoute(39, 1, controllers.routes.ItemNumberController.onLoad())}
    "called with item level box 41" should {checkRoute(41, 1, controllers.routes.ItemNumberController.onLoad())}

    "called with an invalid box number" should {
        s"route for box 0" in new Test {
          val result = intercept[RuntimeException](
            controller.routeToView(0, 1, form.apply(0))(fakeRequest)
          )
          assert(result.getMessage.contains("Invalid Box Number"))
        }
    }
  }

  "routeToView for Weight Amendment" when {
    def checkRoute(boxNumber: Int, itemNumber: Int, back: Call, expectedInputClass: Option[String] = Some("govuk-input--width-10")) = {
      s"render the view using the weightAmendmentView for box ${boxNumber}" in new Test {
        val result = controller.routeToView(boxNumber, itemNumber, form.apply(boxNumber))(fakeRequest)
        result mustBe weightAmendmentView(form.apply(boxNumber), boxNumber, itemNumber, back, inputClass = expectedInputClass)(fakeRequest, messages)
      }
    }

    "called with item level box 35" should {checkRoute(35, 1, controllers.routes.ItemNumberController.onLoad())}
    "called with item level box 38" should {checkRoute(38, 1, controllers.routes.ItemNumberController.onLoad())}

    "called with an invalid box number" should {
      s"route for box 0" in new Test {
        val result = intercept[RuntimeException](
          controller.routeToView(0, 1, form.apply(0))(fakeRequest)
        )
        assert(result.getMessage.contains("Invalid Box Number"))
      }
    }
  }

  "routeToView for Currency Amendment" when {
    def checkRoute(boxNumber: Int, itemNumber: Int, back: Call, expectedInputClass: Option[String] = Some("govuk-input--width-10")) = {
      s"render the view using the currencyAmendmentView for box ${boxNumber}" in new Test {
        val result = controller.routeToView(boxNumber, itemNumber, form.apply(boxNumber))(fakeRequest)
        result mustBe currencyAmendmentView(form.apply(boxNumber), boxNumber, itemNumber, back, inputClass = expectedInputClass)(fakeRequest, messages)
      }
    }

    "called with item level box 46" should {checkRoute(46, 1, controllers.routes.ItemNumberController.onLoad())}

    "called with an invalid box number" should {
      s"route for box 0" in new Test {
        val result = intercept[RuntimeException](
          controller.routeToView(0, 1, form.apply(0))(fakeRequest)
        )
        assert(result.getMessage.contains("Invalid Box Number"))
      }
    }
  }

}
