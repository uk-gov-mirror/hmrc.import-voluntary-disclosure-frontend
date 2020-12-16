/*
 * Copyright 2020 HM Revenue & Customs
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
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json.ImplicitBSONHandlers.JsObjectDocumentWriter
import uk.gov.hmrc.mongo.ReactiveRepository

import java.time.LocalDateTime
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserAnswersRepository @Inject()(mongoComponent: ReactiveMongoComponent, appConfig: AppConfig)
  extends ReactiveRepository[UserAnswers, String](
    collectionName = "user-answers",
    mongo = mongoComponent.mongoConnector.db,
    domainFormat = UserAnswers.format,
    idFormat = implicitly[Format[String]]
  ) with SessionRepository {

  override def indexes: Seq[Index] = Seq(Index(
    key = Seq("lastUpdated" -> IndexType.Ascending),
    name = Some("user-answers-last-updated-index"),
    options = BSONDocument("expireAfterSeconds" -> appConfig.cacheTtl)
  ))

  override def get(id: String)(implicit ec: ExecutionContext): Future[Option[UserAnswers]] =
    collection
      .find(_id(id), None)
      .one[UserAnswers]

  override def set(userAnswers: UserAnswers)(implicit ec: ExecutionContext): Future[Boolean] = {
    val modifier = Json.obj("$set" -> (userAnswers copy (lastUpdated = LocalDateTime.now())))
    collection
      .update(ordered = false)
      .one(_id(userAnswers.id), modifier, upsert = true)
      .map(_.ok)
  }

  override def delete(userAnswers: UserAnswers)(implicit ec: ExecutionContext): Future[Boolean] = {
    collection
      .delete(ordered = false)
      .one(_id(userAnswers.id))
      .map(_.ok)
  }

  override def remove(id: String)(implicit ec: ExecutionContext): Future[String] = {
    val selector = Json.obj("_id" -> id)

    collection.delete().one(selector).map(_ => id)
  }
}

trait SessionRepository {

  def get(id: String)(implicit ec: ExecutionContext): Future[Option[UserAnswers]]

  def set(userAnswers: UserAnswers)(implicit ec: ExecutionContext): Future[Boolean]

  def remove(id: String)(implicit ec: ExecutionContext): Future[String]

  def delete(userAnswers: UserAnswers)(implicit ec: ExecutionContext): Future[Boolean]
}
