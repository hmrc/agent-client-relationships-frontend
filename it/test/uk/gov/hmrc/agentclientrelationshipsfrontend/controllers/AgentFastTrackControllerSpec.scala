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

import org.scalatest.concurrent.Futures.whenReady
import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.test.Helpers.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.routes as fastTrackRoutes
import uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.journey.routes as journeyRoutes
import uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.testOnly.routes as testRoutes
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.AgentFastTrackFormData
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{Journey, JourneyState, JourneyType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.JourneyService
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AuthStubs, ComponentSpecHelper}
import org.scalatest.concurrent.ScalaFutures

class AgentFastTrackControllerSpec extends ComponentSpecHelper with AuthStubs with ScalaFutures {

  lazy val journeyType: JourneyType = JourneyType.AuthorisationRequest

  //TODO Create TestData common and move identifiers there
  val urn = "XXTRUST10000100"
  val utr = "4937455253"
  val plrid = "XAPLR2222222222"
  val cbcId = "XACBC0516273849"
  val nino = "BL661236A"
  val vrn = "101747696"
  val cgtPgRef = "XMCGTP123456789"
  val etmpRegistrationNumber = "XAPPT0000012345"



  val itSaFastTrackRequest: AgentFastTrackFormData = AgentFastTrackFormData(
    clientType = None,
    service = "HMRC-MTD-IT",
    clientIdentifier = nino,
    clientIdentifierType = "ni",
    knownFact = None
  )

  val personalIncomeFastTrackRequest: AgentFastTrackFormData = AgentFastTrackFormData(
    clientType = None,
    service = "PERSONAL-INCOME-RECORD",
    clientIdentifier = nino,
    clientIdentifierType = "ni",
    knownFact = None
  )

  val vatFastTrackRequest: AgentFastTrackFormData = AgentFastTrackFormData(
    clientType = None,
    service = "HMRC-MTD-VAT",
    clientIdentifier = vrn,
    clientIdentifierType = "vrn",
    knownFact = None
  )

  val cgtFastTrackRequest: AgentFastTrackFormData = AgentFastTrackFormData(
    clientType = None,
    service = "HMRC-CGT-PD",
    clientIdentifier = cgtPgRef,
    clientIdentifierType = "CGTPDRef",
    knownFact = None
  )

  val pptFastTrackRequest: AgentFastTrackFormData = AgentFastTrackFormData(
    clientType = None,
    service = "HMRC-PPT-ORG",
    clientIdentifier = etmpRegistrationNumber,
    clientIdentifierType = "EtmpRegistrationNumber",
    knownFact = None
  )

  val cbcFastTrackRequest: AgentFastTrackFormData = AgentFastTrackFormData(
    clientType = None,
    service = "HMRC-CBC-ORG",
    clientIdentifier = cbcId,
    clientIdentifierType = "cbcId",
    knownFact = None
  )

  val pillar2FastTrackRequest: AgentFastTrackFormData = AgentFastTrackFormData(
    clientType = None,
    service = "HMRC-PILLAR2-ORG",
    clientIdentifier = plrid,
    clientIdentifierType = "PLRID",
    knownFact = None
  )

  val tersFastTrackRequest: AgentFastTrackFormData = AgentFastTrackFormData(
    clientType = None,
    service = "HMRC-TERS-ORG",
    clientIdentifier = utr,
    clientIdentifierType = "utr",
    knownFact = None
  )

  val tersNTFastTrackRequest: AgentFastTrackFormData = AgentFastTrackFormData(
    clientType = None,
    service = "HMRC-TERSNT-ORG",
    clientIdentifier = urn,
    clientIdentifierType = "urn",
    knownFact = None
  )

  val validFastTrackRequests: Seq[AgentFastTrackFormData] = Seq(
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

  val invalidClientIdFastTrackRequests: Seq[AgentFastTrackFormData] = Seq(
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

  val invalidClientIdTypeFastTrackRequests: Seq[AgentFastTrackFormData] = Seq(
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


  def toFastTrackRequests(agentFastTrackFormData: AgentFastTrackFormData): Map[String, Seq[String]] = Map(
     "clientType" -> agentFastTrackFormData.clientType.fold(Seq.empty)(Seq(_)),
     "service" -> Seq(agentFastTrackFormData.service),
     "clientIdentifier" -> Seq(agentFastTrackFormData.clientIdentifier),
     "clientIdentifierType" -> Seq(agentFastTrackFormData.clientIdentifierType),
     "knownFact" -> agentFastTrackFormData.knownFact.fold(Seq.empty)(Seq(_))
   )

   def toJourney(fastTrackFormData: AgentFastTrackFormData, journeyType: JourneyType = journeyType): Journey =
     journeyService.newJourney(JourneyType.AuthorisationRequest)
       .copy(
         clientType = fastTrackFormData.clientType,
         clientService = Some(fastTrackFormData.service),
         clientId = Some(fastTrackFormData.clientIdentifier),

       )

  val journeyService: JourneyService = app.injector.instanceOf[JourneyService]

  override def beforeEach(): Unit = {
    await(journeyService.deleteAllAnswersInSession(request))
    super.beforeEach()
  }
  
  "POST /agents/fast-track/agentFastTrack" should {
    validFastTrackRequests.foreach(fastTrackFormData => s"redirect to the next page and store journey data for ${fastTrackFormData.service} service for ${fastTrackFormData.clientIdentifierType}" in {
      authoriseAsAgent()

      val result = post(fastTrackRoutes.AgentFastTrackController.agentFastTrack.url)(toFastTrackRequests(fastTrackFormData))
      result.status shouldBe SEE_OTHER

      fastTrackFormData.service match{
        case "HMRC-TERS-ORG" | "HMRC-TERSNT-ORG" => result.header("Location").value shouldBe journeyRoutes.ServiceRefinementController.show(journeyType).url
        case _ => result.header("Location").value shouldBe journeyRoutes.EnterClientIdController.show(journeyType).url
      }

      whenReady(journeyService.getJourney()) { result =>
        result should contain(toJourney(fastTrackFormData))
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

    validFastTrackRequests.foreach(fastTrackFormData => s"redirect to error ulr when clientId fail regex for ${fastTrackFormData.service} service for ${fastTrackFormData.clientIdentifierType}" in {
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

    validFastTrackRequests.foreach(fastTrackFormData => s"redirect to error ulr when service not supported for ${fastTrackFormData.service} service for ${fastTrackFormData.clientIdentifierType}" in {
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

    validFastTrackRequests.foreach(fastTrackFormData => s"redirect to error ulr when clientId type not supported for ${fastTrackFormData.service} service for ${fastTrackFormData.clientIdentifierType}" in {
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

    invalidClientIdFastTrackRequests.foreach(fastTrackFormData => s"redirect to error ulr when clientId is not for selected service ${fastTrackFormData.service} service for ${fastTrackFormData.clientIdentifierType}" in {
      authoriseAsAgent()
      val errorURLRedirect = ("error", testRoutes.TestOnlyController.getFastTrackForm().url)
      val result = post(fastTrackRoutes.AgentFastTrackController.agentFastTrack.url)(toFastTrackRequests(fastTrackFormData))(Seq(errorURLRedirect))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe s"${errorURLRedirect._2}?issue=INVALID_SUBMISSION"

      whenReady(journeyService.getJourney()) { result =>
        result shouldBe None
      }
    })

    invalidClientIdTypeFastTrackRequests.foreach(fastTrackFormData => s"redirect to error ulr when clientId type is not for selected service ${fastTrackFormData.service} service for ${fastTrackFormData.clientIdentifierType}" in {
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

}
