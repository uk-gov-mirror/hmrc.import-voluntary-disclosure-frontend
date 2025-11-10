/*
 * Copyright 2025 HM Revenue & Customs
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
import models.upscan.FileUpload
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Indexes.ascending
import org.mongodb.scala.model.{IndexModel, IndexOptions, UpdateOptions}
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.Codecs._
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import java.time.{Instant, ZoneId}
import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FileUploadRepositoryImpl @Inject() (mongoComponent: MongoComponent, appConfig: AppConfig)(implicit
  ec: ExecutionContext
) extends PlayMongoRepository[FileUpload](
      collectionName = "file-upload",
      mongoComponent = mongoComponent,
      domainFormat = FileUpload.format,
      indexes = Seq(
        IndexModel(
          ascending("reference"),
          IndexOptions().name("reference-unique-index").unique(true).sparse(false).expireAfter(
            appConfig.fileRepositoryTtl.toLong,
            TimeUnit.SECONDS
          )
        )
      )
    )
    with FileUploadRepository {

  private def getTime: Instant =
    Instant.now().atZone(ZoneId.of("UTC")).toInstant.truncatedTo(java.time.temporal.ChronoUnit.MILLIS)

  private val updateLastUpdatedTimestamp: FileUpload => FileUpload = _.copy(lastUpdatedDate = Some(getTime))

  override def updateRecord(fileUpload: FileUpload)(implicit ec: ExecutionContext): Future[Boolean] = {
    val update = BsonDocument("$set" -> updateLastUpdatedTimestamp(fileUpload).toDocument)
    collection
      .updateOne(equal("reference", fileUpload.reference), update, UpdateOptions().upsert(true))
      .toFuture()
      .map(_.wasAcknowledged())
  }

  override def getRecord(reference: String)(implicit ec: ExecutionContext): Future[Option[FileUpload]] =
    collection
      .find(equal("reference", reference))
      .headOption()

}

trait FileUploadRepository {

  def updateRecord(fileUpload: FileUpload)(implicit ec: ExecutionContext): Future[Boolean]

  def getRecord(reference: String)(implicit ec: ExecutionContext): Future[Option[FileUpload]]

}
