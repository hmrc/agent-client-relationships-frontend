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

import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.libs.json.{JsObject, Json}
import play.api.test.Helpers.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{Journey, JourneyType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.JourneyService
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.WiremockHelper.stubGet
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AuthStubs, ComponentSpecHelper}

import scala.collection.immutable.Seq

class EnterClientIdControllerISpec extends ComponentSpecHelper with AuthStubs {

  def getClientDetailsUrl(service: String, clientId: String) = s"/agent-client-relationships/client/$service/details/$clientId"

  val testClientDetailsResponseJson: JsObject = Json.obj(
    "name" -> "anything",
    "status" -> "Insolvent",
    "isOverseas" -> false,
    "knownFacts" -> Json.arr("anything"),
    "knownFactType" -> "PostalCode"
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

  private val personalAuthorisationRequestJourney: Journey = Journey(journeyType = JourneyType.AuthorisationRequest, clientType = Some("personal"))
  private val businessAuthorisationRequestJourney: Journey = Journey(journeyType = JourneyType.AuthorisationRequest, clientType = Some("business"))
  private val trustAuthorisationRequestJourney: Journey = Journey(journeyType = JourneyType.AuthorisationRequest, clientType = Some("trust"))
  private val personalAgentCancelAuthorisationJourney: Journey = Journey(JourneyType.AgentCancelAuthorisation, clientType = Some("personal"))
  private val businessAgentCancelAuthorisationJourney: Journey = Journey(JourneyType.AgentCancelAuthorisation, clientType = Some("business"))
  private val trustAgentCancelAuthorisationJourney: Journey = Journey(JourneyType.AgentCancelAuthorisation, clientType = Some("trust"))

  private val optionsForPersonal: Seq[String] = Seq("HMRC-MTD-IT", "PERSONAL-INCOME-RECORD", "HMRC-MTD-VAT", "HMRC-CGT-PD", "HMRC-PPT-ORG")
  private val optionsForBusiness: Seq[String] = Seq("HMRC-MTD-VAT", "HMRC-PPT-ORG", "HMRC-CBC-ORG", "HMRC-PILLAR2-ORG")
  private val optionsForTrust: Seq[String] = Seq("HMRC-PPT-ORG", "HMRC-CGT-PD", "HMRC-CBC-ORG", "HMRC-PILLAR2-ORG")

  private val allNonRefinableOptionsForClientType = Map(
    "personal" -> optionsForPersonal,
    "business" -> optionsForBusiness,
    "trust" -> optionsForTrust
  )

  private val allRefinableOptionsForClientType = Map(
    "trust" -> Seq("HMRC-TERS-ORG")
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

  private val allClientTypeAuthJourneys: List[Journey] = List(
    personalAuthorisationRequestJourney,
    businessAuthorisationRequestJourney,
    trustAuthorisationRequestJourney
  )
  private val allClientTypeDeAuthJourneys: List[Journey] = List(
    personalAgentCancelAuthorisationJourney,
    businessAgentCancelAuthorisationJourney,
    trustAgentCancelAuthorisationJourney
  )

  val journeyService: JourneyService = app.injector.instanceOf[JourneyService]

  override def beforeEach(): Unit = {
    await(journeyService.deleteAllAnswersInSession(request))
    super.beforeEach()
  }

  "GET /authorisation-request/client-identifier" should {
    "redirect to ASA dashboard when no journey session present" in {
      authoriseAsAgent()
      val result = get(routes.EnterClientIdController.show(JourneyType.AuthorisationRequest).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe "http://localhost:9401/agent-services-account/home"
    }
    "redirect to the journey start when no service present" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(Journey(journeyType = JourneyType.AuthorisationRequest, clientType = Some("personal"))))
      val result = get(routes.EnterClientIdController.show(JourneyType.AuthorisationRequest).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.SelectClientTypeController.show(JourneyType.AuthorisationRequest).url
    }
    allClientTypeAuthJourneys.foreach(j =>
        allNonRefinableOptionsForClientType(j.getClientType).foreach(o => s"display the client identifier page for ${j.getClientType} $o" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(j.copy(clientService = Some(o))))
      val result = get(routes.EnterClientIdController.show(JourneyType.AuthorisationRequest).url)
      result.status shouldBe OK
    }))
  }

  "POST /authorisation-request/client-identifier" should {
    allClientTypeAuthJourneys.foreach(j =>
      allNonRefinableOptionsForClientType.get(j.getClientType).map(allOptions =>
        allOptions.foreach(o => s"redirect to the next page after storing answer of ${exampleValueForService(o)} for ${j.getClientType} $o" in {
          authoriseAsAgent()
          stubGet(getClientDetailsUrl(o, exampleValueForService(o)), OK, testClientDetailsResponseJson.toString)
          await(journeyService.saveJourney(j.copy(clientService = Some(o))))
          val result = post(routes.EnterClientIdController.onSubmit(JourneyType.AuthorisationRequest).url)(Map(
            s"${getFieldName(o)}" -> Seq(exampleValueForService(o))
          ))
          result.status shouldBe SEE_OTHER
          val expectedLocation = if (o == "HMRC-TERS-ORG" | o == "HMRC-TERSNT-ORG") "routes.ConfirmClientController.show(journeyType).url"
          else routes.EnterClientFactController.show(JourneyType.AuthorisationRequest).url
          result.header("Location").value shouldBe expectedLocation
        })))
    "show an error when no selection is made" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(personalAuthorisationRequestJourney.copy(clientService = Some("HMRC-MTD-IT"))))
      val result = post(routes.EnterClientIdController.onSubmit(JourneyType.AuthorisationRequest).url)("")
      result.status shouldBe BAD_REQUEST
    }
  }

  "GET /agent-cancel-authorisation/client-identifier" should {
    "redirect to ASA dashboard when no journey session present" in {
      authoriseAsAgent()
      val result = get(routes.EnterClientIdController.show(JourneyType.AgentCancelAuthorisation).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe "http://localhost:9401/agent-services-account/home"
    }
    "redirect to the journey start when no client type present" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(Journey(journeyType = JourneyType.AgentCancelAuthorisation, clientType = None)))
      val result = get(routes.EnterClientIdController.show(JourneyType.AgentCancelAuthorisation).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.SelectClientTypeController.show(JourneyType.AgentCancelAuthorisation).url
    }
    allClientTypeDeAuthJourneys.foreach(j =>
      allNonRefinableOptionsForClientType(j.getClientType).foreach(o => s"display the the client identifier page for ${j.getClientType} $o" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(j.copy(clientService = Some(o))))
      val result = get(routes.EnterClientIdController.show(JourneyType.AgentCancelAuthorisation).url)
      result.status shouldBe OK
    }))
  }

  "POST /agent-cancel-authorisation/client-identifier" should {
    allClientTypeDeAuthJourneys.foreach(j =>
      allNonRefinableOptionsForClientType.get(j.getClientType).map(allOptions =>
        allOptions.foreach(o => s"redirect to the next page after storing answer of ${exampleValueForService(o)} for ${j.getClientType} $o" in {
          authoriseAsAgent()
          stubGet(getClientDetailsUrl(o, exampleValueForService(o)), OK, testClientDetailsResponseJson.toString)
          await(journeyService.saveJourney(j.copy(clientService = Some(o))))
          val result = post(routes.EnterClientIdController.onSubmit(JourneyType.AgentCancelAuthorisation).url)(Map(
            s"${getFieldName(o)}" -> Seq(exampleValueForService(o))
          ))
          result.status shouldBe SEE_OTHER
          val expectedLocation = if (o == "HMRC-TERS-ORG" | o == "HMRC-TERSNT-ORG") "routes.ConfirmClientController.show(journeyType).url"
          else routes.EnterClientFactController.show(JourneyType.AgentCancelAuthorisation).url
          result.header("Location").value shouldBe expectedLocation
        })))
    "show an error when no selection is made" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(personalAgentCancelAuthorisationJourney.copy(clientService = Some("HMRC-MTD-IT"))))
      val result = post(routes.EnterClientIdController.onSubmit(JourneyType.AgentCancelAuthorisation).url)("")
      result.status shouldBe BAD_REQUEST
    }
  }

}
