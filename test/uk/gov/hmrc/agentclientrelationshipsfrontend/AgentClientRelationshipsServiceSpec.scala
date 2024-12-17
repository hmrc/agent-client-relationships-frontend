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

package uk.gov.hmrc.agentclientrelationshipsfrontend

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.agentclientrelationshipsfrontend.connectors.AgentClientRelationshipsConnector
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{AgentJourney, AgentJourneyType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.{ClientDetailsResponse, KnownFactType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.AgentClientRelationshipsService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

class AgentClientRelationshipsServiceSpec extends AnyWordSpecLike with Matchers with BeforeAndAfterEach {

  val mockAgentClientRelationshipsConnector: AgentClientRelationshipsConnector = mock[AgentClientRelationshipsConnector]
  implicit val executionContext: ExecutionContextExecutor = ExecutionContext.global
  implicit val hc: HeaderCarrier = HeaderCarrier()

  override def beforeEach(): Unit = {
    reset(mockAgentClientRelationshipsConnector)
    super.beforeEach()
  }

  object TestService extends AgentClientRelationshipsService(
    mockAgentClientRelationshipsConnector
  )

  val testClientDetails: ClientDetailsResponse = ClientDetailsResponse(
    "Name",
    None,
    None,
    Seq("testId"),
    Some(KnownFactType.PostalCode),
    false,
    None
  )
  val testJourney: AgentJourney = AgentJourney(
    AgentJourneyType.AuthorisationRequest,
    Some("testType"),
    Some("testService")
  )

  "getClientDetailsResponse" should {
    "return the connector response" in {
      when(mockAgentClientRelationshipsConnector.getClientDetails(any(), any())(any()))
        .thenReturn(Future.successful(Some(testClientDetails)))

      await(TestService.getClientDetails("testId", testJourney)) shouldBe Some(testClientDetails)
    }
  }
}
