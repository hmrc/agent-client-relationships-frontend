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
        if (journey.clientService.isEmpty) routes.SelectClientTypeController.show(journeyType).url // this page is hard coded to always redirect to Select Tax Service
        // this next step makes the back end call onSubmit of the EnterClientIdController to get the client details needed to complete the journey
        // so fast-track posts for example may not need to go through this step via the UI but instead could make the same back end call within the post handler
        // and then call this routing method to determine the next step with the clientDetailsResponse in place
        else if (journey.clientDetailsResponse.isEmpty) "routes.EnterClientIdController.show(journeyType).url"
        else if (journey.clientDetailsResponse.get.knownFactType.nonEmpty && !journey.clientConfirmed) "routes.EnterClientVerifierController.show(journeyType).url"
        else if (journey.getService.matches("HMRC-MTD-IT") && journey.agentType.isEmpty) "routes.SelectAgentTypeController.show(journeyType).url"
        // this next step uses a method on the ClientDetailsResponse model to determine if errors exist with the journey
        // e.g. authorisation already exists or client is insolvent, the JourneyErrorController can produce a code/slug for the error page to use
        else if (journey.clientDetailsResponse.get.hasErrors(journeyType)) "routes.JourneyErrorController.show(journeyType).url"
        else "routes.CheckYourAnswersController.show(journeyType).url"
      }
      case _ => routes.StartJourneyController.startJourney(journeyType).url
    }
  }
  
}
