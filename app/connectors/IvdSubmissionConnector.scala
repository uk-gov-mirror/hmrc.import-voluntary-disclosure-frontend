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

package connectors

import config.AppConfig
import connectors.httpParsers.IvdHttpParser.*
import connectors.httpParsers.ResponseHttpParser.{HttpGetResult, HttpPostResult}
import models.{EoriDetails, SubmissionResponse, UpdateCaseError, UpdateCaseResponse}
import play.api.libs.json.JsValue
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2

import java.net.URI
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class IvdSubmissionConnector @Inject() (val http: HttpClientV2)(implicit
  val config: AppConfig,
  val ec: ExecutionContext
) {

  private[connectors] def getEoriDetailsUrl(id: String) =
    s"${config.importVoluntaryDisclosureSubmission}/api/eoriDetails?id=$id"

  private[connectors] def createCaseUrl = s"${config.importVoluntaryDisclosureSubmission}/api/case"

  private[connectors] def updateCaseUrl = s"${config.importVoluntaryDisclosureSubmission}/api/update-case"

  def getEoriDetails(id: String)(implicit hc: HeaderCarrier): Future[HttpGetResult[EoriDetails]] =
    http.get(new URI(getEoriDetailsUrl(id)).toURL).execute[HttpGetResult[EoriDetails]]

  def createCase(submission: JsValue)(implicit hc: HeaderCarrier): Future[HttpPostResult[SubmissionResponse]] =
    http.post(new URI(createCaseUrl).toURL).withBody(submission).execute[HttpPostResult[SubmissionResponse]]

  def updateCase(update: JsValue)(implicit hc: HeaderCarrier): Future[Either[UpdateCaseError, UpdateCaseResponse]] =
    http.post(new URI(updateCaseUrl).toURL).withBody(update).execute[Either[UpdateCaseError, UpdateCaseResponse]]

}
