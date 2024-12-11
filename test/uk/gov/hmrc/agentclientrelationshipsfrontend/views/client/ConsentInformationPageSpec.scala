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
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.client.ConsentInformationPage

import java.time.Instant
import scala.language.postfixOps

class ConsentInformationPageSpec extends ViewSpecSupport {

  val viewTemplate: ConsentInformationPage = app.injector.instanceOf[ConsentInformationPage]

  val agentName: String = "ABC Accountants"
  val invitationId: String = "AB1234567890"
  val serviceKey: String = "HMRC-MTD-IT"
  val status: String = "Pending"
  val lastModifiedDate: String = "2024-12-01T12:00:00Z"

  val taxServices: Map[String, String] = Map(
    "income-tax" -> "HMRC-MTD-IT",
    "income-record-viewer" -> "PERSONAL-INCOME-RECORD",
    "vat" -> "HMRC-MTD-VAT",
    "capital-gains-tax-uk-property" -> "HMRC-CGT-PD",
    "plastic-packaging-tax" -> "HMRC-PPT-ORG",
    "country-by-country-reporting" -> "HMRC-CBC-ORG",
    "pillar-2" -> "HMRC-PILLAR2-ORG",
    "trusts-and-estates" -> "HMRC-TERS-ORG",
  )

  // in the test we can align the same url part with other services according to the actual invitation contents
  val altTaxServices: Map[String, String] = Map(
    "income-tax" -> "HMRC-MTD-IT-SUPP",
    "trusts-and-estates" -> "HMRC-TERSNT-ORG",
  )

  object Expected {
    val heading = "Information you need before you authorise an agent"
    val title = s"$heading - Appoint someone to deal with HMRC for you - GOV.UK"
    val p1Main = s"$agentName want to be authorised as your main agent for Making Tax Digital for Income Tax."
    val p1Supporting = s"$agentName want to be authorised as your supporting agent for Making Tax Digital for Income Tax."
    def p1(serviceKey: String) = s"$agentName want to be authorised as your agent for ${taxServiceNames(serviceKey)}."
    val p2 = s"When you have an agent for Making Tax Digital for Income Tax, you can choose how much support you want from them."
    def section1p1(serviceKey: String) = s"Giving your consent means employees of $agentName will be able to access your ${taxServiceNames(serviceKey)} data."
  }

  val taxServiceNames: Map[String, String] = Map(
    "PERSONAL-INCOME-RECORD" -> "Income Record Viewer",
    "HMRC-MTD-IT" -> "Making Tax Digital for Income Tax",
    "HMRC-MTD-IT-SUPP" -> "Making Tax Digital for Income Tax",
    "HMRC-PPT-ORG" -> "Plastic Packaging Tax",
    "HMRC-CGT-PD" -> "Capital Gains Tax on UK property account",
    "HMRC-CBC-ORG" -> "Country-by-country reports",
    "HMRC-CBC-NONUK-ORG" -> "Country-by-country reports",
    "HMRC-MTD-VAT" -> "VAT",
    "HMRC-PILLAR2-ORG" -> "Pillar 2 Top-up Taxes",
    "HMRC-TERS-ORG" -> "Trusts and Estates",
    "HMRC-TERSNT-ORG" -> "Trusts and Estates"
  )

  val journey: ClientJourney = ClientJourney(
    journeyType = JourneyType.ClientResponse
  )

  def journeyForService(serviceKey: String): ClientJourney = journey.copy(
    serviceKey = Some(serviceKey),
    invitationId = Some(invitationId),
    agentName = Some(agentName),
    status = Some(Pending),
    lastModifiedDate = Some(Instant.parse(lastModifiedDate))
  )

  "ConsentInformation view" should {
    implicit val journeyRequest: ClientJourneyRequest[?] = new ClientJourneyRequest(journey, request)

    List(taxServices, altTaxServices).foreach(services =>
      for (taxService <- services.keySet.toList) {
        val serviceKey: String = services(taxService)
        val view: HtmlFormat.Appendable = viewTemplate(journeyForService(serviceKey))
        val doc: Document = Jsoup.parse(view.body)
        s"include the correct title for $serviceKey" in {
          doc.title() shouldBe Expected.title
        }

        s"include the correct H1 text for $serviceKey" in {
          doc.mainContent.extractText(h1, 1).value shouldBe Expected.heading
        }

        s"include the correct p1 text for $serviceKey" in {
          val expectedP1 = serviceKey match {
            case "HMRC-MTD-IT" => Expected.p1Main
            case "HMRC-MTD-IT-SUPP" => Expected.p1Supporting
            case _ => Expected.p1(serviceKey)
          }
          doc.mainContent.extractText(p, 1).value shouldBe expectedP1
        }

        s"include the correct p2 text for $serviceKey" in {
          val expectedP2 = if (taxService == "income-tax") Expected.p2 else Expected.section1p1(serviceKey)
          doc.mainContent.extractText(p, 2).value shouldBe expectedP2
        }

        s"should include a continue button for $serviceKey" in {
          doc.mainContent.extractText(button, 1).value shouldBe "Continue"
        }
      }
    )
  }
}
