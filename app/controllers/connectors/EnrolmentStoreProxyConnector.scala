/*
 * Copyright 2020 HM Revenue & Customs
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

package controllers.connectors

import com.google.inject.Inject
import config.{AppConfig, SessionKeys}
import models.responses.EnrolmentStoreProxy.GroupServicesResponse
import play.api.Logger
import play.api.http.Status.{NO_CONTENT, OK}
import play.api.mvc.Request
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import javax.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EnrolmentStoreProxyConnector @Inject()(httpClient: HttpClient)
                                            (implicit val executionContext: ExecutionContext, config: AppConfig) {

  private val logger = Logger("application." + getClass.getCanonicalName)

  def isGroupEnrolledForPaye(groupId: String, fetchFreshData: Boolean)(implicit hc: HeaderCarrier, request: Request[_]): Future[Boolean] = {

    val enrolment: Option[Boolean] = request.session.get(SessionKeys.groupEnrolledForPAYE).map(_.toBoolean)

    def fetchData: Future[Boolean] = isGroupEnrolledForPayeCall(groupId)

    if (enrolment.isEmpty) {
      fetchData
    } else if (fetchFreshData) {
      fetchData
    } else {
      Future.successful(enrolment.get)
    }
  }

  def isGroupEnrolledForPayeCall(groupId: String)(implicit hc: HeaderCarrier): Future[Boolean] = {

    val endPoint = config.enrolmentStoreUrl + s"/enrolment-store-proxy/enrolment-store/groups/$groupId/services"

    httpClient.GET[HttpResponse](endPoint).map {
      response =>
        logger.debug(s"Success response from Is Group Enrolled For PAYE connector")

        response.status match {
          case NO_CONTENT => false
          case OK => response.json.validate[GroupServicesResponse].getOrElse(GroupServicesResponse(Seq.empty)).services.contains("IR-PAYE")
        }
    }.recover {
      case e: Exception =>
        logger.warn(s"Exception from Is Group Enrolled For PAYE connector - ${e.getMessage}")
        false
    }
  }
}
