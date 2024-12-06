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

package uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey

import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.invitationLink.InvitationStatus

import java.time.Instant

case class ClientJourney(
                          journeyType: JourneyType,
                          consent: Option[Boolean] = None,
                          invitationId: Option[String] = None,
                          serviceKey: Option[String] = None,
                          agentName: Option[String] = None,
                          status: Option[InvitationStatus] = None,
                          lastModifiedDate: Option[Instant] = None
                        ) {
  def getAgentName: String = agentName.getOrElse(throw new RuntimeException("Agent Name is missing"))
  def getInvitationId: String = invitationId.getOrElse(throw new RuntimeException("Invitation Id is missing"))
  def getServiceKey: String = serviceKey.getOrElse(throw new RuntimeException("Service Key is missing"))
  def getConsent: Boolean = consent.getOrElse(throw new RuntimeException("Consent value is missing"))
}

object ClientJourney {
  implicit val format: Format[ClientJourney] = Json.format[ClientJourney]
}
