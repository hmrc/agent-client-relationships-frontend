package controllers.agentInvitation

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.agentInvitation.{ConfirmPostcodeController, StartController}

class StartControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {
  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .build()

  private val fakeRequest = FakeRequest("GET", "/")

  private val controller = app.injector.instanceOf[StartController]

  "GET /agents/" should {
    "return 200" in {
      val result = controller.start(fakeRequest)
      status(result) shouldBe Status.OK
    }
  }
}