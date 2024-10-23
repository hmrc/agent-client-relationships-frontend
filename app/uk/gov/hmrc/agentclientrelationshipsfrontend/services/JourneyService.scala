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

package uk.gov.hmrc.agentclientrelationshipsfrontend.services

import play.api.mvc.Request
import play.api.mvc.Results.Redirect
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.{AgentTypeFieldName, ClientNameFieldName, ClientServiceFieldName, ClientTypeFieldName}
import uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.createInvitation.routes
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{Journey, JourneyType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.repositories.JourneyRepository
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.mongo.cache.DataKey
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.JourneyState
import uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.test.{routes => testRoutes}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class JourneyService @Inject()(journeyRepository: JourneyRepository
                                       )(implicit executionContext: ExecutionContext) {

  val dataKey = DataKey[Journey]("JourneySessionData")

  def saveJourney(journey: Journey)(implicit request: Request[?], ec: ExecutionContext): Future[Unit] = {

    journeyRepository.putSession(dataKey, calculateJourneyState(journey)).map(_ => ())
  }

  def getJourney()(implicit request: Request[Any]): Future[Option[Journey]] =
    journeyRepository.getFromSession(dataKey)

  def deleteAllAnswersInSession(implicit request: Request[Any]): Future[Unit] = {
    journeyRepository.cacheRepo.deleteEntity(request)
  }

  def newJourney(journeyType: JourneyType): Journey = Journey(
    journeyType = journeyType,
    journeyState = JourneyState.SelectClientType,
    clientType = None,
    service = None,
    clientName = None,
    clientConfirmed = None,
    agentType = None,
    suspensionDetails = None,
    AgentReferenceRecord = None,
    storedInvitation = None,
    checkRelationship = None,
    clientDetails = Map.empty[String, String],
    clientDetailsConfirmed = None,
    invitationCreated = None
  )
  
  def nextPageUrl(clientServiceConfig: ClientServiceConfigurationService)(implicit request: Request[Any]): Future[String] =
    for {
      mayBeJourney <- getJourney()
    } yield {
      mayBeJourney match {
        case Some(journey) =>
          journey.journeyType match
            case JourneyType.CreateInvitation =>
              journey.journeyState match
                case JourneyState.SelectClientType => routes.SelectClientServiceController.show.url
                case JourneyState.SelectService => routes.SelectClientServiceController.show.url
                case JourneyState.EnterClientDetails => journey.clientDetails.keys.size match
                  case 0 => routes.EnterClientDetailsController.show(clientServiceConfig.firstClientDetailsFieldFor(journey.getServiceWithDefault).name).url
                  case 1 => routes.EnterClientDetailsController.show(clientServiceConfig.firstClientDetailsFieldFor(journey.getServiceWithDefault).name).url
                case JourneyState.ConfirmClient => routes.ConfirmClientController.show.url
                case JourneyState.SelectAgentType => routes.SelectAgentTypeController.show.url
                case JourneyState.CheckDetails => routes.CheckYourAnswersController.show.url
                case JourneyState.CreateInvitation => ???
                case JourneyState.ClientAlreadyAuthorised => ???
            case JourneyType.CreateInvitationFastTrack => ???
        case None => testRoutes.StartInvitationController.startCreateInvitationJourney().url

      }
    }


  def previousPageUrl(journey: Journey): String = ???

  private def calculateJourneyState(journey: Journey): Journey = journey.journeyState match
    case JourneyState.SelectClientType =>
      if (journey.clientType.isDefined)
        journey.copy(journeyState = JourneyState.SelectService)
      else newJourney(journey.journeyType)

    case JourneyState.SelectService =>
      if (journey.service.isDefined) journey.copy(journeyState = JourneyState.EnterClientDetails)
      else newJourney(journey.journeyType)

    case JourneyState.EnterClientDetails => journey.clientDetails.keys.size match
      case 1 => journey.copy(journeyState = JourneyState.EnterClientDetails)
      case 2 => journey.copy(journeyState = JourneyState.ConfirmClient)
      case _ => newJourney(journey.journeyType)

    case JourneyState.ConfirmClient =>
      if (journey.clientConfirmed.isDefined && journey.clientConfirmed.flatMap(_.toBooleanOption).getOrElse(true)) journey.copy(journeyState = JourneyState.SelectAgentType)
      else newJourney(journey.journeyType)

    case JourneyState.SelectAgentType =>
      if (journey.agentType.isDefined) journey.copy(journeyState = JourneyState.CheckDetails)
      else newJourney(journey.journeyType)

    case JourneyState.CheckDetails =>
      if (journey.clientDetailsConfirmed.isDefined && journey.clientDetailsConfirmed.flatMap(_.toBooleanOption).getOrElse(true)) journey.copy(journeyState = JourneyState.CreateInvitation)
      else newJourney(journey.journeyType)

    case JourneyState.CreateInvitation => journey //TODO WG - that is last State

    case JourneyState.ClientAlreadyAuthorised => newJourney(journey.journeyType)

}
