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

package models.audit

import play.api.libs.json.{JsValue, Json}
import services.JsonAuditModel
import uk.gov.hmrc.auth.core.AffinityGroup
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Organisation}

case class IndividualIneligibleAuditEvent(credId: String) extends JsonAuditModel {
  override val auditType: String = "NotEligibleToClaim"
  override val transactionName = "not-eligible-to-claim"
  override val detail = Json.obj(
    "userType" -> "individual",
    "ineligibilityReason" -> "Account type is individual",
    "credId" -> credId
  )
}

case class AgentIneligibleAuditEvent(credId: String, arn: Option[String]) extends JsonAuditModel {
  override val auditType = "NotEligibleToClaim"
  override val transactionName: String = "not-eligible-to-claim"
  override val detail: JsValue = jsonObjNoNulls(
    "userType" -> "agent",
    "ineligibilityReason" -> "Account type is agent",
    "credId" -> credId,
    "arn" -> arn
  )
}

case class AgentNotAuthorisedAuditEvent(credId: Option[String], arn: Option[String]) extends JsonAuditModel {
  override val auditType = "NotEligibleToClaim"
  override val transactionName: String = "not-eligible-to-claim"
  override val detail: JsValue = jsonObjNoNulls(
    "userType" -> "agent",
    "ineligibilityReason" -> "Agent not authorised",
    "credId" -> credId,
    "arn" -> arn
  )
}

case class MissingEnrolmentAuditEvent(credId: String) extends JsonAuditModel {
  override val auditType = "NotEligibleToClaim"
  override val transactionName: String = "not-eligible-to-claim"
  override val detail: JsValue = Json.obj(
    "userType" -> "organisation",
    "ineligibilityReason" -> "Organisation does not have ePAYE enrolment",
    "credId" -> credId
  )
}

case class UnauthorisedUserAuditEvent(credId: String, arn: Option[String]) extends JsonAuditModel {
  override val auditType = "NotEligibleToClaim"
  override val transactionName: String = "not-eligible-to-claim"
  override val detail: JsValue = jsonObjNoNulls(
    "userType" -> arn.fold[AffinityGroup](Organisation)(_ => Agent).toString.toLowerCase,
    "ineligibilityReason" -> "User in organisation is not authorised",
    "agentReferenceNumber" -> arn,
    "credId" -> credId
  )
}

