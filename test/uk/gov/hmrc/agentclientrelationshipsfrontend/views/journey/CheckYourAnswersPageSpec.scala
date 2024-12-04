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
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.{ClientDetailsResponse, KnownFactType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.journey.CheckYourAnswersPage

class CheckYourAnswersPageSpec extends ViewSpecSupport {

  val viewTemplate: CheckYourAnswersPage = app.injector.instanceOf[CheckYourAnswersPage]
  private val journeyType = JourneyType.AuthorisationRequest
  private val exampleClientId: String = "1234567890"
  private val exampleKnownFact: String = "AA11AA"
  private val clientName = "Test Name"
  private val servicesWithoutAgentRoles = Seq("PERSONAL-INCOME-RECORD", "HMRC-MTD-VAT", "HMRC-CGT-PD", "HMRC-PPT-ORG", "HMRC-CBC-ORG", "HMRC-PILLAR2-ORG", "HMRC-TERS-ORG")
  private val basicClientDetails = ClientDetailsResponse(clientName, None, None, Seq(exampleKnownFact), Some(KnownFactType.PostalCode), false, None)
  private val basicJourney: AgentJourney = AgentJourney(
    journeyType = journeyType,
    clientType = Some("personal"),
    clientService = Some("HMRC-MTD-IT"),
    clientId = Some(exampleClientId),
    clientDetailsResponse = Some(basicClientDetails),
    knownFact = Some(exampleKnownFact),
    clientConfirmed = Some(true),
    agentType = None
  )

  def singleAgentRequestJourney(service: String): AgentJourney = basicJourney.copy(
    clientService = Some(service),
    agentType = None
  )

  def agentRoleBasedRequestJourney(service: String, role: String): AgentJourney = basicJourney.copy(
    clientService = Some(service),
    agentType = Some(role)
  )

  private def serviceLabels(service: String) = service match {
    case "HMRC-MTD-IT" => "Manage their Making Tax Digital for Income Tax"
    case "PERSONAL-INCOME-RECORD" => "View their Income record"
    case "HMRC-MTD-VAT" => "Manage their VAT"
    case "HMRC-CGT-PD" => "Manage their Capital Gains Tax on UK property account"
    case "HMRC-PPT-ORG" => "Manage their Plastic Packaging Tax"
    case "HMRC-CBC-ORG" => "Manage their country-by-country reports"
    case "HMRC-PILLAR2-ORG" => "Manage their Pillar 2 top-up taxes"
    case "HMRC-TERS-ORG" => "Maintain their trust or an estate"
  }

  private def roleLabels(role: String) = role match {
    case "HMRC-MTD-IT" => "As their main agent"
    case "HMRC-MTD-IT-SUPP" => "As a supporting agent"
  }

  servicesWithoutAgentRoles.foreach(service =>
    s"Confirm authorisation request view for $service" should {
      implicit val journeyRequest: AgentJourneyRequest[?] = new AgentJourneyRequest("", singleAgentRequestJourney(service), request)

      val title = "Check your answers - Ask a client to authorise you - GOV.UK"
      val view: HtmlFormat.Appendable = viewTemplate(supportsAgentRoles = false)
      val doc: Document = Jsoup.parse(view.body)

      "have the right title" in {
        doc.title() shouldBe title
      }

      "have a language switcher" in {
        doc.hasLanguageSwitch shouldBe true
      }

      "have a summary list heading" in {
        doc.mainContent.extractText("h2", 1).value shouldBe s"Authorisation details for $clientName"
      }

      "render a summary list" in {
        doc.mainContent.extractSummaryList() shouldBe Some(TestSummaryList(
         rows = List(("What you want to do for the client", serviceLabels(service), "/agent-client-relationships/authorisation-request")),
        ))
      }

      "have a submission button" in {
        doc.mainContent.extractText(button, 1).value shouldBe "Confirm and send"
      }
    })

  List("HMRC-MTD-IT", "HMRC-MTD-IT-SUPP").foreach(role =>
    s"Confirm authorisation request view for $role" should {
      implicit val journeyRequest: AgentJourneyRequest[?] = new AgentJourneyRequest("", agentRoleBasedRequestJourney("HMRC-MTD-IT", role), request)

      val title = "Check your answers - Ask a client to authorise you - GOV.UK"
      val view: HtmlFormat.Appendable = viewTemplate(supportsAgentRoles = true)
      val doc: Document = Jsoup.parse(view.body)

      "have the right title" in {
        doc.title() shouldBe title
      }

      "have a language switcher" in {
        doc.hasLanguageSwitch shouldBe true
      }

      "have a summary list heading" in {
        doc.mainContent.extractText("h2", 1).value shouldBe s"Authorisation details for $clientName"
      }

      "render a summary list" in {
        doc.mainContent.extractSummaryList() shouldBe Some(TestSummaryList(
          rows = List(
            ("What you want to do for the client", serviceLabels("HMRC-MTD-IT"), "/agent-client-relationships/authorisation-request"),
            ("How you want to act for them", roleLabels(role), "/agent-client-relationships/authorisation-request/agent-role")
          )
        ))
      }

      "have a submission button" in {
        doc.mainContent.extractText(button, 1).value shouldBe "Confirm and send"
      }
    })
}
