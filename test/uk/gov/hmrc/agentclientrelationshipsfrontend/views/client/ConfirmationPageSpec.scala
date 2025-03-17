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
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.AuthorisationRequestInfoForClient
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.{Accepted, PartialAuth, Rejected}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{ClientJourney, ClientJourneyRequest}
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.client.ConfirmationPage

import scala.language.postfixOps

class ConfirmationPageSpec extends ViewSpecSupport {

  val viewTemplate: ConfirmationPage = app.injector.instanceOf[ConfirmationPage]
  val testInvitationId: String = "AB1234567890"
  val testClientName: String = "Test Client"
  val agentName: String = "ABC Accountants"
  val serviceLabels: Map[String, String] = Map(
    "HMRC-MTD-IT" -> "Making Tax Digital for Income Tax",
    "PERSONAL-INCOME-RECORD" -> "Income Record Viewer",
    "HMRC-MTD-VAT" -> "VAT",
    "HMRC-CGT-PD" -> "Capital Gains Tax on UK property account",
    "HMRC-PPT-ORG" -> "Plastic Packaging Tax",
    "HMRC-CBC-ORG" -> "Country-by-country reporting",
    "HMRC-PILLAR2-ORG" -> "Pillar 2 Top-up Taxes",
    "HMRC-TERS-ORG" -> "Trusts and Estates",
    "HMRC-TERSNT-ORG" -> "Trusts and Estates"
  )
  val rejectedServiceLabels: Map[String, String] = Map(
    "HMRC-MTD-IT" -> "manage your Making Tax Digital for Income Tax",
    "PERSONAL-INCOME-RECORD" -> "view your personal income record",
    "HMRC-MTD-VAT" -> "manage your VAT",
    "HMRC-CGT-PD" -> "manage your Capital Gains Tax on UK property account",
    "HMRC-PPT-ORG" -> "manage your Plastic Packaging Tax",
    "HMRC-CBC-ORG" -> "manage your Country-by-country reporting",
    "HMRC-PILLAR2-ORG" -> "manage your Pillar 2 Top-up Taxes",
    "HMRC-TERS-ORG" -> "manage your Trusts and Estates",
    "HMRC-TERSNT-ORG" -> "manage your Trusts and Estates"
  )

  val confirmationData: AuthorisationRequestInfoForClient = AuthorisationRequestInfoForClient(
    agentName = agentName,
    service = "HMRC-MTD-IT",
    status = Rejected
  )

  private val completeJourney: ClientJourney = ClientJourney(
    "authorisation-response",
    journeyComplete = Some("invitationId")
  )

  private def agentRoleForService(service: String) = service match {
    case "HMRC-MTD-IT" => s"main agent for ${serviceLabels(service)}"
    case "HMRC-MTD-IT-SUPP" => s"supporting agent for ${serviceLabels(service)}"
    case _ => s"agent for ${serviceLabels(service)}"
  }

  "ConfirmationPage view for accepting an invitation" should {
    List(Accepted, PartialAuth, Rejected).foreach(decision =>
      for (taxService <- serviceLabels.keySet.toList) {
        implicit val journeyRequest: ClientJourneyRequest[?] = new ClientJourneyRequest(completeJourney, request)
        val view: HtmlFormat.Appendable = viewTemplate(confirmationData.copy(service = taxService, status = decision))
        val doc: Document = Jsoup.parse(view.body)
        s"include the correct h1 text for ${decision.toString} $taxService" in {
          val expectedHeading = decision match {
            case Accepted => s"You have authorised $agentName"
            case PartialAuth => s"You have authorised $agentName"
            case _ => s"You declined a request from $agentName"
          }
          doc.mainContent.extractText("h1.govuk-panel__title", 1).value shouldBe expectedHeading
        }
        s"include the correct p1 text for ${decision.toString} $taxService" in {
          val expectedContent = decision match {
            case Accepted => s"$agentName is now your ${agentRoleForService(taxService)}."
            case PartialAuth => s"$agentName is now your ${agentRoleForService(taxService)}."
            case _ => s"You have not given permission for $agentName to ${rejectedServiceLabels(taxService)}."
          }
          doc.mainContent.extractText(p, 1).value shouldBe expectedContent
        }
        s"include the correct p2 text for ${decision.toString} $taxService" in {
          val expectedContent = decision match {
            case Accepted => s"We’ll send an email to $agentName to update them."
            case PartialAuth => s"We’ll send an email to $agentName to update them."
            case _ => s"We’ll send an email to $agentName to update them."
          }
          doc.mainContent.extractText(p, 2).value shouldBe expectedContent
        }
      }
    )
  }

}
