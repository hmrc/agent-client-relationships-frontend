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

package uk.gov.hmrc.agentclientrelationshipsfrontend.views.client

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.ClientExitType.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.JourneyType.ClientResponse
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{ClientJourney, ClientJourneyRequest}
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.client.{ClientExitPage, UnauthorisedExitPage}

import java.time.{Instant, LocalDateTime, ZoneOffset}

class UnauthorisedExitPageSpec extends ViewSpecSupport {
  val testStatus: InvitationStatus = Accepted
  val testLastModifiedDate: Instant = LocalDateTime.of(2024, 1, 10, 0, 0).toInstant(ZoneOffset.UTC)
  val testAgentName = "Agent Name"

  val viewTemplate: UnauthorisedExitPage = app.injector.instanceOf[UnauthorisedExitPage]

  case class ExpectedStrings(title: String, paragraphs: List[String])

  object Expected {
    val agentSuspendedLabel = "You cannot appoint this tax agent - Appoint someone to deal with HMRC for you - GOV.UK"
    val agentSuspendedParagraphOne = "This tax agent cannot manage your Making Tax Digital for Income Tax at this time."
    val agentSuspendedParagraphTwo = "If you have any questions, contact the tax agent who sent you this request."

    val noOutstandingRequestsLabel = "There are no outstanding authorisation requests for you to respond to - Appoint someone to deal with HMRC for you - GOV.UK"
    val noOutstandingRequestsParagraphOne = "If you think this is wrong, contact the agent who sent you the request or view your request history."
  }

  "Exit Page for AgentSuspended view" should {
    val view: HtmlFormat.Appendable = viewTemplate(AgentSuspended)
    val doc: Document = Jsoup.parse(view.body)

    "have the right title" in {
      doc.title() shouldBe Expected.agentSuspendedLabel
    }

    "display paragraph content" in {
      doc.select(".govuk-body").get(0).text() shouldBe Expected.agentSuspendedParagraphOne
      doc.select(".govuk-body").get(1).text() shouldBe Expected.agentSuspendedParagraphTwo
    }

    "have a language switcher" in {
      doc.hasLanguageSwitch shouldBe true
    }

    "have the correct link in the details component content" in {

      doc.mainContent.extractLink(1).value shouldBe TestLink("Finish and sign out", "/agent-client-relationships/sign-out")
    }
  }

  "Exit Page for NoOutstandingRequests view" should {
    val view: HtmlFormat.Appendable = viewTemplate(NoOutstandingRequests)
    val doc: Document = Jsoup.parse(view.body)

    "have the right title" in {
      doc.title() shouldBe Expected.noOutstandingRequestsLabel
    }

    "display paragraph content" in {
      doc.select(".govuk-body").get(0).text() shouldBe Expected.noOutstandingRequestsParagraphOne
    }

    "have a language switcher" in {
      doc.hasLanguageSwitch shouldBe true
    }

    "have the correct link in the details component content" in {

      doc.mainContent.extractLink(1).value shouldBe TestLink("view your request history", "http://localhost:9568#history")
    }
  }
}