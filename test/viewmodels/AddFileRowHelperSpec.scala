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

package viewmodels

import base.SpecBase
import models.{FileUploadInfo, UserAnswers}
import org.scalatest.{MustMatchers, OptionValues, TryValues}
import play.api.libs.json.{JsObject, Json}
import play.twirl.api.Html

class AddFileRowHelperSpec extends SpecBase with MustMatchers with TryValues with OptionValues {

  "rows" should {

    "must return an empty list when there are no files" in {

      val helper = new AddFileNameRowHelper(List.empty)

      helper.rows mustBe empty
    }

    "must contain one item per file" in {

      val data: JsObject = Json.obj("uploaded-files" -> Json.arr(
        Json.obj("fileName" -> "text.txt"),
        Json.obj("fileName" -> "text2.txt"),
        Json.obj("fileName" -> "text3.txt")
      ))

      val answers =
        UserAnswers("id", data)
      val helper = new AddFileNameRowHelper(List(FileUploadInfo("text.txt"),FileUploadInfo("text2.txt"),FileUploadInfo("text3.txt")))

      val result = helper.rows

      result.size mustEqual 3

      val first = result.head
      first.value.content.asHtml mustEqual Html("text.txt")
//      first.removeAction.href mustEqual routes.RemoveRestaurantController.onPageLoad(Index(0)).url

      val second = result(1)
      second.value.content.asHtml mustEqual Html("text2.txt")
//      second.removeAction.href mustEqual routes.RemoveRestaurantController.onPageLoad(Index(1)).url

      val third = result(2)
      third.value.content.asHtml mustEqual Html("text3.txt")
//      third.removeAction.href mustEqual routes.RemoveRestaurantController.onPageLoad(Index(2)).url
    }
  }
}
