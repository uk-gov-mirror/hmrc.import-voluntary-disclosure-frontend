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
import models.{FileUploadInfo, UploadAuthority}
import models.upscan._
import pages.{SplitPaymentPage, UploadAuthorityPage}
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.{FileUploadRepository, SessionRepository}
import services.{FlowService, UpScanService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.{UploadAuthorityProgressView, UploadAuthoritySuccessView, UploadAuthorityView}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


@Singleton
class UploadAuthorityController @Inject()(identify: IdentifierAction,
                                          getData: DataRetrievalAction,
                                          requireData: DataRequiredAction,
                                          mcc: MessagesControllerComponents,
                                          fileUploadRepository: FileUploadRepository,
                                          sessionRepository: SessionRepository,
                                          upScanService: UpScanService,
                                          flowService: FlowService,
                                          view: UploadAuthorityView,
                                          progressView: UploadAuthorityProgressView,
                                          successView: UploadAuthoritySuccessView,
                                          implicit val appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport {

  private def backLink(currentDutyType: String, dan: String, selectedDutyTypes: String, splitPayment: Boolean): Call = {
    selectedDutyTypes match {
      case "both" if splitPayment && currentDutyType=="duty" => controllers.routes.UploadAuthorityController.onLoad(currentDutyType, dan) // RepDanDuty
      case "both" if splitPayment && currentDutyType=="vat" => controllers.routes.UploadAuthorityController.onLoad(currentDutyType, dan) // RepDanVat
      case _ => controllers.routes.RepresentativeDanController.onLoad()
    }
  }

  def onLoad(dutyType: String, dan: String): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val splitPayment = request.userAnswers.get(SplitPaymentPage).getOrElse(false)
    val dutyTypeKey = dutyType match {
      case "vat" => "uploadAuthority.vat"
      case "duty" => "uploadAuthority.duty"
      case "both" => "uploadAuthority.both"
      case _ => "Underpayment Type not found"
    }

    upScanService.initiateAuthorityJourney(dutyType, dan).map { response =>
      Ok(view(response, backLink(dutyType, dan, flowService.dutyType(request.userAnswers), splitPayment), dan, dutyTypeKey))
        .removingFromSession("AuthorityUpscanReference")
        .addingToSession("AuthorityUpscanReference" -> response.reference.value)
    }
  }

  def upscanResponseHandler(dutyType: String,
                            dan: String,
                            key: Option[String] = None,
                            errorCode: Option[String] = None,
                            errorMessage: Option[String] = None,
                            errorResource: Option[String] = None,
                            errorRequestId: Option[String] = None
                           ): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    if (errorCode.isDefined) {
      // TODO: Redirect to synchronous error page
      Future.successful(
        Redirect(controllers.routes.UploadAuthorityController.onLoad(dutyType, dan))
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
          Redirect(controllers.routes.UploadAuthorityController.uploadProgress(dutyType, dan, key))
        }
        case _ => throw new RuntimeException("No key returned for successful upload")
      }
    }
  }

  def uploadProgress(dutyType: String, dan: String, key: String): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    fileUploadRepository.getRecord(key).flatMap {
      case Some(doc) => doc.fileStatus match {
        case Some(status) if (status == FileStatusEnum.READY) => {
           val newAuthority: UploadAuthority = UploadAuthority(
            dan = request.session.get("dan").getOrElse("Deferment Account not found"),
            dutyType = request.session.get("dutyType").getOrElse("Underpayment Type not found"),
            file = FileUploadInfo(
              fileName = doc.fileName.get,
              downloadUrl = doc.downloadUrl.get,
              uploadTimestamp = doc.uploadDetails.get.uploadTimestamp,
              checksum = doc.uploadDetails.get.checksum,
              fileMimeType = doc.uploadDetails.get.fileMimeType
            )
          )

          val updatedList = request.userAnswers.get(UploadAuthorityPage).getOrElse(Seq.empty) ++ Seq(newAuthority)
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(UploadAuthorityPage, updatedList)(UploadAuthorityPage.queryWrites))
            _ <- sessionRepository.set(updatedAnswers)
          } yield {
            Redirect(controllers.routes.UploadAuthorityController.onSuccess(dutyType, dan))
              .addingToSession("AuthorityFilename" -> newAuthority.file.fileName)
          }
        }
        case Some(status) => Future.successful(Redirect(controllers.routes.UploadAuthorityController.onLoad(dutyType, dan))) // TODO: Failure
        case None => Future.successful(Ok(
          progressView(key = key,
            backLink = controllers.routes.UploadAuthorityController.onLoad(dutyType, dan),
            action = controllers.routes.UploadAuthorityController.uploadProgress(dutyType, dan, key).url)
        ))
      }
      case None => Future.successful(InternalServerError)
    }
  }

  def onSuccess(dutyType: String, dan: String): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val splitPayment = request.userAnswers.get(SplitPaymentPage).getOrElse(false)

    val action = flowService.dutyType(request.userAnswers) match {
      case "both" if splitPayment && dutyType=="duty" => controllers.routes.UploadAuthorityController.onLoad(dutyType, dan).url
      case _ => controllers.routes.CheckYourAnswersController.onLoad().url
    }

    Future.successful(
      Ok(successView(
        fileName = request.session.get("AuthorityFilename").getOrElse("No filename"),
        action = action)
      ).removingFromSession("AuthorityFilename")
    )
  }

}
