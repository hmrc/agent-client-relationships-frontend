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

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.createInvitation.routes
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.common.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.ClientServiceConfigurationService

case class Journey(journeyType: JourneyType,
                   journeyState: JourneyState,
                   clientType: Option[String],
                   service: Option[String],
                   clientId: Option[String]
                  ) {
  
  def getClientTypeWithDefault:String = clientType.getOrElse("")
  def getClientType: String = clientType.getOrElse(throw new RuntimeException("clientType not defined"))

  def getServiceWithDefault: String = service.getOrElse("")
  def getService: String = service.getOrElse(throw new RuntimeException("service not defined"))
  
}

object Journey {
  implicit lazy val format: OFormat[Journey] = Json.format[Journey]
}

