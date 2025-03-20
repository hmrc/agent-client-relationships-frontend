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
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{AgentJourney, AgentJourneyType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.{ClientDetailsResponse, KnownFactType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.AgentJourneyService
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.WiremockHelper.stubPost
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AuthStubs, ComponentSpecHelper}

class CheckYourAnswersControllerISpec extends ComponentSpecHelper with AuthStubs {

  val testInvitationId: String = "AB1234567890"
  val testCreateAuthorisationRequestResponseJson: JsObject = Json.obj(
    "invitationId" -> testInvitationId
  )

  val exampleClientId: String = "1234567890" // as we are not posting this we don't care about the format
  val exampleKnownFact: String = "AA11AA"

  private val servicesWithSingleAgentRole = Seq("PERSONAL-INCOME-RECORD", "HMRC-MTD-VAT", "HMRC-CGT-PD", "HMRC-PPT-ORG", "HMRC-CBC-ORG", "HMRC-PILLAR2-ORG", "HMRC-TERS-ORG")
  private val servicesWithAgentRoles = Seq("HMRC-MTD-IT")
  private val existingAgentRoles = Seq(Some("HMRC-MTD-IT"), Some("HMRC-MTD-IT-SUPP"), None)

  private val basicClientDetails = ClientDetailsResponse("Test Name", None, None, Seq(exampleKnownFact), Some(KnownFactType.PostalCode), false, None)
  private val basicJourney: AgentJourney = AgentJourney(
    journeyType = AgentJourneyType.AuthorisationRequest,
    clientType = Some("personal"),
    clientService = Some("HMRC-MTD-IT"),
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
    agentType = if existingRole.contains("HMRC-MTD-IT-SUPP") then Some("HMRC-MTD-IT") else Some("HMRC-MTD-IT-SUPP")
  )

  def existingAuthCancellationJourney(existingAuth: String): AgentJourney = basicJourney.copy(
    journeyType = AgentJourneyType.AgentCancelAuthorisation,
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

  "GET /authorisation-request/confirm" should :
    "redirect to enter client id when client details are missing" in :
      authoriseAsAgent()
      await(journeyService.saveJourney(AgentJourney(journeyType = AgentJourneyType.AuthorisationRequest, clientType = Some("personal"), clientService = Some("HMRC-MTD-IT"))))
      val result = get(routes.CheckYourAnswersController.show(AgentJourneyType.AuthorisationRequest).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.EnterClientIdController.show(AgentJourneyType.AuthorisationRequest).url

    "redirect to ASA home if journeyComplete is defined" in :
      authoriseAsAgent()
      await(journeyService.saveJourney(AgentJourney(journeyType = AgentJourneyType.AuthorisationRequest, journeyComplete = Some("ABC"))))
      val result = get(routes.CheckYourAnswersController.show(AgentJourneyType.AuthorisationRequest).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe appConfig.agentServicesAccountHomeUrl

    servicesWithSingleAgentRole
      .foreach(service => s"display the CYA page on authorisation request for $service journey" in :
        authoriseAsAgent()
        await(journeyService.saveJourney(singleAgentRequestJourney(service)))
        val result = get(routes.CheckYourAnswersController.show(AgentJourneyType.AuthorisationRequest).url)
        result.status shouldBe OK
      )
    servicesWithAgentRoles
      .foreach(service => existingAgentRoles
        .foreach(role => s"display the CYA page on authorisation request for $service journey when existing role is ${role.getOrElse("none")}" in :
        authoriseAsAgent()
        await(journeyService.saveJourney(agentRoleBasedRequestJourney(service, role)))
        val result = get(routes.CheckYourAnswersController.show(AgentJourneyType.AuthorisationRequest).url)
        result.status shouldBe OK
      ))
    servicesWithSingleAgentRole
      .foreach(service => s"display the confirm cancellation page for $service journey" in :
        authoriseAsAgent()
        await(journeyService.saveJourney(existingAuthCancellationJourney(service)))
        val result = get(routes.CheckYourAnswersController.show(AgentJourneyType.AgentCancelAuthorisation).url)
        result.status shouldBe OK
      )
    servicesWithAgentRoles
      .foreach(service => existingAgentRoles.collect({ case Some(role) => role }) // we don't test for None as it should not be possible
        .foreach(role => s"display the confirm cancellation page for $service journey when existing role is $role" in :
        authoriseAsAgent()
        await(journeyService.saveJourney(existingAuthCancellationJourney(service)))
        val result = get(routes.CheckYourAnswersController.show(AgentJourneyType.AgentCancelAuthorisation).url)
        result.status shouldBe OK
      ))

  "POST /authorisation-request/confirm" should {
    servicesWithSingleAgentRole.foreach(service => s"redirect to authorisation request created for $service page when confirmed" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(singleAgentRequestJourney(service)))
      stubPost(createAuthorisationRequestUrl, CREATED, testCreateAuthorisationRequestResponseJson.toString)
      val result = post(routes.CheckYourAnswersController.onSubmit(AgentJourneyType.AuthorisationRequest).url)(Map(
        "confirmed" -> Seq("true")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.ConfirmationController.show(AgentJourneyType.AuthorisationRequest).url
    })
    servicesWithAgentRoles.foreach(service => existingAgentRoles.collect({ case Some(role) if role !== service => role }) // we don't test for None as it should not be possible
      .foreach(role => s"redirect to authorisation request created for $service page when confirmed with existing role as $role" in {
        authoriseAsAgent()
        stubPost(createAuthorisationRequestUrl, CREATED, testCreateAuthorisationRequestResponseJson.toString)
        await(journeyService.saveJourney(agentRoleBasedRequestJourney(service, Some(role))))
        val result = post(routes.CheckYourAnswersController.onSubmit(AgentJourneyType.AuthorisationRequest).url)(Map(
          "confirmed" -> Seq("true")
        ))
        result.status shouldBe SEE_OTHER
        result.header("Location").value shouldBe routes.ConfirmationController.show(AgentJourneyType.AuthorisationRequest).url
      }))
  }
  "POST /agent-cancel-authorisation/confirm" should {
    servicesWithSingleAgentRole.foreach(service => s"redirect to authorisation cancelled for $service page when confirmed" in {
      authoriseAsAgent()
      stubPost(cancelAuthorisationUrl, NO_CONTENT, "")
      await(journeyService.saveJourney(existingAuthCancellationJourney(service)))
      val result = post(routes.CheckYourAnswersController.onSubmit(AgentJourneyType.AgentCancelAuthorisation).url)(Map(
        "confirmCancellation" -> Seq("true")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.ConfirmationController.show(AgentJourneyType.AgentCancelAuthorisation).url
    })
    existingAgentRoles.flatten.foreach(service => s"redirect to authorisation cancelled for $service page when confirmed" in {
      authoriseAsAgent()
      stubPost(cancelAuthorisationUrl, NO_CONTENT, "")
      await(journeyService.saveJourney(existingAuthCancellationJourney(service)))
      val result = post(routes.CheckYourAnswersController.onSubmit(AgentJourneyType.AgentCancelAuthorisation).url)(Map(
        "confirmCancellation" -> Seq("true")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.ConfirmationController.show(AgentJourneyType.AgentCancelAuthorisation).url
    })

    "redirect to start again when selecting not to confirm cancellation" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(existingAuthCancellationJourney("HMRC-MTD-IT")))
      val result = post(routes.CheckYourAnswersController.onSubmit(AgentJourneyType.AgentCancelAuthorisation).url)(Map(
        "confirmCancellation" -> Seq("false")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.StartJourneyController.startJourney(AgentJourneyType.AgentCancelAuthorisation).url
    }

    "show an error when no selection to confirm cancellation is made" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(existingAuthCancellationJourney("HMRC-MTD-IT")))
      val result = post(routes.CheckYourAnswersController.onSubmit(AgentJourneyType.AgentCancelAuthorisation).url)("")
      result.status shouldBe BAD_REQUEST
    }
  }

}
