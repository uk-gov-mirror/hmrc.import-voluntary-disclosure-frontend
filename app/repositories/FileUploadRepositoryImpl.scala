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

import javax.inject.{Inject, Singleton}
import models.upscan.FileUpload
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import reactivemongo.play.json.ImplicitBSONHandlers.JsObjectDocumentWriter
import uk.gov.hmrc.mongo.ReactiveRepository

import java.time.{Instant, ZoneId}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FileUploadRepositoryImpl @Inject()(mongo: ReactiveMongoComponent,
                                         appConfig: AppConfig) extends
  ReactiveRepository[FileUpload, BSONObjectID](
    collectionName = "file-upload",
    mongo = mongo.mongoConnector.db,
    domainFormat = FileUpload.format
  ) with FileUploadRepository {

  override def indexes: Seq[Index] = Seq(
    Index(
      key     = Seq("reference" -> IndexType.Ascending),
      name    = Some("reference-unique-index"),
      unique  = true,
      sparse  = false,
      options = BSONDocument("expireAfterSeconds" -> appConfig.fileRepositoryTtl)
    )
  )

  private def getTime: Instant = Instant.now().atZone(ZoneId.of("UTC")).toInstant

  val updateLastUpdatedTimestamp: FileUpload => FileUpload = _.copy(lastUpdatedDate = Some(getTime))

  override def insertRecord(fileUpload: FileUpload)(implicit ec: ExecutionContext): Future[Boolean] = {
    collection.insert.one(updateLastUpdatedTimestamp(fileUpload)).map(_.ok)
  }

  override def updateRecord(fileUpload: FileUpload)(implicit ec: ExecutionContext): Future[Boolean] = {
    val selector = Json.obj("reference" -> fileUpload.reference)
    val update = Json.obj("$set" -> updateLastUpdatedTimestamp(fileUpload))
    collection
      .update(ordered = false)
      .one(selector, update, upsert = true)
      .map(_.ok)
  }

  override def getRecord(reference: String)(implicit ec: ExecutionContext): Future[Option[FileUpload]] = {
    val selector = Json.obj("reference" -> reference)
    collection
      .find(selector, None)
      .one[FileUpload]
  }

  override def deleteRecord(reference: String)(implicit ec: ExecutionContext): Future[Boolean] = {
    val selector = Json.obj("reference" -> reference)
    collection
      .delete(ordered = false)
      .one(selector)
      .map(_.ok)
  }

  override def getFileName(reference: String)(implicit ec: ExecutionContext): Future[Option[String]] =
    getRecord(reference).map(_.flatMap(fileUpload => fileUpload.uploadDetails.map(_.fileName)))

  override def testOnlyRemoveAllRecords()(implicit ec: ExecutionContext): Future[WriteResult] = {
    logger.info("removing all records in file-upload")
    removeAll()
  }
}


trait FileUploadRepository {

  def insertRecord(fileUpload: FileUpload)(implicit ec: ExecutionContext): Future[Boolean]
  def updateRecord(fileUpload: FileUpload)(implicit ec: ExecutionContext): Future[Boolean]
  def deleteRecord(reference: String)(implicit ec: ExecutionContext): Future[Boolean]
  def getRecord(reference: String)(implicit ec: ExecutionContext): Future[Option[FileUpload]]
  def getFileName(reference: String)(implicit ec: ExecutionContext): Future[Option[String]]
  def testOnlyRemoveAllRecords()(implicit ec: ExecutionContext): Future[WriteResult]

}
