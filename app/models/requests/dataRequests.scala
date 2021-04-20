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

package models.requests

import models.SelectedDutyTypes.{Both, Duty, Neither, SelectedDutyType, Vat}
import models.{UserAnswers, UserType}
import pages.{ImporterEORIExistsPage, UserTypePage}
import pages.underpayments.UnderpaymentDetailSummaryPage
import play.api.mvc.WrappedRequest

case class OptionalDataRequest[A](request: IdentifierRequest[A], credId: String, eori: String, userAnswers: Option[UserAnswers])
  extends WrappedRequest[A](request)

case class DataRequest[A](request: OptionalDataRequest[A], credId: String, eori: String, userAnswers: UserAnswers)
  extends WrappedRequest[A](request) {

  def isRepFlow: Boolean =
    userAnswers.get(UserTypePage) match {
      case Some(userType) => userType == UserType.Representative
      case _ => false
    }

  def doesImporterEORIExist: Boolean =
    userAnswers.get(ImporterEORIExistsPage) match {
      case Some(value) => value
      case _ => false
    }

  def dutyType: SelectedDutyType = {
    val vatUnderpaymentType: String = "B00"
    userAnswers.get(UnderpaymentDetailSummaryPage).map { value =>
      val vatExists = value.exists(_.duty == vatUnderpaymentType)
      val dutyExists = value.exists(_.duty != vatUnderpaymentType)
      (vatExists, dutyExists) match {
        case (true, true) => Both
        case (true, _) => Vat
        case (_, true) => Duty
        case _ => Neither
      }
    }.getOrElse(Neither)
  }

}
