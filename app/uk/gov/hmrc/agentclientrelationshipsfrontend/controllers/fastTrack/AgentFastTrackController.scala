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

package uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.fastTrack

import play.api.libs.json.*
import play.api.mvc.{Action, MessagesControllerComponents}
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.createInvitation.routes
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.fastTrack.AgentFastTrackRequest
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.JourneyType
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{ClientServiceConfigurationService, JourneyService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.*
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AgentFastTrackController @Inject()(mcc: MessagesControllerComponents,
                                         appConfig: AppConfig,
                                         journeyService: JourneyService,
                                         clientServiceConfig: ClientServiceConfigurationService
                                             )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc) {

  private def previousPageUrl(cya: Boolean = false): String = if (cya) routes.CheckYourAnswersController.show.url else appConfig.agentServicesAccountHomeUrl


  def agentFastTrack: Action[JsValue] = Action.async(parse.json) { implicit request =>
    // Pass the injected AgeService to the reads factory method
    implicit val reads: Reads[AgentFastTrackRequest] = AgentFastTrackRequest.readsWithServiceConfigValidation(clientServiceConfig)

    println(s"${Console.MAGENTA} Wojciech AgentFastTrackController  agentFastTrackRequest start  ${Console.RESET}")

    // Validate the JSON payload using the custom Reads
    request.body.validate[AgentFastTrackRequest] match {
      case JsSuccess(agentFastTrackRequest, _) =>
        println(s"${Console.MAGENTA} Wojciech AgentFastTrackController payload agentFastTrackRequest :${agentFastTrackRequest}  ${Console.RESET}")
        val newJourney = journeyService.newJourney(JourneyType.CreateInvitationFastTrack)

        val clientService = agentFastTrackRequest.service
        val clientIdType = clientServiceConfig.clientDetailsFor(clientService).head.name
        val knowFactsType = clientServiceConfig.clientDetailsFor(clientService).last.name

        
        val journeyPartialFilled = newJourney.copy(
          clientType    = agentFastTrackRequest.clientType,
          service       = Some(agentFastTrackRequest.service),
          clientDetails = Map(clientIdType  -> agentFastTrackRequest.clientId))
        
        val updatedJourney = agentFastTrackRequest.knownFact match
          case Some(kf) => journeyPartialFilled.copy(clientDetails = journeyPartialFilled.clientDetails + (knowFactsType -> kf))
          case None => journeyPartialFilled
        
        val nextPageUrl: String =
          if(agentFastTrackRequest.clientType.isEmpty) routes.SelectClientTypeController.show().url
          else if (agentFastTrackRequest.knownFact.isEmpty) routes.EnterClientDetailsController.show(knowFactsType).url
          else routes.CheckYourAnswersController.show.url

        println(s"${Console.MAGENTA} Wojciech AgentFastTrackController updatedJourney:${updatedJourney}  ${Console.RESET}")
        println(s"${Console.MAGENTA} Wojciech AgentFastTrackController nextPageUrl:${nextPageUrl}  ${Console.RESET}")
        
        journeyService.saveJourney(updatedJourney)
          .map(_ => Redirect(nextPageUrl))

      case JsError(errors) =>
//        Invalid(ValidationError("INVALID_SUBMISSION"))
        println(s"${Console.MAGENTA} Wojciech AgentFastTrackController payload errors :${errors}  ${Console.RESET}")
        Future.successful(BadRequest(Json.obj("status" -> "error", "message" -> JsError.toJson(errors))))
    }
  }
}


    

