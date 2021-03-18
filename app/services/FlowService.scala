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

package services

import models.{UnderpaymentType, UserAnswers, UserType}
import pages.{ImporterEORIExistsPage, UnderpaymentTypePage, UserTypePage}

import javax.inject.Singleton

@Singleton
class FlowService {

  def isRepFlow(userAnswers: UserAnswers): Boolean =
    userAnswers.get(UserTypePage) match {
      case Some(userType) => userType == UserType.Representative
      case _ => false
    }

  def doesImporterEORIExist(userAnswers: UserAnswers): Boolean =
    userAnswers.get(ImporterEORIExistsPage) match {
      case Some(value) => value
      case _ => false
    }

  def dutyType(userAnswers: UserAnswers): String = {
    userAnswers.get(UnderpaymentTypePage) match {
      case Some(UnderpaymentType(false, true, false)) => "vat"
      case Some(UnderpaymentType(_, false, _)) => "duty"
      case Some(_) => "both"
      case _ => "none"
    }
  }

}
