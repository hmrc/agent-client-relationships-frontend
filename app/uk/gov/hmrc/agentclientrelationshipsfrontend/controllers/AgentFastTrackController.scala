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

package uk.gov.hmrc.agentclientrelationshipsfrontend.controllers

import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.agentclientrelationshipsfrontend.actions.Actions
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.connectors.AgentClientRelationshipsConnector
import uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.journey.routes
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.FastTrackErrors
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.journey.AgentFastTrackForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.JourneyType
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{ClientServiceConfigurationService, JourneyService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.*
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AgentFastTrackController @Inject()(mcc: MessagesControllerComponents,
                                         appConfig: AppConfig,
                                         journeyService: JourneyService,
                                         clientServiceConfig: ClientServiceConfigurationService,
                                         actions: Actions,
                                         agentClientRelationshipsConnector: AgentClientRelationshipsConnector
                                        )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc) with I18nSupport {

  //TODO - rethink journeyType
  lazy val journeyType: JourneyType = JourneyType.AuthorisationRequest

  def agentFastTrack: Action[AnyContent] = actions.getFastTrackUrl.async { implicit agentFastTrackRequest =>
    
    AgentFastTrackForm.form(clientServiceConfig).bindFromRequest().fold(
      formWithErrors => {
        agentFastTrackRequest.errorUrl match {
          case Some(errorUrl) =>
            Future.successful(Redirect(Call("GET", errorUrl + s"?issue=${formWithErrors.errorsAsJson.as[FastTrackErrors].formErrorsMessages}")))
          case None =>
            Future.successful(Redirect(routes.StartJourneyController.startJourney(journeyType)))
        }
      },
      agentFastTrackFormData => {
        for {
          clientDetails <- agentClientRelationshipsConnector.getClientDetails(agentFastTrackFormData.service, agentFastTrackFormData.clientId)
          newJourney = journeyService.newJourney(journeyType)
            .copy(
              clientType = agentFastTrackFormData.clientType,
              clientService = Some(agentFastTrackFormData.service),
              clientId = Some(agentFastTrackFormData.clientId),
              clientDetailsResponse = clientDetails
              //TODO - add knowFacts here
            )
          _ <- journeyService.saveJourney(newJourney)
          nextPage <- journeyService.nextPageUrl(journeyType)
        } yield Redirect(nextPage)


      }
    )
  }
}




    

