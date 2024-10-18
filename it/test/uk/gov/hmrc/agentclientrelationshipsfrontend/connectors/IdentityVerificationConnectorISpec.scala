package uk.gov.hmrc.agentclientrelationshipsfrontend.connectors

import play.api.libs.json.Json
import play.api.test.Helpers.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.ComponentSpecHelper
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.WiremockHelper.stubGet
import uk.gov.hmrc.http.HeaderCarrier

class IdentityVerificationConnectorISpec extends ComponentSpecHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  val connector: IdentityVerificationConnector = app.injector.instanceOf[IdentityVerificationConnector]

  val testJourneyId = "123"

  def ivUrl(id: String) = s"/mdtp/journey/journeyId/$id"

  "getIVResult" should {
    Seq(Success, Incomplete, PreconditionFailed, LockedOut, InsufficientEvidence, FailedMatching,
      TechnicalIssue, UserAborted, TimedOut, FailedIv, FailedDirectorCheck).foreach { ivResult =>
      s"return the $ivResult reason when the journey id is valid" in {
        stubGet(ivUrl(testJourneyId), OK, Json.obj("result" -> Json.toJson[IvResult](ivResult)).toString)
        val result = connector.getIVResult(testJourneyId)
        await(result) shouldBe Some(ivResult)
      }
    }
    "return nothing when the journey id is not valid" in {
      stubGet(ivUrl(testJourneyId), NOT_FOUND, "")
      val result = connector.getIVResult(testJourneyId)
      await(result) shouldBe None
    }
  }
}
