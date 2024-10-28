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

import play.api.libs.json.{Json, OFormat, Reads}

import java.net.URL
import java.time.{LocalDate, LocalDateTime}

case class StoredInvitation(
                             arn: String, //TODO WG - change to ARN
                             clientType: Option[String],
                             service: String,
                             clientId: String,
                             clientIdType: String,
                             suppliedClientId: String,
                             suppliedClientIdType: String,
                             detailsForEmail: Option[DetailsForEmail],
                             status: String,
                             created: LocalDateTime,
                             lastUpdated: LocalDateTime,
                             expiryDate: LocalDate,
                             invitationId: String,
                             isRelationshipEnded: Boolean,
                             relationshipEndedBy: Option[String] = None//,
                             //selfUrl: URL
                           ) {
//  val altItsa: Option[Boolean] = if (service == Service.MtdIt) Some(clientId == suppliedClientId) else None
}

object StoredInvitation {
  
  implicit val formats: OFormat[StoredInvitation] = Json.format[StoredInvitation]
  
  def apply(
             arn: String,
             clientType: Option[String],
             service: String,
             clientId: String,
             detailsForEmail: Option[DetailsForEmail],
             status: String,
             created: LocalDateTime,
             lastUpdated: LocalDateTime,
             expiryDate: LocalDate,
             invitationId: String,
             isRelationshipEnded: Boolean,
             relationshipEndedBy: Option[String]//,
//             selfUrl: URL //TODO WG - TBC
           ): StoredInvitation =
    StoredInvitation(
      arn,
      clientType,
      service,
      clientId,
      "", //service.supportedSuppliedClientIdType.id, //TODO WG - TBC
      clientId,
      "", //service.supportedSuppliedClientIdType.id, //TODO WG - TBC
      detailsForEmail,
      status,
      created,
      lastUpdated,
      expiryDate,
      invitationId,
      isRelationshipEnded,
      relationshipEndedBy//,
//      selfUrl
    )

}

case class DetailsForEmail(agencyEmail: String, agencyName: String, clientName: String)
object DetailsForEmail {
  implicit val formats: OFormat[DetailsForEmail] = Json.format[DetailsForEmail]
}
