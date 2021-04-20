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

package controllers

import config.AppConfig
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import javax.inject.{Inject, Singleton}
import models.FileUploadInfo
import models.upscan._
import pages.{AnyOtherSupportingDocsPage, FileUploadPage}
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.{FileUploadRepository, SessionRepository}
import services.UpScanService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.{UploadFileView, UploadProgressView}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


@Singleton
class UploadFileController @Inject()(identify: IdentifierAction,
                                     getData: DataRetrievalAction,
                                     requireData: DataRequiredAction,
                                     mcc: MessagesControllerComponents,
                                     fileUploadRepository: FileUploadRepository,
                                     sessionRepository: SessionRepository,
                                     upScanService: UpScanService,
                                     view: UploadFileView,
                                     progressView: UploadProgressView,
                                     implicit val appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport {

  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    lazy val backLink = request.userAnswers.get(AnyOtherSupportingDocsPage) match {
      case Some(true) => controllers.routes.AnyOtherSupportingDocsController.onLoad //TODO - This will need to redirect to the additional documents page
      case _ => controllers.routes.AnyOtherSupportingDocsController.onLoad
    }

    upScanService
    .initiateNewJourney().map { response =>
      Ok(view(response, backLink))
        .removingFromSession("UpscanReference")
        .addingToSession("UpscanReference" -> response.reference.value)
    }
  }

  def upscanResponseHandler(key: Option[String] = None,
                            errorCode: Option[String] = None,
                            errorMessage: Option[String] = None,
                            errorResource: Option[String] = None,
                            errorRequestId: Option[String] = None
                           ): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    if (errorCode.isDefined) {
      // TODO: Redirect to synchronous error page
      Future.successful(
        Redirect(controllers.routes.UploadFileController.onLoad)
          .flashing(
            "key" -> key.getOrElse(""),
            "errorCode" -> errorCode.getOrElse(""),
            "errorMessage" -> errorMessage.getOrElse(""),
            "errorResource" -> errorResource.getOrElse(""),
            "errorRequestId" -> errorRequestId.getOrElse("")
          )
      )
    } else {
      key match {
        case Some(key) => fileUploadRepository.updateRecord(FileUpload(key, Some(request.credId))).map { _ =>
          Thread.sleep(appConfig.upScanPollingDelayMilliSeconds)
          Redirect(controllers.routes.UploadFileController.uploadProgress(key))
        }
        case _ => throw new RuntimeException("No key returned for successful upload")
      }
    }
  }

  def uploadProgress(key: String): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    fileUploadRepository.getRecord(key).flatMap {
      case Some(doc) => doc.fileStatus match {
        case Some(status) if (status == FileStatusEnum.READY) => {
          val newFile: FileUploadInfo = FileUploadInfo(
            fileName = doc.fileName.get,
            downloadUrl = doc.downloadUrl.get,
            uploadTimestamp = doc.uploadDetails.get.uploadTimestamp,
            checksum = doc.uploadDetails.get.checksum,
            fileMimeType = doc.uploadDetails.get.fileMimeType
          )

          val updatedListFiles = request.userAnswers.get(FileUploadPage).getOrElse(Seq.empty) ++ Seq(newFile)
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(FileUploadPage, updatedListFiles)(FileUploadPage.queryWrites))
            _ <- sessionRepository.set(updatedAnswers)
          } yield {
            Redirect(controllers.routes.UploadAnotherFileController.onLoad())
          }
        }
        case Some(status) => Future.successful(Redirect(controllers.routes.UploadFileController.onLoad())) // TODO: Failure
        case None => Future.successful(Ok(progressView(key, controllers.routes.UploadFileController.onLoad)))
      }
      case None => Future.successful(InternalServerError)
    }
  }

}
