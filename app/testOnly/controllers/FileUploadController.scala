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

package testOnly.controllers

import play.api.i18n.I18nSupport
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, MessagesControllerComponents}
import repositories.FileUploadRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class FileUploadController @Inject()(val mcc: MessagesControllerComponents,
                                     repository: FileUploadRepository)
  extends FrontendController(mcc) with I18nSupport {

  def deleteAll(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    repository.testOnlyRemoveAllRecords() map { result => Ok(Json.obj("count" -> result.n)) }
  }

  def delete(reference: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    repository.deleteRecord(reference) map { result => Ok(Json.obj("success" -> result)) }
  }

}
