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
import play.api.libs.json.{JsObject, JsValue, Json}
import play.twirl.api.Html

class AddFileRowHelperSpec extends SpecBase with MustMatchers with TryValues with OptionValues {

  "rows" should {

    "must return an empty list when there are no files" in {

      val helper = new AddFileNameRowHelper(Seq.empty)

      helper.rows mustBe empty
    }

    "must contain one item per file" in {
      def fileUploadInfo(filename: String): JsValue = {
        Json.obj("fileName" -> s"${filename}",
          "downloadUrl" -> "http://localhost:9570/upscan/download/6f531dec-108d-4dc9-a586-9a97cf78bc34",
          "uploadTimestamp" -> "2021-01-26T13:22:59.388",
          "checksum" -> "396f101dd52e8b2ace0dcf5ed09b1d1f030e608938510ce46e7a5c7a4e775100",
          "fileMimeType" -> "application/txt")
      }

      val data: JsObject = Json.obj("uploaded-files" -> Json.arr(
        fileUploadInfo("text.txt"),
        fileUploadInfo("text2.txt"),
        fileUploadInfo("text3.txt")
      ))

      val answers =
        UserAnswers("id", data)
      val helper = new AddFileNameRowHelper(
        Seq(
          Json.fromJson[FileUploadInfo](fileUploadInfo("text.txt")).get,
          Json.fromJson[FileUploadInfo](fileUploadInfo("text2.txt")).get,
          Json.fromJson[FileUploadInfo](fileUploadInfo("text3.txt")).get
        ))

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
