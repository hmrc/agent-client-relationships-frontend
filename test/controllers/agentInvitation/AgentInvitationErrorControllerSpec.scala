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

package uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.agentInvitation

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.inject.guice.GuiceApplicationBuilder

class AgentInvitationErrorControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {
  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .build()

  private val fakeRequest = FakeRequest("GET", "/")

  private val fakePost = FakeRequest("POST", "/")

  private val controller = app.injector.instanceOf[AgentInvitationErrorController]

  "GET /agents/not-signed-up" should {
    "return 200" in {
      val result = controller.showClientNotSignedUp(fakeRequest)
      status(result) shouldBe Status.OK
    }
  }

  "GET /agents/client-not-registered" should {
    "return 200" in {
      val result = controller.showClientNotRegistered(fakeRequest)
      status(result) shouldBe Status.OK
    }
  }

  "GET /agents/not-matched" should {
    "return 200" in {
      val result = controller.showNotMatched(fakeRequest)
      status(result) shouldBe Status.OK
    }
  }

  "GET /agents/cannot-create-request" should {
    "return 200" in {
      val result = controller.showCannotCreateRequest(fakeRequest)
      status(result) shouldBe Status.OK
    }
  }

  "GET /agents/all-authorisations-removed" should {
    "return 200" in {
      val result = controller.showAllAuthorisationsRemoved(fakeRequest)
      status(result) shouldBe Status.OK
    }
  }

  "GET /agents/already-authorisation-pending" should {
    "return 200" in {
      val result = controller.showPendingAuthorisationExists(fakeRequest)
      status(result) shouldBe Status.OK
    }
  }

  "GET /agents/already-authorisation-present" should {
    "return 200" in {
      val result = controller.showActiveAuthorisationExists(fakeRequest)
      status(result) shouldBe Status.OK
    }
  }

  "GET /agents/all-create-authorisation-failed" should {
    "return 200" in {
      val result = controller.showAllAuthorisationsFailed(fakeRequest)
      status(result) shouldBe Status.OK
    }
  }

  "GET /agents/some-create-authorisation-failed" should {
    "return 200" in {
      val result = controller.showSomeAuthorisationsFailed(fakeRequest)
      status(result) shouldBe Status.OK
    }
  }

  "POST /agents/some-create-authorisation-failed" should {
    "return 200" in {
      val result = controller.submitSomeAuthorisationsFailed(fakePost)
      status(result) shouldBe Status.OK
    }
  }

  "GET /agents/access-removed" should {
    "return 200" in {
      val result = controller.showAgentSuspended(fakeRequest)
      status(result) shouldBe Status.OK
    }
  }

  "GET /agents/already-copied-across-itsa" should {
    "return 200" in {
      val result = controller.showAlreadyCopiedAcrossItsa(fakeRequest)
      status(result) shouldBe Status.OK
    }
  }

  "GET /agents/client-insolvent" should {
    "return 200" in {
      val result = controller.showClientInsolvent(fakeRequest)
      status(result) shouldBe Status.OK
    }
  }

}
