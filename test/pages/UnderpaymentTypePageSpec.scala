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

package pages
import base.SpecBase
import models.{UnderpaymentType, UserAnswers}
import views.data.UnderpaymentSummaryData._

class UnderpaymentTypePageSpec extends SpecBase {

  "UnderpaymentTypePageSpec" must {

    "remove deselected Underpayments" in {
      val answers: UserAnswers = UserAnswers("some-cred-id")
        .set(UnderpaymentTypePage, UnderpaymentType(customsDuty = false, importVAT = false, exciseDuty = false)).success.value
        .set(CustomsDutyPage, cdUnderpayment).success.value
        .set(ImportVATPage, ivUnderpayment).success.value
        .set(ExciseDutyPage, edUnderpayment).success.value
      val underpaymentTypes = UnderpaymentType(customsDuty = false, importVAT = true, exciseDuty = false)

      val result = answers.set(UnderpaymentTypePage, underpaymentTypes).success.value

      result.get(CustomsDutyPage) mustBe None
      result.get(ImportVATPage) mustBe Some(ivUnderpayment)
      result.get(ExciseDutyPage) mustBe None
    }

    "not remove any Underpayments if all selected" in {
      val answers: UserAnswers = UserAnswers("some-cred-id")
        .set(UnderpaymentTypePage, UnderpaymentType(customsDuty = false, importVAT = false, exciseDuty = false)).success.value
        .set(CustomsDutyPage, cdUnderpayment).success.value
        .set(ImportVATPage, ivUnderpayment).success.value
        .set(ExciseDutyPage, edUnderpayment).success.value
      val underpaymentTypes = UnderpaymentType(customsDuty = true, importVAT = true, exciseDuty = true)

      val result = answers.set(UnderpaymentTypePage, underpaymentTypes).success.value

      result.get(CustomsDutyPage) mustBe Some(cdUnderpayment)
      result.get(ImportVATPage) mustBe Some(ivUnderpayment)
      result.get(ExciseDutyPage) mustBe Some(edUnderpayment)
    }
  }
}