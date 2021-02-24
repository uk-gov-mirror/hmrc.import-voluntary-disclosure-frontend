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

package stubs

import config.AppConfig
import models.{MongoDateTimeFormats, UserAnswers}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.json.{JsValue, Json}
import play.modules.reactivemongo.ReactiveMongoComponent
import repositories.UserAnswersRepository

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object UserAnswersStub extends PlaySpec with GuiceOneServerPerSuite {

  lazy val mongo: ReactiveMongoComponent = app.injector.instanceOf[ReactiveMongoComponent]
  lazy val appConfig: AppConfig = app.injector.instanceOf[AppConfig]

  lazy val fakeNow: LocalDateTime = LocalDateTime.now()

  lazy val repo: UserAnswersRepository = new UserAnswersRepository(mongo: ReactiveMongoComponent, appConfig)

  val mongoDate: JsValue = Json.toJson(fakeNow)(MongoDateTimeFormats.localDateTimeWrite)

  def createUserAnswers(credId: String): Future[Boolean] = {
    val userAnswers: UserAnswers = UserAnswers(
      id = credId,
      data = Json.obj(
        "claimPeriodPage" -> Json.obj(
          "startDateValue" -> "2020-03-01",
          "endDateValue" -> "2020-04-01"
        )
      ),
      lastUpdated = fakeNow
    )
    repo.remove(credId)
    repo.set(userAnswers)
  }
}