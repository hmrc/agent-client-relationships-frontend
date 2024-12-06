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

import play.api.mvc.{ActionBuilder, AnyContent, DefaultActionBuilder}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{AgentJourneyRequest, ClientJourneyRequest, JourneyType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.AgentFastTrackRequestWithRedirectUrls

import javax.inject.{Inject, Singleton}

@Singleton
class Actions @Inject()(
    actionBuilder:    DefaultActionBuilder,
    getJourneyAction: GetJourneyAction,
    authActions:      AuthActions,
    getFastTrackUrlAction : GetFastTrackUrlAction
) {
  def getAgentJourney(journeyTypeFromUrl: JourneyType): ActionBuilder[AgentJourneyRequest, AnyContent] =
    actionBuilder andThen authActions.agentAuthAction andThen getJourneyAction.agentJourneyAction(journeyTypeFromUrl)

  def agentAuthenticate: ActionBuilder[AgentRequest, AnyContent] =
    actionBuilder andThen authActions.agentAuthAction

  def getFastTrackUrl: ActionBuilder[AgentFastTrackRequestWithRedirectUrls, AnyContent] =
    actionBuilder andThen authActions.agentAuthAction andThen getFastTrackUrlAction.getFastTrackUrlAction

  def getClientJourney(taxService: String): ActionBuilder[ClientJourneyRequest, AnyContent] =
    actionBuilder andThen authActions.clientAuthActionWithEnrolmentCheck(taxService) andThen getJourneyAction.clientJourneyAction

  def clientAuthenticate: ActionBuilder[ClientJourneyRequest, AnyContent] =
    actionBuilder andThen authActions.clientAuthAction andThen getJourneyAction.clientJourneyAction
}