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

package uk.gov.hmrc.agentclientrelationshipsfrontend.models.common

import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.ClientType
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{JourneyErrors, AgentJourneyType}

case class ServiceData(
                        serviceOption: Boolean = false,
                        supportedAgentRoles: Seq[String] = Seq.empty,
                        supportedEnrolments: Seq[String] = Seq.empty,
                        serviceName: String,
                        overseasServiceName: Option[String] = None,
                        urlPart: Map[String, Set[String]],
                        clientTypes: Set[ClientType],
                        clientDetails: Seq[ClientDetailsConfiguration],
                        journeyErrors: Map[AgentJourneyType, JourneyErrors] = Map(
                          AgentJourneyType.AuthorisationRequest -> JourneyErrors(),
                          AgentJourneyType.AgentCancelAuthorisation -> JourneyErrors()
                        )
                      )
