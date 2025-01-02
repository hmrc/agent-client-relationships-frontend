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
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.common.ClientDetailsConfiguration
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.journey.EnterClientIdForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.journey.EnterClientIdPage

class EnterClientIdPageSpec extends ViewSpecSupport {

  val viewTemplate: EnterClientIdPage = app.injector.instanceOf[EnterClientIdPage]

  private val authorisationRequestJourney: AgentJourney = AgentJourney(AgentJourneyType.AuthorisationRequest)
  private val agentCancelAuthorisationJourney: AgentJourney = AgentJourney(AgentJourneyType.AgentCancelAuthorisation)

  val ninoField: ClientDetailsConfiguration = ClientDetailsConfiguration(
    name = "nino",
    regex = "[[A-Z]&&[^DFIQUV]][[A-Z]&&[^DFIQUVO]] ?\\d{2} ?\\d{2} ?\\d{2} ?[A-D]{1}",
    inputType = "text",
    width = 10,
    clientIdType = "ni"
  )

  val vrnField: ClientDetailsConfiguration = ClientDetailsConfiguration(
    name = "vrn",
    regex = "^[0-9]{9}$",
    inputType = "text",
    width = 10,
    clientIdType = "vrn"
  )

  val urnField: ClientDetailsConfiguration = ClientDetailsConfiguration(
    name = "urn",
    regex = "^[A-Z]{2}TRUST[0-9]{8}$",
    inputType = "text",
    width = 20,
    clientIdType = "urn"
  )


  val utrField: ClientDetailsConfiguration = ClientDetailsConfiguration(
    name = "utr",
    regex = "^[0-9]{10}$",
    inputType = "text",
    width = 10,
    clientIdType = "utr"
  )


  val cgtField: ClientDetailsConfiguration = ClientDetailsConfiguration(
    name = "cgtRef",
    regex = "^X[A-Z]CGTP[0-9]{9}$",
    inputType = "text",
    width = 20,
    clientIdType = "CGTPDRef"
  )


  val pptField: ClientDetailsConfiguration = ClientDetailsConfiguration(
    name = "pptRef",
    regex = "^X[A-Z]PPT000[0-9]{7}$",
    inputType = "text",
    width = 20,
    clientIdType = "EtmpRegistrationNumber"
  )

  val cbcField: ClientDetailsConfiguration = ClientDetailsConfiguration(
    name = "cbcId",
    regex = "^X[A-Z]CBC[0-9]{10}$",
    inputType = "text",
    width = 20,
    clientIdType = "cbcId"
  )

  val plrField: ClientDetailsConfiguration = ClientDetailsConfiguration(
    name = "PlrId",
    regex = "^X[A-Z]{1}PLR[0-9]{10}$",
    inputType = "text",
    width = 20,
    clientIdType = "PLRID"
  )

  val mapOfFieldConfiguration: Map[String, (ClientDetailsConfiguration, ServiceStrings)] =
    Map(
      "nino" -> (ninoField, Expected.Nino),
      "vrn" -> (vrnField, Expected.Vrn),
      "urn" -> (urnField, Expected.Urn),
      "utr" -> (utrField, Expected.Utr),
      "cgtRef" -> (cgtField, Expected.CgtRef),
      "pptRef" -> (pptField, Expected.PptRef),
      "cbcId" -> (cbcField, Expected.CbcId),
      "PlrId" -> (plrField, Expected.PlrId)
    )

  trait ServiceStrings {
    val label: String
    val hint: String
    val errorInvalid: String
    val errorRequired: String
    val authorisationRequestTitle: String
    val cancelAuthorisationTitle: String
  }

  object Expected {

    val FormError = "Select what you want the client to authorise you to do"


    object Nino extends ServiceStrings {
      val label = "What is your client’s National Insurance number?"
      val hint = "For example, QQ 12 34 56 C"
      val errorInvalid = "National Insurance number must be 2 letters, 6 numbers, then A, B, C or D, like QQ 12 34 56 C"
      val errorRequired = "Enter your client’s National Insurance number"
      val authorisationRequestTitle = s"$label - Ask a client to authorise you - GOV.UK"
      val cancelAuthorisationTitle = s"$label - Cancel a client’s authorisation - GOV.UK"
    }

    object Vrn extends ServiceStrings {
      val label = "What is your client’s VAT registration number?"
      val hint = "This is 9 numbers, for example, 123456789"
      val errorInvalid = "VAT registration number must be 9 numbers "
      val errorRequired = "Enter your client’s VAT registration number"
      val authorisationRequestTitle = s"$label - Ask a client to authorise you - GOV.UK"
      val cancelAuthorisationTitle = s"$label - Cancel a client’s authorisation - GOV.UK"
    }

    object Utr extends ServiceStrings {
      val label = "What is your client’s Unique Taxpayer Reference (UTR)?"
      val hint = "Enter the last 10 digits only. For example, 12345 67890"
      val errorInvalid = "Enter the Unique Taxpayer Reference (UTR) in the correct format"
      val errorRequired = "Enter your client’s Unique Taxpayer Reference (UTR)"
      val authorisationRequestTitle = s"$label - Ask a client to authorise you - GOV.UK"
      val cancelAuthorisationTitle = s"$label - Cancel a client’s authorisation - GOV.UK"
    }

    object Urn extends ServiceStrings {
      val label = "What is your client’s Unique Reference Number (URN)?"
      val hint = "This is 15 characters, for example, XATRUST12345678"
      val errorInvalid = "Enter the Unique Reference Number (URN) in the correct format"
      val errorRequired = "Enter your client’s Unique Reference Number (URN)"
      val authorisationRequestTitle = s"$label - Ask a client to authorise you - GOV.UK"
      val cancelAuthorisationTitle = s"$label - Cancel a client’s authorisation - GOV.UK"
    }

    object CgtRef extends ServiceStrings {
      val label = "What is your client’s Capital Gains Tax account reference?"
      val hint = "This is 15 characters, for example XYCGTP123456789. Your client received this when they created their account."
      val errorInvalid = "Enter your client’s Capital Gains Tax account reference"
      val errorRequired = "Enter your client’s Capital Gains Tax account reference in the correct format"
      val authorisationRequestTitle = s"$label - Ask a client to authorise you - GOV.UK"
      val cancelAuthorisationTitle = s"$label - Cancel a client’s authorisation - GOV.UK"
    }

    object PptRef extends ServiceStrings {
      val label = "What is your client’s Plastic Packaging Tax reference?"
      val hint = "This is 15 characters, for example XMPPT0000000001. Your client received this when they registered for Plastic Packaging Tax."
      val errorInvalid = "Enter your client’s Plastic Packaging Tax reference in the correct format"
      val errorRequired = "Enter your client’s Plastic Packaging Tax reference"
      val authorisationRequestTitle = s"$label - Ask a client to authorise you - GOV.UK"
      val cancelAuthorisationTitle = s"$label - Cancel a client’s authorisation - GOV.UK"
    }

    object CbcId extends ServiceStrings {
      val label = "What is your client’s Country-by-country ID?"
      val hint = "For example, XACBC0000999999."
      val errorInvalid = "Enter your client’s country-by-country ID must start with an ’X’ followed by a letter, then ’CBC’ and then 10 numbers"
      val errorRequired = "Enter your client’s country-by-country ID"
      val authorisationRequestTitle = s"$label - Ask a client to authorise you - GOV.UK"
      val cancelAuthorisationTitle = s"$label - Cancel a client’s authorisation - GOV.UK"
    }

    object PlrId extends ServiceStrings {
      val label = "What is your client’s Pillar 2 top-up taxes ID?"
      val hint = "This is 15 characters, for example, XAPLR0000999999. The current filing member can find it on their Report Pillar 2 top-up taxes homepage."
      val errorInvalid = "Enter a valid Pillar 2 top-up taxes registration date"
      val errorRequired = "Enter your client’s date of registration to report Pillar 2 top-up taxes"
      val authorisationRequestTitle = s"$label - Ask a client to authorise you - GOV.UK"
      val cancelAuthorisationTitle = s"$label - Cancel a client’s authorisation - GOV.UK"
    }

    val buttonContent = "Continue"
  }

  List(authorisationRequestJourney, agentCancelAuthorisationJourney).foreach(j =>
    mapOfFieldConfiguration.keys.map(field => s"EnterClientId for a $field ${j.journeyType.toString} view" should {
      implicit val journeyRequest: AgentJourneyRequest[?] = new AgentJourneyRequest("", j, request)

      val serviceStrings: ServiceStrings = mapOfFieldConfiguration(field)._2
      val title = if (j.journeyType == AgentJourneyType.AuthorisationRequest) serviceStrings.authorisationRequestTitle else serviceStrings.cancelAuthorisationTitle

      val form: Form[String] = EnterClientIdForm.form(mapOfFieldConfiguration(field)._1, j.journeyType.toString)
      val view: HtmlFormat.Appendable = viewTemplate(form, mapOfFieldConfiguration(field)._1)
      val doc: Document = Jsoup.parse(view.body)
      "have the right title" in {
        doc.title() shouldBe title
      }
      "have a language switcher" in {
        doc.hasLanguageSwitch shouldBe true
      }
      "render input form for client details" in {
        doc.mainContent.extractInputField() shouldBe Some(TestInputField(
          label = serviceStrings.label,
          hint = Some(serviceStrings.hint),
          inputName = field
        ))
      }
      "have a submission button" in {
        doc.mainContent.extractText(button, 1).value shouldBe Expected.buttonContent
      }
      "render error for the correct journey" in {
        val formWithErrors = form.bind(Map.empty)
        val viewWithErrors: HtmlFormat.Appendable = viewTemplate(formWithErrors, mapOfFieldConfiguration(field)._1)
        val docWithErrors: Document = Jsoup.parse(viewWithErrors.body)
        docWithErrors.mainContent.extractText(errorSummaryList, 1).value shouldBe serviceStrings.errorRequired
      }
    }))
}
