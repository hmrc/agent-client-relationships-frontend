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
import play.api.test.Helpers.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{Journey, JourneyState, JourneyType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.JourneyService
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AuthStubs, ComponentSpecHelper}

class SelectServiceControllerISpec extends ComponentSpecHelper with AuthStubs {

  private val personalAuthorisationRequestJourney: Journey = Journey(journeyType = JourneyType.AuthorisationRequest, clientType = Some("personal"))
  private val businessAuthorisationRequestJourney: Journey = Journey(journeyType = JourneyType.AuthorisationRequest, clientType = Some("business"))
  private val trustAuthorisationRequestJourney: Journey = Journey(journeyType = JourneyType.AuthorisationRequest, clientType = Some("trust"))
  private val personalAgentCancelAuthorisationJourney: Journey = Journey(JourneyType.AgentCancelAuthorisation, clientType = Some("personal"))
  private val businessAgentCancelAuthorisationJourney: Journey = Journey(JourneyType.AgentCancelAuthorisation, clientType = Some("business"))
  private val trustAgentCancelAuthorisationJourney: Journey = Journey(JourneyType.AgentCancelAuthorisation, clientType = Some("trust"))

  private val optionsForPersonal: Seq[String] = Seq("HMRC-MTD-IT", "PERSONAL-INCOME-RECORD", "HMRC-MTD-VAT", "HMRC-CGT-PD", "HMRC-PPT-ORG")
  private val optionsForBusiness: Seq[String] = Seq("HMRC-MTD-VAT", "HMRC-PPT-ORG", "HMRC-CBC-ORG", "HMRC-PILLAR2-ORG")
  private val optionsForTrust: Seq[String] = Seq("HMRC-PPT-ORG", "HMRC-CGT-PD", "HMRC-CBC-ORG", "HMRC-PILLAR2-ORG")
  private val allRefinableOptionsForClientType = Map(
    "trust" -> Seq("HMRC-TERS-ORG")
  )
  private val allNonRefinableOptionsForClientType = Map(
    "personal" -> optionsForPersonal,
    "business" -> optionsForBusiness,
    "trust" -> optionsForTrust
  )

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

  "GET /authorisation-request/select-service" should {
    "redirect to ASA dashboard when no journey session present" in {
      authoriseAsAgent()
      val result = get(routes.SelectServiceController.show(JourneyType.AuthorisationRequest).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe "http://localhost:9401/agent-services-account/home"
    }
    "redirect to the journey start when no client type present" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(Journey(journeyType = JourneyType.AuthorisationRequest, clientType = None)))
      val result = get(routes.SelectServiceController.show(JourneyType.AuthorisationRequest).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.SelectClientTypeController.show(JourneyType.AuthorisationRequest).url
    }
    allClientTypeAuthJourneys.foreach(j => s"display the select service page for ${j.getClientType} services" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(j))
      val result = get(routes.SelectServiceController.show(JourneyType.AuthorisationRequest).url)
      result.status shouldBe OK
    })
  }

  "POST /authorisation-request/select-service" should {
    allClientTypeAuthJourneys.foreach(j => {
      allNonRefinableOptionsForClientType.get(j.getClientType).map(allOptions =>
        allOptions.foreach(o => s"redirect to the next page after storing answer of ${o} for ${j.getClientType}" in {
          authoriseAsAgent()
          await(journeyService.saveJourney(j))
          val result = post(routes.SelectServiceController.onSubmit(JourneyType.AuthorisationRequest).url)(Map(
            "clientService" -> Seq(o)
          ))
          result.status shouldBe SEE_OTHER
          result.header("Location").value shouldBe routes.EnterClientIdController.show(JourneyType.AuthorisationRequest).url
        }))
    })
    allClientTypeAuthJourneys.foreach(j => {
      allRefinableOptionsForClientType.get(j.getClientType).map(allOptions =>
        allOptions.foreach(o => s"redirect to the next page after storing answer of ${o} for ${j.getClientType}" in {
          authoriseAsAgent()
          await(journeyService.saveJourney(j))
          val result = post(routes.SelectServiceController.onSubmit(JourneyType.AuthorisationRequest).url)(Map(
            "clientService" -> Seq(o)
          ))
          result.status shouldBe SEE_OTHER
          result.header("Location").value shouldBe routes.ServiceRefinementController.show(JourneyType.AuthorisationRequest).url
        }))
    })
  
    "show an error when no selection is made" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(personalAuthorisationRequestJourney))
      val result = post(routes.SelectServiceController.onSubmit(JourneyType.AuthorisationRequest).url)("")
      result.status shouldBe BAD_REQUEST
    }
  }

  "GET /agent-cancel-authorisation/select-service" should {
    "redirect to ASA dashboard when no journey session present" in {
      authoriseAsAgent()
      val result = get(routes.SelectServiceController.show(JourneyType.AgentCancelAuthorisation).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe "http://localhost:9401/agent-services-account/home"
    }
    "redirect to the journey start when no client type present" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(Journey(journeyType = JourneyType.AgentCancelAuthorisation, clientType = None)))
      val result = get(routes.SelectServiceController.show(JourneyType.AgentCancelAuthorisation).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.SelectClientTypeController.show(JourneyType.AgentCancelAuthorisation).url
    }
    allClientTypeDeAuthJourneys.foreach(j => s"display the select service page for ${j.getClientType} services" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(j))
      val result = get(routes.SelectServiceController.show(JourneyType.AgentCancelAuthorisation).url)
      result.status shouldBe OK
    })
  }

  "POST /agent-cancel-authorisation/client-type" should {
    allClientTypeDeAuthJourneys.foreach(j =>
      allNonRefinableOptionsForClientType.get(j.getClientType).map(allOptions =>
        allOptions.foreach(o => s"redirect to the next page after storing answer of ${o} for ${j.getClientType}" in {
          authoriseAsAgent()
          await(journeyService.saveJourney(j))
          val result = post(routes.SelectServiceController.onSubmit(JourneyType.AgentCancelAuthorisation).url)(Map(
            "clientService" -> Seq(o)
          ))
          result.status shouldBe SEE_OTHER
          result.header("Location").value shouldBe routes.EnterClientIdController.show(JourneyType.AgentCancelAuthorisation).url
        })))

    allClientTypeDeAuthJourneys.foreach(j =>
      allRefinableOptionsForClientType.get(j.getClientType).map(allOptions =>
        allOptions.foreach(o => s"redirect to the refinement page after storing answer of ${o} for ${j.getClientType}" in {
          authoriseAsAgent()
          await(journeyService.saveJourney(j))
          val result = post(routes.SelectServiceController.onSubmit(JourneyType.AgentCancelAuthorisation).url)(Map(
            "clientService" -> Seq(o)
          ))
          result.status shouldBe SEE_OTHER
          result.header("Location").value shouldBe routes.ServiceRefinementController.show(JourneyType.AgentCancelAuthorisation).url
        })))
    
    "show an error when no selection is made" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(personalAgentCancelAuthorisationJourney))
      val result = post(routes.SelectServiceController.onSubmit(JourneyType.AgentCancelAuthorisation).url)("")
      result.status shouldBe BAD_REQUEST
    }
  }

}
