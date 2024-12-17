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

package uk.gov.hmrc.agentclientrelationshipsfrontend.views.journey

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.journey.JourneyExitPage

class AgentJourneyExitPageSpec extends ViewSpecSupport {

  val viewTemplate: JourneyExitPage = app.injector.instanceOf[JourneyExitPage]

  private val authorisationRequestJourney: AgentJourney = AgentJourney(AgentJourneyType.AuthorisationRequest)
  private val agentCancelAuthorisationJourney: AgentJourney = AgentJourney(AgentJourneyType.AgentCancelAuthorisation)

  case class ExpectedStrings(authorisationTitle: String, cancelAuthorisationTitle: String)
  private val supportedErrorCodes: Map[JourneyExitType, ExpectedStrings] = Map(
    JourneyExitType.NotFound -> ExpectedStrings(
      authorisationTitle = "We could not find your client - Ask a client to authorise you - GOV.UK",
      cancelAuthorisationTitle = "We could not find your client - Cancel a client’s authorisation - GOV.UK"
    ),
    JourneyExitType.NotRegistered ->  ExpectedStrings(
      authorisationTitle = "Your client needs to register for Self Assessment - Ask a client to authorise you - GOV.UK",
      cancelAuthorisationTitle = "Your client needs to register for Self Assessment - Cancel a client’s authorisation - GOV.UK"
    )
  )

  List(authorisationRequestJourney, agentCancelAuthorisationJourney).foreach(j =>
    supportedErrorCodes.map(errorCode => s"JourneyErrorPage for ${errorCode._1} ${j.journeyType.toString} view" should {
      implicit val journeyRequest: AgentJourneyRequest[?] = new AgentJourneyRequest("", j, request)
      val expectedStrings = errorCode._2
      val title = if (j.journeyType == AgentJourneyType.AuthorisationRequest) expectedStrings.authorisationTitle else expectedStrings.cancelAuthorisationTitle
      val view: HtmlFormat.Appendable = viewTemplate(j.journeyType, errorCode._1)
      val doc: Document = Jsoup.parse(view.body)
      "have the right title" in {
        doc.title() shouldBe title
      }
      "have a language switcher" in {
        doc.hasLanguageSwitch shouldBe true
      }
    }))
}
