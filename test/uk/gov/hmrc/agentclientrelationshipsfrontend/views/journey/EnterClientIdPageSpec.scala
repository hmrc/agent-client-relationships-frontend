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
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.journey.EnterClientIdPage

class EnterClientIdPageSpec extends ViewSpecSupport {

  val viewTemplate: EnterClientIdPage = app.injector.instanceOf[EnterClientIdPage]

  object Expected {

    val FormError = "Select what you want the client to authorise you to do"


    val clientIdNinoLabel = "What is your client’s National Insurance number?"
    val clientIdNinoHint = "For example, QQ 12 34 56 C"
    val clientIdNinoErrorInvalid = "National Insurance number must be 2 letters, 6 numbers, then A, B, C or D, like QQ 12 34 56 C"
    val clientIdNinoErrorRequired = "Enter your client’s National Insurance number"

    val clientIdVrnLabel = "What is your client’s VAT registration number?"
    val clientIdVrnHint = "This is 9 numbers, for example, 123456789"
    val clientIdVrnErrorInvalid = "VAT registration number must be 9 numbers clientId.vrn.error.required = Enter your client’s VAT registration number"

    val clientIdUtrLabel = "What is your client’s Unique Taxpayer Reference (UTR)?"
    val clientIdUtrHint = "Enter the last 10 digits only.For example, 1234567890"
    val clientIdUtrErrorInvalid = "Enter the Unique Taxpayer Reference(UTR) or Unique Reference Number(URN) in the correct format"
    val clientIdUtrErrorRequired = "Enter your client’s Unique Taxpayer Reference (UTR) or Unique Reference Number(URN)"

    val clientIdCgtRefLabel = "What is your client’s Capital Gains Tax account reference?"
    val clientIdCgtRefHint = "This is 15 characters, for example XYCGTP123456789. Your client receivedthis when they created their account."
    val clientIdCgtRefErrorInvalid = "Enter your client’s Capital Gains Tax account reference"
    val clientIdCgtRefErrorRequired = "Enter your client’s Capital Gains Tax account reference in the correct format"

    val clientIdPptRefLabel = "What is your client’s Plastic Packaging Tax reference"
    val clientIdPptRefHint = "This is 15 characters, for example XMPPT0000000001. Your client received this when they registered for Plastic Packaging Tax."
    val clientIdPptRefErrorInvalid = "Enter your client’s Plastic Packaging Tax reference in the correct format"
    val clientIdPptRefErrorRequired = "Enter your client’s Plastic Packaging Tax reference"

    val clientIdCbcIdLabel = "What is your client’s Country-by-country ID"
    val clientIdCbcIdHint = "This is the email address we currently hold for your client. If they have 2 contacts, enter the email address for the first contact."
    val clientIdCbcIdErrorInvalid = "Enter your client’s country-by-country ID must start with an ’X’ followed by a letter, then ’CBC’ and then 10 numbers"
    val clientIdCbcIdErrorRequired = "Enter your client’s country-by-country ID"

    val clientIdPillar2Label = "What is your client’s Pillar 2 top -up taxes ID"
    val clientIdPillar2Hint = "This is 15 characters, for example, XAPLR0000999999.The current filing member can find it on their Report Pillar2 top -up taxes homepage."
    val clientIdPillar2ErrorInvalid = "Enter a valid Pillar 2 top -up taxes registration date"
    val clientIdPillar2ErrorRequired = "Enter your client’s date of registration to report Pillar2 top -up taxes"

    val buttonContent = "Continue"
  }

  "EnterClientId for authorisation request view" should {
    implicit val journeyRequest: AgentJourneyRequest[?] = new AgentJourneyRequest("", Journey(journeyType = JourneyType.AuthorisationRequest), request)

    val form: Form[String] = SelectFromOptionsForm.form("clientDetailField", options, "authorisation-request")
    val view: HtmlFormat.Appendable = viewTemplate(form, options)
    val doc: Document = Jsoup.parse(view.body)
    "have the right title" in {
      doc.title() shouldBe Expected.authorisationRequestTitle
    }
    "have a language switcher" in {
      doc.hasLanguageSwitch shouldBe true
    }
//    "have the right heading" in {
//      doc.mainContent.extractText(h1, 1).value shouldBe Expected.authorisationRequestHeading
//    }
//    "render a radio button for each option" in {
//      val expectedRadioGroup: TestRadioGroup = TestRadioGroup(Expected.authorisationRequestHeading, List(Expected.personalLabel -> "personal", Expected.businessLabel -> "business", Expected.trustLabel -> "trust"), None)
//      doc.mainContent.extractRadios(1).value shouldBe expectedRadioGroup
//    }
    "render input form for client details" in {}
    
    "render error for the correct journey" in {
      val formWithErrors = form.bind(Map.empty)
      val viewWithErrors: HtmlFormat.Appendable = viewTemplate(formWithErrors, options)
      val docWithErrors: Document = Jsoup.parse(viewWithErrors.body)
      docWithErrors.mainContent.extractText(errorSummaryList, 1).value shouldBe "Select the type of client you need authorisation from"
    }
  }
}
