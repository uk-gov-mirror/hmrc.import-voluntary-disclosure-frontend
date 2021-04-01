package controllers.underpayments

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import forms.underpayments.UnderpaymentDetailSummaryFormProvider
import mocks.repositories.MockSessionRepository
import models.UserAnswers
import utils.ReusableValues
import views.html.underpayments.UnderpaymentDetailSummaryView

import scala.concurrent.Future

class UnderpaymentDetailSummaryControllerSpec extends ControllerSpecBase with ReusableValues {

  trait Test extends MockSessionRepository {
    private lazy val underpaymentDetailSummaryView: UnderpaymentDetailSummaryView = app.injector.instanceOf[UnderpaymentDetailSummaryView]

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    val formProvider: UnderpaymentDetailSummaryFormProvider = injector.instanceOf[UnderpaymentDetailSummaryFormProvider]
    val form: UnderpaymentDetailSummaryFormProvider = formProvider

    MockedSessionRepository.set(Future.successful(true))

    lazy val controller = new UnderpaymentDetailSummaryController(authenticatedAction, dataRetrievalAction, dataRequiredAction,
      messagesControllerComponents, underpaymentDetailSummaryView, form)
  }

}
