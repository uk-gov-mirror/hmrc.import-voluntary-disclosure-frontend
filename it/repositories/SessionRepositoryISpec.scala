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
import models.{MongoDateTimeFormats, UserAnswers}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.json.{JsResult, JsValue, Json}
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import play.modules.reactivemongo.ReactiveMongoComponent

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global

class SessionRepositoryISpec extends PlaySpec with GuiceOneServerPerSuite with FutureAwaits with DefaultAwaitTimeout {

  val mongo: ReactiveMongoComponent = app.injector.instanceOf[ReactiveMongoComponent]
  val appConfig: AppConfig = app.injector.instanceOf[AppConfig]

  val fakeNow: LocalDateTime = LocalDateTime.now()

  val repo: UserAnswersRepository = new UserAnswersRepository(mongo: ReactiveMongoComponent, appConfig)

  private def count = await(repo.count)

  val mongoDate: JsValue = Json.toJson(fakeNow)(MongoDateTimeFormats.localDateTimeWrite)

  val userAnswersJson: JsValue = Json.parse(
    s"""{
       | "_id": "12345",
       | "data":{
       |   "claimPeriodPage": {
       |      "startDateValue": "2020-03-01",
       |      "endDateValue": "2020-04-01"
       |     }
       |   },
       | "lastUpdated": $mongoDate
       |}""".stripMargin
  )

  val id = "12345"

  val userAnswers: UserAnswers = UserAnswers(
    id = id,
    data = Json.obj(
      "claimPeriodPage" -> Json.obj(
        "startDateValue" -> "2020-03-01",
        "endDateValue" -> "2020-04-01"
      )
    ),
    lastUpdated = fakeNow
  )

  trait Test {
    await(repo.drop)
  }

  "repository domainFormatImplicit reads" should {

    "read in json as per format of mongo reads" in {
      val answers: JsResult[UserAnswers] = Json.fromJson[UserAnswers](userAnswersJson)(repo.domainFormatImplicit)
      answers.get.data mustBe userAnswers.data
    }
  }
  "repository domainFormatImplicit writes" should {

    "write json as per format of mongo writes" in {
      val answersJson: JsValue = Json.toJson[UserAnswers](userAnswers)(repo.domainFormatImplicit)
      answersJson mustBe userAnswersJson
    }
  }

  "set" should {

    "add a single set of user answers to the collection" in new Test {
      count mustBe 0
      await(repo.set(userAnswers))
      count mustBe 1
    }
  }

  "get" should {

    "retrieve a single set of user answers" in new Test {
      count mustBe 0
      await(repo.set(userAnswers))
      count mustBe 1
      await(repo.get(id)).fold(fail("expected document not found in mongo")) { answers =>
        // lastUpdated date will be different because it's set using the current date
        answers mustBe userAnswers.copy(lastUpdated = answers.lastUpdated)
      }
    }

    "return None for no match when no users answers found" in new Test {
      count mustBe 0
      await(repo.set(userAnswers.copy(id = "999")))
      count mustBe 1
      await(repo.get(id)) mustBe None
    }
  }

  "remove user answers" should {

    "remove a draft claim" in new Test {
      count mustBe 0
      await(repo.set(userAnswers))
      count mustBe 1

      await(repo.remove(id)) mustBe id
      count mustBe 0
    }

    "remove no claim" in new Test {
      count mustBe 0
      await(repo.set(userAnswers))
      count mustBe 1

      await(repo.remove("999")) mustBe "999"
      count mustBe 1
    }
  }

  "delete user answers" should {

    "remove a draft claim" in new Test {
      count mustBe 0
      await(repo.set(userAnswers))
      count mustBe 1

      await(repo.delete(userAnswers)) mustBe true
      count mustBe 0
    }

    "remove user answers" in new Test {
      count mustBe 0
      await(repo.set(userAnswers))
      count mustBe 1

      await(repo.delete(userAnswers.copy(id = "999"))) mustBe true
      count mustBe 1
    }
  }

}