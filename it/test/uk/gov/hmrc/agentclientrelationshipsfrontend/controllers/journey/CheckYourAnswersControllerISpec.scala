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
import org.scalatest.concurrent.ScalaFutures
import play.api.test.Helpers.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.{HMRCMTDIT, HMRCMTDITSUPP, HMRCMTDVAT}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.AgentJourney
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.AgentJourneyType.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.JourneyExitType.AuthorisationAlreadyRemoved
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.JourneyExitType.{ClientAlreadyInvited, NoAuthorisationExists}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.{ClientDetailsResponse, KnownFactType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.AgentJourneyService
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.WiremockHelper.stubPost
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AuthStubs, ComponentSpecHelper}

class CheckYourAnswersControllerISpec extends ComponentSpecHelper with ScalaFutures with AuthStubs:

  val testInvitationId: String = "AB1234567890"
  val testCreateAuthorisationRequestResponseJson: JsObject = Json.obj(
    "invitationId" -> testInvitationId
  )

  val exampleClientId: String = "1234567890" // as we are not posting this we don't care about the format
  val exampleKnownFact: String = "AA11AA"

  private val servicesWithSingleAgentRole = Seq("PERSONAL-INCOME-RECORD", "HMRC-MTD-VAT", "HMRC-CGT-PD", "HMRC-PPT-ORG", "HMRC-CBC-ORG", "HMRC-PILLAR2-ORG", "HMRC-TERS-ORG")
  private val servicesWithAgentRoles = Seq(HMRCMTDIT)
  private val existingAgentRoles = Seq(Some(HMRCMTDIT), Some(HMRCMTDITSUPP), None)

  private val basicClientDetails = ClientDetailsResponse("Test Name", None, None, Seq(exampleKnownFact), Some(KnownFactType.PostalCode), false, None)
  private val basicJourney: AgentJourney = AgentJourney(
    journeyType = AuthorisationRequest,
    clientType = Some("personal"),
    clientService = Some(HMRCMTDIT),
    clientId = Some(exampleClientId),
    clientDetailsResponse = Some(basicClientDetails),
    knownFact = Some(exampleKnownFact),
    clientConfirmed = Some(true),
    agentType = None
  )

  def singleAgentRequestJourney(service: String): AgentJourney = basicJourney.copy(
    clientService = Some(service),
    clientDetailsResponse = Some(basicClientDetails.copy(hasExistingRelationshipFor = None)),
    agentType = None
  )

  def agentRoleBasedRequestJourney(service: String, existingRole: Option[String]): AgentJourney = basicJourney.copy(
    clientService = Some(service),
    clientDetailsResponse = Some(basicClientDetails.copy(hasExistingRelationshipFor = existingRole)),
    agentType = if existingRole.contains(HMRCMTDITSUPP) then Some(HMRCMTDIT) else Some(HMRCMTDITSUPP)
  )

  def existingAuthCancellationJourney(existingAuth: String): AgentJourney = basicJourney.copy(
    journeyType = AgentCancelAuthorisation,
    clientService = Some(existingAuth),
    clientDetailsResponse = Some(basicClientDetails.copy(hasExistingRelationshipFor = Some(existingAuth)))
  )

  val exampleNino: String = "AB123456C"
  val exampleUtr: String = "1234567890"
  val exampleVrn: String = "123456789"
  val exampleUrn: String = "XATRUST12345678"
  val exampleCgtRef: String = "XMCGTP123456789"
  val examplePptRef: String = "XMPPT0001234567"
  val exampleCbcId: String = "XMCBC1234567890"
  val examplePlrId: String = "XMPLR1234567890"

  def createAuthorisationRequestUrl = s"/agent-client-relationships/agent/$testArn/authorisation-request"

  def cancelAuthorisationUrl = s"/agent-client-relationships/agent/$testArn/remove-authorisation"

  val journeyService: AgentJourneyService = app.injector.instanceOf[AgentJourneyService]

  override def beforeEach(): Unit = {
    await(journeyService.deleteAllAnswersInSession(request))
    super.beforeEach()
  }

  "GET /authorisation-request/confirm" should:
    "redirect to enter client id when client details are missing" in:
      authoriseAsAgent()
      await(journeyService.saveJourney(AgentJourney(journeyType = AuthorisationRequest, clientType = Some("personal"), clientService = Some(HMRCMTDIT))))
      val result = get(routes.CheckYourAnswersController.show(AuthorisationRequest).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.EnterClientIdController.show(AuthorisationRequest).url

    "redirect to ASA home if journeyComplete is defined" in:
      authoriseAsAgent()
      await(journeyService.saveJourney(AgentJourney(journeyType = AuthorisationRequest, journeyComplete = Some("ABC"))))
      val result = get(routes.CheckYourAnswersController.show(AuthorisationRequest).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe appConfig.agentServicesAccountHomeUrl
      
    "redirect to an exit URL if there is an exit condition" in:
      authoriseAsAgent()
      val insolventClientSession = singleAgentRequestJourney(HMRCMTDVAT).copy(
        clientDetailsResponse = Some(basicClientDetails.copy(hasPendingInvitation = true))
      )
      await(journeyService.saveJourney(insolventClientSession))
      val result = get(routes.CheckYourAnswersController.show(AuthorisationRequest).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.JourneyExitController.show(AuthorisationRequest, ClientAlreadyInvited).url

    servicesWithSingleAgentRole
      .foreach(service => s"display the CYA page on authorisation request for $service journey" in:
        authoriseAsAgent()
        await(journeyService.saveJourney(singleAgentRequestJourney(service)))
        val result = get(routes.CheckYourAnswersController.show(AuthorisationRequest).url)
        result.status shouldBe OK
      )
    servicesWithAgentRoles
      .foreach(service => existingAgentRoles
        .foreach(role => s"display the CYA page on authorisation request for $service journey when existing role is ${role.getOrElse("none")}" in:
        authoriseAsAgent()
        await(journeyService.saveJourney(agentRoleBasedRequestJourney(service, role)))
        val result = get(routes.CheckYourAnswersController.show(AuthorisationRequest).url)
        result.status shouldBe OK
      ))

  "GET /agent-cancel-authorisation/confirm" should:

    "redirect to an exit URL if there is an exit condition" in:
      authoriseAsAgent()
      val insolventClientSession = existingAuthCancellationJourney(HMRCMTDVAT).copy(
        clientDetailsResponse = Some(basicClientDetails.copy(hasExistingRelationshipFor = None))
      )
      await(journeyService.saveJourney(insolventClientSession))
      val result = get(routes.CheckYourAnswersController.show(AgentCancelAuthorisation).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.JourneyExitController.show(AgentCancelAuthorisation, NoAuthorisationExists).url

    servicesWithSingleAgentRole
      .foreach(service => s"display the confirm cancellation page for $service journey" in:
        authoriseAsAgent()
        await(journeyService.saveJourney(existingAuthCancellationJourney(service)))
        val result = get(routes.CheckYourAnswersController.show(AgentCancelAuthorisation).url)
        result.status shouldBe OK
      )
    servicesWithAgentRoles
      .foreach(service => existingAgentRoles.collect({ case Some(role) => role }) // we don't test for None as it should not be possible
        .foreach(role => s"display the confirm cancellation page for $service journey when existing role is $role" in:
        authoriseAsAgent()
        await(journeyService.saveJourney(existingAuthCancellationJourney(service)))
        val result = get(routes.CheckYourAnswersController.show(AgentCancelAuthorisation).url)
        result.status shouldBe OK
      ))

  "POST /authorisation-request/confirm" should:
    servicesWithSingleAgentRole.foreach(service => s"redirect to authorisation request created for $service page when confirmed" in:
      authoriseAsAgent()
      await(journeyService.saveJourney(singleAgentRequestJourney(service)))
      stubPost(createAuthorisationRequestUrl, CREATED, testCreateAuthorisationRequestResponseJson.toString)
      val result = post(routes.CheckYourAnswersController.onSubmit(AuthorisationRequest).url)(Map(
        "confirmed" -> Seq("true")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.ConfirmationController.show(AuthorisationRequest).url
    )

    servicesWithAgentRoles.foreach(service => existingAgentRoles.collect({ case Some(role) if role !== service => role }) // we don't test for None as it should not be possible
      .foreach(role => s"redirect to authorisation request created for $service page when confirmed with existing role as $role" in:
        authoriseAsAgent()
        stubPost(createAuthorisationRequestUrl, CREATED, testCreateAuthorisationRequestResponseJson.toString)
        await(journeyService.saveJourney(agentRoleBasedRequestJourney(service, Some(role))))
        val result = post(routes.CheckYourAnswersController.onSubmit(AuthorisationRequest).url)(Map(
          "confirmed" -> Seq("true")
        ))
        result.status shouldBe SEE_OTHER
        result.header("Location").value shouldBe routes.ConfirmationController.show(AuthorisationRequest).url
      ))

    "redirect to authorisation request created when journeyComplete is set" in:
      authoriseAsAgent()
      journeyService.saveJourney(
        journey = AgentJourney(
          journeyType = AuthorisationRequest,
          journeyComplete = Some(testInvitationId)
        )
      ).futureValue
      val result = post(routes.CheckYourAnswersController.onSubmit(AuthorisationRequest).url)(Map(
        "confirmed" -> Seq("true")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.ConfirmationController.show(AuthorisationRequest).url

    "redirect to enter client id when client details are missing" in:
      authoriseAsAgent()
      journeyService.saveJourney(
        journey = AgentJourney(
          journeyType = AuthorisationRequest,
          clientType = Some("personal"),
          clientService = Some(HMRCMTDIT)
        )
      ).futureValue
      val result = post(routes.CheckYourAnswersController.onSubmit(AuthorisationRequest).url)(Map(
        "confirmed" -> Seq("true")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.EnterClientIdController.show(AuthorisationRequest).url

    "redirect to an exit URL if there is an exit condition" in:
      authoriseAsAgent()
      val insolventClientSession = singleAgentRequestJourney(HMRCMTDVAT).copy(
        clientDetailsResponse = Some(basicClientDetails.copy(hasPendingInvitation = true))
      )
      await(journeyService.saveJourney(insolventClientSession))
      val result = get(routes.CheckYourAnswersController.onSubmit(AuthorisationRequest).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.JourneyExitController.show(AuthorisationRequest, ClientAlreadyInvited).url

  "POST /agent-cancel-authorisation/confirm" should:
    servicesWithSingleAgentRole.foreach(service => s"redirect to authorisation cancelled for $service page when confirmed" in:
      authoriseAsAgent()
      stubPost(cancelAuthorisationUrl, NO_CONTENT, "")
      await(journeyService.saveJourney(existingAuthCancellationJourney(service)))
      val result = post(routes.CheckYourAnswersController.onSubmit(AgentCancelAuthorisation).url)(Map(
        "confirmCancellation" -> Seq("true")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.ConfirmationController.show(AgentCancelAuthorisation).url
    )

    existingAgentRoles.flatten.foreach(service => s"redirect to authorisation cancelled for $service page when confirmed" in:
      authoriseAsAgent()
      stubPost(cancelAuthorisationUrl, NO_CONTENT, "")
      await(journeyService.saveJourney(existingAuthCancellationJourney(service)))
      val result = post(routes.CheckYourAnswersController.onSubmit(AgentCancelAuthorisation).url)(Map(
        "confirmCancellation" -> Seq("true")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.ConfirmationController.show(AgentCancelAuthorisation).url
    )

    "redirect to authorisation cancelled when journeyComplete is set" in:
      authoriseAsAgent()
      journeyService.saveJourney(
        journey = AgentJourney(
          journeyType = AgentCancelAuthorisation,
          journeyComplete = Some(testInvitationId)
        )
      ).futureValue
      val result = post(routes.CheckYourAnswersController.onSubmit(AgentCancelAuthorisation).url)(Map(
        "confirmCancellation" -> Seq("true")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.ConfirmationController.show(AgentCancelAuthorisation).url

    "redirect to processing your request when confirmed but backend responds with LOCKED" in:
      authoriseAsAgent()
      stubPost(cancelAuthorisationUrl, LOCKED, "")
      await(journeyService.saveJourney(existingAuthCancellationJourney(HMRCMTDIT)))
      val result = post(routes.CheckYourAnswersController.onSubmit(AgentCancelAuthorisation).url)(Map(
        "confirmCancellation" -> Seq("true")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.CheckYourAnswersController.processingYourRequest(AgentCancelAuthorisation).url

    "redirect to start again when selecting not to confirm cancellation" in:
      authoriseAsAgent()
      await(journeyService.saveJourney(existingAuthCancellationJourney(HMRCMTDIT)))
      val result = post(routes.CheckYourAnswersController.onSubmit(AgentCancelAuthorisation).url)(Map(
        "confirmCancellation" -> Seq("false")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.StartJourneyController.startJourney(AgentCancelAuthorisation).url

    "show an error when no selection to confirm cancellation is made" in:
      authoriseAsAgent()
      await(journeyService.saveJourney(existingAuthCancellationJourney(HMRCMTDIT)))
      val result = post(routes.CheckYourAnswersController.onSubmit(AgentCancelAuthorisation).url)("")
      result.status shouldBe BAD_REQUEST

    "redirect to enter client id when client details are missing" in:
      authoriseAsAgent()
      journeyService.saveJourney(
        journey = AgentJourney(
          journeyType = AgentCancelAuthorisation,
          clientType = Some("personal"),
          clientService = Some(HMRCMTDIT)
        )
      ).futureValue
      val result = post(routes.CheckYourAnswersController.onSubmit(AgentCancelAuthorisation).url)(Map(
        "confirmed" -> Seq("true")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.EnterClientIdController.show(AgentCancelAuthorisation).url

    "redirect to exit page for no relationship found when relationship removed since loading form" in:
      authoriseAsAgent()
      stubPost(cancelAuthorisationUrl, NOT_FOUND, "")
      await(journeyService.saveJourney(existingAuthCancellationJourney(HMRCMTDIT)))
      val result = post(routes.CheckYourAnswersController.onSubmit(AgentCancelAuthorisation).url)(Map(
        "confirmCancellation" -> Seq("true")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.JourneyExitController.show(AgentCancelAuthorisation, AuthorisationAlreadyRemoved).url

    "redirect to exit page for no relationship exists when client details does not contain relationship" in:
      authoriseAsAgent()
      val insolventClientSession = existingAuthCancellationJourney(HMRCMTDVAT).copy(
        clientDetailsResponse = Some(basicClientDetails.copy(hasExistingRelationshipFor = None))
      )
      await(journeyService.saveJourney(insolventClientSession))
      val result = get(routes.CheckYourAnswersController.onSubmit(AgentCancelAuthorisation).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.JourneyExitController.show(AgentCancelAuthorisation, NoAuthorisationExists).url

  "GET /agent-cancel-authorisation/processing-your-request" should:
    "redirect the user to the confirmation controller" when:
      "journey complete flag is found in the cache" in:
        authoriseAsAgent()
        journeyService.saveJourney(
          journey = AgentJourney(
            journeyType = AgentCancelAuthorisation,
            journeyComplete = Some(testInvitationId)
          )
        ).futureValue
        val result = get(routes.CheckYourAnswersController.processingYourRequest(AgentCancelAuthorisation).url)
        result.status shouldBe SEE_OTHER
        result.header("Location").value shouldBe routes.ConfirmationController.show(AgentCancelAuthorisation).url

    "show tech difficulties" when:
      "backend response error flag is found in the cache" in:
        authoriseAsAgent()
        journeyService.saveJourney(
          journey = AgentJourney(
            journeyType = AgentCancelAuthorisation,
            backendErrorResponse = Some(true)
          )
        ).futureValue
        val result = get(routes.CheckYourAnswersController.processingYourRequest(AgentCancelAuthorisation).url)
        result.status shouldBe INTERNAL_SERVER_ERROR

    "stay on processing your request" when:
      "a valid session for CYA is found in the cache" in:
        authoriseAsAgent()
        await(journeyService.saveJourney(existingAuthCancellationJourney(HMRCMTDIT)))
        val result = get(routes.CheckYourAnswersController.processingYourRequest(AgentCancelAuthorisation).url)
        result.status shouldBe OK

    "redirect to enter client id" when:
      "the journey is missing data" in:
        authoriseAsAgent()
        journeyService.saveJourney(
          journey = AgentJourney(
            journeyType = AgentCancelAuthorisation
          )
        ).futureValue
        val result = get(routes.CheckYourAnswersController.processingYourRequest(AgentCancelAuthorisation).url)
        result.status shouldBe SEE_OTHER
        result.header("Location").value shouldBe routes.EnterClientIdController.show(AgentCancelAuthorisation).url