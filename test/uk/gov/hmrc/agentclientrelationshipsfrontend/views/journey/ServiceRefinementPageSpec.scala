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
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.journey.ServiceRefinementPage

class ServiceRefinementPageSpec extends ViewSpecSupport {

  val viewTemplate: ServiceRefinementPage = app.injector.instanceOf[ServiceRefinementPage]

  object Expected {
    val authorisationRequestHeading = "Is your client’s trust or estate taxable?"
    val cancelAuthorisationHeading = "Is your client’s trust or estate taxable?"
    val authorisationRequestTitle = s"$authorisationRequestHeading - Ask a client to authorise you - GOV.UK"
    val cancelAuthorisationTitle = s"$cancelAuthorisationHeading - Cancel a client’s authorisation - GOV.UK"
    val authorisationRequestFormError = "Select what you want the client to authorise you to do"
    val cancelAuthorisationFormError = "Select which authorisation you want to cancel for the client"
    val radioLabel1 = "Yes"
    val radioLabel2 = "No, it’s exempt from tax"

    val buttonContent = "Continue"
  }

  val optionsForTrust: Seq[String] = Seq("HMRC-TERS-ORG", "HMRC-TERSNT-ORG")

  "ServiceRefinementPage for authorisation request view" should {
    implicit val journeyRequest: AgentJourneyRequest[?] =
      new AgentJourneyRequest("", AgentJourney(journeyType = AuthorisationRequest, clientType = Some("trust"), clientService = Some("HMRC-TERS-ORG")), request)
    val refinementForm: Form[String] = SelectFromOptionsForm.form("clientService", optionsForTrust, AuthorisationRequest.toString)
    val view: HtmlFormat.Appendable = viewTemplate(refinementForm, optionsForTrust)
    val doc: Document = Jsoup.parse(view.body)

    "have the right title" in {
      doc.title() shouldBe Expected.authorisationRequestTitle
    }

    "have the right heading" in {
      doc.mainContent.extractText(h1, 1).value shouldBe Expected.authorisationRequestHeading
    }

    "render a radio button for each option" when {

      "clientService is HMRC-TERS-ORG" in {
        val expectedRadioGroup: TestRadioGroup = TestRadioGroup(
          Expected.authorisationRequestHeading,
          List(
            Expected.radioLabel1 -> "HMRC-TERS-ORG",
            Expected.radioLabel2 -> "HMRC-TERSNT-ORG"
          ),
          None
        )
        doc.mainContent.extractRadios().value shouldBe expectedRadioGroup
      }

    }

    "have a submission button" in {
      doc.mainContent.extractText(".govuk-button", 1).value shouldBe Expected.buttonContent
    }

    "render error for the correct journey" in {
      val formWithErrors = refinementForm.bind(Map.empty)
      val viewWithErrors: HtmlFormat.Appendable = viewTemplate(formWithErrors, optionsForTrust)
      val docWithErrors: Document = Jsoup.parse(viewWithErrors.body)
      docWithErrors.mainContent.extractText(errorSummaryList, 1).value shouldBe Expected.authorisationRequestFormError
    }
  }

  "ServiceRefinementPage for cancel authorisation view" should {
    implicit val journeyRequest: AgentJourneyRequest[?] =
      new AgentJourneyRequest("", AgentJourney(journeyType = AgentCancelAuthorisation, clientType = Some("trust"), clientService = Some("HMRC-TERS-ORG")), request)
    val refinementForm: Form[String] = SelectFromOptionsForm.form("clientService", optionsForTrust, AgentCancelAuthorisation.toString)
    val view: HtmlFormat.Appendable = viewTemplate(refinementForm, optionsForTrust)
    val doc: Document = Jsoup.parse(view.body)

    "have the right title" in {
      doc.title() shouldBe Expected.cancelAuthorisationTitle
    }

    "have the right heading" in {
      doc.mainContent.extractText(h1, 1).value shouldBe Expected.cancelAuthorisationHeading
    }

    "render a radio button for each option" when {

      "clientService is HMRC-TERS-ORG" in {
        val expectedRadioGroup: TestRadioGroup = TestRadioGroup(
          Expected.authorisationRequestHeading,
          List(
            Expected.radioLabel1 -> "HMRC-TERS-ORG",
            Expected.radioLabel2 -> "HMRC-TERSNT-ORG"
          ),
          None
        )
        doc.mainContent.extractRadios().value shouldBe expectedRadioGroup
      }

      "have a submission button" in {
        doc.mainContent.extractText(".govuk-button", 1).value shouldBe Expected.buttonContent
      }

      "render error for the correct journey" in {
        val formWithErrors = refinementForm.bind(Map.empty)
        val viewWithErrors: HtmlFormat.Appendable = viewTemplate(formWithErrors, optionsForTrust)
        val docWithErrors: Document = Jsoup.parse(viewWithErrors.body)
        docWithErrors.mainContent.extractText(errorSummaryList, 1).value shouldBe Expected.cancelAuthorisationFormError
      }
    }
  }
}
