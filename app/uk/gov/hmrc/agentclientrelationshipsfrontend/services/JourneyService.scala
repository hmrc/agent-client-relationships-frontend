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
import uk.gov.hmrc.agentclientrelationshipsfrontend.connectors.AgentClientRelationshipsConnector
import uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.journey.routes
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.repositories.JourneyRepository
import uk.gov.hmrc.mongo.cache.DataKey

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class JourneyService @Inject()(journeyRepository: JourneyRepository,
                               agentClientRelationshipsConnector: AgentClientRelationshipsConnector
                                       )(implicit executionContext: ExecutionContext) {

  val dataKey = DataKey[Journey]("JourneySessionData")

  def saveJourney(journey: Journey)(implicit request: Request[?], ec: ExecutionContext): Future[Unit] = {
    journeyRepository.putSession(dataKey, journey).map(_ => ())
  }

  def getJourney()(implicit request: Request[Any]): Future[Option[Journey]] =
    journeyRepository.getFromSession(dataKey)

  def deleteAllAnswersInSession(implicit request: Request[Any]): Future[Unit] = {
    journeyRepository.cacheRepo.deleteEntity(request)
  }

  def newJourney(journeyType: JourneyType): Journey = Journey(
    journeyType = journeyType
  )

  def nextPageUrl(journeyType: JourneyType)(implicit request: Request[Any]): Future[String] = {
    for {
      journey <- getJourney()
    } yield journey match {
      case Some(journey) if journey.journeyType != journeyType => routes.StartJourneyController.startJourney(journeyType).url
      case Some(journey) => {
        if (journey.clientService.isEmpty) routes.SelectClientTypeController.show(journeyType).url
        else if (journey.clientDetailsResponse.isEmpty) routes.EnterClientIdController.show(journeyType).url
        else if (journey.clientDetailsResponse.get.knownFactType.nonEmpty && !journey.clientConfirmed) "routes.EnterClientFactController.show(journeyType).url"
        else if(!journey.clientConfirmed) "routes.ConfirmClientController.show(journeyType).url"
        else if (journey.getService.matches("HMRC-MTD-IT") && journey.agentType.isEmpty) "routes.SelectAgentTypeController.show(journeyType).url"
        else if (journey.hasErrors(journeyType)) "routes.JourneyErrorController.show(journeyType).url"
        else "routes.CheckYourAnswersController.show(journeyType).url"
      }
      case _ => routes.StartJourneyController.startJourney(journeyType).url
    }
  }
  
}
