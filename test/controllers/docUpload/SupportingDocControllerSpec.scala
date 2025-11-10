/*
 * Copyright 2025 HM Revenue & Customs
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

package controllers.docUpload

import base.ControllerSpecBase
import config.AppConfig
import controllers.actions.FakeDataRetrievalAction
import mocks.config.MockAppConfig
import mocks.repositories.MockSessionRepository
import models.{FileUploadInfo, UserAnswers}
import pages.docUpload.FileUploadPage
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers._
import views.html.docUpload.SupportingDocView

import java.time.Instant
import scala.concurrent.Future

class SupportingDocControllerSpec extends ControllerSpecBase {

  trait Test extends MockSessionRepository {

    val testConfig = appConfig
    lazy val controller = new SupportingDocController(
      authenticatedAction,
      dataRetrievalAction,
      messagesControllerComponents,
      dataRequiredAction,
      view
    )
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)
    val view                             = injector.instanceOf[SupportingDocView]
    val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id"))
  }

  "GET onLoad" should {
    "return 200" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

    "redirect to the summary page when uploaded file already exists" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("some-cred-id")
          .set(
            FileUploadPage,
            Seq(
              FileUploadInfo("file-ref-1", "test.pdf", "downloadUrl", Instant.now(), "checksum", "fileMimeType")
            )
          ).success.value
      )
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.docUpload.routes.UploadAnotherFileController.onLoad().url)

    }
  }

  "Back link" should {
    "return to Underpayment Reason Summary if otherItemEnabled feature switch is on" in new Test {
      override val testConfig: AppConfig = MockAppConfig.appConfig
      controller.backLink(
        userAnswers.get
      ) mustBe controllers.reasons.routes.UnderpaymentReasonSummaryController.onLoad()
    }
  }

}
