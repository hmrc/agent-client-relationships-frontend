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
import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.agentclientrelationshipsfrontend.actions.AgentRequest
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.AuthorisationRequestInfo
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.AgentCancelInvitationForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.AgentCancelInvitationPage

import java.time.LocalDate

class AgentCancelInvitationPageSpec extends ViewSpecSupport {

  val viewTemplate: AgentCancelInvitationPage = app.injector.instanceOf[AgentCancelInvitationPage]
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
    "HMRC-CBC-ORG" -> "Country-by-country reports",
    "HMRC-CBC-NONUK-ORG" -> "Country-by-country reports",
    "HMRC-PILLAR2-ORG" -> "Pillar 2 Top-up Taxes",
    "HMRC-TERSNT-ORG" -> "Trusts and Estates"
  )

  object Expected {
    val heading = "Are you sure you want to cancel this authorisation request?"
    val title = s"$heading - Ask a client to authorise you - GOV.UK"
    def hint(service: String) = s"If you cancel this request, you will not be able to manage their ${serviceLabel(service)}."
  }

  "AgentCancelInvitationPage view" when {
    serviceLabel.keySet.foreach(service => s"render the correct template for $service" should {
      implicit val agentRequest: AgentRequest[?] = new AgentRequest("", request)
      val form: Form[Boolean] = AgentCancelInvitationForm.form
      val view: HtmlFormat.Appendable = viewTemplate(form, confirmationData(service))

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
      s"have the correct hint for the service $service" in {
        doc.mainContent.extractText(hint, "agentCancelInvitation-hint").value shouldBe Expected.hint(service)
      }
    })

  }
}
