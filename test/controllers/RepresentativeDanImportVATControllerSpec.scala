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
import forms.RepresentativeDanFormProvider
import mocks.repositories.MockSessionRepository
import models.SelectedDutyTypes.Vat
import models.UserAnswers
import pages.{AdditionalDefermentNumberPage, AdditionalDefermentTypePage}
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers._
import views.html.RepresentativeDanImportVATView

import scala.concurrent.Future

class RepresentativeDanImportVATControllerSpec extends ControllerSpecBase {

  def buildForm(accountNumber: Option[String] = Some("1234567"),
                danType: Option[String] = Some("A")): Seq[(String, String)] =
    (
      (accountNumber.map(_ => "accountNumber" -> accountNumber.get) ++
        danType.map(_ => "value" -> danType.get)).toSeq
      )

  trait Test extends MockSessionRepository {
    private lazy val representativeDanView: RepresentativeDanImportVATView = app.injector.instanceOf[RepresentativeDanImportVATView]

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    val formProvider: RepresentativeDanFormProvider = injector.instanceOf[RepresentativeDanFormProvider]
    val form: RepresentativeDanFormProvider = formProvider

    MockedSessionRepository.set(Future.successful(true))

    lazy val controller = new RepresentativeDanImportVATController(authenticatedAction, dataRetrievalAction, dataRequiredAction,
      mockSessionRepository, messagesControllerComponents, representativeDanView, form)
  }

  "GET Representative Dan Import VAT page" should {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      override val userAnswers: Option[UserAnswers] =
        Some(
          UserAnswers("some-cred-id")
            .set(AdditionalDefermentTypePage, "A").success.value
            .set(AdditionalDefermentNumberPage, "1234567").success.value
        )
      val result: Future[Result] = controller.onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

    "the backLink functionality is called" should {
      "redirect to the representative duty page" in new Test {
        controller.backLink mustBe controllers.routes.RepresentativeDanDutyController.onLoad()
      }

    }
  }

  "POST Representative Dan Import VAT page" when {
    "payload contains valid data" should {

      "return a SEE OTHER response and redirect to correct location when dan type is A" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody(buildForm(accountNumber = Some("1234567"), danType = Some("A")): _*)
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.CheckYourAnswersController.onLoad().url)
      }

      "return a SEE OTHER response and redirect to correct location when dan type is B" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody(buildForm(accountNumber = Some("1234567"), danType = Some("B")): _*)
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.UploadAuthorityController.onLoad(Vat, "1234567").url)
      }

      "return a SEE OTHER response and redirect to correct location when dan type is C" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody(buildForm(accountNumber = Some("1234567"), danType = Some("C")): _*)
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.CheckYourAnswersController.onLoad().url)
      }

      "update the UserAnswers in session" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody(buildForm(): _*)
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
