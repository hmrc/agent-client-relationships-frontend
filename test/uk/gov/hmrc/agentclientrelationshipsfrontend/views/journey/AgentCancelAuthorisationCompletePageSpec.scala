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
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.AgentCancelAuthorisationResponse
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{AgentJourneyRequest, AgentJourney, JourneyType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.journey.AgentCancelAuthorisationCompletePage

import scala.language.postfixOps

class AgentCancelAuthorisationCompletePageSpec extends ViewSpecSupport {

  val viewTemplate: AgentCancelAuthorisationCompletePage = app.injector.instanceOf[AgentCancelAuthorisationCompletePage]
  val testInvitationId: String = "AB1234567890"
  val testClientName: String = "Test Client"
  val agentName: String = "ABC Accountants"
  val serviceLabels: Map[String, String] = Map(
    "HMRC-MTD-IT" -> "Manage their Making Tax Digital for Income Tax",
    "HMRC-MTD-VAT" -> "Manage their VAT",
    "HMRC-CGT-PD" -> "Manage their Capital Gains Tax on UK property account",
    "HMRC-PPT-ORG" -> "Manage their Plastic Packaging Tax",
    "HMRC-CBC-ORG" -> "Manage their country-by-country reports",
    "HMRC-PILLAR2-ORG" -> "Manage their Pillar 2 top-up taxes",
    "HMRC-TERS-ORG" -> "Maintain their trust or an estate",
    "HMRC-TERSNT-ORG" -> "Maintain their trust or an estate"
  )

  val confirmationData: AgentCancelAuthorisationResponse = AgentCancelAuthorisationResponse(
    clientName = testClientName,
    agentName = agentName,
    service = "HMRC-MTD-IT",
    date = "2024-12-25"
  )

  private val completeJourney: AgentJourney = AgentJourney(
    JourneyType.AuthorisationRequest,
    journeyComplete = Some("2024-12-25"),
    confirmationClientName = Some(testClientName),
    confirmationService = Some("HMRC-MTD-IT")
  )

  "CreateAuthorisationRequestCompletePage view for non-ITSA services" should {
    for (taxService <- serviceLabels.keySet.toList.filterNot(_ == "HMRC-MTD-IT")) {
      implicit val journeyRequest: AgentJourneyRequest[?] = new AgentJourneyRequest("", completeJourney.copy(confirmationService = Some(taxService)), request)
      val view: HtmlFormat.Appendable = viewTemplate(confirmationData.copy(service = taxService))
      val doc: Document = Jsoup.parse(view.body)
      s"include the correct H1 text for $taxService" in {
        doc.mainContent.extractText("h1.govuk-panel__title", 1).value shouldBe "Authorisation cancelled"
      }
      s"include the correct panel body text for $taxService" in {
        doc.mainContent.extractText(".govuk-panel__body", 1).value shouldBe "25 December 2024"
      }
      s"include the correct p1 text for $taxService" in {
        doc.mainContent.extractText(p, 1).value shouldBe s"$agentName is no longer authorised to manage $testClientName’s ${serviceLabels(taxService)}."
      }
      s"include no content about SA for $taxService" in {
        doc.mainContent.extractText(h2, 2).isEmpty shouldBe true
      }
      s"have a print link for $taxService" in {
        doc.mainContent.extractLink(1).value shouldBe TestLink("Print this page", "#print-dialogue")
      }
      s"have correct link for returning to Agent Services Account home on page for confirming $taxService" in {
        val expectedUrl = "http://localhost:9401/agent-services-account/home"
        doc.mainContent.extractLink(2).value shouldBe TestLink(s"Return to agent services account homepage", expectedUrl)
      }
    }
  }

  "CreateAuthorisationRequestCompletePage view for HMRC-MTD-IT" should {
      implicit val journeyRequest: AgentJourneyRequest[?] = new AgentJourneyRequest("", completeJourney, request)
      val view: HtmlFormat.Appendable = viewTemplate(confirmationData)
      val doc: Document = Jsoup.parse(view.body)
      s"include the correct H1 text for HMRC-MTD-IT" in {
        doc.mainContent.extractText("h1.govuk-panel__title", 1).value shouldBe "Authorisation cancelled"
      }
      s"include the correct panel body text for HMRC-MTD-IT" in {
        doc.mainContent.extractText(".govuk-panel__body", 1).value shouldBe "25 December 2024"
      }
      s"include the correct p1 text for HMRC-MTD-IT" in {
        doc.mainContent.extractText(p, 1).value shouldBe s"$agentName is no longer authorised to manage $testClientName’s ${serviceLabels("HMRC-MTD-IT")}."
      }
      s"include the correct SA content for HMRC-MTD-IT" in {
          doc.mainContent.extractText(h2, 2).value shouldBe "If you’re still authorised to manage Self Assessment for this client"
      }
      s"include a link to sign into online services for agents account" in {
        doc.mainContent.extractLink(1).value shouldBe TestLink("sign into your HMRC online services for agents account", "https://www.gov.uk/guidance/self-assessment-for-agents-online-service")
      }
      s"have a print link for HMRC-MTD-IT" in {
        doc.mainContent.extractLink(2).value shouldBe TestLink("Print this page", "#print-dialogue")
      }
      s"have correct link for returning to Agent Services Account home on page for confirming HMRC-MTD-IT" in {
        val expectedUrl = "http://localhost:9401/agent-services-account/home"
        doc.mainContent.extractLink(3).value shouldBe TestLink(s"Return to agent services account homepage", expectedUrl)
      }
    }
}
