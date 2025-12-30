/*
 * Copyright 2025 HM Revenue & Customs
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
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.client.DeclineRequestForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{ClientJourney, ClientJourneyRequest}
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.client.DeclineRequestPage

class DeclineRequestPageSpec extends ViewSpecSupport:

  val viewTemplate: DeclineRequestPage = app.injector.instanceOf[DeclineRequestPage]
  val newAgentName = "ABC Agents"
  val mainRole = "mainAgent"
  val suppRole = "suppAgent"
  val genericRole = "agent"
  val uid = "ABC123"

  val genericRoleServices: Seq[String] = Seq(
    "PERSONAL-INCOME-RECORD", "HMRC-MTD-VAT", "HMRC-CGT-PD",
    "HMRC-PPT-ORG", "HMRC-CBC-ORG", "HMRC-PILLAR2-ORG", "HMRC-TERS-ORG"
  )
  val mainRoleServices: Seq[String] = Seq("HMRC-MTD-IT")
  val suppRoleServices: Seq[String] = Seq("HMRC-MTD-IT-SUPP")

  val serviceKeyToNameMap: Map[String, String] = Map(
    "HMRC-MTD-IT" -> "income-tax",
    "HMRC-MTD-IT-SUPP" -> "income-tax",
    "PERSONAL-INCOME-RECORD" -> "income-record-viewer",
    "HMRC-MTD-VAT" -> "vat",
    "HMRC-CGT-PD" -> "capital-gains-tax-uk-property",
    "HMRC-PPT-ORG" -> "plastic-packaging-tax",
    "HMRC-CBC-ORG" -> "country-by-country-reporting",
    "HMRC-PILLAR2-ORG" -> "pillar-2",
    "HMRC-TERS-ORG" -> "trusts-and-estates"
  )

  val baseJourneyModel: ClientJourney = ClientJourney(
    "authorisation-response",
    agentName = Some(newAgentName)
  )

  object Expected:
    val title = "Decline a request - Appoint someone to deal with HMRC for you - GOV.UK"
    val heading = "Decline a request"
    def caption(service: String): String = messages(service).capitalize
    def introductionParagraph(role: String, service: String): String =
      s"$newAgentName want to be your ${messages(s"confirmDecline.$role")} for ${messages(service)}."
    val guidanceLink =
      "Find out how main and supporting agents can act for you (opens in new tab)."
    def ifYouDeclineParagraph(role: String, service: String): String =
      s"If you decline the request from $newAgentName, they will not be able to act as your " +
        s"${messages(s"confirmDecline.$role")} for ${messages(service)}."
    val changeYourMindParagraph = "You can change your mind later - just ask them to send you another request."
    val formLegend = s"Do you want to decline the request from $newAgentName?"
    val yesOption = "Yes"
    val noOption = "No, I want to consider this request"
    def formError(service: String): String = s"Error: Select ‘yes’ if you want to decline the request from $newAgentName"
    val continue = "Continue"

  "The Decline Request page" when:

    "the request is for a tax type that does not allow for main/supporting agents" should:

      genericRoleServices.foreach: service =>

        s"the tax service is $service" should:

          val form = DeclineRequestForm.form(newAgentName)
          implicit val journeyRequest: ClientJourneyRequest[?] =
            ClientJourneyRequest(baseJourneyModel.copy(serviceKey = Some(service)), request)
          val view = viewTemplate(form, genericRole, uid, serviceKeyToNameMap(service))
          val doc: Document = Jsoup.parse(view.body)

          "have the correct title" in:
            doc.title() shouldBe Expected.title

          "have the correct heading" in:
            doc.mainContent.extractText(h1, 1).value shouldBe Expected.heading

          "have the correct caption" in:
            doc.mainContent.extractText(h2, 1).value shouldBe Expected.caption(service)

          "have the correct first paragraph" in:
            doc.mainContent.extractText(p, 1).value shouldBe Expected.introductionParagraph(genericRole, service)

          "not have the agent role guidance paragraph" in:
            doc.mainContent.extractText("#agent-role-guidance", 1) shouldBe None

          "have the correct second paragraph" in:
            doc.mainContent.extractText(p, 2).value shouldBe Expected.ifYouDeclineParagraph(genericRole, service)

          "have the correct third paragraph" in:
            doc.mainContent.extractText(p, 3).value shouldBe Expected.changeYourMindParagraph

          "have the correct radio group form" in:
            val expectedRadioGroup = TestRadioGroup(
              Expected.formLegend,
              List(
                Expected.yesOption -> "true",
                Expected.noOption -> "false"
              ),
              None
            )
            doc.mainContent.extractRadios().value shouldBe expectedRadioGroup

          "have the correct button" in:
            doc.mainContent.extractText(".govuk-button", 1).value shouldBe Expected.continue

    "the request is for a main agent" should:

      mainRoleServices.foreach: service =>

        s"the tax service is $service" should :

          val form = DeclineRequestForm.form(newAgentName)
          implicit val journeyRequest: ClientJourneyRequest[?] =
            ClientJourneyRequest(baseJourneyModel.copy(serviceKey = Some(service)), request)
          val view = viewTemplate(form, mainRole, uid, serviceKeyToNameMap(service))
          val doc: Document = Jsoup.parse(view.body)

          "have the correct title" in :
            doc.title() shouldBe Expected.title

          "have the correct heading" in :
            doc.mainContent.extractText(h1, 1).value shouldBe Expected.heading

          "have the correct caption" in :
            doc.mainContent.extractText(h2, 1).value shouldBe Expected.caption(service)

          "have the correct first paragraph" in :
            doc.mainContent.extractText(p, 1).value shouldBe Expected.introductionParagraph(mainRole, service)

          "have the agent role guidance paragraph" in :
            doc.mainContent.extractText("#agent-role-guidance", 1).value shouldBe Expected.guidanceLink

          "have the correct third paragraph" in :
            doc.mainContent.extractText(p, 3).value shouldBe Expected.ifYouDeclineParagraph(mainRole, service)

          "have the correct fourth paragraph" in :
            doc.mainContent.extractText(p, 4).value shouldBe Expected.changeYourMindParagraph

          "have the correct radio group form" in :
            val expectedRadioGroup = TestRadioGroup(
              Expected.formLegend,
              List(
                Expected.yesOption -> "true",
                Expected.noOption -> "false"
              ),
              None
            )
            doc.mainContent.extractRadios().value shouldBe expectedRadioGroup

          "have the correct button" in :
            doc.mainContent.extractText(".govuk-button", 1).value shouldBe Expected.continue

    "the request is for a supporting agent" should :

      suppRoleServices.foreach: service =>

        s"the tax service is $service" should :

          val form = DeclineRequestForm.form(newAgentName)
          implicit val journeyRequest: ClientJourneyRequest[?] =
            ClientJourneyRequest(baseJourneyModel.copy(serviceKey = Some(service)), request)
          val view = viewTemplate(form, suppRole, uid, serviceKeyToNameMap(service))
          val doc: Document = Jsoup.parse(view.body)

          "have the correct title" in :
            doc.title() shouldBe Expected.title

          "have the correct heading" in :
            doc.mainContent.extractText(h1, 1).value shouldBe Expected.heading

          "have the correct caption" in :
            doc.mainContent.extractText(h2, 1).value shouldBe Expected.caption(service)

          "have the correct first paragraph" in :
            doc.mainContent.extractText(p, 1).value shouldBe Expected.introductionParagraph(suppRole, service)

          "have the agent role guidance paragraph" in :
            doc.mainContent.extractText("#agent-role-guidance", 1).value shouldBe Expected.guidanceLink

          "have the correct third paragraph" in :
            doc.mainContent.extractText(p, 3).value shouldBe Expected.ifYouDeclineParagraph(suppRole, service)

          "have the correct fourth paragraph" in :
            doc.mainContent.extractText(p, 4).value shouldBe Expected.changeYourMindParagraph

          "have the correct radio group form" in :
            val expectedRadioGroup = TestRadioGroup(
              Expected.formLegend,
              List(
                Expected.yesOption -> "true",
                Expected.noOption -> "false"
              ),
              None
            )
            doc.mainContent.extractRadios().value shouldBe expectedRadioGroup

          "have the correct button" in :
            doc.mainContent.extractText(".govuk-button", 1).value shouldBe Expected.continue

    "there are errors in the form" should:

      val form = DeclineRequestForm.form(newAgentName).bind(Map())
      implicit val journeyRequest: ClientJourneyRequest[?] =
        ClientJourneyRequest(baseJourneyModel.copy(serviceKey = Some("HMRC-MTD-VAT")), request)
      val view = viewTemplate(form, genericRole, uid, serviceKeyToNameMap("HMRC-MTD-VAT"))
      val doc: Document = Jsoup.parse(view.body)

      "have the correct error message" in:
        doc.mainContent.extractText(".govuk-error-message", 1).value shouldBe Expected.formError(newAgentName)