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

package uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.journey

import play.api.i18n.I18nSupport
import play.api.mvc.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.actions.Actions
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.AgentRoleFieldName
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.ClientDetailsResponse
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.journey.SelectFromOptionsForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{AgentJourneyRequest, AgentRoleChangeType, Journey, JourneyExitType, JourneyType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{ClientServiceConfigurationService, JourneyService}
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.journey.SelectAgentRolePage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SelectAgentRoleController @Inject()(mcc: MessagesControllerComponents,
                                          serviceConfig: ClientServiceConfigurationService,
                                          journeyService: JourneyService,
                                          selectAgentRolePage: SelectAgentRolePage,
                                          actions: Actions
                                       )(implicit val executionContext: ExecutionContext, appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport:
  
  private def getAgentRoleChangeType(journey: Journey, options: Seq[String]): AgentRoleChangeType = {
    journey.getClientDetailsResponse.hasExistingRelationshipFor match {
      case Some(existing) if options.head == existing => AgentRoleChangeType.MainToSupporting
      case Some(_) => AgentRoleChangeType.SupportingToMain
      case _ => AgentRoleChangeType.NewRelationship
    }
  }

  def show(journeyType: JourneyType): Action[AnyContent] = actions.getJourney(journeyType):
    journeyRequest =>
      given AgentJourneyRequest[?] = journeyRequest
      val journey = journeyRequest.journey
      if journey.clientDetailsResponse.isEmpty then Redirect(routes.EnterClientIdController.show(journey.journeyType))
      else {
        val options = serviceConfig.getSupportedAgentRoles(journey.getService)
        Ok(selectAgentRolePage(
          form = SelectFromOptionsForm.form(AgentRoleFieldName, options, journey.journeyType.toString, journey.getClientDetailsResponse.name).fill(journey.agentType.getOrElse("")),
          options,
          getAgentRoleChangeType(journey, options)
        ))
      }
      

  def onSubmit(journeyType: JourneyType): Action[AnyContent] = actions.getJourney(journeyType).async:
    journeyRequest =>
      given AgentJourneyRequest[?] = journeyRequest
      val journey = journeyRequest.journey
      val options = serviceConfig.getSupportedAgentRoles(journey.getServiceWithDefault)
      SelectFromOptionsForm.form(AgentRoleFieldName, options, journeyType.toString, journey.getClientDetailsResponse.name).bindFromRequest().fold(
        formWithErrors => {
          Future.successful(BadRequest(selectAgentRolePage(
            formWithErrors,
            options,
            getAgentRoleChangeType(journey, options)
          )))
        },
        agentRole => {
          if(journey.getClientDetailsResponse.hasExistingRelationshipFor.contains(agentRole)) {
            Future.successful(Redirect(routes.JourneyExitController.show(journeyType, JourneyExitType.NoChangeOfAgentRole)))
          } else {
            val newJourney = journey.copy(
              agentType = Some(agentRole)
            )
            journeyService.saveJourney(newJourney).flatMap { _ =>
              journeyService.nextPageUrl(journeyType).map(Redirect(_))
            }
          }
        }
  )
