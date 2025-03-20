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

package uk.gov.hmrc.agentclientrelationshipsfrontend.services

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.Reads
import play.api.mvc.{AnyContentAsEmpty, RequestHeader}
import play.api.test.FakeRequest
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.{Authorisation, AuthorisationsCache}
import uk.gov.hmrc.agentclientrelationshipsfrontend.repositories.AuthorisationsCacheRepository
import uk.gov.hmrc.mongo.cache.DataKey

import java.time.LocalDate
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuthorisationsCacheServiceSpec extends AnyWordSpecLike with Matchers with MockitoSugar :

  val mockSessionRepo: AuthorisationsCacheRepository = mock[AuthorisationsCacheRepository]
  val service = new AuthorisationsCacheService(mockSessionRepo)

  ".getAuthorisation" should :

    "return an authorisation when one is found in the session cache repository" in :
      val authorisation = Authorisation(
        "testUid",
        "HMRC-MTD-IT",
        "testNino",
        LocalDate.now(),
        "testArn",
        "testAgentName"
      )
      when(mockSessionRepo.getFromSession(any[DataKey[AuthorisationsCache]]())(using any[Reads[AuthorisationsCache]](), any[RequestHeader]()))
        .thenReturn(Future.successful(Some(AuthorisationsCache(Seq(authorisation)))))
      implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

      await(service.getAuthorisation("testUid")) shouldBe Some(authorisation)

    "return None when an authorisation could not be found in the session cache repository" in :
      when(mockSessionRepo.getFromSession(any[DataKey[AuthorisationsCache]]())(using any[Reads[AuthorisationsCache]](), any[RequestHeader]()))
        .thenReturn(Future.successful(None))
      implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

      await(service.getAuthorisation("testUid")) shouldBe None
