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

package uk.gov.hmrc.agentclientrelationshipsfrontend.services.journey

import play.api.mvc.Request
import play.api.mvc.Results.Redirect
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.{AgentTypeFieldName, ClientNameFieldName, ClientServiceFieldName, ClientTypeFieldName}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{Journey, JourneyState, JourneyType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.repositories.journey.JourneyRepository
import uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.journey.routes
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.mongo.cache.DataKey

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
    clientId = None
  )
  
  //TODO - change routing
  def nextPageUrl()(implicit request: Request[Any]): Future[String] = 
    for {
      mayBeJourney <- getJourney()
    } yield {
      mayBeJourney match {
        case Some(journey) =>
          journey.journeyState match
            case JourneyState.SelectClientType => routes.SelectClientTypeController.show(journey.journeyType).url
            case JourneyState.SelectService => "???" //routes.SelectClientServiceController.show.url
            case JourneyState.EnterClientId => journey.journeyType match
              case JourneyType.AuthorisationRequest | JourneyType.AgentCancelAuthorisation => ???
        case None => ??? //TODO WG - route to page where user select JOurney Type
      }
    }
  

  //TODO - 
  def previousPageUrl(journey: Journey): String = ???

  private def calculateJourneyState(journey: Journey): Journey = journey.journeyState match
    case JourneyState.SelectClientType if journey.clientType.isDefined => journey.copy(journeyState = JourneyState.SelectService)
    case JourneyState.SelectService if journey.service.isDefined => journey.copy(journeyState = JourneyState.EnterClientId)
    case JourneyState.EnterClientId if journey.clientId.isDefined => ???
    case _ => newJourney(journey.journeyType)
  
}
