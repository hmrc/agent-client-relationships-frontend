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

package uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.createInvitation

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, MessagesRequest}
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.ClientNameFieldName
import uk.gov.hmrc.agentclientrelationshipsfrontend.connectors.AgentClientRelationshipsConnector
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{ClientServiceConfigurationService, CreateInvitationService}
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.createInvitation.invitation_created
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.LocalDate

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class InvitationCreatedController @Inject()(mcc: MessagesControllerComponents,
                                            appConfig: AppConfig,
                                            view: invitation_created,
                                            clientServiceConfig: ClientServiceConfigurationService,
                                            createInvitationService: CreateInvitationService,
                                            agentClientRelationshipsConnector: AgentClientRelationshipsConnector
                                       )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc):
  
  private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

  def show(invitationId: String): Action[AnyContent] = Action.async:
    request =>
      given MessagesRequest[AnyContent] = request
      
      val agentEmail = "test@email.com" //TODO where does this come from?

      agentClientRelationshipsConnector.getInvitation(invitationId).map { invitationDetails =>
        Ok(view(
          clientName = invitationDetails.clientName,
          invitationLink = s"https://tax.service.gov.uk/invitations/${invitationDetails.invitationId}",
          invitationExpiryDate = invitationDetails.expiryDate.format(formatter),
          daysUntilInvitationExpires = ChronoUnit.DAYS.between(LocalDate.now(), invitationDetails.expiryDate).toString,
          agentEmail = agentEmail,
          agentHomeLink = appConfig.agentServicesAccountHomeUrl
        ))
      }
