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

package uk.gov.hmrc.agentclientrelationshipsfrontend.models

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.{HMRCMTDIT, HMRCMTDITSUPP, HMRCMTDVAT}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.ClientStatus.{Inactive, Insolvent}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.AgentJourney
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.AgentJourneyType.{AgentCancelAuthorisation, AuthorisationRequest}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.JourneyExitType.*

class AgentJourneySpec extends AnyWordSpecLike with Matchers :

  ".getExitType" when :

    "journey type is AuthorisationRequest" should :

      val model = AgentJourney(AuthorisationRequest, clientService = Some(HMRCMTDIT))

      "return ClientStatusInsolvent when the client's status is Insolvent" in :
        val clientDetails = ClientDetailsResponse("Client", Some(Insolvent), None, Seq(), None, false, None)
        model.getExitType(AuthorisationRequest, clientDetails) shouldBe Some(ClientStatusInsolvent)

      "return ClientStatusInvalid when the client's status is defined and not recognised" in :
        val clientDetails = ClientDetailsResponse("Client", Some(Inactive), None, Seq(), None, false, None)
        model.getExitType(AuthorisationRequest, clientDetails) shouldBe Some(ClientStatusInvalid)

      "return ClientAlreadyInvited when the client already has a pending invitation" in :
        val clientDetails = ClientDetailsResponse("Client", None, None, Seq(), None, true, None)
        model.getExitType(AuthorisationRequest, clientDetails) shouldBe Some(ClientAlreadyInvited)

      "return AuthorisationAlreadyExists when the client has a relationship for the same service, and agent roles are not provided" in :
        val clientDetails = ClientDetailsResponse("Client", None, None, Seq(), None, false, Some(HMRCMTDIT))
        model.getExitType(AuthorisationRequest, clientDetails) shouldBe Some(AuthorisationAlreadyExists)

      "return None when the client has a relationship for the same service, and agent roles are provided" in :
        val clientDetails = ClientDetailsResponse("Client", None, None, Seq(), None, false, Some(HMRCMTDIT))
        model.getExitType(AuthorisationRequest, clientDetails, Seq(HMRCMTDIT, HMRCMTDITSUPP)) shouldBe None

      "return None when the client has none of the above exit conditions" in :
        val clientDetails = ClientDetailsResponse("Client", None, None, Seq(), None, false, None)
        model.getExitType(AuthorisationRequest, clientDetails) shouldBe None

      "return ClientAlreadyMapped when the agent already has a mapping for this client and agent wants to be 'main'" in :
        val model = AgentJourney(AuthorisationRequest, clientService = Some(HMRCMTDIT), agentType = Some(HMRCMTDIT))
        val clientDetails = ClientDetailsResponse("Client", None, None, Seq(), None, false, None, Some(true), Some(Seq("A12345")))
        model.getExitType(AuthorisationRequest, clientDetails) shouldBe Some(ClientAlreadyMapped)

      "return ClientAlreadyMapped when the agent already has a mapping for this client and agent wants to be 'supporting'" in :
        val model = AgentJourney(AuthorisationRequest, clientService = Some(HMRCMTDIT), agentType = Some(HMRCMTDITSUPP))
        val clientDetails = ClientDetailsResponse("Client", None, None, Seq(), None, false, None, Some(true), Some(Seq("A12345")))
        model.getExitType(AuthorisationRequest, clientDetails) shouldBe None

      "return None when the agent does not have a mapping for this client" in :
        val clientDetails = ClientDetailsResponse("Client", None, None, Seq(), None, false, None, Some(false), Some(Seq("A12345")))
        model.getExitType(AuthorisationRequest, clientDetails) shouldBe None

    "journey type is AgentCancelAuthorisation" should :

      val model = AgentJourney(AgentCancelAuthorisation, clientService = Some(HMRCMTDIT))

      "return NoAuthorisationExists when the client does not have a relationship for any service" in :
        val clientDetails = ClientDetailsResponse("Client", None, None, Seq(), None, false, None)
        model.getExitType(AgentCancelAuthorisation, clientDetails) shouldBe Some(NoAuthorisationExists)

      "return NoAuthorisationExists when the client has a relationship but not for the requested service" in :
        val clientDetails = ClientDetailsResponse("Client", None, None, Seq(), None, false, Some(HMRCMTDVAT))
        model.getExitType(AgentCancelAuthorisation, clientDetails) shouldBe Some(NoAuthorisationExists)

      "return None when the client has a relationship for the same service" in :
        val clientDetails = ClientDetailsResponse("Client", None, None, Seq(), None, false, Some(HMRCMTDIT))
        model.getExitType(AgentCancelAuthorisation, clientDetails) shouldBe None

      "return None when the client service is one of the supported agent roles" in :
        val clientDetails = ClientDetailsResponse("Client", None, None, Seq(), None, false, Some(HMRCMTDITSUPP))
        model.getExitType(AgentCancelAuthorisation, clientDetails, Seq(HMRCMTDIT, HMRCMTDITSUPP)) shouldBe None