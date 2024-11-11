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
import uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.journey.routes
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.journey.AgentFastTrackForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.JourneyType
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.{ClientDetailsResponse, FastTrackErrors}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{AgentClientRelationshipsService, ClientServiceConfigurationService, JourneyService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.*
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AgentFastTrackController @Inject()(mcc: MessagesControllerComponents,
                                         journeyService: JourneyService,
                                         serviceConfig: ClientServiceConfigurationService,
                                         actions: Actions,
                                         agentClientRelationshipsService: AgentClientRelationshipsService
                                        )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc) with I18nSupport {

  lazy val journeyType: JourneyType = JourneyType.AuthorisationRequest

  private def stripWhiteSpaces(str: String): String = str.trim.replaceAll("\\s", "")

  private def checkKnownFact(knownFact:String, clientDetailsResponse: ClientDetailsResponse):Boolean = {
    val validKnownFact = clientDetailsResponse.knownFactType
      .map(serviceConfig.clientFactFieldFor).map(_.regex)
      .exists(stripWhiteSpaces(knownFact).matches)
    
    val matchedKnownFact = clientDetailsResponse.knownFacts.contains(knownFact)
    
    validKnownFact && matchedKnownFact
  }


  def agentFastTrack: Action[AnyContent] = actions.getFastTrackUrl.async { implicit agentFastTrackRequest =>
    AgentFastTrackForm.form(serviceConfig).bindFromRequest().fold(
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
          clientDetails  <- agentClientRelationshipsService.getClientDetails(agentFastTrackFormData.clientIdentifier, agentFastTrackFormData.service)

          checkedKnownFact = agentFastTrackFormData.knownFact.flatMap{ knownFact =>
            clientDetails.map(checkKnownFact(knownFact, _))
          }

          newJourney = journeyService.newJourney(journeyType)
            .copy(
              clientService = Some(agentFastTrackFormData.service),
              clientId = Some(agentFastTrackFormData.clientIdentifier),
              clientDetailsResponse = clientDetails,
              knownFact = if(checkedKnownFact.contains(true)) agentFastTrackFormData.knownFact else None
            )

          _ <- journeyService.saveJourney(newJourney)

          nextPage <- (clientDetails, checkedKnownFact) match
            case (Some(_), None | Some(true)) => journeyService.nextPageUrl(journeyType)
            case _ => Future.successful(routes.JourneyErrorController.show(journeyType, serviceConfig.getNotFoundError(journeyType, agentFastTrackFormData.service)).url)
        } yield Redirect(nextPage)
      }
    )
  }
}




    

