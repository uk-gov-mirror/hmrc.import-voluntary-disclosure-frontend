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

import com.google.inject.Inject
import config.AppConfig
import connectors.UpScanConnector
import models.upscan.{UpScanInitiateRequest, UpScanInitiateResponse}
import uk.gov.hmrc.http.{HeaderCarrier, InternalServerException}

import scala.concurrent.{ExecutionContext, Future}

class UpScanService @Inject()(upScanConnector: UpScanConnector,
                              appConfig: AppConfig) {

  lazy val buildInitiateRequest: UpScanInitiateRequest = UpScanInitiateRequest(
    appConfig.upScanCallbackUrlForSuccessOrFailureOfFileUpload,
    appConfig.upScanSuccessRedirectForUser,
    appConfig.upScanErrorRedirectForUser,
    appConfig.upScanMinFileSize,
    appConfig.upScanMaxFileSize
  )

  private[services] def buildAuthorityInitiateRequest(dutyType: String, dan: String): UpScanInitiateRequest = UpScanInitiateRequest(
    appConfig.upScanCallbackUrlForSuccessOrFailureOfFileUpload,
    appConfig.upScanAuthoritySuccessRedirectForUser + s"/$dutyType/$dan/upscan-response",
    appConfig.upScanAuthorityErrorRedirectForUser + s"/$dutyType/$dan/upscan-response",
    appConfig.upScanMinFileSize,
    appConfig.upScanMaxFileSize
  )

  def initiateNewJourney()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[UpScanInitiateResponse] = {
    upScanConnector.postToInitiate(buildInitiateRequest).map {
      case Right(upScanInitiateResponse) => upScanInitiateResponse
      case Left(error) => throw new InternalServerException(error.message)
    }
  }

  def initiateAuthorityJourney(dutyType: String, dan: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[UpScanInitiateResponse] = {
    upScanConnector.postToInitiate(buildAuthorityInitiateRequest(dutyType, dan)).map {
      case Right(upScanInitiateResponse) => upScanInitiateResponse
      case Left(error) => throw new InternalServerException(error.message)
    }
  }

}
