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
import models.UserType.{Importer, Representative}
import models.{UserAnswers, UserType}
import pages.{ImporterEORIExistsPage, UserTypePage}


class FlowServiceSpec extends SpecBase {

  trait Test {
    lazy val userType: UserType = Importer
    lazy val service = new FlowService

    def setupUserAnswersForRepFlow = UserAnswers("some-cred-id").set(UserTypePage, userType).success.value
    def setupUserAnswersForEoriExists(exists: Boolean) = UserAnswers("some-cred-id").set(ImporterEORIExistsPage, exists).success.value
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

}
