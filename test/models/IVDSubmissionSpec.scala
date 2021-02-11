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

package models

import assets.IVDSubmissionTestData
import base.SpecBase
import play.api.libs.json.Json

class IVDSubmissionSpec extends SpecBase with IVDSubmissionTestData {

  "IVD Submission model" when {
    "converting from a user answers" should {
      "produce a valid model" in {
        val result = Json.fromJson[IVDSubmission](userAnswersJson).get
        result mustBe ivdSubmission
      }
    }
    "serialising a model" should {
      "produce valid json" in {
        val result = Json.toJson(ivdSubmission)
        result mustBe ivdSubmissionJson
      }
    }
  }

}
