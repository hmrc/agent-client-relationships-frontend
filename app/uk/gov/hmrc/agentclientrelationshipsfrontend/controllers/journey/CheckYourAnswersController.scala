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

package uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.journey

import play.api.i18n.I18nSupport
import play.api.mvc.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.actions.Actions
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.ConfirmCancellationFieldName
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.{AppConfig, ErrorHandler}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.SubmissionResponse.RelationshipNotFound
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.SubmissionResponse.{SubmissionLocked, SubmissionSuccess}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.journey.ConfirmCancellationForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.JourneyExitType.AuthorisationAlreadyRemoved
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{AgentJourney, AgentJourneyRequest, AgentJourneyType, CheckYourAnswersShowEntry, CheckYourAnswersSubmitEntry}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{AgentClientRelationshipsService, AgentJourneyService, ClientServiceConfigurationService}
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.ProcessingYourRequestPage
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.agentJourney.{CheckYourAnswersPage, ConfirmCancellationPage}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import java.time.LocalDate
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CheckYourAnswersController @Inject()(mcc: MessagesControllerComponents,
                                           serviceConfig: ClientServiceConfigurationService,
                                           journeyService: AgentJourneyService,
                                           agentClientRelationshipsService: AgentClientRelationshipsService,
                                           checkYourAnswersPage: CheckYourAnswersPage,
                                           confirmCancellationPage: ConfirmCancellationPage,
                                           processingYourRequestPage: ProcessingYourRequestPage,
                                           errorHandler: ErrorHandler,
                                           actions: Actions
                                          )(implicit val executionContext: ExecutionContext, appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport:

  def show(journeyType: AgentJourneyType): Action[AnyContent] = actions.getAgentJourney(journeyType):
    journeyRequest =>
      given AgentJourneyRequest[?] = journeyRequest

      journeyService.checkYourAnswersShowEntry(journeyType) match
        case CheckYourAnswersShowEntry.RedirectToAgentServicesAccount =>
          Redirect(appConfig.agentServicesAccountHomeUrl)
        case CheckYourAnswersShowEntry.RedirectToSelectClientType =>
          Redirect(routes.SelectClientTypeController.show(journeyType))
        case CheckYourAnswersShowEntry.RedirectToEnterClientId =>
          Redirect(routes.EnterClientIdController.show(journeyType))
        case CheckYourAnswersShowEntry.Exit(exitType) =>
          Redirect(routes.JourneyExitController.show(journeyType, exitType))
        case CheckYourAnswersShowEntry.ShowAuthorisationRequest(data) =>
          Ok(checkYourAnswersPage(serviceConfig.supportsAgentRoles(data.service)))
        case CheckYourAnswersShowEntry.ShowAgentCancelAuthorisation =>
          Ok(confirmCancellationPage(ConfirmCancellationForm.form(ConfirmCancellationFieldName, journeyType.toString)))

  def onSubmit(journeyType: AgentJourneyType): Action[AnyContent] = actions.getAgentJourney(journeyType).async:
    journeyRequest =>
      given AgentJourneyRequest[?] = journeyRequest

      journeyService.checkYourAnswersSubmitEntry(journeyType) match
        case CheckYourAnswersSubmitEntry.RedirectToConfirmation =>
          Future.successful(Redirect(routes.ConfirmationController.show(journeyType)))
        case CheckYourAnswersSubmitEntry.RedirectToSelectClientType =>
          Future.successful(Redirect(routes.SelectClientTypeController.show(journeyType)))
        case CheckYourAnswersSubmitEntry.RedirectToEnterClientId =>
          Future.successful(Redirect(routes.EnterClientIdController.show(journeyType)))
        case CheckYourAnswersSubmitEntry.Exit(exitType) =>
          Future.successful(Redirect(routes.JourneyExitController.show(journeyType, exitType)))
        case CheckYourAnswersSubmitEntry.SubmitAuthorisationRequest(data) =>
          for {
            invitationId <- agentClientRelationshipsService.createAuthorisationRequest(data.journey)
            _ <- journeyService.saveJourney(AgentJourney(
              journeyType = data.journey.journeyType,
              journeyComplete = Some(invitationId)
            ))
          } yield Redirect(routes.ConfirmationController.show(data.journey.journeyType))
        case CheckYourAnswersSubmitEntry.SubmitAgentCancelAuthorisation(data) =>
          ConfirmCancellationForm.form(ConfirmCancellationFieldName, data.journey.journeyType.toString)
            .bindFromRequest()
            .fold(
              formWithErrors => {
                Future.successful(BadRequest(confirmCancellationPage(formWithErrors)))
              },
              confirmCancellation => {
                if confirmCancellation then agentClientRelationshipsService.cancelAuthorisation(
                  arn = journeyRequest.arn,
                  clientId = data.journey.getClientId,
                  service = data.activeRelationship
                ).flatMap {
                  case SubmissionSuccess =>
                    journeyService.saveJourney(AgentJourney(
                      journeyType = data.journey.journeyType,
                      confirmationService = data.journey.clientService,
                      confirmationClientName = Some(data.clientName),
                      journeyComplete = Some(LocalDate.now().toString)
                    )).map(_ => Redirect(routes.ConfirmationController.show(data.journey.journeyType)))
                  case SubmissionLocked =>
                    journeyService.saveJourney(data.journey.copy(backendErrorResponse = None)).map(_ =>
                      Redirect(routes.CheckYourAnswersController.processingYourRequest(data.journey.journeyType))
                    )
                  case RelationshipNotFound =>
                    journeyService.saveJourney(data.journey.copy(backendErrorResponse = Some(true))).map(_ =>
                      Redirect(routes.JourneyExitController.show(
                        journeyType = data.journey.journeyType,
                        exitType = AuthorisationAlreadyRemoved
                      ))
                    )
                }.recover {
                  case ex =>
                    journeyService.saveJourney(data.journey.copy(backendErrorResponse = Some(true)))
                    throw ex
                }
                else Future.successful(Redirect(routes.StartJourneyController.startJourney(data.journey.journeyType)))
              })

  // Deauth only
  def processingYourRequest(journeyType: AgentJourneyType): Action[AnyContent] = actions.getAgentJourney(journeyType).async:
    implicit request =>
      (request.journey.journeyComplete, request.journey.backendErrorResponse, request.journey.clientDetailsResponse) match {
        case (Some(_), _, _) =>
          Future.successful(Redirect(routes.ConfirmationController.show(request.journey.journeyType)))
        case (_, Some(_), _) =>
          errorHandler.internalServerErrorTemplate.map(InternalServerError(_))
        case (_, _, Some(_)) =>
          Future.successful(Ok(processingYourRequestPage(
            routes.CheckYourAnswersController.processingYourRequest(request.journey.journeyType).url,
            isAgent = true,
            Some(request.journey.journeyType)
          )))
        case _ =>
          Future.successful(Redirect(routes.EnterClientIdController.show(request.journey.journeyType)))
      }
