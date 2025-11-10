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
import models.UserAnswers
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Indexes.ascending
import org.mongodb.scala.model.{IndexModel, IndexOptions, UpdateOptions}
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.Codecs._
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import java.time.Instant
import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserAnswersRepository @Inject() (mongoComponent: MongoComponent, appConfig: AppConfig)(implicit
  ec: ExecutionContext
) extends PlayMongoRepository[UserAnswers](
      collectionName = "user-answers",
      mongoComponent = mongoComponent,
      domainFormat = UserAnswers.format,
      indexes = Seq(
        IndexModel(
          ascending("lastUpdated"),
          IndexOptions().name("user-answers-last-updated-index").expireAfter(appConfig.cacheTtl.toLong, TimeUnit.SECONDS)
        )
      )
    )
    with SessionRepository {

  override def get(id: String)(implicit ec: ExecutionContext): Future[Option[UserAnswers]] =
    collection
      .find(equal("_id", id))
      .headOption()

  override def set(userAnswers: UserAnswers)(implicit ec: ExecutionContext): Future[Boolean] = {
    val modifier = userAnswers copy (lastUpdated = Instant.now())
    val update   = BsonDocument("$set" -> modifier.toDocument)
    collection
      .updateOne(equal("_id", userAnswers.id), update, UpdateOptions().upsert(true))
      .toFuture()
      .map(_.wasAcknowledged())
  }

  override def delete(userAnswers: UserAnswers)(implicit ec: ExecutionContext): Future[Boolean] =
    collection
      .deleteOne(equal("_id", userAnswers.id))
      .toFuture()
      .map(_.wasAcknowledged())

  override def remove(id: String)(implicit ec: ExecutionContext): Future[String] =
    collection
      .deleteOne(equal("_id", id))
      .toFuture()
      .map(_ => id)
}

trait SessionRepository {

  def get(id: String)(implicit ec: ExecutionContext): Future[Option[UserAnswers]]

  def set(userAnswers: UserAnswers)(implicit ec: ExecutionContext): Future[Boolean]

  def remove(id: String)(implicit ec: ExecutionContext): Future[String]

  def delete(userAnswers: UserAnswers)(implicit ec: ExecutionContext): Future[Boolean]
}
