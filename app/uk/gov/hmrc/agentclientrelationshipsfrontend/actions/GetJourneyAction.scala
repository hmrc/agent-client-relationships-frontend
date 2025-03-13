/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.agentclientrelationshipsfrontend.actions

import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionFunction, Request, Result}
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{AgentJourneyRequest, AgentJourneyType, ClientJourney, ClientJourneyRequest}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{AgentJourneyService, ClientJourneyService}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetJourneyAction @Inject()(agentJourneyService: AgentJourneyService,
                                 clientJourneyService: ClientJourneyService,
                                 appConfig: AppConfig
                                )(implicit ec: ExecutionContext) {

  def agentJourneyAction(journeyTypeFromUrl: AgentJourneyType): ActionFunction[AgentRequest, AgentJourneyRequest] = new ActionFunction[AgentRequest, AgentJourneyRequest]:
    override protected def executionContext: ExecutionContext = ec

    override def invokeBlock[A](request: AgentRequest[A], block: AgentJourneyRequest[A] => Future[Result]): Future[Result] =
      given AgentRequest[A] = request

      agentJourneyService.getJourney.flatMap {
        case Some(journey) if journey.journeyType == journeyTypeFromUrl =>
          block(new AgentJourneyRequest(request.arn, journey, request.request))
        case _ =>
          Future.successful(Redirect(appConfig.agentServicesAccountHomeUrl))
      }

  def clientJourneyAction: ActionFunction[Request, ClientJourneyRequest] = new ActionFunction[Request, ClientJourneyRequest]:
    override protected def executionContext: ExecutionContext = ec

    override def invokeBlock[A](request: Request[A], block: ClientJourneyRequest[A] => Future[Result]): Future[Result] =
      given Request[A] = request

      clientJourneyService.getJourney.flatMap {
        mJourney => block(ClientJourneyRequest(mJourney.getOrElse(ClientJourney(journeyType = "authorisation-response")), request))
      }
}
