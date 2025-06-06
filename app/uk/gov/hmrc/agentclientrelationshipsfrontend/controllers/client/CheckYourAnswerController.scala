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

import com.google.inject.{Inject, Singleton}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.agentclientrelationshipsfrontend.actions.Actions
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.{AppConfig, ErrorHandler}
import uk.gov.hmrc.agentclientrelationshipsfrontend.connectors.AgentClientRelationshipsConnector
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.SubmissionResponse.{SubmissionLocked, SubmissionSuccess}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.ClientJourney
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.ClientJourneyService
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.ProcessingYourRequestPage
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.client.CheckYourAnswerPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CheckYourAnswerController @Inject()(mcc: MessagesControllerComponents,
                                          actions: Actions,
                                          agentClientRelationshipsConnector: AgentClientRelationshipsConnector,
                                          checkYourAnswerPage: CheckYourAnswerPage,
                                          processingYourRequestPage: ProcessingYourRequestPage,
                                          journeyService: ClientJourneyService,
                                          errorHandler: ErrorHandler
                                         )(implicit ec: ExecutionContext, appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport:

  def show: Action[AnyContent] = actions.clientJourneyRequired:
    implicit request =>
      if request.journey.consent.isDefined then Ok(checkYourAnswerPage())
      else Redirect(routes.ManageYourTaxAgentsController.show)

  def submit: Action[AnyContent] = actions.clientJourneyRequired.async:
    implicit request =>
      val consentAnswer: Option[Boolean] = request.journey.consent
      val invitationId: Option[String] = request.journey.invitationId

      (request.journey.consent, request.journey.invitationId, request.journey.journeyComplete) match {
        case (Some(true), Some(invId), _) =>
          agentClientRelationshipsConnector.acceptAuthorisation(invId).flatMap {
            case SubmissionSuccess =>
              journeyService.saveJourney(ClientJourney(
                journeyType = request.journey.journeyType,
                journeyComplete = Some(invId)
              )).map(_ => Redirect(routes.ConfirmationController.show))
            // Ensuring we remove the leftovers from the previous lock
            case SubmissionLocked =>
              journeyService.saveJourney(request.journey.copy(backendErrorResponse = None)).map(_ =>
                Redirect(routes.CheckYourAnswerController.processingYourRequest)
              )
          }.recover {
            case ex =>
              // This ensures the submissionInProgress is aware the original request failed
              journeyService.saveJourney(request.journey.copy(backendErrorResponse = Some(true)))
              throw ex
          }
        case (Some(false), Some(invId), _) => for {
          _ <- agentClientRelationshipsConnector.rejectAuthorisation(invId)
          _ <- journeyService.saveJourney(ClientJourney(
            journeyType = request.journey.journeyType,
            journeyComplete = Some(invId)
          ))
        } yield Redirect(routes.ConfirmationController.show)
        case _ =>
          Future.successful(Redirect(routes.ManageYourTaxAgentsController.show))
      }

  def processingYourRequest: Action[AnyContent] = actions.clientJourneyRequired.async:
    implicit request =>
      (request.journey.journeyComplete, request.journey.backendErrorResponse, request.journey.consent) match {
        case (Some(_), _, _) => Future.successful(Redirect(routes.ConfirmationController.show))
        case (_, Some(_), _) => errorHandler.internalServerErrorTemplate.map(InternalServerError(_))
        case (_, _, Some(_)) => Future.successful(Ok(processingYourRequestPage(routes.CheckYourAnswerController.processingYourRequest.url, isAgent = false)))
        case _ => Future.successful(Redirect(routes.ManageYourTaxAgentsController.show))
      }