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
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.ResendInvitationLink

import java.time.LocalDate
import scala.language.postfixOps

class ResendInvitationLinkSpec extends ViewSpecSupport {

  val viewTemplate: ResendInvitationLink = app.injector.instanceOf[ResendInvitationLink]
  val testInvitationId: String = "AB1234567890"
  val testClientName: String = "Test Client"
  val agentName: String = "ABC Accountants"
  val urlParts: Map[String, String] = Map(
    "HMRC-MTD-IT" -> "income-tax",
    "HMRC-MTD-IT-SUPP" -> "income-tax",
    "HMRC-MTD-VAT" -> "vat",
    "HMRC-CGT-PD" -> "capital-gains-tax-uk-property",
    "HMRC-PPT-ORG" -> "plastic-packaging-tax",
    "HMRC-CBC-ORG" -> "country-by-country-reporting",
    "HMRC-PILLAR2-ORG" -> "pillar-2",
    "HMRC-TERS-ORG" -> "trusts-and-estates",
    "HMRC-TERSNT-ORG" -> "trusts-and-estates"
  )

  val confirmationData: AuthorisationRequestInfo = AuthorisationRequestInfo(
    invitationId = "someInvitationId",
    agentReference = "ABC123",
    normalizedAgentName = "abc-accountants",
    agentEmailAddress = "info@example.com",
    clientName = testClientName,
    service = "HMRC-MTD-IT",
    expiryDate = LocalDate.of(2025, 1, 1),
    suppliedClientId = "AB1234567890",
    clientType = "personal"
  )

  private def makeTestLink(service: String): String =
    s"${appConfig.baseUrl}/agent-client-relationships/appoint-someone-to-deal-with-HMRC-for-you/${confirmationData.agentReference}/${confirmationData.normalizedAgentName}/${urlParts(service)}"

  "ResendInvitationLink view" should {
    for (taxService <- urlParts.keySet.toList) {
      implicit val agentRequest: AgentRequest[?] = new AgentRequest("", request)
      val view: HtmlFormat.Appendable = viewTemplate(confirmationData.copy(service = taxService), makeTestLink(taxService))
      val doc: Document = Jsoup.parse(view.body)
      s"include the correct H1 text for $taxService" in {
        doc.mainContent.extractText("h1", 1).value shouldBe "Resend this link to your client"
      }
      s"include the correct client link text for $taxService" in {
        doc.mainContent.extractText(p, 2).value shouldBe makeTestLink(taxService)
      }
      s"have correct link to track requests when service is $taxService" in {
        val expectedUrl = "/agent-client-relationships/manage-authorisation-requests"
        doc.mainContent.extractLink(1).value shouldBe TestLink("Manage your recent authorisation requests", expectedUrl)
      }
      s"have correct link for returning to Agent Services Account home when service is $taxService" in {
        val expectedUrl = "http://localhost:9401/agent-services-account/home"
        doc.mainContent.extractLink(2).value shouldBe TestLink(s"Go to your agent services account homepage", expectedUrl)
      }
    }
  }
}
