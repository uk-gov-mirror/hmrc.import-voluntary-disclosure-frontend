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

package config

object SessionKeys {

  // TODO if this is needed update the names to match the project one
  val acknowledgementReference = "IRR_ACK_REF"
  val employerData = "CVJRS_EMPLOYER_DATA"
  val empref = "CVJRS_EMPREF"
  val upscanReference = "CVJRS_UPSCAN_REFERENCE"
  val successRedirectForUser = "CJVRS_UPLOAD_SUCCESS_REDIRECT_URL"
  val errorRedirectForUser = "CJVRS_UPLOAD_FAILURE_REDIRECT_URL"
  val userType = "CJVRS_USER_TYPE"
  val agentReturnJourneyUrl = "CJVRS_AGENT_RETURN_URL"
  val groupEnrolledForPAYE = "CJVRS_GROUP_ENROLLED_FOR_PAYE"
  val deleteClaimDetails = "CVJRS_DELETE_CLAIM_DETAILS"
  val startedBeforePreJulyClassicV1JourneyDisabled = "CJVRS_STARTED_BEFORE_PRE_JULY_CLASSIC_V1_JOURNEY_DISABLED"

}
