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

package uk.gov.hmrc.agentclientrelationshipsfrontend.views.journey

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.journey.SelectFromOptionsForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.JourneyType.{AgentCancelAuthorisation, AuthorisationRequest}
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.journey.SelectClientServicePage

class SelectClientServicePageSpec extends ViewSpecSupport {

  val viewTemplate: SelectClientServicePage = app.injector.instanceOf[SelectClientServicePage]

  object Expected {
    val authorisationRequestHeading = "What do you want the client to authorise you to do?"
    val cancelAuthorisationHeading = "Which authorisation do you want to cancel for the client?"
    val authorisationRequestTitle = s"$authorisationRequestHeading - Ask a client to authorise you - GOV.UK"
    val cancelAuthorisationTitle = s"$cancelAuthorisationHeading - Cancel a client’s authorisation - GOV.UK"
    val formHint = "You need to create a separate request for each tax service."
    val detailsHeading = "I need authorisation for something else"
    val detailsContent = "Find out more about asking a client to authorise you to deal with other tax services (opens in new tab)."
    val detailsLinkContent = "asking a client to authorise you to deal with other tax services (opens in new tab)"
    val authorisationRequestFormError = "Select what you want the client to authorise you to do"
    val cancelAuthorisationFormError = "Select which authorisation you want to cancel for the client"
    val personalIncomeRecordLabel = "View their Income record"
    val incomeTaxPersonalLabel = "Manage their Making Tax Digital for Income Tax"
    val pptPersonalLabel = "Manage their Plastic Packaging Tax"
    val pptBusinessLabel = "Manage their Plastic Packaging Tax"
    val pptTrustLabel = "Manage their trust’s Plastic Packaging Tax"
    val cgtPersonalLabel = "Manage their Capital Gains Tax on UK property account"
    val cgtTrustLabel = "Manage their trust’s Capital Gains Tax on UK property account"
    val vatLabel = "Manage their VAT"
    val pillar2Label = "Manage their Pillar 2 top-up taxes"
    val trustNtLabel = "Maintain their non-taxable trust"
    val trustLabel = "Maintain their trust or an estate"
    val cbcTrustLabel = "Manage their trust’s country-by-country reports"
    val cbcLabel = "Manage their country-by-country reports"
    val buttonContent = "Continue"
  }

  val optionsForPersonal: Seq[String] = Seq("HMRC-MTD-IT", "PERSONAL-INCOME-RECORD", "HMRC-MTD-VAT", "HMRC-CGT-PD", "HMRC-PPT-ORG")
  val optionsForBusiness: Seq[String] = Seq("HMRC-MTD-VAT", "HMRC-PPT-ORG", "HMRC-CBC-ORG", "HMRC-PILLAR2-ORG")
  val optionsForTrust: Seq[String] = Seq("HMRC-TERS-ORG", "HMRC-PPT-ORG", "HMRC-CGT-PD", "HMRC-CBC-ORG", "HMRC-PILLAR2-ORG")

  "SelectClientServicePage for authorisation request view" should {
    implicit val journeyRequest: AgentJourneyRequest[?] =
      new AgentJourneyRequest("", Journey(journeyType = AuthorisationRequest), request)
    val formForPersonal: Form[String] = SelectFromOptionsForm.form("clientService", optionsForPersonal, AuthorisationRequest.toString)
    val view: HtmlFormat.Appendable = viewTemplate(formForPersonal, "personal", optionsForPersonal)
    val doc: Document = Jsoup.parse(view.body)

    "have the right title" in {
      doc.title() shouldBe Expected.authorisationRequestTitle
    }

    "have the right heading" in {
      doc.mainContent.extractText(h1, 1).value shouldBe Expected.authorisationRequestHeading
    }

    "render a radio button for each option" when {

      "clientType is personal" in {
        val expectedRadioGroup: TestRadioGroup = TestRadioGroup(
          Expected.authorisationRequestHeading,
          List(
            Expected.incomeTaxPersonalLabel -> "HMRC-MTD-IT",
            Expected.personalIncomeRecordLabel -> "PERSONAL-INCOME-RECORD",
            Expected.vatLabel -> "HMRC-MTD-VAT",
            Expected.cgtPersonalLabel -> "HMRC-CGT-PD",
            Expected.pptPersonalLabel -> "HMRC-PPT-ORG"
          ),
          Some(Expected.formHint)
        )
        doc.mainContent.extractRadios().value shouldBe expectedRadioGroup
      }

      "clientType is business" in {

        val formForBusiness: Form[String] = SelectFromOptionsForm.form("clientService", optionsForBusiness, AuthorisationRequest.toString)
        val viewForBusiness: HtmlFormat.Appendable = viewTemplate(formForBusiness, "business", optionsForBusiness)
        val docForBusiness: Document = Jsoup.parse(viewForBusiness.body)

        val expectedRadioGroup: TestRadioGroup = TestRadioGroup(
          Expected.authorisationRequestHeading,
          List(
            Expected.vatLabel -> "HMRC-MTD-VAT",
            Expected.pptBusinessLabel-> "HMRC-PPT-ORG",
            Expected.cbcLabel -> "HMRC-CBC-ORG",
            Expected.pillar2Label -> "HMRC-PILLAR2-ORG"
          ),
          Some(Expected.formHint)
        )
        docForBusiness.mainContent.extractRadios().value shouldBe expectedRadioGroup
      }

      "clientType is trust" in {

        val formForTrust: Form[String] = SelectFromOptionsForm.form("clientService", optionsForTrust, AuthorisationRequest.toString)
        val viewForTrust: HtmlFormat.Appendable = viewTemplate(formForTrust, "trust", optionsForTrust)
        val docForTrust: Document = Jsoup.parse(viewForTrust.body)

        val expectedRadioGroup: TestRadioGroup = TestRadioGroup(
          Expected.authorisationRequestHeading,
          List(
            Expected.trustLabel -> "HMRC-TERS-ORG",
            Expected.pptTrustLabel -> "HMRC-PPT-ORG",
            Expected.cgtTrustLabel -> "HMRC-CGT-PD",
            Expected.cbcTrustLabel -> "HMRC-CBC-ORG",
            Expected.pillar2Label -> "HMRC-PILLAR2-ORG"
          ),
          Some(Expected.formHint)
        )
        docForTrust.mainContent.extractRadios().value shouldBe expectedRadioGroup
      }
    }

    "have a details component with the correct heading" in {
      doc.mainContent.extractText(".govuk-details__summary-text", 1).value shouldBe Expected.detailsHeading
    }

    "have a details component with the correct content" in {
      doc.mainContent.extractText(".govuk-details__text", 1).value shouldBe Expected.detailsContent
    }

    "have the correct link in the details component content" in {
      val expectedLink = TestLink(
        Expected.detailsLinkContent,
        "https://www.gov.uk/guidance/client-authorisation-an-overview#how-to-set-up-agent-authorisation"
      )
      doc.mainContent.extractLink("guidanceLink").value shouldBe expectedLink
    }

    "have a submission button" in {
      doc.mainContent.extractText(".govuk-button", 1).value shouldBe Expected.buttonContent
    }

    "render error for the correct journey" in {
      val formWithErrors = formForPersonal.bind(Map.empty)
      val viewWithErrors: HtmlFormat.Appendable = viewTemplate(formWithErrors, "personal", optionsForPersonal)
      val docWithErrors: Document = Jsoup.parse(viewWithErrors.body)
      docWithErrors.mainContent.extractText(errorSummaryList, 1).value shouldBe Expected.authorisationRequestFormError
    }
  }

  "SelectClientServicePage for cancel authorisation view" should {
    implicit val journeyRequest: AgentJourneyRequest[?] =
      new AgentJourneyRequest("", Journey(journeyType = AgentCancelAuthorisation), request)
    val formForPersonal: Form[String] = SelectFromOptionsForm.form("clientService", optionsForPersonal, AgentCancelAuthorisation.toString)
    val view: HtmlFormat.Appendable = viewTemplate(formForPersonal, "personal", optionsForPersonal)
    val doc: Document = Jsoup.parse(view.body)

    "have the right title" in {
      doc.title() shouldBe Expected.cancelAuthorisationTitle
    }

    "have the right heading" in {
      doc.mainContent.extractText(h1, 1).value shouldBe Expected.cancelAuthorisationHeading
    }

    "render a radio button for each option" when {

      "clientType is personal" in {
        val expectedRadioGroup: TestRadioGroup = TestRadioGroup(
          Expected.cancelAuthorisationHeading,
          List(
            Expected.incomeTaxPersonalLabel -> "HMRC-MTD-IT",
            Expected.personalIncomeRecordLabel -> "PERSONAL-INCOME-RECORD",
            Expected.vatLabel -> "HMRC-MTD-VAT",
            Expected.cgtPersonalLabel -> "HMRC-CGT-PD",
            Expected.pptPersonalLabel -> "HMRC-PPT-ORG"
          ),
          None
        )
        doc.mainContent.extractRadios().value shouldBe expectedRadioGroup
      }

      "clientType is business" in {

        val formForBusiness: Form[String] = SelectFromOptionsForm.form("clientService", optionsForBusiness, AgentCancelAuthorisation.toString)
        val viewForBusiness: HtmlFormat.Appendable = viewTemplate(formForBusiness, "business", optionsForBusiness)
        val docForBusiness: Document = Jsoup.parse(viewForBusiness.body)

        val expectedRadioGroup: TestRadioGroup = TestRadioGroup(
          Expected.cancelAuthorisationHeading,
          List(
            Expected.vatLabel -> "HMRC-MTD-VAT",
            Expected.pptBusinessLabel-> "HMRC-PPT-ORG",
            Expected.cbcLabel -> "HMRC-CBC-ORG",
            Expected.pillar2Label -> "HMRC-PILLAR2-ORG"
          ),
          None
        )
        docForBusiness.mainContent.extractRadios().value shouldBe expectedRadioGroup
      }

      "clientType is trust" in {

        val formForTrust: Form[String] = SelectFromOptionsForm.form("clientService", optionsForTrust, AgentCancelAuthorisation.toString)
        val viewForTrust: HtmlFormat.Appendable = viewTemplate(formForTrust, "trust", optionsForTrust)
        val docForTrust: Document = Jsoup.parse(viewForTrust.body)

        val expectedRadioGroup: TestRadioGroup = TestRadioGroup(
          Expected.cancelAuthorisationHeading,
          List(
            Expected.trustLabel -> "HMRC-TERS-ORG",
            Expected.pptTrustLabel -> "HMRC-PPT-ORG",
            Expected.cgtTrustLabel -> "HMRC-CGT-PD",
            Expected.cbcTrustLabel -> "HMRC-CBC-ORG",
            Expected.pillar2Label -> "HMRC-PILLAR2-ORG"
          ),
          None
        )
        docForTrust.mainContent.extractRadios().value shouldBe expectedRadioGroup
      }
    }

    "have a submission button" in {
      doc.mainContent.extractText(".govuk-button", 1).value shouldBe Expected.buttonContent
    }

    "render error for the correct journey" in {
      val formWithErrors = formForPersonal.bind(Map.empty)
      val viewWithErrors: HtmlFormat.Appendable = viewTemplate(formWithErrors, "personal", optionsForPersonal)
      val docWithErrors: Document = Jsoup.parse(viewWithErrors.body)
      docWithErrors.mainContent.extractText(errorSummaryList, 1).value shouldBe Expected.cancelAuthorisationFormError
    }
  }
}