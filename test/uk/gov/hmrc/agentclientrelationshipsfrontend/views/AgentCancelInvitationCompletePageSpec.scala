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

package uk.gov.hmrc.agentclientrelationshipsfrontend.views

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.agentclientrelationshipsfrontend.actions.AgentRequest
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.AuthorisationRequestInfo
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.AgentCancelInvitationCompletePage

import java.time.LocalDate

class AgentCancelInvitationCompletePageSpec extends ViewSpecSupport {

  val viewTemplate: AgentCancelInvitationCompletePage = app.injector.instanceOf[AgentCancelInvitationCompletePage]
  def confirmationData(service: String): AuthorisationRequestInfo = AuthorisationRequestInfo(
    invitationId = "someInvitationId",
    agentReference = "ABC123",
    normalizedAgentName = "abc-accountants",
    agentEmailAddress = "info@example.com",
    clientName = "Client Name",
    service = service,
    expiryDate = LocalDate.of(2025, 1, 1),
    suppliedClientId = "AB1234567890",
    clientType = "personal"
  )
  val serviceLabel: Map[String, String] = Map(
    "HMRC-MTD-IT" -> "Making Tax Digital for Income Tax",
    "HMRC-MTD-VAT" -> "VAT",
    "HMRC-TERS-ORG" -> "Trusts and Estates",
    "HMRC-CGT-PD" -> "Capital Gains Tax on UK property account",
    "PERSONAL-INCOME-RECORD" -> "Income Record Viewer",
    "HMRC-PPT-ORG" -> "Plastic Packaging Tax",
    "HMRC-CBC-ORG" -> "Country-by-country reporting",
    "HMRC-CBC-NONUK-ORG" -> "Country-by-country reporting",
    "HMRC-PILLAR2-ORG" -> "Pillar 2 Top-up Taxes",
    "HMRC-TERSNT-ORG" -> "Trusts and Estates"
  )

  object Expected {
    val heading = "Authorisation request cancelled"
    val title = s"$heading - Ask a client to authorise you - GOV.UK"
    def p1(service: String) = s"You have cancelled your authorisation request to manage their ${serviceLabel(service)}."
    val inset = "Client Name can no longer respond to this request."
    val link = "Manage your recent authorisation requests"
    val p2 = "If you cancelled your authorisation request by mistake, you will need to start a new authorisation request."
  }

  "AgentCancelInvitationPage view" when {
    serviceLabel.keySet.foreach(service => s"render the correct template for $service" should {
      implicit val agentRequest: AgentRequest[?] = new AgentRequest("", request)
      val view: HtmlFormat.Appendable = viewTemplate(confirmationData(service))

      val doc: Document = Jsoup.parse(view.body)
      "have the right title" in {
        doc.title() shouldBe Expected.title
      }
      "have a language switcher" in {
        doc.hasLanguageSwitch shouldBe true
      }
      "have the right heading" in {
        doc.mainContent.extractText(h1, 1).value shouldBe Expected.heading
      }
      s"have the correct p1 for the service $service" in {
        doc.mainContent.extractText(p,1).value shouldBe Expected.p1(service)
      }
      "have the right inset" in {
        doc.mainContent.extractText(inset, 1).value shouldBe Expected.inset
      }
      "have the right link" in {
        doc.mainContent.extractText(link, 1).value shouldBe Expected.link
      }
      "have the right p2" in {
        doc.mainContent.extractText(p, 4).value shouldBe Expected.p2
      }
    })

  }

}
