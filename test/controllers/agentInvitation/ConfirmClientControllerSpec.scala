package uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.agentInvitation

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.inject.guice.GuiceApplicationBuilder

class ConfirmClientControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {
  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .build()

  private val fakeRequest = FakeRequest("GET", "/")

  private val fakePost = FakeRequest("POST", "/")

  private val controller = app.injector.instanceOf[ConfirmClientController]

  "GET /agents/confirm-client" should {
    "return 200" in {
      val result = controller.show(fakeRequest)
      status(result) shouldBe Status.OK
    }
  }

  "POST /agents/confirm-client" should {
    "return 200" in {
      val result = controller.submit(fakePost)
      status(result) shouldBe Status.OK
    }
  }
}