/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.agentclientrelationshipsfrontend.services

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.Reads
import play.api.mvc.RequestHeader
import play.api.test.FakeRequest
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.HMRCMTDVAT
import uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.journey.routes
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.ClientDetailsResponse
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.ClientStatus.Insolvent
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.KnownFactType.Date
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{AgentJourney, JourneyExitType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.AgentJourneyType.{AgentCancelAuthorisation, AuthorisationRequest}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.JourneyExitType.ClientStatusInsolvent
import uk.gov.hmrc.agentclientrelationshipsfrontend.repositories.JourneyRepository
import uk.gov.hmrc.mongo.cache.DataKey

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AgentJourneyServiceSpec extends AnyWordSpecLike with Matchers with MockitoSugar with BeforeAndAfterEach :

  val mockJourneyRepo: JourneyRepository = mock[JourneyRepository]
  val mockClientServiceConfigService: ClientServiceConfigurationService = mock[ClientServiceConfigurationService]
  implicit val mockAppConfig: AppConfig = mock[AppConfig]
  val service = new AgentJourneyService(mockJourneyRepo, mockClientServiceConfigService)
  val baseAgentJourney: AgentJourney = AgentJourney(AuthorisationRequest)
  val arn = "XARN1234567"
  implicit val fakeRequest: FakeRequest[?] = FakeRequest()

  def givenJourneyInSession(sessionJourney: AgentJourney): OngoingStubbing[Future[Option[AgentJourney]]] =
    when(mockJourneyRepo.getFromSession(any[DataKey[AgentJourney]]())(using any[Reads[AgentJourney]](), any[RequestHeader]()))
    .thenReturn(Future.successful(Some(sessionJourney)))

  override def beforeEach(): Unit = {
    reset(mockJourneyRepo, mockClientServiceConfigService)
  }

  ".nextPageUrl" should :

    "return the Start URL when there is no journey in session" in :
      when(mockJourneyRepo.getFromSession(any[DataKey[AgentJourney]]())(using any[Reads[AgentJourney]](), any[RequestHeader]()))
        .thenReturn(Future.successful(None))

      val result = await(service.nextPageUrl(AuthorisationRequest))
      result shouldBe routes.StartJourneyController.startJourney(AuthorisationRequest).url

    "return the Start URL when journey type does not match" in :
      val sessionJourney = baseAgentJourney.copy(journeyType = AgentCancelAuthorisation)
      givenJourneyInSession(sessionJourney)

      val result = await(service.nextPageUrl(AuthorisationRequest))
      result shouldBe routes.StartJourneyController.startJourney(AuthorisationRequest).url

    "return the ASA home URL when journeyComplete is present" in :
      val sessionJourney = baseAgentJourney.copy(journeyComplete = Some("ABC123"))
      givenJourneyInSession(sessionJourney)
      when(mockAppConfig.agentServicesAccountHomeUrl).thenReturn("/asa-home")

      val result = await(service.nextPageUrl(AuthorisationRequest))
      result shouldBe mockAppConfig.agentServicesAccountHomeUrl

    "return the SelectClientType URL when clientService is empty" in :
      val sessionJourney = baseAgentJourney
      givenJourneyInSession(sessionJourney)

      val result = await(service.nextPageUrl(AuthorisationRequest))
      result shouldBe routes.SelectClientTypeController.show(AuthorisationRequest).url

    "return the ServiceRefinement URL if the clientService needs refining" in :
      val sessionJourney = baseAgentJourney.copy(clientService = Some(HMRCMTDVAT))
      givenJourneyInSession(sessionJourney)
      when(mockClientServiceConfigService.requiresRefining(any[String]())).thenReturn(true)

      val result = await(service.nextPageUrl(AuthorisationRequest))
      result shouldBe routes.ServiceRefinementController.show(AuthorisationRequest).url

    "return the EnterClientId URL if there is no existing ID for the clientService" in :
      val sessionJourney = baseAgentJourney.copy(clientService = Some(HMRCMTDVAT))
      givenJourneyInSession(sessionJourney)

      val result = await(service.nextPageUrl(AuthorisationRequest))
      result shouldBe routes.EnterClientIdController.show(AuthorisationRequest).url

    "return the EnterClientFact URL if there is no existing known fact for the provided client" in :
      val sessionJourney = baseAgentJourney.copy(
        clientService = Some(HMRCMTDVAT),
        clientDetailsResponse = Some(ClientDetailsResponse("Colin Client", None, None, Seq(), Some(Date), false, None))
      )
      givenJourneyInSession(sessionJourney)

      val result = await(service.nextPageUrl(AuthorisationRequest))
      result shouldBe routes.EnterClientFactController.show(AuthorisationRequest).url

    "return the ConfirmClient URL if the provided client details have not been confirmed yet" in :
      val sessionJourney = baseAgentJourney.copy(
        clientService = Some(HMRCMTDVAT),
        clientDetailsResponse = Some(ClientDetailsResponse("Colin Client", None, None, Seq(), Some(Date), false, None)),
        knownFact = Some("2020-01-01")
      )
      givenJourneyInSession(sessionJourney)

      val result = await(service.nextPageUrl(AuthorisationRequest))
      result shouldBe routes.ConfirmClientController.show(AuthorisationRequest).url

    "return the Start URL if the clientConfirmed is defined but set to false" in :
      val sessionJourney = baseAgentJourney.copy(
        clientService = Some(HMRCMTDVAT),
        clientDetailsResponse = Some(ClientDetailsResponse("Colin Client", None, None, Seq(), Some(Date), false, None)),
        knownFact = Some("2020-01-01"),
        clientConfirmed = Some(false)
      )
      givenJourneyInSession(sessionJourney)

      val result = await(service.nextPageUrl(AuthorisationRequest))
      result shouldBe routes.StartJourneyController.startJourney(AuthorisationRequest).url

    "return the AgentRole URL if the provided client details are confirmed and the service supports agent roles" in :
      val sessionJourney = baseAgentJourney.copy(
        clientService = Some(HMRCMTDVAT),
        clientDetailsResponse = Some(ClientDetailsResponse("Colin Client", None, None, Seq(), Some(Date), false, None)),
        knownFact = Some("2020-01-01"),
        clientConfirmed = Some(true)
      )
      when(mockClientServiceConfigService.supportsAgentRoles(any[String]())).thenReturn(true)
      givenJourneyInSession(sessionJourney)

      val result = await(service.nextPageUrl(AuthorisationRequest))
      result shouldBe routes.SelectAgentRoleController.show(AuthorisationRequest).url

    "return an exit page URL if the session indicates there is an exit reason" in :
      val sessionJourney = baseAgentJourney.copy(
        clientService = Some(HMRCMTDVAT),
        clientDetailsResponse = Some(ClientDetailsResponse("Colin Client", Some(Insolvent), None, Seq(), Some(Date), false, None)),
        knownFact = Some("2020-01-01"),
        clientConfirmed = Some(true),
        agentType = Some("main")
      )
      givenJourneyInSession(sessionJourney)

      val result = await(service.nextPageUrl(AuthorisationRequest))
      result shouldBe routes.JourneyExitController.show(AuthorisationRequest, ClientStatusInsolvent).url

    "return the CheckYourAnswers URL if all necessary values are defined and there is no reason to exit" in :
      val sessionJourney = baseAgentJourney.copy(
        clientService = Some(HMRCMTDVAT),
        clientDetailsResponse = Some(ClientDetailsResponse("Colin Client", None, None, Seq(), Some(Date), false, None)),
        knownFact = Some("2020-01-01"),
        clientConfirmed = Some(true),
        agentType = Some("main")
      )
      givenJourneyInSession(sessionJourney)

      val result = await(service.nextPageUrl(AuthorisationRequest))
      result shouldBe routes.CheckYourAnswersController.show(AuthorisationRequest).url