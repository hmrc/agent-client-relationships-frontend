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
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.{AgentData, AgentsAuthorisationsResponse, AgentsInvitationsResponse, AuthorisationEventsResponse, ManageYourTaxAgentsData, Pending}
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
  val invitationId: String = "AB1234567890"
  val serviceKey: String = "HMRC-MTD-IT"
  val status: String = "Pending"
  val expiryDate: LocalDate = LocalDate.now().plusDays(11)
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
    "HMRC-CBC-ORG" -> "Country-by-country reports",
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
            invitationId = "1234567890",
            service = "HMRC-MTD-IT",
            clientName = "Some test",
            status = Pending,
            expiryDate = expiryDate,
            lastUpdated = Instant.now()
          ),
          Invitation(
            invitationId = "123LD67891",
            service = "HMRC-PILLAR2-ORG",
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
    agentsAuthorisations = AgentsAuthorisationsResponse(agentsAuthorisations = Seq.empty),
    authorisationEvents = AuthorisationEventsResponse(authorisationEvents = Seq.empty)
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
            "Main",
            expiryDate.format(dateFormatter),
            s"Respond to request from $agentName to manage your Making Tax Digital for Income Tax as a Main agent"
          ),
          IndexedSeq(
            "Pillar 2 Top-up Taxes",
            "Main",
            expiryDate.format(dateFormatter),
            s"Respond to request from $agentName to manage your Pillar 2 Top-up Taxes as a Main agent"
          )
        ))
      doc.mainContent.extractTable(2, 4).value shouldBe TestTable(
        caption = agentName2,
        rows = List(
          IndexedSeq(
            "VAT",
            "Main",
            expiryDate.format(dateFormatter),
            s"Respond to request from $agentName2 to manage your VAT as a Main agent"
          )
        ))
    }

  }

  "ManageYourTaxAgentsPage view without any current requests" should {

    val view: HtmlFormat.Appendable = viewTemplate(services, mytaData.copy(agentsInvitations = AgentsInvitationsResponse(agentsInvitations = Seq.empty)))
    val doc: Document = Jsoup.parse(view.body)

    "include the correct number of tabs" in {
      doc.mainContent.extractText(tabLink, 1).value shouldBe Expected.tab2
      doc.mainContent.extractText(tabLink, 2).value shouldBe Expected.tab3
    }

  }

}
