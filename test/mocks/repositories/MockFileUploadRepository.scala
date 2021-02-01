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

package mocks.repositories

import base.RepositorySpecBase
import models.upscan.FileUpload
import org.scalamock.handlers.CallHandler
import repositories.FileUploadRepository

import scala.concurrent.{ExecutionContext, Future}

trait MockFileUploadRepository extends RepositorySpecBase {

  val mockFileUploadRepository: FileUploadRepository = mock[FileUploadRepository]

  object MockedFileUploadRepository {

    def updateRecord(response: Future[Boolean]): CallHandler[Future[Boolean]] =
      (mockFileUploadRepository.updateRecord(_: FileUpload)(_: ExecutionContext))
        .expects(*, *)
        .returning(response)

    def getRecord(response: Future[Option[FileUpload]]): CallHandler[Future[Option[FileUpload]]] =
      (mockFileUploadRepository.getRecord(_: String)(_: ExecutionContext))
        .expects(*, *)
        .returning(response)

  }

}