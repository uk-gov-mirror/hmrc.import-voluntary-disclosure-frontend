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

package repositories

import config.AppConfig
import models.MongoDateTimeFormats
import models.upscan._
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.json.{JsResult, JsValue, Json}
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import play.modules.reactivemongo.ReactiveMongoComponent

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global

class FileUploadRepositoryISpec extends PlaySpec with GuiceOneServerPerSuite with FutureAwaits with DefaultAwaitTimeout {

  val mongo: ReactiveMongoComponent = app.injector.instanceOf[ReactiveMongoComponent]
  val appConfig: AppConfig = app.injector.instanceOf[AppConfig]

  val fakeNow: LocalDateTime = LocalDateTime.now()

  val repo: FileUploadRepositoryImpl = new FileUploadRepositoryImpl(mongo: ReactiveMongoComponent, appConfig)

  private def count = await(repo.count)

  val mongoDate: JsValue = Json.toJson(fakeNow)(MongoDateTimeFormats.localDateTimeWrite)

  trait Test {
    await(repo.drop)
  }

  val fileUploadModel: FileUpload = FileUpload(
    reference = "11370e18-6e24-453e-b45a-76d3e32ea33d",
    credId = Some("cred Id"),
    fileStatus = Some(FileStatusEnum.READY),
    uploadDetails = Some(
      UploadDetails(
        uploadTimestamp = fakeNow,
        checksum = "396f101dd52e8b2ace0dcf5ed09b1d1f030e608938510ce46e7a5c7a4e775100",
        fileName = "test.xls",
        fileMimeType = "application/pdf")
    ),
    lastUpdatedDate = None
  )
  val fileUploadModelAlternative: FileUpload = fileUploadModel.copy(reference = "123", credId = Some("Another cred Id"))
  val jsonInRepo: JsValue = Json.obj(
    "reference" -> "11370e18-6e24-453e-b45a-76d3e32ea33d",
    "credId" -> "cred Id",
    "fileStatus" -> FileStatusEnum.READY.toString,
    "uploadDetails" -> Json.toJson(UploadDetails(
      uploadTimestamp = fakeNow,
      checksum = "396f101dd52e8b2ace0dcf5ed09b1d1f030e608938510ce46e7a5c7a4e775100",
      fileName = "test.xls",
      fileMimeType = "application/pdf"))
  )

  "repository domainFormatImplicit reads" should {

    "read in json as per format of mongo reads" in new Test {
      val fileUpload: JsResult[FileUpload] = Json.fromJson[FileUpload](jsonInRepo)(repo.domainFormatImplicit)
      fileUpload.get mustBe fileUploadModel
    }
  }

  "repository domainFormatImplicit writes" should {

    "write json as per format of mongo writes" in new Test {
      val fileUploadJson: JsValue = Json.toJson[FileUpload](fileUploadModel)(repo.domainFormatImplicit)
      fileUploadJson mustBe jsonInRepo
    }
  }

  "insertRecord" should {
    "insert the document, including setting the new timestamp value" in new Test {

      await(repo.count) mustBe 0

      val insertedResult = await(repo.insertRecord(fileUploadModel))

      insertedResult mustBe true
      await(repo.count) mustBe 1
      await(repo.getRecord(fileUploadModel.reference)).get.lastUpdatedDate.isDefined mustBe true
    }
  }

  "updateRecord" should {
    "upsert the document, including setting the new timestamp value" in new Test {

      await(repo.count) mustBe 0
      val updatedResult = await(repo.updateRecord(fileUploadModel))

      updatedResult mustBe true
      await(repo.count) mustBe 1
      await(repo.getRecord(fileUploadModel.reference)).get.lastUpdatedDate.isDefined mustBe true
    }

    "update the document, including setting the new timestamp value" in new Test {

      await(repo.count) mustBe 0
      await(repo.insertRecord(fileUploadModel))

      await(repo.count) mustBe 1
      await(repo.getRecord(fileUploadModel.reference)).get.credId mustBe Some("cred Id")
      val updatedResult = await(repo.updateRecord(fileUploadModel.copy(credId = Some("Another cred Id"))))
      updatedResult mustBe true

      await(repo.count) mustBe 1
      await(repo.getRecord(fileUploadModel.reference)).get.credId mustBe Some("Another cred Id")
    }
  }

  "getRecord" should {

    "retrieve a single document" in new Test {
      count mustBe 0
      await(repo.insertRecord(fileUploadModel))
      await(repo.insertRecord(fileUploadModelAlternative))

      count mustBe 2
      val getResult = await(repo.getRecord(fileUploadModel.reference))
      getResult.size mustBe 1
      getResult.get.credId mustBe Some("cred Id")
    }

    "return None for no match found" in new Test {
      count mustBe 0
      await(repo.insertRecord(fileUploadModelAlternative))
      count mustBe 1
      await(repo.getRecord(fileUploadModel.reference)) mustBe None
    }
  }

  "deleteRecord" should {

    "remove a single document" in new Test {
      count mustBe 0
      await(repo.insertRecord(fileUploadModel))
      await(repo.insertRecord(fileUploadModelAlternative))
      count mustBe 2

      await(repo.deleteRecord("123")) mustBe true
      count mustBe 1
      await(repo.getRecord(fileUploadModel.reference)).size mustBe 1
    }
  }

  "getFilename" should {

    "retrieve the correct document" in new Test {
      count mustBe 0
      await(repo.insertRecord(fileUploadModel))
      await(repo.insertRecord(fileUploadModelAlternative))
      count mustBe 2

      await(repo.getFileName("123")) mustBe fileUploadModel.fileName
    }
  }

  "testOnlyRemoveAllRecords" should {

    "remove all documents" in new Test {
      count mustBe 0
      await(repo.insertRecord(fileUploadModel))
      await(repo.insertRecord(fileUploadModel))
      await(repo.insertRecord(fileUploadModelAlternative))
      count mustBe 3

      await(repo.testOnlyRemoveAllRecords())
      count mustBe 0
    }
  }

}