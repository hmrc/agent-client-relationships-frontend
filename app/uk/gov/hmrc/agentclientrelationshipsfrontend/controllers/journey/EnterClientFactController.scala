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
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.CountryNamesLoader
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.KnownFactType
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.common.KnownFactsConfiguration
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.journey.EnterClientFactForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{AgentJourneyRequest, AgentJourney, JourneyExitType, AgentJourneyType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.AgentJourneyService
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.agentJourney.EnterClientFactPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EnterClientFactController @Inject()(mcc: MessagesControllerComponents,
                                          journeyService: AgentJourneyService,
                                          enterKnownFactPage: EnterClientFactPage,
                                          actions: Actions,
                                          countryNamesLoader: CountryNamesLoader
                                         )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc) with I18nSupport:


  private val countries = countryNamesLoader.load
  private val validCountryCodes = countries.keys.toSet

  private def knownFactField(knownFactType: KnownFactType): KnownFactsConfiguration =
    if knownFactType == KnownFactType.CountryCode then
      knownFactType.fieldConfiguration.copy(validOptions = Some(countries.toSeq))
    else
      knownFactType.fieldConfiguration

  private def knownFactForm(journey: AgentJourney): Form[String] = EnterClientFactForm.form(
    journey.getKnownFactType.fieldConfiguration,
    journey.getService,
    validCountryCodes
  )

  def show(journeyType: AgentJourneyType): Action[AnyContent] = actions.getAgentJourney(journeyType):
    journeyRequest =>
      given AgentJourneyRequest[?] = journeyRequest

      val journey = journeyRequest.journey

      if journey.clientDetailsResponse.isEmpty || journey.clientId.isEmpty then Redirect(routes.SelectClientTypeController.show(journeyType))
      else {
        val form = journey.knownFact match {
          case Some(fact) => knownFactForm(journey).fill(fact)
          case None => knownFactForm(journey)
        }
        Ok(enterKnownFactPage(
          form,
          knownFactField(journey.clientDetailsResponse.get.knownFactType.getOrElse(throw RuntimeException("Known fact type is missing")))
        ))
      }


  def onSubmit(journeyType: AgentJourneyType): Action[AnyContent] = actions.getAgentJourney(journeyType).async:
    journeyRequest =>
      given AgentJourneyRequest[?] = journeyRequest

      val journey = journeyRequest.journey

      knownFactForm(journey).bindFromRequest().fold(
        formWithErrors => {
          Future.successful(BadRequest(enterKnownFactPage(
            formWithErrors,
            knownFactField(journey.clientDetailsResponse.get.knownFactType.getOrElse(throw RuntimeException("Known fact type is missing")))
          )))
        },
        knownFact => {
          for
            _ <- if journey.knownFact.contains(knownFact) then Future.successful(()) else journeyService.saveJourney(journey.copy(
              knownFact = Some(knownFact),
              clientConfirmed = None,
              agentType = None,
              confirmationClientName = None,
              journeyComplete = None
            ))
            redirectUrl <-
              if journey.clientDetailsResponse.exists(_.knownFacts.contains(knownFact)) then
                journeyService.nextPageUrl(journeyType)
              else
                Future.successful(routes.JourneyExitController.show(journeyType, JourneyExitType.NotFound).url)
          yield
            Redirect(redirectUrl)
        }
      )
