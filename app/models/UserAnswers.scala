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

package models

import pages.QuestionPage
import play.api.libs.json._
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.Instant
import scala.annotation.{nowarn, tailrec}
import scala.util.{Failure, Success, Try}

// $COVERAGE-OFF$Code taken from another [trusted] service
final case class UserAnswers(id: String, data: JsObject = Json.obj(), lastUpdated: Instant = Instant.now()) {

  def get[A](page: Gettable[A])(implicit rds: Reads[A]): Option[A] =
    Reads.optionNoError(Reads.at(page.path)).reads(data).getOrElse(None)

  def get[A, B](derivable: Derivable[A, B])(implicit rds: Reads[A]): Option[B] =
    get(derivable: Gettable[A]).map(derivable.derive)

  def set[A](page: Settable[A], value: A)(implicit writes: Writes[A]): Try[UserAnswers] = {

    val updatedData = data.setObject(page.path, Json.toJson(value)) match {
      case JsSuccess(jsValue, _) =>
        Success(jsValue)
      case JsError(errors) =>
        Failure(JsResultException(errors))
    }

    updatedData.flatMap { d =>
      val updatedAnswers = copy(data = d)
      page.cleanup(Some(value), updatedAnswers)
    }
  }

  def remove[A](page: Settable[A]): Try[UserAnswers] = {

    val updatedData = data.removeObject(page.path) match {
      case JsSuccess(jsValue, _) =>
        Success(jsValue)
      case JsError(_) =>
        Success(data)
    }

    updatedData.flatMap { d =>
      val updatedAnswers = copy(data = d)
      page.cleanup(None, updatedAnswers)
    }
  }

  def removeMany(pages: Seq[QuestionPage[_]]): UserAnswers = {
    @nowarn("msg=match may not be exhaustive")
    @tailrec
    def removePages(userAnswers: UserAnswers, pages: Seq[QuestionPage[_]]): UserAnswers = pages match {
      case Nil => userAnswers
      case page :: remainingPages =>
        userAnswers.remove(page) match {
          case Success(answers)   => removePages(answers, remainingPages)
          case Failure(exception) => throw exception
        }
    }

    removePages(this, pages)
  }

  def preserve(pages: Seq[QuestionPage[_]]): UserAnswers = {
    val answers = UserAnswers(this.id)
    val preservedAnswers: Seq[JsObject] = pages.map { page =>
      val answer = Reads.jsPick[JsValue](page.path).reads(data) match {
        case JsSuccess(value, _) => Some(value)
        case JsError(_)          => None
      }
      page.path -> answer
    }.collect { case (path, Some(value)) =>
      Json.obj().setObject(path, value) match {
        case JsSuccess(value, _) => value
        case JsError(_)          => Json.obj()
      }
    }

    val json = preservedAnswers.fold(Json.obj())(_ ++ _)
    answers.copy(data = json)
  }
}

object UserAnswers {

  implicit lazy val format: Format[UserAnswers] = Format(reads, writes)

  val reads: Reads[UserAnswers] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "_id").read[String] and
        (__ \ "data").read[JsObject] and
        (__ \ "lastUpdated").read(MongoJavatimeFormats.instantReads)
    )(UserAnswers.apply _)
  }

  val writes: OWrites[UserAnswers] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "_id").write[String] and
        (__ \ "data").write[JsObject] and
        (__ \ "lastUpdated").write(MongoJavatimeFormats.instantWrites)
    )(ua => Tuple.fromProductTyped(ua))
  }
}

trait Derivable[A, B] extends Gettable[A] {

  val derive: A => B
}

sealed trait Query {

  def path: JsPath
}

trait Gettable[A] extends Query

trait Settable[A] extends Query {

  def cleanup(value: Option[A], userAnswers: UserAnswers): Try[UserAnswers] = Success(userAnswers)

}

// $COVERAGE-ON$
