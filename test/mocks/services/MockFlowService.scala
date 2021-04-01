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

package mocks.services

import base.SpecBase
import models.SelectedDutyTypes.SelectedDutyType
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import services.FlowService

trait MockFlowService extends SpecBase with MockFactory {

  val mockFlowService: FlowService = mock[FlowService]

  object MockedFlowService {
    def isRepFlow(response: Boolean): CallHandler[Boolean] = {
      (mockFlowService.isRepFlow(_))
        .expects(*)
        .returns(response)
    }
    def doesImporterEORIExist(response: Boolean): CallHandler[Boolean] = {
      (mockFlowService.doesImporterEORIExist(_))
        .expects(*)
        .returns(response)
    }
    def dutyType(response: SelectedDutyType): CallHandler[SelectedDutyType] = {
      (mockFlowService.dutyType(_))
        .expects(*)
        .returns(response)
    }
  }
}
