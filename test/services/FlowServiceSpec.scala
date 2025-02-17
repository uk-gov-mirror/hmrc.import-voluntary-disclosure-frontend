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

package services

import base.SpecBase
import mocks.config.MockAppConfig
import models.SelectedDutyTypes.{Both, Duty, Neither, Vat}
import models.UserType.{Importer, Representative}
import models.underpayments.UnderpaymentDetail
import models.{UserAnswers, UserType}
import pages.underpayments.UnderpaymentDetailSummaryPage
import pages.{ImporterEORIExistsPage, UserTypePage}


class FlowServiceSpec extends SpecBase {

  trait Test {
    val mockAppConfig = MockAppConfig
    lazy val userType: UserType = Importer
    lazy val service = new FlowService()(mockAppConfig)

    def setupUserAnswersForRepFlow = UserAnswers("some-cred-id").set(UserTypePage, userType).success.value

    def setupUserAnswersForEoriExists(exists: Boolean) = UserAnswers("some-cred-id").set(ImporterEORIExistsPage, exists).success.value

    val setupUserAnswersForDutyType: UserAnswers = UserAnswers("some-cred-id")

  }

  "isRep call" should {

    "return true for representative journey" in new Test {
      override lazy val userType = Representative
      service.isRepFlow(setupUserAnswersForRepFlow) mustBe true
    }

    "return false for Importer journey" in new Test {
      override lazy val userType = Importer
      service.isRepFlow(setupUserAnswersForRepFlow) mustBe false
    }
  }

  "doesImporterEORIExist call" should {
    "return true if EORI exist" in new Test {
      service.doesImporterEORIExist(setupUserAnswersForEoriExists(true)) mustBe true
    }

    "return false if EORI does not exist" in new Test {
      service.doesImporterEORIExist(setupUserAnswersForEoriExists(false)) mustBe false
    }
  }

    "dutyType call" when {
      "using the oldUnderpayment feature" should {
        "return Vat if only Vat is present" in new Test {
          override val setupUserAnswersForDutyType: UserAnswers = UserAnswers("some-cred-id")
            .set(UnderpaymentDetailSummaryPage, Seq(UnderpaymentDetail("B00", 0.0, 1.0))).success.value
          service.dutyType(setupUserAnswersForDutyType) mustBe Vat
        }

        "return Duty if only Duty is present" in new Test {
          override val setupUserAnswersForDutyType: UserAnswers = UserAnswers("some-cred-id")
            .set(UnderpaymentDetailSummaryPage, Seq(UnderpaymentDetail("A00", 0.0, 1.0))).success.value
          service.dutyType(setupUserAnswersForDutyType) mustBe Duty
        }

        "return Both if Both are present" in new Test {
          override val setupUserAnswersForDutyType: UserAnswers = UserAnswers("some-cred-id")
            .set(UnderpaymentDetailSummaryPage, Seq(
              UnderpaymentDetail("A20", 0.0, 1.0),
              UnderpaymentDetail("B00", 0.0, 1.0)
            )).success.value
          service.dutyType(setupUserAnswersForDutyType) mustBe Both
        }

        "return Neither if Neither present" in new Test {
          service.dutyType(setupUserAnswersForDutyType) mustBe Neither
        }
      }
    }

}
