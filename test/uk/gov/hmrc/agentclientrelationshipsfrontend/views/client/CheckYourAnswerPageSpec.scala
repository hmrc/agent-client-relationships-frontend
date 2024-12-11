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
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{ClientJourney, ClientJourneyRequest, JourneyType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.client.CheckYourAnswerPage

import java.time.Instant


class CheckYourAnswerPageSpec extends ViewSpecSupport {

  val agentName: String = "ABC Accountants"
  val invitationId: String = "AB1234567890"
  val serviceKey: String = "HMRC-MTD-IT"
  val status: String = "Pending"
  val lastModifiedDate: String = "2024-12-01T12:00:00Z"

  val journey: ClientJourney = ClientJourney(
    journeyType = JourneyType.ClientResponse
  )

  def journeyForService(serviceKey: String, choice: Boolean): ClientJourney = journey.copy(
    consent = Some(choice),
    serviceKey = Some(serviceKey),
    invitationId = Some(invitationId),
    agentName = Some(agentName),
    status = Some(Pending),
    lastModifiedDate = Some(Instant.parse(lastModifiedDate))
  )
  val viewTemplate: CheckYourAnswerPage = app.injector.instanceOf[CheckYourAnswerPage]
  val taxServiceNames: Map[String, String] = Map(
    "HMRC-MTD-IT" -> "Making Tax Digital for Income Tax",
    "HMRC-MTD-IT-SUPP" -> "Making Tax Digital for Income Tax",
    "PERSONAL-INCOME-RECORD" -> "Income Record Viewer",
    "HMRC-MTD-VAT" -> "VAT",
    "HMRC-CGT-PD" -> "Capital Gains Tax on UK property account",
    "HMRC-PPT-ORG" -> "Plastic Packaging Tax",
    "HMRC-CBC-ORG" -> "Country-by-country reporting",
    "HMRC-PILLAR2-ORG" -> "Pillar 2 Top-up Taxes",
    "HMRC-TERS-ORG" -> "Trusts and Estates"
  )


  object Expected {
    val pageTitle = "Check your answer - Appoint someone to deal with HMRC for you - GOV.UK"
    val h2Content = "Declaration"
    val buttonContent = "Accept and send"
    val declarationNoP2 = "You can change your mind later - just send them another request."
  }

  def questionSummaryList(taxService: String): String = taxService match {
    case "HMRC-MTD-IT" => s"Do you want $agentName to be your main agent for ${taxServiceNames(taxService)}?"
    case "HMRC-MTD-IT-SUPP" => s"Do you want $agentName to be your supporting agent for ${taxServiceNames(taxService)}?"
    case "PERSONAL-INCOME-RECORD" => s"Do you want $agentName to be your agent for ${taxServiceNames(taxService)}?"
    case "HMRC-MTD-VAT" => s"Do you want $agentName to be your agent for ${taxServiceNames(taxService)}?"
    case "HMRC-CGT-PD" => s"Do you want $agentName to be your agent for ${taxServiceNames(taxService)}?"
    case "HMRC-PPT-ORG" => s"Do you want $agentName to be your agent for ${taxServiceNames(taxService)}?"
    case "HMRC-CBC-ORG" => s"Do you want $agentName to be your agent for ${taxServiceNames(taxService)}?"
    case "HMRC-PILLAR2-ORG" => s"Do you want $agentName to be your agent for ${taxServiceNames(taxService)}?"
    case "HMRC-TERS-ORG" => s"Do you want $agentName to be your agent for ${taxServiceNames(taxService)}?"
  }

  def expectedDeclarationYesP1(taxService: String): String = taxService match {
    case "HMRC-MTD-IT" => s"I understand how $agentName will be able to act on my behalf. I confirm that HMRC can allow $agentName to act as my main agent for ${taxServiceNames(taxService)}."
    case "HMRC-MTD-IT-SUPP" => s"I understand how $agentName will be able to act on my behalf. I confirm that HMRC can allow $agentName to act as my supporting agent for ${taxServiceNames(taxService)}."
    case "PERSONAL-INCOME-RECORD" => s"I understand how $agentName will be able to act on my behalf. I confirm that HMRC can allow $agentName to act as my agent for ${taxServiceNames(taxService)}."
    case "HMRC-MTD-VAT" => s"I understand how $agentName will be able to act on my behalf. I confirm that HMRC can allow $agentName to act as my agent for ${taxServiceNames(taxService)}."
    case "HMRC-CGT-PD" => s"I understand how $agentName will be able to act on my behalf. I confirm that HMRC can allow $agentName to act as my agent for ${taxServiceNames(taxService)}."
    case "HMRC-PPT-ORG" => s"I understand how $agentName will be able to act on my behalf. I confirm that HMRC can allow $agentName to act as my agent for ${taxServiceNames(taxService)}."
    case "HMRC-CBC-ORG" => s"I understand how $agentName will be able to act on my behalf. I confirm that HMRC can allow $agentName to act as my agent for ${taxServiceNames(taxService)}."
    case "HMRC-PILLAR2-ORG" => s"I understand how $agentName will be able to act on my behalf. I confirm that HMRC can allow $agentName to act as my agent for ${taxServiceNames(taxService)}."
    case "HMRC-TERS-ORG" => s"I understand how $agentName will be able to act on my behalf. I confirm that HMRC can allow $agentName to act as my agent for ${taxServiceNames(taxService)}."
  }

  def expectedDeclarationNoP1(taxService: String): String = taxService match {
    case "HMRC-MTD-IT" => s"Declining this request from $agentName means they will not be able to act as your main agent for ${taxServiceNames(taxService)}."
    case "HMRC-MTD-IT-SUPP" => s"Declining this request from $agentName means they will not be able to act as your supporting agent for ${taxServiceNames(taxService)}."
    case "PERSONAL-INCOME-RECORD" => s"Declining this request from $agentName means they will not be able to act as your agent for ${taxServiceNames(taxService)}."
    case "HMRC-MTD-VAT" => s"Declining this request from $agentName means they will not be able to act as your agent for ${taxServiceNames(taxService)}."
    case "HMRC-CGT-PD" => s"Declining this request from $agentName means they will not be able to act as your agent for ${taxServiceNames(taxService)}."
    case "HMRC-PPT-ORG" => s"Declining this request from $agentName means they will not be able to act as your agent for ${taxServiceNames(taxService)}."
    case "HMRC-CBC-ORG" => s"Declining this request from $agentName means they will not be able to act as your agent for ${taxServiceNames(taxService)}."
    case "HMRC-PILLAR2-ORG" => s"Declining this request from $agentName means they will not be able to act as your agent for ${taxServiceNames(taxService)}."
    case "HMRC-TERS-ORG" => s"Declining this request from $agentName means they will not be able to act as your agent for ${taxServiceNames(taxService)}."
  }


  "CheckYourAnswerPage for authorisation journey view" should {

    for (choice <- Seq("Yes", "No")) {
      taxServiceNames.keySet.foreach {
        taxService =>
          implicit val journeyRequest: ClientJourneyRequest[?] = new ClientJourneyRequest(journeyForService(
            serviceKey = taxService,
            choice = choice == "Yes"
          ), request)
          val view: HtmlFormat.Appendable = viewTemplate()
          val doc: Document = Jsoup.parse(view.body)

          s"have the correct page title when choice is $choice for $taxService" in {
            doc.title() shouldBe Expected.pageTitle
          }

          s"render a summaryList when choice is $choice for $taxService" in {
            doc.mainContent.extractSummaryList() shouldBe Some(TestSummaryList(
              rows = List((
                questionSummaryList(taxService),
                choice,
                s"/agent-client-relationships/authorisation-response/confirm-consent/$invitationId"
              ))
            ))
          }

          s"have the correct h2 for declaration when choice is $choice for $taxService" in {
            doc.mainContent.extractText(h2, 1).value shouldBe Expected.h2Content
          }

          s"have the correct declaration content when choice is $choice for $taxService" in {
            val expectedDeclaration = if choice == "Yes" then expectedDeclarationYesP1(taxService) else expectedDeclarationNoP1(taxService)
            doc.mainContent.extractText(p, 1).value shouldBe expectedDeclaration
          }

          s"have the correct declaration content for p2 when choice is $choice for $taxService" in {
            if choice == "No" then
              doc.mainContent.extractText(p, 2).value shouldBe Expected.declarationNoP2
          }

          s"contain a acceptAndSend button with the correct text when choice is $choice for $taxService" in {
            doc.mainContent.extractText(button, 1).value shouldBe Expected.buttonContent
          }
      }
    }
  }
}
