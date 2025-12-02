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
  val newAgentName:String  = "ABC Accountants New"
  val existingAgent: ExistingMainAgent = ExistingMainAgent(agentName, false)
  val invitationId: String = "AB1234567890"
  val lastModifiedDate: String = "2024-12-01T12:00:00Z"


  val taxServices:Set[String] = Set(
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

  object ExpectedItsa {
    def heading(serviceKey: String, agentName:String) = s"$agentName want to be your ${indefiniteAgentRole(serviceKey)}"
    def title(serviceKey: String, agentName:String) = s"${heading(serviceKey, agentName)} - Appoint someone to deal with HMRC for you - GOV.UK"
    def section1p1(serviceKey: String) = s"An agent can either be ‘main agent’ or a ‘supporting agent’ when they manage your ${taxServiceNames(serviceKey)}."
    def section2p1(serviceKey: String, agentName:String) = s"Giving consent means employees of $agentName will be able to access your ${taxServiceNames(serviceKey)} data."
  }

  object ExpectedItsaChangeAgent {
    def heading(serviceKey: String, agentName: String) = s"$agentName want to be your ${indefiniteAgentRole(serviceKey)}"

    def title(serviceKey: String, agentName: String) = s"${heading(serviceKey, agentName)} - Appoint someone to deal with HMRC for you - GOV.UK"

    def section1p1(serviceKey: String) = s"An agent can either be ‘main agent’ or a ‘supporting agent’ when they manage your ${taxServiceNames(serviceKey)}."

    def section1p2(serviceKey: String, agentName: String, newAgentName: String) = s"If you authorise $newAgentName, we will remove $agentName as your existing main agent."
    
    def section2p1(serviceKey: String, agentName: String) = s"Giving consent means employees of $agentName will be able to access your ${taxServiceNames(serviceKey)} data."
  }

  object Expected {
    def heading(serviceKey: String) = s"$agentName want to be your ${indefiniteAgentRole(serviceKey)}"

    def title(serviceKey: String) = s"${heading(serviceKey)} - Appoint someone to deal with HMRC for you - GOV.UK"

    def section2p1(serviceKey: String) = s"Giving consent means employees of $agentName will be able to access your ${taxServiceNames(serviceKey)} data."
  }

  object ExpectedChangeAgent {
    def heading(serviceKey: String, agentName: String) = s"$agentName want to be your ${indefiniteAgentRole(serviceKey)}"

    def title(serviceKey: String, agentName: String) = s"${heading(serviceKey, agentName)} - Appoint someone to deal with HMRC for you - GOV.UK"

    def section1p1(serviceKey: String, agentName: String, newAgentName: String) = s"If you authorise $newAgentName, we will remove $agentName as your existing agent."

    def section2p1(serviceKey: String, agentName: String) = s"Giving consent means employees of $agentName will be able to access your ${taxServiceNames(serviceKey)} data."
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
    "HMRC-CBC-ORG" -> "Country-by-country reporting",
    "HMRC-CBC-NONUK-ORG" -> "Country-by-country reporting",
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
    agentName = Some(agentName),
    status = Some(Pending),
    lastModifiedDate = Some(Instant.parse(lastModifiedDate))
  )


  "ConsentInformation view ITSA no agent change" should {
    implicit val journeyRequest: ClientJourneyRequest[?] = new ClientJourneyRequest(journey, request)

    itsaServices.foreach(serviceKey =>
        val view: HtmlFormat.Appendable = viewTemplate(journeyForService(serviceKey), agentRole(serviceKey))
        val doc: Document = Jsoup.parse(view.body)
        s"include the correct title for $serviceKey" in {
          doc.title() shouldBe ExpectedItsa.title(serviceKey, agentName)
        }

        s"include the correct H1 text for $serviceKey" in {
          doc.mainContent.extractText(h1, 1).value shouldBe ExpectedItsa.heading(serviceKey, agentName)
        }

        s"include the correct p1 text for $serviceKey" in {
          doc.mainContent.extractText(p, 1).value shouldBe ExpectedItsa.section1p1(serviceKey)
        }

        s"include the correct p4 text for $serviceKey" in {
          doc.mainContent.extractText(p, 4).value shouldBe ExpectedItsa.section2p1(serviceKey, agentName)
        }

        s"should include a continue button for $serviceKey" in {
          doc.mainContent.extractText(button, 1).value shouldBe "Continue"
        }
      
    )
  }


  "ConsentInformation view HMRC-MTD-IT change agent to HMRC-MTD-IT" should {
    implicit val journeyRequest: ClientJourneyRequest[?] = new ClientJourneyRequest(journey, request)

    val serviceKey = "HMRC-MTD-IT" 
    val view: HtmlFormat.Appendable = viewTemplate(
      journeyForService(serviceKey).copy(
        existingMainAgent = Some(existingAgent),
        agentName = Some(newAgentName)), 
      agentRole(serviceKey))
    val doc: Document = Jsoup.parse(view.body)
    s"include the correct title for $serviceKey" in {
      doc.title() shouldBe ExpectedItsaChangeAgent.title(serviceKey, newAgentName)
    }

    s"include the correct H1 text for $serviceKey" in {
      doc.mainContent.extractText(h1, 1).value shouldBe ExpectedItsaChangeAgent.heading(serviceKey, newAgentName)
    }

    s"include the correct p1 text for $serviceKey" in {
      doc.mainContent.extractText(p, 1).value shouldBe ExpectedItsaChangeAgent.section1p1(serviceKey)
    }

    s"include the correct p3 text for $serviceKey" in {
      doc.mainContent.extractText(p, 3).value shouldBe ExpectedItsaChangeAgent.section1p2(serviceKey, agentName, newAgentName)
    }


    s"include the correct p5 text for $serviceKey" in {
      doc.mainContent.extractText(p, 5).value shouldBe ExpectedItsaChangeAgent.section2p1(serviceKey, newAgentName)
    }

  }

  "ConsentInformation view HMRC-MTD-IT change agent to HMRC-MTD-IT-SUPP" should {
    implicit val journeyRequest: ClientJourneyRequest[?] = new ClientJourneyRequest(journey, request)

    val serviceKey = "HMRC-MTD-IT-SUPP"
    val view: HtmlFormat.Appendable = viewTemplate(
      journeyForService(serviceKey).copy(
        existingMainAgent = Some(existingAgent),
        agentName = Some(newAgentName)),
      agentRole(serviceKey))
    val doc: Document = Jsoup.parse(view.body)
    s"include the correct title for $serviceKey" in {
      doc.title() shouldBe ExpectedItsaChangeAgent.title(serviceKey, newAgentName)
    }

    s"include the correct H1 text for $serviceKey" in {
      doc.mainContent.extractText(h1, 1).value shouldBe ExpectedItsaChangeAgent.heading(serviceKey, newAgentName)
    }

    s"include the correct p1 text for $serviceKey" in {
      doc.mainContent.extractText(p, 1).value shouldBe ExpectedItsaChangeAgent.section1p1(serviceKey)
    }

    s"include the correct p4 text for $serviceKey" in {
      doc.mainContent.extractText(p, 4).value shouldBe ExpectedItsaChangeAgent.section2p1(serviceKey, newAgentName)
    }

    s"should include a continue button for $serviceKey" in {
      doc.mainContent.extractText(button, 1).value shouldBe "Continue"
    }
  }


  "ConsentInformation view Not ITSA" should {
    implicit val journeyRequest: ClientJourneyRequest[?] = new ClientJourneyRequest(journey, request)

    taxServices.foreach(serviceKey =>
      val view: HtmlFormat.Appendable = viewTemplate(journeyForService(serviceKey), agentRole(serviceKey))
      val doc: Document = Jsoup.parse(view.body)
      s"include the correct title for $serviceKey" in {
        doc.title() shouldBe Expected.title(serviceKey)
      }

      s"include the correct H1 text for $serviceKey" in {
        doc.mainContent.extractText(h1, 1).value shouldBe Expected.heading(serviceKey)
      }

      s"include the correct p1 text for $serviceKey" in {
        doc.mainContent.extractText(p, 1).value shouldBe Expected.section2p1(serviceKey)
      }
      
      s"should include a continue button for $serviceKey" in {
        doc.mainContent.extractText(button, 1).value shouldBe "Continue"
      }

    )
  }

  "ConsentInformation view Not ITSA agent change" should {
    implicit val journeyRequest: ClientJourneyRequest[?] = new ClientJourneyRequest(journey, request)

    taxServices.foreach(serviceKey =>
      val view: HtmlFormat.Appendable = viewTemplate( journeyForService(serviceKey).copy(
        existingMainAgent = Some(existingAgent),
        agentName = Some(newAgentName)),
        agentRole(serviceKey))

      val doc: Document = Jsoup.parse(view.body)
      s"include the correct title for $serviceKey" in {
        doc.title() shouldBe ExpectedChangeAgent.title(serviceKey,newAgentName)
      }

      s"include the correct H1 text for $serviceKey" in {
        doc.mainContent.extractText(h1, 1).value shouldBe ExpectedChangeAgent.heading(serviceKey, newAgentName)
      }

      s"include the correct p1 text for $serviceKey" in {
        doc.mainContent.extractText(p, 1).value shouldBe ExpectedChangeAgent.section1p1(serviceKey, agentName, newAgentName)
      }

      s"include the correct p2 text for $serviceKey" in {
        doc.mainContent.extractText(p, 2).value shouldBe ExpectedItsaChangeAgent.section2p1(serviceKey, newAgentName)
      }

      s"should include a continue button for $serviceKey" in {
        doc.mainContent.extractText(button, 1).value shouldBe "Continue"
      }

    )
  }
}
