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
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.KnownFactType.PostalCode
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.AgentJourneyType.AgentCancelAuthorisation
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.{ClientDetailsResponse, KnownFactType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{AgentJourney, AgentJourneyType, JourneyExitType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.AgentJourneyService
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.WiremockHelper.stubGet
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AuthStubs, ComponentSpecHelper}

import scala.collection.immutable.Seq

class EnterClientIdControllerISpec extends ComponentSpecHelper with AuthStubs :

  def getClientDetailsUrl(service: String, clientId: String) = s"/agent-client-relationships/client/$service/details/$clientId"

  def testClientDetailsResponseJson(isTrust: Boolean): JsObject = if isTrust then Json.obj(
    "name" -> "anything",
    "knownFacts" -> Json.arr(),
    "hasPendingInvitation" -> false
  ) else Json.obj(
    "name" -> "anything",
    "isOverseas" -> false,
    "knownFacts" -> Json.arr("anything"),
    "knownFactType" -> "PostalCode",
    "hasPendingInvitation" -> false
  )

  val testOverseasClientDetailsResponseJson: JsObject = Json.obj(
    "name" -> "anything",
    "isOverseas" -> true,
    "knownFacts" -> Json.arr("anything"),
    "knownFactType" -> "Email",
    "hasPendingInvitation" -> false
  )

  val testUKClientDetailsResponseJson: JsObject = Json.obj(
    "name" -> "anything",
    "isOverseas" -> false,
    "knownFacts" -> Json.arr("anything"),
    "knownFactType" -> "Email",
    "hasPendingInvitation" -> false
  )

  val testOverseasClientDetailsResponse: ClientDetailsResponse = ClientDetailsResponse(
    name = "anything",
    status = None,
    isOverseas = Some(true),
    knownFacts = Seq("anything"),
    knownFactType = Some(KnownFactType.Email),
    hasPendingInvitation = false,
    hasExistingRelationshipFor = None
  )

  val testUKClientDetailsResponse: ClientDetailsResponse = ClientDetailsResponse(
    name = "anything",
    status = None,
    isOverseas = Some(false),
    knownFacts = Seq("anything"),
    knownFactType = Some(KnownFactType.Email),
    hasPendingInvitation = false,
    hasExistingRelationshipFor = None
  )

  val exampleNino: String = "AB123456C"
  val exampleUtr: String = "1234567890"
  val exampleVrn: String = "123456789"
  val exampleUrn: String = "XATRUST12345678"
  val exampleCgtRef: String = "XMCGTP123456789"
  val examplePptRef: String = "XMPPT0001234567"
  val exampleCbcId: String = "XMCBC1234567890"
  val examplePlrId: String = "XMPLR1234567890"

  private def getFieldName(service: String) = service match {
    case "HMRC-MTD-IT" => "nino"
    case "PERSONAL-INCOME-RECORD" => "nino"
    case "HMRC-TERS-ORG" => "utr"
    case "HMRC-TERSNT-ORG" => "urn"
    case "HMRC-MTD-VAT" => "vrn"
    case "HMRC-CGT-PD" => "cgtRef"
    case "HMRC-PPT-ORG" => "pptRef"
    case "HMRC-CBC-ORG" => "cbcId"
    case "HMRC-PILLAR2-ORG" => "PlrId"
  }

  private val personalAuthorisationRequestJourney: AgentJourney = AgentJourney(journeyType = AgentJourneyType.AuthorisationRequest, clientType = Some("personal"))
  private val businessAuthorisationRequestJourney: AgentJourney = AgentJourney(journeyType = AgentJourneyType.AuthorisationRequest, clientType = Some("business"))
  private val trustAuthorisationRequestJourney: AgentJourney = AgentJourney(journeyType = AgentJourneyType.AuthorisationRequest, clientType = Some("trust"), refinedService = Some(true))
  private val personalAgentCancelAuthorisationJourney: AgentJourney = AgentJourney(AgentJourneyType.AgentCancelAuthorisation, clientType = Some("personal"))
  private val businessAgentCancelAuthorisationJourney: AgentJourney = AgentJourney(AgentJourneyType.AgentCancelAuthorisation, clientType = Some("business"))
  private val trustAgentCancelAuthorisationJourney: AgentJourney = AgentJourney(AgentJourneyType.AgentCancelAuthorisation, clientType = Some("trust"), refinedService = Some(true))

  private val optionsForPersonal: Seq[String] = Seq("HMRC-MTD-IT", "PERSONAL-INCOME-RECORD", "HMRC-MTD-VAT", "HMRC-CGT-PD", "HMRC-PPT-ORG")
  private val optionsForBusiness: Seq[String] = Seq("HMRC-MTD-VAT", "HMRC-PPT-ORG", "HMRC-CBC-ORG", "HMRC-PILLAR2-ORG")
  private val optionsForTrust: Seq[String] = Seq("HMRC-PPT-ORG", "HMRC-TERS-ORG", "HMRC-TERSNT-ORG", "HMRC-CGT-PD", "HMRC-CBC-ORG", "HMRC-PILLAR2-ORG")

  private val allOptionsForClientType = Map(
    "personal" -> optionsForPersonal,
    "business" -> optionsForBusiness,
    "trust" -> optionsForTrust
  )

  private def exampleValueForService(service: String): String = service match {
    case "HMRC-MTD-IT" => exampleNino
    case "PERSONAL-INCOME-RECORD" => exampleNino
    case "HMRC-TERS-ORG" => exampleUtr
    case "HMRC-TERSNT-ORG" => exampleUrn
    case "HMRC-MTD-VAT" => exampleVrn
    case "HMRC-CGT-PD" => exampleCgtRef
    case "HMRC-PPT-ORG" => examplePptRef
    case "HMRC-CBC-ORG" => exampleCbcId
    case "HMRC-PILLAR2-ORG" => examplePlrId
  }

  private val allClientTypeAuthJourneys: List[AgentJourney] = List(
    personalAuthorisationRequestJourney,
    businessAuthorisationRequestJourney,
    trustAuthorisationRequestJourney
  )
  private val allClientTypeDeAuthJourneys: List[AgentJourney] = List(
    personalAgentCancelAuthorisationJourney,
    businessAgentCancelAuthorisationJourney,
    trustAgentCancelAuthorisationJourney
  )

  val journeyService: AgentJourneyService = app.injector.instanceOf[AgentJourneyService]

  override def beforeEach(): Unit = {
    await(journeyService.deleteAllAnswersInSession(request))
    super.beforeEach()
  }

  "GET /authorisation-request/client-identifier" should :
    "redirect to ASA dashboard when no journey session present" in :
      authoriseAsAgent()
      val result = get(routes.EnterClientIdController.show(AgentJourneyType.AuthorisationRequest).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe "http://localhost:9401/agent-services-account/home"

    "redirect to the journey start when no service present" in :
      authoriseAsAgent()
      await(journeyService.saveJourney(AgentJourney(journeyType = AgentJourneyType.AuthorisationRequest, clientType = Some("personal"))))
      val result = get(routes.EnterClientIdController.show(AgentJourneyType.AuthorisationRequest).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.SelectClientTypeController.show(AgentJourneyType.AuthorisationRequest).url

    allClientTypeAuthJourneys.foreach(j =>
      allOptionsForClientType(j.getClientType).foreach(o => s"display the client identifier page for ${j.getClientType} $o" in :
        authoriseAsAgent()
        await(journeyService.saveJourney(j.copy(clientService = Some(o))))
        val result = get(routes.EnterClientIdController.show(AgentJourneyType.AuthorisationRequest).url)
        result.status shouldBe OK
      ))

  "POST /authorisation-request/client-identifier" should :
    allClientTypeAuthJourneys.foreach(j =>
      allOptionsForClientType.get(j.getClientType).map(allOptions =>
        allOptions.foreach(serviceName => s"redirect to the next page after storing answer of ${exampleValueForService(serviceName)} for ${j.getClientType} $serviceName" in :
          authoriseAsAgent()
          stubGet(getClientDetailsUrl(serviceName, exampleValueForService(serviceName)), OK, testClientDetailsResponseJson(serviceName == "HMRC-TERS-ORG" | serviceName == "HMRC-TERSNT-ORG").toString)
          await(journeyService.saveJourney(j.copy(clientService = Some(serviceName), refinedService = Some(true))))
          val result = post(routes.EnterClientIdController.onSubmit(AgentJourneyType.AuthorisationRequest).url)(Map(
            s"${getFieldName(serviceName)}" -> Seq(exampleValueForService(serviceName))
          ))
          result.status shouldBe SEE_OTHER
          val expectedLocation = if serviceName == "HMRC-TERS-ORG" | serviceName == "HMRC-TERSNT-ORG"
          then routes.ConfirmClientController.show(AgentJourneyType.AuthorisationRequest).url
          else routes.EnterClientFactController.show(AgentJourneyType.AuthorisationRequest).url
          result.header("Location").value shouldBe expectedLocation

          val updatedJourney = await(journeyService.getJourney)
          updatedJourney.get.getService shouldBe serviceName

        )))

    "refine service name to use overseas service name if supported and client is overseas" in :
      authoriseAsAgent()
      stubGet(getClientDetailsUrl("HMRC-CBC-ORG", exampleCbcId), OK, testOverseasClientDetailsResponseJson.toString)
      await(journeyService.saveJourney(businessAuthorisationRequestJourney.copy(clientService = Some("HMRC-CBC-ORG"))))
      val result = post(routes.EnterClientIdController.onSubmit(AgentJourneyType.AuthorisationRequest).url)(Map(
        "cbcId" -> Seq(exampleCbcId)
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.EnterClientFactController.show(AgentJourneyType.AuthorisationRequest).url
      val expectedJourneyWithOverseasServiceName = businessAuthorisationRequestJourney.copy(
        clientId = Some(exampleCbcId),
        clientService = Some("HMRC-CBC-NONUK-ORG"),
        clientDetailsResponse = Some(testOverseasClientDetailsResponse)
      )
      val updatedJourney = await(journeyService.getJourney)
      updatedJourney shouldBe Some(expectedJourneyWithOverseasServiceName)

    "refine service name to use UK service name if supported and client is UK" in :
      authoriseAsAgent()
      stubGet(getClientDetailsUrl("HMRC-CBC-ORG", exampleCbcId), OK, testUKClientDetailsResponseJson.toString)
      await(journeyService.saveJourney(businessAuthorisationRequestJourney.copy(clientService = Some("HMRC-CBC-NONUK-ORG"))))
      val result = post(routes.EnterClientIdController.onSubmit(AgentJourneyType.AuthorisationRequest).url)(Map(
        "cbcId" -> Seq(exampleCbcId)
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.EnterClientFactController.show(AgentJourneyType.AuthorisationRequest).url
      val expectedJourneyWithUKServiceName = businessAuthorisationRequestJourney.copy(
        clientId = Some(exampleCbcId),
        clientService = Some("HMRC-CBC-ORG"),
        clientDetailsResponse = Some(testUKClientDetailsResponse)
      )
      val updatedJourney = await(journeyService.getJourney)
      updatedJourney shouldBe Some(expectedJourneyWithUKServiceName)


    "show an error when no selection is made" in :
      authoriseAsAgent()
      await(journeyService.saveJourney(personalAuthorisationRequestJourney.copy(clientService = Some("HMRC-MTD-IT"))))
      val result = post(routes.EnterClientIdController.onSubmit(AgentJourneyType.AuthorisationRequest).url)("")
      result.status shouldBe BAD_REQUEST

  "GET /agent-cancel-authorisation/client-identifier" should :
    "redirect to ASA dashboard when no journey session present" in :
      authoriseAsAgent()
      val result = get(routes.EnterClientIdController.show(AgentJourneyType.AgentCancelAuthorisation).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe "http://localhost:9401/agent-services-account/home"

    "redirect to the journey start when no client type present" in :
      authoriseAsAgent()
      await(journeyService.saveJourney(AgentJourney(journeyType = AgentJourneyType.AgentCancelAuthorisation, clientType = None)))
      val result = get(routes.EnterClientIdController.show(AgentJourneyType.AgentCancelAuthorisation).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.SelectClientTypeController.show(AgentJourneyType.AgentCancelAuthorisation).url

    allClientTypeDeAuthJourneys.foreach(j =>
      allOptionsForClientType(j.getClientType).foreach(o => s"display the the client identifier page for ${j.getClientType} $o" in :
        authoriseAsAgent()
        await(journeyService.saveJourney(j.copy(clientService = Some(o))))
        val result = get(routes.EnterClientIdController.show(AgentJourneyType.AgentCancelAuthorisation).url)
        result.status shouldBe OK
      ))

  "POST /agent-cancel-authorisation/client-identifier" should :
    allClientTypeDeAuthJourneys.foreach(j =>
      allOptionsForClientType.get(j.getClientType).map(allOptions =>
        allOptions.foreach(o => s"redirect to the next page after storing answer of ${exampleValueForService(o)} for ${j.getClientType} $o" in :
          authoriseAsAgent()
          stubGet(getClientDetailsUrl(o, exampleValueForService(o)), OK, testClientDetailsResponseJson(o == "HMRC-TERS-ORG" | o == "HMRC-TERSNT-ORG").toString)
          await(journeyService.saveJourney(j.copy(clientService = Some(o))))
          val result = post(routes.EnterClientIdController.onSubmit(AgentJourneyType.AgentCancelAuthorisation).url)(Map(
            s"${getFieldName(o)}" -> Seq(exampleValueForService(o))
          ))
          result.status shouldBe SEE_OTHER
          val expectedLocation = if o == "HMRC-TERS-ORG" | o == "HMRC-TERSNT-ORG"
          then routes.ConfirmClientController.show(AgentJourneyType.AgentCancelAuthorisation).url
          else routes.EnterClientFactController.show(AgentJourneyType.AgentCancelAuthorisation).url
          result.header("Location").value shouldBe expectedLocation
        )))

    "show an error when no selection is made" in :
      authoriseAsAgent()
      await(journeyService.saveJourney(personalAgentCancelAuthorisationJourney.copy(clientService = Some("HMRC-MTD-IT"))))
      val result = post(routes.EnterClientIdController.onSubmit(AgentJourneyType.AgentCancelAuthorisation).url)("")
      result.status shouldBe BAD_REQUEST

    "redirect to the ConfirmClient page when the clientId and clientDetailsResponse are already in session and  knowFacts match" in :
      authoriseAsAgent()
      val knowFact = "TestPotsCode"
      val clientDetailsResponse = ClientDetailsResponse("", None, None, Seq(knowFact), None, false, None)
      await(journeyService.saveJourney(businessAgentCancelAuthorisationJourney.copy(clientService = Some("HMRC-CBC-ORG"), clientId = Some(exampleCbcId), clientDetailsResponse = Some(clientDetailsResponse), knownFact = Some(knowFact))))
      val result = post(routes.EnterClientIdController.onSubmit(AgentJourneyType.AgentCancelAuthorisation).url)(Map(
        getFieldName("HMRC-CBC-ORG") -> Seq(exampleCbcId)
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.ConfirmClientController.show(AgentCancelAuthorisation).url

    "redirect to the Enter client details page when the clientId and clientDetailsResponse are already in session and knowFacts do not match" in :
      authoriseAsAgent()
      val knowFact = "TestPotsCode"
      val inputKnowFact = "OtherTestPotsCode"
      val clientDetailsResponse = ClientDetailsResponse(name = "", status = None, isOverseas = None, knownFacts = Seq(knowFact), knownFactType = Some(PostalCode), hasPendingInvitation = false, hasExistingRelationshipFor = None)
      await(journeyService.saveJourney(businessAgentCancelAuthorisationJourney.copy(clientService = Some("HMRC-CBC-ORG"), clientId = Some(exampleCbcId), clientDetailsResponse = Some(clientDetailsResponse), knownFact = Some(inputKnowFact))))
      val result = post(routes.EnterClientIdController.onSubmit(AgentJourneyType.AgentCancelAuthorisation).url)(Map(
        getFieldName("HMRC-CBC-ORG") -> Seq(exampleCbcId)
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.EnterClientFactController.show(AgentCancelAuthorisation).url

    "redirect to the journey exit when the clientDetailsResponse from the API was empty" in :
      authoriseAsAgent()
      stubGet(getClientDetailsUrl("HMRC-CBC-ORG", exampleCbcId), NOT_FOUND, "")
      await(journeyService.saveJourney(businessAgentCancelAuthorisationJourney.copy(clientService = Some("HMRC-CBC-ORG"))))
      val result = post(routes.EnterClientIdController.onSubmit(AgentJourneyType.AgentCancelAuthorisation).url)(Map(
        getFieldName("HMRC-CBC-ORG") -> Seq(exampleCbcId)
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.JourneyExitController.show(AgentCancelAuthorisation, JourneyExitType.NotFound).url
