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

package connectors

import config.AppConfig
import connectors.httpParsers.ResponseHttpParser.{HttpGetResult, HttpPostResult}
import connectors.httpParsers.IVDSubmissionHttpParser._
import models.{IVDSubmission, SubmissionResponse, ContactAddress}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class IVDSubmissionConnector @Inject()(val http: HttpClient,
                                       implicit val config: AppConfig){

  private[connectors] def getAddressUrl(id: String) = s"${config.importVoluntaryDisclosureSubmission}/api/address?id=$id"
  private[connectors] def postSubmissionUrl = s"${config.importVoluntaryDisclosureSubmission}/api/case"

  def getAddress(id: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[ContactAddress]] = {
    http.GET[HttpGetResult[ContactAddress]](getAddressUrl(id))
  }

  def postSubmission(submission: IVDSubmission)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpPostResult[SubmissionResponse]] = {
    http.POST[IVDSubmission, HttpPostResult[SubmissionResponse]](postSubmissionUrl, submission)
  }

}
