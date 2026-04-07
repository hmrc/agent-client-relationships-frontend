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
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.Pending
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{ClientJourney, ClientJourneyRequest}
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.client.ConsentInformationPage
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.ExistingMainAgent

import java.time.Instant
import scala.language.postfixOps

class ConsentInformationPageSpec extends ViewSpecSupport {

  val viewTemplate: ConsentInformationPage = app.injector.instanceOf[ConsentInformationPage]

  val agentName: String = "ABC Accountants"
  val newAgentName: String = "ABC Accountants New"
  val existingAgent: ExistingMainAgent = ExistingMainAgent(agentName, false)
  val invitationId: String = "AB1234567890"
  val lastModifiedDate: String = "2024-12-01T12:00:00Z"

  val taxServices: Set[String] = Set(
    "PERSONAL-INCOME-RECORD",
    "HMRC-MTD-VAT",
    "HMRC-CGT-PD",
    "HMRC-PPT-ORG",
    "HMRC-CBC-ORG",
    "HMRC-PILLAR2-ORG",
    "HMRC-TERS-ORG",
  )

  // in the test we can align the same url part with other services according to the actual invitation contents
  val itsaServices: Set[String] = Set(
    "HMRC-MTD-IT",
    "HMRC-MTD-IT-SUPP"
  )

  object Expected {
    def heading(serviceKey: String, agentName: String) = s"$agentName want to be your ${indefiniteAgentRole(serviceKey)}"

    def title(serviceKey: String, agentName: String) = s"${heading(serviceKey, agentName)} - Appoint someone to deal with HMRC for you - GOV.UK"

    def warning(agentName: String, newAgentName: String) = s"If you authorise $newAgentName, we will remove $agentName as your existing agent."

    def warningMain(agentName: String, newAgentName: String) = s"If you authorise $newAgentName, we will remove $agentName as your existing main agent."

    def warningSupp(agentName: String) = s"If you authorise $agentName as your supporting agent, they can not longer act as main agent."

    val section1h2 = "What you need to know first"

    def section1p1(serviceKey: String) = s"An agent can either be ‘main agent’ or a ‘supporting agent’ when they manage your ${taxServiceNames(serviceKey)}."

    val section1p2 = "Some important tasks - like submitting your tax returns - can only be completed by you, or by a main agent acting on your behalf."
    val section1link = "Find out how main and supporting agents can act for you (opens in a new tab)"
    val section1p3 = s"$section1link."
    val itsaInfoUrl = "https://www.gov.uk/guidance/choose-agents-for-making-tax-digital-for-income-tax"

    def section2h2(agentName: String) = s"What it means to give consent to $agentName"

    def section2p1(serviceKey: String, agentName: String) = s"Giving consent means employees of $agentName will be able to access your ${taxServiceNames(serviceKey)} data. They will only be allowed to access details they need to carry out their duties as your agent."

    def itsaSection2p1(agentName: String) = s"Giving consent means employees of $agentName will be able to access some of your personal data and Income Tax details. They will only be allowed to access details they need to carry out their duties as your agent."

    val section2link = "privacy notice (opens in a new tab)"
    val section2p2 = s"For details about how we store and process your information, see our $section2link."
    val privacyNoticeUrl = "https://www.gov.uk/government/publications/data-protection-act-dpa-information-hm-revenue-and-customs-hold-about-you/data-protection-act-dpa-information-hm-revenue-and-customs-hold-about-you"

    def section3h2(agentName: String) = s"Do you have to give consent to $agentName?"

    val section3p1 = "No. You must make the best decision for you."
    val section3p2 = "You can seek a different agent, or decide to manage your own taxes if you prefer."

    val section4h2 = "How long does consent last?"
    val section4p1Main = "Consent lasts until:"

    def section4bullet1(agentName: String) = s"you tell us that $agentName can no longer act on your behalf, or"

    def section4bullet2(serviceKey: String) = s"you choose a different main agent for $serviceKey"

    def section4p1(agentName: String) = s"Consent lasts until you tell us that $agentName can no longer act on your behalf."

    def section4p2(agentName: String) = s"HMRC might contact you in the future to check if you still want $agentName to act for you."

    val detailsSummary = "You can remove consent at any time – find out how"
    val detailsContent = "You can remove consent consent at any time by using Manage who can deal with HMRC for you. A link to this page can be found in your Personal Tax Account or Business Tax Account."
    val mytaUrl = ""

    val section5h2 = "Before you decide"

    def section5p1(serviceKey: String, agentName: String) = s"On the next page we list the tasks that $agentName can carry out on your behalf, if you authorise them as your ${indefiniteAgentRole(serviceKey)}."
  }

  val indefiniteAgentRole: Map[String, String] = Map(
    "PERSONAL-INCOME-RECORD" -> "agent",
    "HMRC-MTD-IT" -> "main agent",
    "HMRC-MTD-IT-SUPP" -> "supporting agent",
    "HMRC-PPT-ORG" -> "agent",
    "HMRC-CGT-PD" -> "agent",
    "HMRC-CBC-ORG" -> "agent",
    "HMRC-CBC-NONUK-ORG" -> "agent",
    "HMRC-MTD-VAT" -> "agent",
    "HMRC-PILLAR2-ORG" -> "agent",
    "HMRC-TERS-ORG" -> "agent",
    "HMRC-TERSNT-ORG" -> "agent"
  )

  val taxServiceNames: Map[String, String] = Map(
    "PERSONAL-INCOME-RECORD" -> "Income Record Viewer",
    "HMRC-MTD-IT" -> "Making Tax Digital for Income Tax",
    "HMRC-MTD-IT-SUPP" -> "Making Tax Digital for Income Tax",
    "HMRC-PPT-ORG" -> "Plastic Packaging Tax",
    "HMRC-CGT-PD" -> "Capital Gains Tax on UK property account",
    "HMRC-CBC-ORG" -> "country-by-country reporting",
    "HMRC-CBC-NONUK-ORG" -> "country-by-country reporting",
    "HMRC-MTD-VAT" -> "VAT",
    "HMRC-PILLAR2-ORG" -> "Pillar 2 Top-up Taxes",
    "HMRC-TERS-ORG" -> "Trusts and Estates",
    "HMRC-TERSNT-ORG" -> "Trusts and Estates"
  )

  val agentRole: Map[String, String] = Map(
    "PERSONAL-INCOME-RECORD" -> "agent",
    "HMRC-MTD-IT" -> "mainAgent",
    "HMRC-MTD-IT-SUPP" -> "suppAgent",
    "HMRC-PPT-ORG" -> "agent",
    "HMRC-CGT-PD" -> "agent",
    "HMRC-CBC-ORG" -> "agent",
    "HMRC-CBC-NONUK-ORG" -> "agent",
    "HMRC-MTD-VAT" -> "agent",
    "HMRC-PILLAR2-ORG" -> "agent",
    "HMRC-TERS-ORG" -> "agent",
    "HMRC-TERSNT-ORG" -> "agent"
  )

  val journey: ClientJourney = ClientJourney(
    journeyType = "authorisation-response"
  )

  def journeyForService(serviceKey: String): ClientJourney = journey.copy(
    serviceKey = Some(serviceKey),
    invitationId = Some(invitationId),
    agentName = Some(newAgentName),
    status = Some(Pending),
    lastModifiedDate = Some(Instant.parse(lastModifiedDate))
  )

  val services: Seq[String] = Seq(
    "PERSONAL-INCOME-RECORD",
    "HMRC-MTD-IT",
    "HMRC-MTD-IT-SUPP",
    "HMRC-PPT-ORG",
    "HMRC-CGT-PD",
    "HMRC-CBC-ORG",
    "HMRC-CBC-NONUK-ORG",
    "HMRC-MTD-VAT",
    "HMRC-PILLAR2-ORG",
    "HMRC-TERS-ORG",
    "HMRC-TERSNT-ORG"
  )

  services.foreach { serviceKey =>
    implicit val journeyRequest: ClientJourneyRequest[?] = new ClientJourneyRequest(journey, request)

    s"ConsentInformation view for $serviceKey" when {
      "there is no existing agent" should {
        val view: HtmlFormat.Appendable = viewTemplate(
          journeyForService(serviceKey),
          agentRole(serviceKey)
        )
        val doc: Document = Jsoup.parse(view.body)
        "include the correct title" in {
          doc.title() shouldBe Expected.title(serviceKey, newAgentName)
        }

        "include the correct H1 text" in {
          doc.mainContent.extractText(h1, 1).value shouldBe Expected.heading(serviceKey, newAgentName)
        }

        if (serviceKey == "HMRC-MTD-IT" || serviceKey == "HMRC-MTD-IT-SUPP") {
          "include section 1 (main/supporting info)" in {
            doc.mainContent.extractText(h2, 2).value shouldBe Expected.section1h2
            doc.mainContent.extractText(p, 1).value shouldBe Expected.section1p1(serviceKey)
            doc.mainContent.extractText(p, 2).value shouldBe Expected.section1p2
            doc.mainContent.extractText(p, 3).value shouldBe Expected.section1p3
            doc.mainContent.extractLink(1).value shouldBe TestLink(Expected.section1link, Expected.itsaInfoUrl)
          }
          "include section 2" in {
            doc.mainContent.extractText(h2, 3).value shouldBe Expected.section2h2(newAgentName)
            doc.mainContent.extractText(p, 4).value shouldBe Expected.itsaSection2p1(newAgentName)
            doc.mainContent.extractText(p, 5).value shouldBe Expected.section2p2
            doc.mainContent.extractLink(2).value shouldBe TestLink(Expected.section2link, Expected.privacyNoticeUrl)
          }
          "include section 3" in {
            doc.mainContent.extractText(h2, 4).value shouldBe Expected.section3h2(newAgentName)
            doc.mainContent.extractText(p, 6).value shouldBe Expected.section3p1
            doc.mainContent.extractText(p, 7).value shouldBe Expected.section3p2
          }
          "include section 4" in {
            doc.mainContent.extractText(h2, 5).value shouldBe Expected.section4h2
            if (serviceKey == "HMRC-MTD-IT") {
              doc.mainContent.extractText(p, 8).value shouldBe Expected.section4p1Main
              doc.mainContent.extractList(1) shouldBe List(
                Expected.section4bullet1(newAgentName),
                Expected.section4bullet2(taxServiceNames(serviceKey))
              )
            } else {
              doc.mainContent.extractText(p, 8).value shouldBe Expected.section4p1(newAgentName)
            }
            doc.mainContent.extractText(p, 9).value shouldBe Expected.section4p2(newAgentName)
          }
          "include details" in {
            doc.mainContent.extractText(detailsSummary, 1).value shouldBe Expected.detailsSummary
            doc.mainContent.extractText(detailsContent, 1).value shouldBe Expected.detailsContent
          }
          "include section 5" in {
            doc.mainContent.extractText(h2, 6).value shouldBe Expected.section5h2
            doc.mainContent.extractText(p, 11).value shouldBe Expected.section5p1(serviceKey, newAgentName)
          }
          "include a continue button" in {
            doc.mainContent.extractText(button, 1).value shouldBe "Continue"
          }
        } else {
          "include section 2" in {
            doc.mainContent.extractText(h2, 2).value shouldBe Expected.section2h2(newAgentName)
            doc.mainContent.extractText(p, 1).value shouldBe Expected.section2p1(serviceKey, newAgentName)
            doc.mainContent.extractText(p, 2).value shouldBe Expected.section2p2
            doc.mainContent.extractLink(1).value shouldBe TestLink(Expected.section2link, Expected.privacyNoticeUrl)
          }
          "include section 3" in {
            doc.mainContent.extractText(h2, 3).value shouldBe Expected.section3h2(newAgentName)
            doc.mainContent.extractText(p, 3).value shouldBe Expected.section3p1
            doc.mainContent.extractText(p, 4).value shouldBe Expected.section3p2
          }
          "include section 4" in {
            doc.mainContent.extractText(h2, 4).value shouldBe Expected.section4h2
            doc.mainContent.extractText(p, 5).value shouldBe Expected.section4p1(newAgentName)
            doc.mainContent.extractText(p, 6).value shouldBe Expected.section4p2(newAgentName)
          }
          "include details" in {
            doc.mainContent.extractText(detailsSummary, 1).value shouldBe Expected.detailsSummary
            doc.mainContent.extractText(detailsContent, 1).value shouldBe Expected.detailsContent
          }
          "include section 5" in {
            doc.mainContent.extractText(h2, 5).value shouldBe Expected.section5h2
            doc.mainContent.extractText(p, 8).value shouldBe Expected.section5p1(serviceKey, newAgentName)
          }
          "include a continue button" in {
            doc.mainContent.extractText(button, 1).value shouldBe "Continue"
          }
        }
      }
      if (serviceKey != "HMRC-MTD-IT-SUPP") {
        "there is an existing agent" should {
          val view: HtmlFormat.Appendable = viewTemplate(
            journeyForService(serviceKey).copy(
              existingMainAgent = Some(existingAgent)
            ),
            agentRole(serviceKey)
          )
          val doc: Document = Jsoup.parse(view.body)
          "include agent change warning" in {
            if (serviceKey == "HMRC-MTD-IT") {
              doc.mainContent.extractText(p, 3).value shouldBe Expected.warningMain(agentName, newAgentName)
            } else {
              doc.mainContent.extractText(p, 1).value shouldBe Expected.warning(agentName, newAgentName)
            }
          }
        }
      }
      if (serviceKey == "HMRC-MTD-IT-SUPP") {
        "the agent is changing from main to supp" should {
          val view: HtmlFormat.Appendable = viewTemplate(
            journeyForService(serviceKey).copy(
              existingMainAgent = Some(ExistingMainAgent(agentName, true)),
              agentName = Some(agentName)
            ),
            agentRole(serviceKey)
          )
          val doc: Document = Jsoup.parse(view.body)
          "include role change warning" in {
            doc.mainContent.extractText(p, 3).value shouldBe Expected.warningSupp(agentName)
          }
        }
      }
    }
  }
}
