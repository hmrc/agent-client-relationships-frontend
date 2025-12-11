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
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.HMRCMTDIT
import uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.journey.routes
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.journey.AgentFastTrackForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.AgentJourneyType
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.{AgentFastTrackRequestWithRedirectUrls, ClientDetailsResponse, FastTrackErrors}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{AgentClientRelationshipsService, AgentJourneyService, ClientServiceConfigurationService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.*
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AgentFastTrackController @Inject()(mcc: MessagesControllerComponents,
                                         journeyService: AgentJourneyService,
                                         serviceConfig: ClientServiceConfigurationService,
                                         actions: Actions,
                                         agentClientRelationshipsService: AgentClientRelationshipsService
                                        )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc) with I18nSupport {

  lazy val journeyType: AgentJourneyType = AgentJourneyType.AuthorisationRequest

  private def stripWhiteSpaces(str: String): String = str.trim.replaceAll("\\s", "")

  private def checkKnownFact(knownFact:String, clientDetailsResponse: ClientDetailsResponse):Boolean = {
    val validKnownFact = clientDetailsResponse.knownFactType
      .map(_.fieldConfiguration.regex)
      .exists(stripWhiteSpaces(knownFact).matches)
    
    val matchedKnownFact = clientDetailsResponse.knownFacts.contains(knownFact)
    
    validKnownFact && matchedKnownFact
  }

  private def getRedirectUrlFromRequest(implicit request: AgentFastTrackRequestWithRedirectUrls[AnyContent]): Future[String] =
    AgentFastTrackForm.form(serviceConfig).bindFromRequest().fold(
      formWithErrors => {
        request.errorUrl match {
          case Some(errorUrl) =>
            Future.successful(Call("GET", errorUrl + s"?issue=${formWithErrors.errorsAsJson.as[FastTrackErrors].formErrorsMessages}").url)
          case None =>
            Future.successful(routes.StartJourneyController.startJourney(journeyType).url)
        }
      },

      agentFastTrackFormData => {
        for {
          clientDetails  <- agentClientRelationshipsService.getClientDetails(agentFastTrackFormData.clientIdentifier, agentFastTrackFormData.service)

          checkedKnownFact = agentFastTrackFormData.knownFact.flatMap{ knownFact =>
            clientDetails.map(checkKnownFact(knownFact, _))
          }

          possiblyInferredClientType = if (agentFastTrackFormData.clientType.isDefined) agentFastTrackFormData.clientType else serviceConfig.inferredClientType(agentFastTrackFormData.service)

          newJourney = journeyService.newJourney(journeyType)
            .copy(
              clientType = possiblyInferredClientType,
              clientService = Some(agentFastTrackFormData.service),
              clientId = Some(agentFastTrackFormData.clientIdentifier),
              clientDetailsResponse = clientDetails,
              knownFact = if(checkedKnownFact.contains(true)) agentFastTrackFormData.knownFact else None
            )

          _ <- journeyService.saveJourney(newJourney)

          nextPage <- (clientDetails, checkedKnownFact, agentFastTrackFormData.service) match
            case (_, _, HMRCMTDIT) => Future.successful(routes.FastTrackLandingController.show(journeyType).url)
            case (Some(_), None | Some(true), _) => journeyService.nextPageUrl(journeyType)
            case (_, _, _) => Future.successful(routes.JourneyExitController.show(journeyType, serviceConfig.getNotFoundError(journeyType, agentFastTrackFormData.service)).url)
        } yield nextPage
      }
    )

  def agentFastTrack: Action[AnyContent] = actions.getFastTrackUrl.async { implicit agentFastTrackRequest =>
    getRedirectUrlFromRequest.map(url => Redirect(url))
  }

  // This function supports the re-routing of a fast track request that is posted to agent-invitations-frontend
  // This function and associated endpoint can be removed once agent-invitations-frontend is decommissioned
  def agentFastTrackGetRedirectUrl: Action[AnyContent] = actions.getFastTrackUrl.async { implicit agentFastTrackRequest =>
    getRedirectUrlFromRequest.map(url => Ok(url))
  }
}




    

