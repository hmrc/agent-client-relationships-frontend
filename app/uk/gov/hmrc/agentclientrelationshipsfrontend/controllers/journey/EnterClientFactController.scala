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

import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.actions.Actions
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.common.FieldConfiguration
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.journey.EnterClientFactForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{AgentJourneyRequest, Journey, JourneyType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{AgentClientRelationshipsService, ClientServiceConfigurationService, JourneyService}
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.journey.EnterClientFactPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EnterClientFactController @Inject()(mcc: MessagesControllerComponents,
                                          serviceConfig: ClientServiceConfigurationService,
                                          journeyService: JourneyService,
                                          agentClientRelationshipsService: AgentClientRelationshipsService,
                                          enterKnownFactPage: EnterClientFactPage,
                                          actions: Actions
                                         )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc) with I18nSupport:

  private def knownFactField(journey: Journey): FieldConfiguration =
    serviceConfig.clientFactFieldFor(journey.clientDetailsResponse.get.knownFactType.get)

  private def knownFactForm(journey: Journey): Form[String] = EnterClientFactForm.form(
    knownFactField(journey),
    journey.journeyType.toString
  )

  def show(journeyType: JourneyType): Action[AnyContent] = actions.getJourney(journeyType):
    journeyRequest =>
      given AgentJourneyRequest[?] = journeyRequest

      val journey = journeyRequest.journey

      if journey.clientDetailsResponse.isEmpty || journey.clientId.isEmpty then Redirect(routes.SelectClientTypeController.show(journeyType))
      else
        Ok(enterKnownFactPage(
          knownFactForm(journey).fill(journey.knownFact.getOrElse("")),
          knownFactField(journey)
        ))


  def onSubmit(journeyType: JourneyType): Action[AnyContent] = actions.getJourney(journeyType).async:
    journeyRequest =>
      given AgentJourneyRequest[?] = journeyRequest

      val journey = journeyRequest.journey

      knownFactForm(journey).bindFromRequest().fold(
        formWithErrors => {
          Future.successful(BadRequest(enterKnownFactPage(
            formWithErrors,
            knownFactField(journey)
          )))
        },
        knownFact => {
          for
            _ <- journeyService.saveJourney(journey.copy(
              knownFact = Some(knownFact),
              clientConfirmed = false,
              agentType = None
            )) if !journey.knownFact.contains(knownFact) // if user changes known fact answer then clean up session / store if never answered
            redirectUrl <-
              if journey.clientDetailsResponse.exists(_.knownFacts.contains(knownFact)) then
                journeyService.nextPageUrl(journeyType)
              else
                Future.successful(routes.JourneyErrorController.show(journeyType, "known-fact-not-matched").url)
          yield
            Redirect(redirectUrl)
        }
      )
