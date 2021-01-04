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
import models.NumberOfEntries
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers._
import views.html.NumberOfEntriesView

import scala.concurrent.Future

class NumberOfEntriesControllerSpec extends ControllerSpecBase {

  private lazy val numberOfEntriesPage: NumberOfEntriesView = app.injector.instanceOf[NumberOfEntriesView]

  private lazy val dataRetrievalAction = new FakeDataRetrievalAction(None)

  val formProvider = injector.instanceOf[NumberOfEntriesFormProvider]
  val form = formProvider

  private lazy val controller = new NumberOfEntriesController(authenticatedAction, dataRetrievalAction,
    sessionRepository, appConfig, messagesControllerComponents, form, numberOfEntriesPage)

  "GET /" should {
    "return 200" in {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "POST oneEntry" should {
    "return a OK, write UserAnswers and redirect to next page" in {

      val request = fakeRequest.withFormUrlEncodedBody(
        "value" -> NumberOfEntries.OneEntry.toString
      )

      val result: Future[Result] = controller.onSubmit(request)
      status(result) mustBe Status.SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.NumberOfEntriesController.onLoad().url)
    }

    "return a BAD REQUEST and errors when invalid data is submitted" in {

      val result: Future[Result] = controller.onSubmit(fakeRequest)
      status(result) mustBe Status.BAD_REQUEST

    }
  }

  "POST moreThanOneEntry" should {
    "return a OK, write UserAnswers and redirect to next page" in {

      val request = fakeRequest.withFormUrlEncodedBody(
        "value" -> NumberOfEntries.MoreThanOneEntry.toString
      )

      val result: Future[Result] = controller.onSubmit(request)
      status(result) mustBe Status.SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.NumberOfEntriesController.onLoad().url)
    }

    "return a BAD REQUEST and errors when invalid data is submitted" in {

      val result: Future[Result] = controller.onSubmit(fakeRequest)
      status(result) mustBe Status.BAD_REQUEST

    }
  }

}
