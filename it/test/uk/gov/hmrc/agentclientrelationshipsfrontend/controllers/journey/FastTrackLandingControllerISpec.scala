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

import play.api.libs.json.{JsObject, Json}
import play.api.test.Helpers.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.{ClientDetailsResponse, KnownFactType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{AgentJourney, AgentJourneyType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.AgentJourneyService
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.WiremockHelper.stubGet
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AuthStubs, ComponentSpecHelper}

import scala.collection.immutable.Seq

class FastTrackLandingControllerISpec extends ComponentSpecHelper with AuthStubs :

  def getClientDetailsUrl(service: String, clientId: String) = s"/agent-client-relationships/client/$service/details/$clientId"

  val testUKClientDetailsResponseJson: JsObject = Json.obj(
    "name" -> "anything",
    "isOverseas" -> false,
    "knownFacts" -> Json.arr(),
    "knownFactType" -> "PostalCode",
    "hasPendingInvitation" -> false
  )

  val testUKClientDetailsResponse: ClientDetailsResponse = ClientDetailsResponse(
    name = "anything",
    status = None,
    isOverseas = Some(false),
    knownFacts = Seq("TestKnownFact"),
    knownFactType = Some(KnownFactType.PostalCode),
    hasPendingInvitation = false,
    hasExistingRelationshipFor = None
  )

  private val journeyType = AgentJourneyType.AuthorisationRequest
  private val exampleClientId: String = "1234567890"
  private val basicJourney: AgentJourney = AgentJourney(
    journeyType = journeyType,
    clientType = Some("personal"),
    clientService = Some("HMRC-MTD-IT"),
    clientId = Some(exampleClientId),
    clientDetailsResponse = Some(testUKClientDetailsResponse)
  )

  def agentRoleBasedRequestJourney(service: String, role: String): AgentJourney = basicJourney.copy(
    clientService = Some(service),
    agentType = Some(role)
  )

  val exampleNino: String = "AB123456C"

  private val personalAuthorisationRequestJourney: AgentJourney = AgentJourney(journeyType = AgentJourneyType.AuthorisationRequest, clientType = Some("personal"))

  private val optionsForPersonal: Seq[String] = Seq("HMRC-MTD-IT")

  private val allOptionsForClientType = Map(
    "personal" -> optionsForPersonal
  )

  private def exampleValueForService(service: String): String = service match {
    case "HMRC-MTD-IT" => exampleNino
  }

  private val allClientTypeAuthJourneys: List[AgentJourney] = List(
    personalAuthorisationRequestJourney
  )

  val journeyService: AgentJourneyService = app.injector.instanceOf[AgentJourneyService]

  override def beforeEach(): Unit = {
    await(journeyService.deleteAllAnswersInSession(request))
    super.beforeEach()
  }

  "GET /authorisation-request/need-more-information" should :
    "redirect to ASA dashboard when no journey session present" in :
      authoriseAsAgent()
      val result = get(routes.FastTrackLandingController.show(AgentJourneyType.AuthorisationRequest).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe "http://localhost:9401/agent-services-account/home"

    "redirect to the journey start when no service present" in :
      authoriseAsAgent()
      await(journeyService.saveJourney(AgentJourney(journeyType = AgentJourneyType.AuthorisationRequest, clientType = Some("personal"))))
      val result = get(routes.FastTrackLandingController.show(AgentJourneyType.AuthorisationRequest).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.SelectClientTypeController.show(AgentJourneyType.AuthorisationRequest).url

    allClientTypeAuthJourneys.foreach(j =>
      allOptionsForClientType(j.getClientType).foreach(o => s"display the client identifier page for ${j.getClientType} $o" in :
        authoriseAsAgent()
        await(journeyService.saveJourney(basicJourney.copy(clientService = Some("HMRC-MTD-IT"), agentType = Some("HMRC-MTD-IT"))))
        val result = get(routes.FastTrackLandingController.show(AgentJourneyType.AuthorisationRequest).url)
        result.status shouldBe OK
      ))
