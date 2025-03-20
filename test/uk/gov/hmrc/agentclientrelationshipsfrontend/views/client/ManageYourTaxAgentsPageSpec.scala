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
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.Invitation
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.{AgentData, AgentsAuthorisationsResponse, AgentsInvitationsResponse, Authorisation, AuthorisationEvent, AuthorisationEventsResponse, AuthorisedAgent, Cancelled, DeAuthorised, Expired, Accepted, ManageYourTaxAgentsData, Pending, Rejected}
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.client.ManageYourTaxAgentsPage

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDate}
import java.util.Locale
import scala.language.postfixOps

class ManageYourTaxAgentsPageSpec extends ViewSpecSupport {

  val viewTemplate: ManageYourTaxAgentsPage = app.injector.instanceOf[ManageYourTaxAgentsPage]
  val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM uuuu", Locale.UK)
  val agentName: String = "ABC Accountants"
  val agentName2: String = "DEF Accountants"
  val authorisedAgentName: String = "GH Accountants"
  val authorisedAgentName2: String = "IJ Accountants"
  val invitationId: String = "AB1234567890"
  val serviceKey: String = "HMRC-MTD-IT"
  val status: String = "Pending"
  val expiryDate: LocalDate = LocalDate.now().plusDays(11)
  val startDate: LocalDate = LocalDate.now().minusDays(10)
  def agentRoleFromService(service: String): String = if (service == "HMRC-MTD-IT-SUPP") "Supporting" else "Main"

  val services: Map[String, String] = Map(
    "PERSONAL-INCOME-RECORD" -> "income-record-viewer",
    "HMRC-MTD-IT" -> "income-tax",
    "HMRC-PPT-ORG" -> "plastic-packaging-tax",
    "HMRC-CGT-PD" -> "capital-gains-tax-uk-property",
    "HMRC-CBC-ORG" -> "country-by-country-reporting",
    "HMRC-MTD-VAT" -> "vat",
    "HMRC-PILLAR2-ORG" -> "pillar-2",
    "HMRC-TERS-ORG" -> "trusts-and-estates"
  )

  object Expected {
    val heading = "Manage who can deal with HMRC for you"
    val title = s"$heading - GOV.UK"
    val tab1 = "Current requests"
    val tab2 = "Authorised agents"
    val tab3 = "History"
  }

  val taxServiceNames: Map[String, String] = Map(
    "PERSONAL-INCOME-RECORD" -> "Income Record Viewer",
    "HMRC-MTD-IT" -> "Making Tax Digital for Income Tax",
    "HMRC-PPT-ORG" -> "Plastic Packaging Tax",
    "HMRC-CGT-PD" -> "Capital Gains Tax on UK property account",
    "HMRC-CBC-ORG" -> "Country-by-country reporting",
    "HMRC-MTD-VAT" -> "VAT",
    "HMRC-PILLAR2-ORG" -> "Pillar 2 Top-up Taxes",
    "HMRC-TERS-ORG" -> "Trusts and Estates"
  )

  val mytaData: ManageYourTaxAgentsData = ManageYourTaxAgentsData(
    agentsInvitations = AgentsInvitationsResponse(agentsInvitations = Seq(
      AgentData(
        uid = "NBM9TUDA",
        agentName = agentName,
        invitations = Seq(
          Invitation(
            invitationId = "4454567890",
            service = "HMRC-MTD-IT-SUPP",
            clientName = "Some test",
            status = Pending,
            expiryDate = expiryDate,
            lastUpdated = Instant.now()
          ),
          Invitation(
            invitationId = "336LD67891",
            service = "HMRC-PILLAR2-ORG",
            clientName = "Some test",
            status = Pending,
            expiryDate = expiryDate,
            lastUpdated = Instant.now()
          ),
          Invitation(
            invitationId = "456LD67891",
            service = "HMRC-TERSNT-ORG",
            clientName = "Some test",
            status = Pending,
            expiryDate = expiryDate,
            lastUpdated = Instant.now()
          ),
          Invitation(
            invitationId = "456LD67891",
            service = "HMRC-CBC-NONUK-ORG",
            clientName = "Some test",
            status = Pending,
            expiryDate = expiryDate,
            lastUpdated = Instant.now()
          )
        )
      ),
      AgentData(
        uid = "1234590",
        agentName = agentName2,
        invitations = Seq(
          Invitation(
            invitationId = "7714567891",
            service = "HMRC-MTD-IT",
            clientName = "Some test",
            status = Pending,
            expiryDate = expiryDate,
            lastUpdated = Instant.now()
          ),
          Invitation(
            invitationId = "123456BFX90",
            service = "HMRC-MTD-VAT",
            clientName = "Some test",
            status = Pending,
            expiryDate = expiryDate,
            lastUpdated = Instant.now()
          )
        )
      )
    )),
    agentsAuthorisations = AgentsAuthorisationsResponse(agentsAuthorisations = Seq(
      AuthorisedAgent(
        agentName = authorisedAgentName,
        arn = "1234567890",
        authorisations = Seq(
          Authorisation(
            uid = "1234567890",
            service = "HMRC-MTD-IT",
            clientId = "1234567890",
            date = startDate,
            arn = "1234567890",
            agentName = authorisedAgentName
          ),
          Authorisation(
            uid = "123LD67891",
            service = "HMRC-PILLAR2-ORG",
            clientId = "1234567890",
            date = startDate,
            arn = "1234567890",
            agentName = authorisedAgentName
          )
        )
      ),
      AuthorisedAgent(
        agentName = authorisedAgentName2,
        arn = "987654221",
        authorisations = Seq(
          Authorisation(
            uid = "123456BFX90",
            service = "HMRC-MTD-VAT",
            clientId = "1234567890",
            date = startDate,
            arn = "987654221",
            agentName = authorisedAgentName2
          )
        )
      )
    )),
    authorisationEvents = AuthorisationEventsResponse(authorisationEvents = Seq(
      AuthorisationEvent(
        agentName = "Test Agent",
        service = "HMRC-MTD-IT",
        date = startDate,
        eventType = Cancelled
      ),
      AuthorisationEvent(
        agentName = "Top Tax Agents Ltd",
        service = "HMRC-MTD-VAT",
        date = startDate.minusDays(2),
        eventType = DeAuthorised
      ),
      AuthorisationEvent(
        agentName = "Old School Books",
        service = "HMRC-PILLAR2-ORG",
        date = startDate.minusDays(3),
        eventType = Expired
      ),
      AuthorisationEvent(
        agentName = "Bottom Line",
        service = "HMRC-CGT-PD",
        date = startDate.minusDays(4),
        eventType = Rejected
      ),
      AuthorisationEvent(
        agentName = authorisedAgentName,
        service = "HMRC-MTD-IT",
        date = startDate.minusDays(5),
        eventType = Accepted
      )
    ))
  )

  "ManageYourTaxAgentsPage view with current requests" should {

    val view: HtmlFormat.Appendable = viewTemplate(services, mytaData)
    val doc: Document = Jsoup.parse(view.body)

    "include the correct title" in {
      doc.title() shouldBe Expected.title
    }

    "include the correct H1 text" in {
      doc.mainContent.extractText(h1, 1).value shouldBe Expected.heading
    }

    "include the correct list of services" in {
      doc.mainContent.extractList(1).sorted shouldBe taxServiceNames.values.toList.sorted
    }

    "include the correct number of tabs" in {
      doc.mainContent.extractText(tabLink, 1).value shouldBe Expected.tab1
      doc.mainContent.extractText(tabLink, 2).value shouldBe Expected.tab2
      doc.mainContent.extractText(tabLink, 3).value shouldBe Expected.tab3
    }

    "include a table of current requests for each agent" in {
      doc.mainContent.extractTable(1, 4).value shouldBe TestTable(
        caption = agentName,
        rows = List(
          IndexedSeq(
            "Making Tax Digital for Income Tax",
            "Supporting",
            expiryDate.format(dateFormatter),
            s"Respond to request from $agentName to manage your Making Tax Digital for Income Tax as a Supporting agent"
          ),
          IndexedSeq(
            "Pillar 2 Top-up Taxes",
            "Main",
            expiryDate.format(dateFormatter),
            s"Respond to request from $agentName to manage your Pillar 2 Top-up Taxes as a Main agent"
          ),
          IndexedSeq(
            "Trusts and Estates",
            "Main",
            expiryDate.format(dateFormatter),
            s"Respond to request from $agentName to manage your Trusts and Estates as a Main agent"
          ),
          IndexedSeq(
            "Country-by-country reporting",
            "Main",
            expiryDate.format(dateFormatter),
            s"Respond to request from $agentName to manage your Country-by-country reporting as a Main agent"
          )
        ))
      doc.mainContent.extractTable(2, 4).value shouldBe TestTable(
        caption = agentName2,
        rows = List(
          IndexedSeq(
            "Making Tax Digital for Income Tax",
            "Main",
            expiryDate.format(dateFormatter),
            s"Respond to request from $agentName2 to manage your Making Tax Digital for Income Tax as a Main agent"
          ),
          IndexedSeq(
            "VAT",
            "Main",
            expiryDate.format(dateFormatter),
            s"Respond to request from $agentName2 to manage your VAT as a Main agent"
          )
        ))
    }

    "include a table of authorisations for each authorised agent" in {
      doc.mainContent.extractTable(3, 4).value shouldBe TestTable(
        caption = authorisedAgentName,
        rows = List(
          IndexedSeq(
            "Pillar 2 Top-up Taxes",
            "Main",
            startDate.format(dateFormatter),
            s"Remove authorisation from $authorisedAgentName to manage your Pillar 2 Top-up Taxes as a Main agent"
          ),
          IndexedSeq(
            "Making Tax Digital for Income Tax",
            "Main",
            startDate.format(dateFormatter),
            s"Remove authorisation from $authorisedAgentName to manage your Making Tax Digital for Income Tax as a Main agent"
          )
        ))
      doc.mainContent.extractTable(4, 4).value shouldBe TestTable(
        caption = authorisedAgentName2,
        rows = List(
          IndexedSeq(
            "VAT",
            "Main",
            startDate.format(dateFormatter),
            s"Remove authorisation from $authorisedAgentName2 to manage your VAT as a Main agent"
          )
        ))
    }

    "include a table of authorisation events in history" in {
      doc.mainContent.extractTable(5, 3).value shouldBe TestTable(
        caption = "History of authorisations",
        rows = List(
          IndexedSeq(
            startDate.format(dateFormatter),
            "Making Tax Digital for Income Tax",
            "Test Agent cancelled the request to be your Main agent"
          ),
          IndexedSeq(
            startDate.minusDays(2).format(dateFormatter),
            "VAT",
            "Top Tax Agents Ltd is no longer authorised as your Main agent"
          ),
          IndexedSeq(
            startDate.minusDays(3).format(dateFormatter),
            "Pillar 2 Top-up Taxes",
            "The request from Old School Books to be your Main agent expired"
          ),
          IndexedSeq(
            startDate.minusDays(4).format(dateFormatter),
            "Capital Gains Tax on UK property account",
            "You declined Bottom Line as your Main agent"
          ),
          IndexedSeq(
            startDate.minusDays(5).format(dateFormatter),
            "Making Tax Digital for Income Tax",
            s"You accepted $authorisedAgentName as your Main agent"
          )
        ))
    }

  }

  "ManageYourTaxAgentsPage view without any current requests, authorisations or history" should {

    val view: HtmlFormat.Appendable = viewTemplate(services, mytaData.copy(
      agentsInvitations = AgentsInvitationsResponse(agentsInvitations = Seq.empty),
      agentsAuthorisations = AgentsAuthorisationsResponse(agentsAuthorisations = Seq.empty),
      authorisationEvents = AuthorisationEventsResponse(authorisationEvents = Seq.empty)
    ))
    val doc: Document = Jsoup.parse(view.body)

    "include the correct number of tabs" in {
      doc.mainContent.extractText(tabLink, 1).value shouldBe Expected.tab2
      doc.mainContent.extractText(tabLink, 2).value shouldBe Expected.tab3
    }

    "include a message when there are no current authorised agents" in {
      doc.extractById(main, "authorisedAgents").get.extractText(p, 1).value shouldBe "You do not have any authorised agents."
    }

    "include a message when there is no history" in {
      doc.extractById(main, "history").get.extractText(p, 1).value shouldBe "There is no authorisation history on this account."
    }

  }

}
