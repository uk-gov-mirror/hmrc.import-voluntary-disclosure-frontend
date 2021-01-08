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

package forms.mappings

import java.time.LocalDate

import play.api.data.FormError
import play.api.data.format.Formatter
import play.api.i18n.Messages

import scala.util.{Failure, Success, Try}


private[mappings] class LocalDateFormatter(invalidKey: String,
                                           allRequiredKey: String,
                                           twoRequiredKey: String,
                                           requiredKey: String,
                                           dayMonthLengthKey: String,
                                           yearLengthKey: String,
                                           validatePastKey: Option[String],
                                           args: Seq[String] = Seq.empty)(implicit messages: Messages)
  extends Formatter[LocalDate] with Formatters with Constraints {

  private val fieldKeys: List[String] = List("day", "month", "year")

  private val yearLength = 4
  private val dayMonthLengthMax = 2

  private def toDate(key: String, day: Int, month: Int, year: Int): Either[Seq[FormError], LocalDate] =
    Try(LocalDate.of(year, month, day)) match {
      case Success(date) =>
        Right(date)
      case Failure(_) =>
        Left(Seq(FormError(s"$key.day", invalidKey, fieldKeys ++ args)))
    }

  private def formatDate(key: String, data: Map[String, String]): Either[Seq[FormError], LocalDate] = {

    val int = intFormatter(
      requiredKey = invalidKey,
      wholeNumberKey = invalidKey,
      nonNumericKey = invalidKey,
      args
    )

    val date = for {
      day <- int.bind(s"$key.day", data).right
      month <- int.bind(s"$key.month", data).right
      year <- int.bind(s"$key.year", data).right
      date <- toDate(key, day, month, year).right
    } yield date

    if (validatePastKey.isDefined) {
      date.fold(
        err => Left(err),
        dt => if(dt.isAfter(LocalDate.now)) Left(List(FormError(s"$key.day", validatePastKey.get, fieldKeys))) else Right(dt)
      )
    } else {
      date
    }
  }

  override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], LocalDate] = {

    val fields = fieldKeys.map {
      field =>
        field -> data.get(s"$key.$field").filter(_.nonEmpty).map(f => filter(f))
    }.toMap

    lazy val missingFields = fields
      .withFilter(_._2.isEmpty)
      .map(_._1)
      .toList


    fields.count(_._2.isDefined) match {
      case 3 =>
        val lengthErrors = fields.collect {
          case (id, value) if !id.contains("year") && value.exists(_.length > dayMonthLengthMax) => id
        }

        val yearLengthError = fields.collect {
          case (id, value) if id.contains("year") && value.exists(_.length != yearLength) => id
        }

        (lengthErrors.nonEmpty, yearLengthError.nonEmpty) match {
          case (true, true) => Left(List(FormError(s"$key.${lengthErrors.head}", dayMonthLengthKey, Seq(lengthErrors.mkString(messages("site.and")))),
                                          FormError(s"$key.year", yearLengthKey, Seq(yearLengthError))))
          case (true, false) => Left(List(FormError(s"$key.${lengthErrors.head}", dayMonthLengthKey, Seq(lengthErrors.mkString(messages("site.and"))))))
          case (false, true) => Left(List(FormError(s"$key.year", yearLengthKey, Seq(yearLengthError))))
          case _ => formatDate(key, data)
        }
      case 2 =>
        Left(List(FormError(s"$key.${missingFields.head}", requiredKey, missingFields ++ args)))
      case 1 =>
        Left(List(FormError(s"$key.${missingFields.head}", twoRequiredKey, missingFields ++ args)))
      case _ =>
        Left(List(FormError(s"$key.day", allRequiredKey, fieldKeys ++ args)))
    }

  }

  override def unbind(key: String, value: LocalDate): Map[String, String] =
    Map(
      s"$key.day" -> value.getDayOfMonth.toString,
      s"$key.month" -> value.getMonthValue.toString,
      s"$key.year" -> value.getYear.toString
    )
}
