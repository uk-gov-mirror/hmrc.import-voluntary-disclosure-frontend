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

import java.time.{LocalDate, LocalDateTime}
import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import mocks.connectors.MockIvdSubmissionConnector
import mocks.repositories.MockSessionRepository
import models._
import pages._
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, status}
import views.data.UnderpaymentSummaryData.{cdUnderpayment, edUnderpayment, ivUnderpayment}
import views.html.{CheckYourAnswersView, ConfirmationView}

import scala.concurrent.Future


class CheckYourAnswersControllerSpec extends ControllerSpecBase {

  trait Test extends MockSessionRepository with MockIvdSubmissionConnector {

    private def setupConnectorMock(response: Either[ErrorModel, SubmissionResponse]) = {
      setupMockPostSubmission(response)
    }
    private lazy val checkYourAnswersView: CheckYourAnswersView = app.injector.instanceOf[CheckYourAnswersView]
    private lazy val confirmationView: ConfirmationView = app.injector.instanceOf[ConfirmationView]

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
      .set(UserTypePage, UserType.Importer).success.value
      .set(KnownEoriDetails, EoriDetails(
        "GB000000001",
        "Importers Inc.",
        ContactAddress("street", None, "city", Some("postcode"), "country code"))
      ).success.value
      .set(NumberOfEntriesPage,NumberOfEntries.OneEntry).success.value
      .set(EntryDetailsPage, EntryDetails("123","123456Q",LocalDate.of(2020, 12, 1))).success.value
      .set(AcceptanceDatePage,true).success.value
      .set(CustomsDutyPage, cdUnderpayment).success.value
      .set(ImportVATPage, ivUnderpayment).success.value
      .set(ExciseDutyPage, edUnderpayment).success.value
      .set(FileUploadPage,Seq(FileUploadInfo(
        "test.pdf",
        "https://bucketName.s3.eu-west-2.amazonaws.com?1235676",
        LocalDateTime.now,
        "396f101dd52e8b2ace0dcf5ed09b1d1f030e608938510ce46e7a5c7a4e775100",
        "application/pdf"))).success.value
      .set(DeclarantContactDetailsPage,ContactDetails(
        "f",
        "fefewfew@gmail.com",
        "07485939292")).success.value
      .set(TraderAddressPage,ContactAddress(
        "street", None, "city", Some("postcode"), "country code")).success.value
      .set(EnterCustomsProcedureCodePage,"3333333").success.value
      .set(DefermentPage,true).success.value
      .set(MoreInformationPage, "some text").success.value
      .set(UnderpaymentReasonsPage, Seq(UnderpaymentReason(1, 0, "GBP100", "GBP200"))).success.value
    )

    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    MockedSessionRepository.set(Future.successful(true))

    lazy val connectorMock: Either[ErrorModel, SubmissionResponse] = Right(SubmissionResponse("123"))
    lazy val controller = {
      setupConnectorMock(connectorMock)
      new CheckYourAnswersController(authenticatedAction, dataRetrievalAction, dataRequiredAction,
        messagesControllerComponents, mockIVDSubmissionConnector, checkYourAnswersView, confirmationView, ec)
    }
  }


  "GET onLoad" should {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "GET onSubmit" should {

    "return Redirect" in new Test {
      val result: Future[Result] = controller.onSubmit()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return Internal Server error is submission fails" in new Test {
      override lazy val connectorMock = Left(ErrorModel(Status.INTERNAL_SERVER_ERROR,"Not Working"))
      val result: Future[Result] = controller.onSubmit()(fakeRequest)
      status(result) mustBe Status.INTERNAL_SERVER_ERROR
    }

    "throw a Runtime error if the user answers doesn't parse to a submission model" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
        .set(UserTypePage, UserType.Importer).success.value)

      val result = intercept[RuntimeException](await(controller.onSubmit()(fakeRequest)))
      assert(result.getMessage.contains("Completed journey answers does not parse to IVDSubmission model"))
    }

  }
}
