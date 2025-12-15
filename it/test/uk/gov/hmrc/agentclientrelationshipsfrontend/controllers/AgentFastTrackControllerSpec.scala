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

import org.scalatest.concurrent.ScalaFutures
import play.api.libs.json.Json
import play.api.test.Helpers.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.journey.routes as journeyRoutes
import uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.routes as fastTrackRoutes
import uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.testOnly.routes as testRoutes
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{AgentJourney, AgentJourneyType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.{AgentFastTrackRequest, ClientDetailsResponse, KnownFactType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{AgentJourneyService, ClientServiceConfigurationService}
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AgentClientRelationshipStub, AuthStubs, ComponentSpecHelper}

class AgentFastTrackControllerSpec extends ComponentSpecHelper with AuthStubs with AgentClientRelationshipStub with ScalaFutures {

  lazy val journeyType: AgentJourneyType = AgentJourneyType.AuthorisationRequest

  def getClientDetailsUrl(service: String, clientId: String) = s"/agent-client-relationships/client/$service/details/$clientId"

  //TODO Create TestData common and move identifiers there
  val urn = "XXTRUST10000100"
  val utr = "4937455253"
  val plrid = "XAPLR2222222222"
  val cbcId = "XACBC0516273849"
  val nino = "BL661236A"
  val vrn = "101747696"
  val cgtPgRef = "XMCGTP123456789"
  val etmpRegistrationNumber = "XAPPT0000012345"



  val itSaFastTrackRequest: AgentFastTrackRequest = AgentFastTrackRequest(
    clientType = Some("personal"),
    service = "HMRC-MTD-IT",
    clientIdentifier = nino,
    clientIdentifierType = "ni",
    knownFact = Some("BX9 1AS")
  )

  val personalIncomeFastTrackRequest: AgentFastTrackRequest = AgentFastTrackRequest(
    clientType = Some("personal"),
    service = "PERSONAL-INCOME-RECORD",
    clientIdentifier = nino,
    clientIdentifierType = "ni",
    knownFact = Some("BX9 1AS")
  )

  val vatFastTrackRequest: AgentFastTrackRequest = AgentFastTrackRequest(
    clientType = Some("business"),
    service = "HMRC-MTD-VAT",
    clientIdentifier = vrn,
    clientIdentifierType = "vrn",
    knownFact = Some("BX9 1AS")
  )

  val cgtFastTrackRequest: AgentFastTrackRequest = AgentFastTrackRequest(
    clientType = Some("personal"),
    service = "HMRC-CGT-PD",
    clientIdentifier = cgtPgRef,
    clientIdentifierType = "CGTPDRef",
    knownFact = Some("BX9 1AS")
  )

  val pptFastTrackRequest: AgentFastTrackRequest = AgentFastTrackRequest(
    clientType = Some("personal"),
    service = "HMRC-PPT-ORG",
    clientIdentifier = etmpRegistrationNumber,
    clientIdentifierType = "EtmpRegistrationNumber",
    knownFact = Some("BX9 1AS")
  )

  val cbcFastTrackRequest: AgentFastTrackRequest = AgentFastTrackRequest(
    clientType = Some("business"),
    service = "HMRC-CBC-ORG",
    clientIdentifier = cbcId,
    clientIdentifierType = "cbcId",
    knownFact = Some("BX9 1AS")
  )

  val pillar2FastTrackRequest: AgentFastTrackRequest = AgentFastTrackRequest(
    clientType = Some("business"),
    service = "HMRC-PILLAR2-ORG",
    clientIdentifier = plrid,
    clientIdentifierType = "PLRID",
    knownFact = Some("BX9 1AS")
  )

  val tersFastTrackRequest: AgentFastTrackRequest = AgentFastTrackRequest(
    clientType = Some("trust"),
    service = "HMRC-TERS-ORG",
    clientIdentifier = utr,
    clientIdentifierType = "utr",
    knownFact = Some("BX9 1AS")
  )

  val tersNTFastTrackRequest: AgentFastTrackRequest = AgentFastTrackRequest(
    clientType = Some("trust"),
    service = "HMRC-TERSNT-ORG",
    clientIdentifier = urn,
    clientIdentifierType = "urn",
    knownFact = Some("BX9 1AS")
  )

  val validFastTrackRequests: Seq[AgentFastTrackRequest] = Seq(
    itSaFastTrackRequest,
    personalIncomeFastTrackRequest,
    vatFastTrackRequest,
    cgtFastTrackRequest,
    pptFastTrackRequest,
    cbcFastTrackRequest,
    pillar2FastTrackRequest,
    tersFastTrackRequest,
    tersNTFastTrackRequest
  )

  val missingKnownFactsFastTrackRequests: Seq[AgentFastTrackRequest] = Seq(
    itSaFastTrackRequest.copy(knownFact = None),
    personalIncomeFastTrackRequest.copy(knownFact = None),
    vatFastTrackRequest.copy(knownFact = None),
    cgtFastTrackRequest.copy(knownFact = None),
    pptFastTrackRequest.copy(knownFact = None),
    cbcFastTrackRequest.copy(knownFact = None),
    pillar2FastTrackRequest.copy(knownFact = None),
    tersFastTrackRequest.copy(knownFact = None),
    tersNTFastTrackRequest.copy(knownFact = None)
  )

  val invalidClientIdFastTrackRequests: Seq[AgentFastTrackRequest] = Seq(
    itSaFastTrackRequest.copy(clientIdentifier = urn),
    personalIncomeFastTrackRequest.copy(clientIdentifier = urn),
    vatFastTrackRequest.copy(clientIdentifier = urn),
    cgtFastTrackRequest.copy(clientIdentifier = urn),
    pptFastTrackRequest.copy(clientIdentifier = urn),
    cbcFastTrackRequest.copy(clientIdentifier = urn),
    pillar2FastTrackRequest.copy(clientIdentifier = urn),
    tersFastTrackRequest.copy(clientIdentifier = nino),
    tersNTFastTrackRequest.copy(clientIdentifier = nino)
  )

  val invalidClientIdTypeFastTrackRequests: Seq[AgentFastTrackRequest] = Seq(
    itSaFastTrackRequest.copy(clientIdentifierType = "urn"),
    personalIncomeFastTrackRequest.copy(clientIdentifierType = "urn"),
    vatFastTrackRequest.copy(clientIdentifierType = "urn"),
    cgtFastTrackRequest.copy(clientIdentifierType = "urn"),
    pptFastTrackRequest.copy(clientIdentifierType = "urn"),
    cbcFastTrackRequest.copy(clientIdentifierType = "urn"),
    pillar2FastTrackRequest.copy(clientIdentifierType = "urn"),
    tersFastTrackRequest.copy(clientIdentifierType = "urn"),
    tersNTFastTrackRequest.copy(clientIdentifierType = "ni")
  )


  def toFastTrackRequests(agentFastTrackRequest: AgentFastTrackRequest): Map[String, Seq[String]] = Map(
     "clientType" -> agentFastTrackRequest.clientType.fold(Seq.empty)(Seq(_)),
     "service" -> Seq(agentFastTrackRequest.service),
     "clientIdentifier" -> Seq(agentFastTrackRequest.clientIdentifier),
     "clientIdentifierType" -> Seq(agentFastTrackRequest.clientIdentifierType),
     "knownFact" -> agentFastTrackRequest.knownFact.fold(Seq.empty)(Seq(_))
   )

   def toJourney(agentFastTrackRequest: AgentFastTrackRequest,clientDetailsResponse: Option[ClientDetailsResponse], journeyType: AgentJourneyType = journeyType): AgentJourney =
     journeyService.newJourney(AgentJourneyType.AuthorisationRequest)
       .copy(
         clientType = agentFastTrackRequest.clientType,
         clientService = Some(agentFastTrackRequest.service),
         clientId = Some(agentFastTrackRequest.clientIdentifier),
         clientDetailsResponse = clientDetailsResponse,
         knownFact = agentFastTrackRequest.knownFact

       )

   def toClientRelationship(agentFastTrackRequest: AgentFastTrackRequest, knownFactType: Option[KnownFactType] = Some(KnownFactType.PostalCode)):ClientDetailsResponse = ClientDetailsResponse(
     name = "AnyName",
     status = None,
     isOverseas = None,
     knownFacts = agentFastTrackRequest.knownFact.toSeq,
     knownFactType = knownFactType,
     hasPendingInvitation = false,
     hasExistingRelationshipFor = None
   )

  val journeyService: AgentJourneyService = app.injector.instanceOf[AgentJourneyService]
  val serviceConfig: ClientServiceConfigurationService = app.injector.instanceOf[ClientServiceConfigurationService]

  override def beforeEach(): Unit = {
    await(journeyService.deleteAllAnswersInSession(request))
    super.beforeEach()
  }
  
  "POST /agents/fast-track" should {
    validFastTrackRequests.foreach(fastTrackFormData => s"redirect to the next page and store journey data for ${fastTrackFormData.service} service for ${fastTrackFormData.clientIdentifierType} and knownFacts: ${fastTrackFormData.knownFact}" in {
      authoriseAsAgent()
      //TODO - test with different patterns
      val clientDetailsResponse: Option[ClientDetailsResponse] = Some(toClientRelationship(fastTrackFormData, Some(KnownFactType.PostalCode)))
      givenClientRelationshipFor(fastTrackFormData.service, fastTrackFormData.clientIdentifier, Json.toJson(clientDetailsResponse).toString)

      val result = post(fastTrackRoutes.AgentFastTrackController.agentFastTrack.url)(toFastTrackRequests(fastTrackFormData))
      result.status shouldBe SEE_OTHER

      fastTrackFormData.service match{
        case "HMRC-TERS-ORG" | "HMRC-TERSNT-ORG" => result.header("Location").value shouldBe journeyRoutes.ServiceRefinementController.show(journeyType).url
        case _ => result.header("Location").value shouldBe journeyRoutes.ConfirmClientController.show(journeyType).url
      }

      whenReady(journeyService.getJourney()) { result =>
        result should contain(toJourney(fastTrackFormData, clientDetailsResponse))
      }
    })

    missingKnownFactsFastTrackRequests.foreach(fastTrackFormData => s"redirect to the next page and store journey data for ${fastTrackFormData.service} service for ${fastTrackFormData.clientIdentifierType} and knownFacts missing" in {
      authoriseAsAgent()
      val clientDetailsResponse: Option[ClientDetailsResponse] = Some(toClientRelationship(fastTrackFormData, Some(KnownFactType.PostalCode)))
        .map(_.copy(knownFacts = List("TestKnowFacts")))
      givenClientRelationshipFor(fastTrackFormData.service, fastTrackFormData.clientIdentifier, Json.toJson(clientDetailsResponse).toString)

      val result = post(fastTrackRoutes.AgentFastTrackController.agentFastTrack.url)(toFastTrackRequests(fastTrackFormData))
      result.status shouldBe SEE_OTHER

      fastTrackFormData.service match {
        case "HMRC-MTD-IT" => result.header("Location").value shouldBe journeyRoutes.FastTrackLandingController.show(journeyType).url
        case "HMRC-TERS-ORG" | "HMRC-TERSNT-ORG" => result.header("Location").value shouldBe journeyRoutes.ServiceRefinementController.show(journeyType).url
        case _ => result.header("Location").value shouldBe journeyRoutes.EnterClientFactController.show(journeyType).url
      }

      whenReady(journeyService.getJourney()) { result =>
        result should contain(toJourney(fastTrackFormData, clientDetailsResponse))
      }
    })

    validFastTrackRequests.foreach(originalFastTrackFormData => s"redirect to known-fact-not-matched page and store journey data for ${originalFastTrackFormData.service} service for ${originalFastTrackFormData.clientIdentifierType} when  knownFacts format check fail" in {
      val fastTrackFormData = originalFastTrackFormData.copy(knownFact = Some("WrongKnownFacts"))
      val clientDetailsResponse: Option[ClientDetailsResponse] = Some(toClientRelationship(fastTrackFormData))

      authoriseAsAgent()
      givenClientRelationshipFor(fastTrackFormData.service, fastTrackFormData.clientIdentifier, Json.toJson(clientDetailsResponse).toString)

      val result = post(fastTrackRoutes.AgentFastTrackController.agentFastTrack.url)(toFastTrackRequests(fastTrackFormData))
      result.status shouldBe SEE_OTHER

      result.header("Location").value shouldBe journeyRoutes.JourneyExitController.show(journeyType, serviceConfig.getNotFoundError(journeyType, fastTrackFormData.service)).url

      whenReady(journeyService.getJourney()) { result =>
        result should contain(toJourney(fastTrackFormData, clientDetailsResponse).copy(knownFact = None))
      }
    })

    validFastTrackRequests.foreach(fastTrackFormData => s"redirect to client-not-found page and store journey data for ${fastTrackFormData.service} service for ${fastTrackFormData.clientIdentifierType} when  clientRelation data are missing" in {
      val clientDetailsResponse: Option[ClientDetailsResponse] = None

      authoriseAsAgent()
      givenNotFoundForServiceAndClient(fastTrackFormData.service, fastTrackFormData.clientIdentifier)

      val result = post(fastTrackRoutes.AgentFastTrackController.agentFastTrack.url)(toFastTrackRequests(fastTrackFormData))
      result.status shouldBe SEE_OTHER

      result.header("Location").value shouldBe journeyRoutes.JourneyExitController.show(journeyType, serviceConfig.getNotFoundError(journeyType, fastTrackFormData.service)).url

      whenReady(journeyService.getJourney()) { result =>
        result should contain(toJourney(fastTrackFormData, clientDetailsResponse).copy(knownFact = None))
      }
    })

    validFastTrackRequests.foreach(fastTrackFormData => s"redirect to start journey when clientId fail regex and no error redirect configured for ${fastTrackFormData.service} service for ${fastTrackFormData.clientIdentifierType}" in {
      authoriseAsAgent()
      val corruptedClientIdFormData = fastTrackFormData.copy(clientIdentifier = "FakeClientId")

      val result = post(fastTrackRoutes.AgentFastTrackController.agentFastTrack.url)(toFastTrackRequests(corruptedClientIdFormData))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe journeyRoutes.StartJourneyController.startJourney(journeyType).url

      whenReady(journeyService.getJourney()) { result =>
        result shouldBe None
      }
    })

    validFastTrackRequests.foreach(fastTrackFormData => s"redirect to start journey when service is not supported and no error redirect configured for ${fastTrackFormData.service} service for ${fastTrackFormData.clientIdentifierType}" in {
      authoriseAsAgent()
      val corruptedClientIdFormData = fastTrackFormData.copy(service = "FakeService")
      val result = post(fastTrackRoutes.AgentFastTrackController.agentFastTrack.url)(toFastTrackRequests(corruptedClientIdFormData))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe journeyRoutes.StartJourneyController.startJourney(journeyType).url

      whenReady(journeyService.getJourney()) { result =>
        result shouldBe None
      }
    })

    validFastTrackRequests.foreach(fastTrackFormData => s"redirect to error url when clientId fail regex for ${fastTrackFormData.service} service for ${fastTrackFormData.clientIdentifierType}" in {
      authoriseAsAgent()
      val corruptedClientIdFormData = fastTrackFormData.copy(clientIdentifier = "FakeClientId")
      val errorURLRedirect = ("error", testRoutes.TestOnlyController.getFastTrackForm().url)
      val result = post(fastTrackRoutes.AgentFastTrackController.agentFastTrack.url)(toFastTrackRequests(corruptedClientIdFormData))(Seq(errorURLRedirect))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe s"${errorURLRedirect._2}?issue=INVALID_CLIENT_ID_RECEIVED:FAKECLIENTID"

      whenReady(journeyService.getJourney()) { result =>
        result shouldBe None
      }
    })

    validFastTrackRequests.foreach(fastTrackFormData => s"redirect to error url when service not supported for ${fastTrackFormData.service} service for ${fastTrackFormData.clientIdentifierType}" in {
      authoriseAsAgent()
      val corruptedClientIdFormData = fastTrackFormData.copy(service = "FakeService")
      val errorURLRedirect = ("error", testRoutes.TestOnlyController.getFastTrackForm().url)
      val result = post(fastTrackRoutes.AgentFastTrackController.agentFastTrack.url)(toFastTrackRequests(corruptedClientIdFormData))(Seq(errorURLRedirect))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe s"${errorURLRedirect._2}?issue=UNSUPPORTED_SERVICE"

      whenReady(journeyService.getJourney()) { result =>
        result shouldBe None
      }
    })

    validFastTrackRequests.foreach(fastTrackFormData => s"redirect to error url when clientId type not supported for ${fastTrackFormData.service} service for ${fastTrackFormData.clientIdentifierType}" in {
      authoriseAsAgent()
      val corruptedClientIdFormData = fastTrackFormData.copy(clientIdentifierType = "FakeClientIdentifier")
      val errorURLRedirect = ("error", testRoutes.TestOnlyController.getFastTrackForm().url)
      val result = post(fastTrackRoutes.AgentFastTrackController.agentFastTrack.url)(toFastTrackRequests(corruptedClientIdFormData))(Seq(errorURLRedirect))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe s"${errorURLRedirect._2}?issue=UNSUPPORTED_CLIENT_ID_TYPE"

      whenReady(journeyService.getJourney()) { result =>
        result shouldBe None
      }
    })

    invalidClientIdFastTrackRequests.foreach(fastTrackFormData => s"redirect to error url when clientId is not for selected service ${fastTrackFormData.service} service for ${fastTrackFormData.clientIdentifierType}" in {
      authoriseAsAgent()
      val errorURLRedirect = ("error", testRoutes.TestOnlyController.getFastTrackForm().url)
      val result = post(fastTrackRoutes.AgentFastTrackController.agentFastTrack.url)(toFastTrackRequests(fastTrackFormData))(Seq(errorURLRedirect))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe s"${errorURLRedirect._2}?issue=INVALID_SUBMISSION"

      whenReady(journeyService.getJourney()) { result =>
        result shouldBe None
      }
    })

    invalidClientIdTypeFastTrackRequests.foreach(fastTrackFormData => s"redirect to error url when clientId type is not for selected service ${fastTrackFormData.service} service for ${fastTrackFormData.clientIdentifierType}" in {
      authoriseAsAgent()
      val errorURLRedirect = ("error", testRoutes.TestOnlyController.getFastTrackForm().url)
      val result = post(fastTrackRoutes.AgentFastTrackController.agentFastTrack.url)(toFastTrackRequests(fastTrackFormData))(Seq(errorURLRedirect))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe s"${errorURLRedirect._2}?issue=INVALID_SUBMISSION"

      whenReady(journeyService.getJourney()) { result =>
        result shouldBe None
      }
    })
  }

  "POST /agents/fast-track/redirect" should {

    validFastTrackRequests.foreach { fastTrackFormData =>
      s"return the correct redirect URL for ${fastTrackFormData.service} service for ${fastTrackFormData.clientIdentifierType} and knownFacts: ${fastTrackFormData.knownFact}" in {
        authoriseAsAgent()
        val clientDetailsResponse: Option[ClientDetailsResponse] = Some(toClientRelationship(fastTrackFormData, Some(KnownFactType.PostalCode)))
        givenClientRelationshipFor(fastTrackFormData.service, fastTrackFormData.clientIdentifier, Json.toJson(clientDetailsResponse).toString)

        val result = post(fastTrackRoutes.AgentFastTrackController.agentFastTrackGetRedirectUrl.url)(toFastTrackRequests(fastTrackFormData))
        val resultBody = result.body
        val expectedUrl = fastTrackFormData.service match {
          case "HMRC-TERS-ORG" | "HMRC-TERSNT-ORG" => journeyRoutes.ServiceRefinementController.show(journeyType).url
          case _ => journeyRoutes.ConfirmClientController.show(journeyType).url
        }

        result.status shouldBe OK
        resultBody shouldBe expectedUrl
      }
    }

    missingKnownFactsFastTrackRequests.foreach { fastTrackFormData =>
      s"return the correct redirect URL for ${fastTrackFormData.service} service for ${fastTrackFormData.clientIdentifierType} and knownFacts missing" in {
        authoriseAsAgent()
        val clientDetailsResponse: Option[ClientDetailsResponse] = Some(toClientRelationship(fastTrackFormData, Some(KnownFactType.PostalCode)))
          .map(_.copy(knownFacts = List("TestKnowFacts")))
        givenClientRelationshipFor(fastTrackFormData.service, fastTrackFormData.clientIdentifier, Json.toJson(clientDetailsResponse).toString)

        val result = post(fastTrackRoutes.AgentFastTrackController.agentFastTrackGetRedirectUrl.url)(toFastTrackRequests(fastTrackFormData))
        val resultBody = result.body
        val expectedUrl = fastTrackFormData.service match {
          case "HMRC-MTD-IT" => journeyRoutes.FastTrackLandingController.show(journeyType).url
          case "HMRC-TERS-ORG" | "HMRC-TERSNT-ORG" => journeyRoutes.ServiceRefinementController.show(journeyType).url
          case _ => journeyRoutes.EnterClientFactController.show(journeyType).url
        }

        result.status shouldBe OK
        resultBody shouldBe expectedUrl
      }
    }

    validFastTrackRequests.foreach { originalFastTrackFormData =>
      s"return the correct redirect URL for ${originalFastTrackFormData.service} service for ${originalFastTrackFormData.clientIdentifierType} when knownFacts format check fail" in {
        val fastTrackFormData = originalFastTrackFormData.copy(knownFact = Some("WrongKnownFacts"))
        val clientDetailsResponse: Option[ClientDetailsResponse] = Some(toClientRelationship(fastTrackFormData))

        authoriseAsAgent()
        givenClientRelationshipFor(fastTrackFormData.service, fastTrackFormData.clientIdentifier, Json.toJson(clientDetailsResponse).toString)

        val result = post(fastTrackRoutes.AgentFastTrackController.agentFastTrackGetRedirectUrl.url)(toFastTrackRequests(fastTrackFormData))
        val resultBody = result.body

        result.status shouldBe OK
        resultBody shouldBe journeyRoutes.JourneyExitController.show(journeyType, serviceConfig.getNotFoundError(journeyType, fastTrackFormData.service)).url
      }
    }

    validFastTrackRequests.foreach { fastTrackFormData =>
      s"return the correct redirect URL for ${fastTrackFormData.service} service for ${fastTrackFormData.clientIdentifierType} when  clientRelation data are missing" in {
        authoriseAsAgent()
        givenNotFoundForServiceAndClient(fastTrackFormData.service, fastTrackFormData.clientIdentifier)

        val result = post(fastTrackRoutes.AgentFastTrackController.agentFastTrackGetRedirectUrl.url)(toFastTrackRequests(fastTrackFormData))
        val resultBody = result.body

        result.status shouldBe OK
        resultBody shouldBe journeyRoutes.JourneyExitController.show(journeyType, serviceConfig.getNotFoundError(journeyType, fastTrackFormData.service)).url
      }
    }

    validFastTrackRequests.foreach { fastTrackFormData =>
      s"return the correct redirect URL for ${fastTrackFormData.service} service for ${fastTrackFormData.clientIdentifierType}" in {
        authoriseAsAgent()
        val corruptedClientIdFormData = fastTrackFormData.copy(clientIdentifier = "FakeClientId")

        val result = post(fastTrackRoutes.AgentFastTrackController.agentFastTrackGetRedirectUrl.url)(toFastTrackRequests(corruptedClientIdFormData))
        val resultBody = result.body

        result.status shouldBe OK
        resultBody shouldBe journeyRoutes.StartJourneyController.startJourney(journeyType).url
      }
    }

    validFastTrackRequests.foreach { fastTrackFormData =>
      s"return the correct redirect URL when service is not supported and no error redirect configured for ${fastTrackFormData.service} service for ${fastTrackFormData.clientIdentifierType}" in {
        authoriseAsAgent()
        val corruptedClientIdFormData = fastTrackFormData.copy(service = "FakeService")
        val result = post(fastTrackRoutes.AgentFastTrackController.agentFastTrackGetRedirectUrl.url)(toFastTrackRequests(corruptedClientIdFormData))
        val resultBody = result.body

        result.status shouldBe OK
        resultBody shouldBe journeyRoutes.StartJourneyController.startJourney(journeyType).url
      }
    }

    validFastTrackRequests.foreach { fastTrackFormData =>
      s"return the correct redirect URL when clientId fail regex for ${fastTrackFormData.service} service for ${fastTrackFormData.clientIdentifierType}" in {
        authoriseAsAgent()
        val corruptedClientIdFormData = fastTrackFormData.copy(clientIdentifier = "FakeClientId")
        val errorURLRedirect = ("error", testRoutes.TestOnlyController.getFastTrackForm().url)
        val result = post(fastTrackRoutes.AgentFastTrackController.agentFastTrackGetRedirectUrl.url)(toFastTrackRequests(corruptedClientIdFormData))(Seq(errorURLRedirect))
        val resultBody = result.body

        result.status shouldBe OK
        resultBody shouldBe s"${errorURLRedirect._2}?issue=INVALID_CLIENT_ID_RECEIVED:FAKECLIENTID"
      }
    }

    validFastTrackRequests.foreach { fastTrackFormData =>
      s"return the correct redirect URL when service not supported for ${fastTrackFormData.service} service for ${fastTrackFormData.clientIdentifierType}" in {
        authoriseAsAgent()
        val corruptedClientIdFormData = fastTrackFormData.copy(service = "FakeService")
        val errorURLRedirect = ("error", testRoutes.TestOnlyController.getFastTrackForm().url)
        val result = post(fastTrackRoutes.AgentFastTrackController.agentFastTrackGetRedirectUrl.url)(toFastTrackRequests(corruptedClientIdFormData))(Seq(errorURLRedirect))
        val resultBody = result.body

        result.status shouldBe OK
        resultBody shouldBe s"${errorURLRedirect._2}?issue=UNSUPPORTED_SERVICE"
      }
    }

    validFastTrackRequests.foreach { fastTrackFormData =>
      s"return the correct redirect URL when clientId type not supported for ${fastTrackFormData.service} service for ${fastTrackFormData.clientIdentifierType}" in {
        authoriseAsAgent()
        val corruptedClientIdFormData = fastTrackFormData.copy(clientIdentifierType = "FakeClientIdentifier")
        val errorURLRedirect = ("error", testRoutes.TestOnlyController.getFastTrackForm().url)
        val result = post(fastTrackRoutes.AgentFastTrackController.agentFastTrackGetRedirectUrl.url)(toFastTrackRequests(corruptedClientIdFormData))(Seq(errorURLRedirect))
        val resultBody = result.body

        result.status shouldBe OK
        resultBody shouldBe s"${errorURLRedirect._2}?issue=UNSUPPORTED_CLIENT_ID_TYPE"
      }
    }

    invalidClientIdFastTrackRequests.foreach { fastTrackFormData =>
      s"return the correct redirect URL when clientId is not for selected service ${fastTrackFormData.service} service for ${fastTrackFormData.clientIdentifierType}" in {
        authoriseAsAgent()
        val errorURLRedirect = ("error", testRoutes.TestOnlyController.getFastTrackForm().url)
        val result = post(fastTrackRoutes.AgentFastTrackController.agentFastTrackGetRedirectUrl.url)(toFastTrackRequests(fastTrackFormData))(Seq(errorURLRedirect))
        val resultBody = result.body

        result.status shouldBe OK
        resultBody shouldBe s"${errorURLRedirect._2}?issue=INVALID_SUBMISSION"
      }
    }

    invalidClientIdTypeFastTrackRequests.foreach { fastTrackFormData =>
      s"return the correct redirect URL when clientId type is not for selected service ${fastTrackFormData.service} service for ${fastTrackFormData.clientIdentifierType}" in {
        authoriseAsAgent()
        val errorURLRedirect = ("error", testRoutes.TestOnlyController.getFastTrackForm().url)
        val result = post(fastTrackRoutes.AgentFastTrackController.agentFastTrackGetRedirectUrl.url)(toFastTrackRequests(fastTrackFormData))(Seq(errorURLRedirect))
        val resultBody = result.body

        result.status shouldBe OK
        resultBody shouldBe s"${errorURLRedirect._2}?issue=INVALID_SUBMISSION"
      }
    }
  }
}
