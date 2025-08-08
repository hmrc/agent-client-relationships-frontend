/*
 * Copyright 2024 HM Revenue & Customs
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

package uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.client

import com.google.inject.Inject
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.agentclientrelationshipsfrontend.actions.Actions
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.{AppConfig, ErrorHandler}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.SubmissionResponse.RelationshipNotFound
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.SubmissionResponse.{SubmissionLocked, SubmissionSuccess}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.AuthorisationsCache
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.client.ConfirmDeauthForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{AgentClientRelationshipsService, AuthorisationsCacheService, ClientServiceConfigurationService}
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.client.{ConfirmDeauthPage, ConfirmationOfDeauthPage, ManageYourTaxAgentsPage, RelationshipNotFoundPage}
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.{PageNotFound, ProcessingYourRequestPage}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ManageYourTaxAgentsController @Inject()(
                                               serviceConfigurationService: ClientServiceConfigurationService,
                                               agentClientRelationshipsService: AgentClientRelationshipsService,
                                               mytaPage: ManageYourTaxAgentsPage,
                                               confirmDeauthPage: ConfirmDeauthPage,
                                               deauthCompletePage: ConfirmationOfDeauthPage,
                                               pageNotFound: PageNotFound,
                                               processingYourRequestPage: ProcessingYourRequestPage,
                                               relationshipNotFoundPage: RelationshipNotFoundPage,
                                               authorisationsCacheService: AuthorisationsCacheService,
                                               errorHandler: ErrorHandler,
                                               actions: Actions,
                                               mcc: MessagesControllerComponents
                                             )(implicit val executionContext: ExecutionContext, appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport:

  def show: Action[AnyContent] = actions.clientAuthorised.async:
    implicit request =>
      for {
        mytaData <- agentClientRelationshipsService.getManageYourTaxAgentsData()
        _ <- authorisationsCacheService
          .putAuthorisations(AuthorisationsCache(
            authorisations = Seq(mytaData.agentsAuthorisations.agentsAuthorisations).flatten.flatMap(_.authorisations))
          )
      } yield Ok(mytaPage(serviceConfigurationService.allEnabledServices.map(s => (s, serviceConfigurationService.getUrlPart(s))).toMap, mytaData))

  def showConfirmDeauth(id: String): Action[AnyContent] = actions.clientAuthorised.async:
    implicit request =>
      authorisationsCacheService.getAuthorisation(id).map {
        case Some(authorisation) if authorisation.deauthorised.isEmpty => Ok(confirmDeauthPage(ConfirmDeauthForm.form, authorisation))
        case Some(_) => Redirect(routes.ManageYourTaxAgentsController.show)
        case None => NotFound(pageNotFound())
      }

  def submitDeauth(id: String): Action[AnyContent] = actions.clientAuthorised.async:
    implicit request =>
      ConfirmDeauthForm.form.bindFromRequest().fold(
        formWithErrors => authorisationsCacheService.getAuthorisation(id).map {
          case Some(authorisation) if authorisation.deauthorised.isEmpty => BadRequest(confirmDeauthPage(formWithErrors, authorisation))
          case _ => NotFound(pageNotFound())
        },
        confirmDeauth =>
          authorisationsCacheService.getAuthorisation(id).flatMap {
            case Some(authorisation) if authorisation.deauthorised.isEmpty =>
              if confirmDeauth then
                agentClientRelationshipsService.cancelAuthorisation(authorisation.arn, authorisation.clientId, authorisation.service).flatMap {
                  case SubmissionSuccess =>
                    // destroy all other authorisation items from session cache and mark this one as deauthorised
                    authorisationsCacheService.putAuthorisations(AuthorisationsCache(
                      authorisations = Seq(authorisation.copy(deauthorised = Some(true)))
                    )).map(_ => Redirect(routes.ManageYourTaxAgentsController.deauthComplete(id)))
                  case SubmissionLocked =>
                    // Ensuring we remove the leftovers from the previous lock
                    authorisationsCacheService.putAuthorisations(AuthorisationsCache(Seq(authorisation), backendErrorResponse = None))
                      .map(_ => Redirect(routes.ManageYourTaxAgentsController.processingYourRequest(id)))
                  case RelationshipNotFound =>
                    // This ensures the submissionInProgress is aware the original request failed
                    authorisationsCacheService.putAuthorisations(AuthorisationsCache(Seq(authorisation), backendErrorResponse = Some(true)))
                      .map(_ => Redirect(routes.ManageYourTaxAgentsController.handleRelationshipNotFound().url))
                }.recover {
                  case ex =>
                    // This ensures the submissionInProgress is aware the original request failed
                    authorisationsCacheService.putAuthorisations(AuthorisationsCache(Seq(authorisation), backendErrorResponse = Some(true)))
                    throw ex
                }
              else Future.successful(Redirect(routes.ManageYourTaxAgentsController.show))
            case _ => Future.successful(Redirect(routes.ManageYourTaxAgentsController.show))
          }
      )

  def deauthComplete(id: String): Action[AnyContent] = actions.clientAuthorised.async:
    implicit request =>
      authorisationsCacheService.getAuthorisation(id).flatMap {
        case Some(authorisation) if authorisation.deauthorised.contains(true) => Future.successful(Ok(deauthCompletePage(authorisation)))
        case _ => Future.successful(NotFound(pageNotFound()))
      }

  // Deauth only
  def processingYourRequest(id: String): Action[AnyContent] = actions.clientAuthorised.async:
    implicit request =>
      authorisationsCacheService.getAuthorisations.flatMap {
        case Some(AuthorisationsCache(auths, _)) if auths.exists(auth => auth.uid == id && auth.deauthorised.contains(true)) =>
          Future.successful(Redirect(routes.ManageYourTaxAgentsController.deauthComplete(id)))
        case Some(AuthorisationsCache(_, Some(_))) =>
          errorHandler.internalServerErrorTemplate.map(InternalServerError(_))
        case Some(AuthorisationsCache(auth :: _, _)) =>
          Future.successful(Ok(processingYourRequestPage(
            routes.ManageYourTaxAgentsController.processingYourRequest(id).url,
            isAgent = false
          )))
        case _ =>
          Future.successful(Redirect(routes.ManageYourTaxAgentsController.show))
      }

  def handleRelationshipNotFound(): Action[AnyContent] = actions.clientAuthorised:
    implicit request =>
      Ok(relationshipNotFoundPage())
