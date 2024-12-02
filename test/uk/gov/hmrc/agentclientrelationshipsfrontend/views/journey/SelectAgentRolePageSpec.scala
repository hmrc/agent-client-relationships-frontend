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
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.{ClientDetailsResponse, KnownFactType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.journey.SelectFromOptionsForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.journey.SelectAgentRolePage

class SelectAgentRolePageSpec extends ViewSpecSupport {

  val viewTemplate: SelectAgentRolePage = app.injector.instanceOf[SelectAgentRolePage]

  val testClientName = "Test Client"
  val testPostcode = "AA1 1AA"
  val mainRole = "HMRC-MTD-IT"
  val supportingRole = "HMRC-MTD-IT-SUPP"
  val journeyType: JourneyType = JourneyType.AuthorisationRequest

  object Expected {

    val noExistingRelationshipHeading = s"How do you want to act for $testClientName?"
    val existingRelationshipHeading = s"You are already an agent for $testClientName"
    val noExistingRelationshipTitle = s"$noExistingRelationshipHeading - Ask a client to authorise you - GOV.UK"
    val existingRelationshipTitle = s"$existingRelationshipHeading - Ask a client to authorise you - GOV.UK"
    val noExistingRelationshipLegend = s"How do you want to act for $testClientName?"
    val existingRelationshipLegend = "Do you want to change how you act for this client?"
    val formError = s"Select how you want to act for $testClientName"
    val buttonContent = "Continue"
  }

  val clientDetailsWithOutExisting: ClientDetailsResponse = ClientDetailsResponse(
    name = testClientName,
    status = None,
    hasPendingInvitation = false,
    hasExistingRelationshipFor = None,
    knownFactType = Some(KnownFactType.PostalCode),
    knownFacts = Seq(testPostcode),
    isOverseas = Some(false)
  )

  val clientDetailsWithExistingMain: ClientDetailsResponse = clientDetailsWithOutExisting.copy(hasExistingRelationshipFor = Some(mainRole))
  val clientDetailsWithExistingSupp: ClientDetailsResponse = clientDetailsWithOutExisting.copy(hasExistingRelationshipFor = Some(supportingRole))

  private val journey = Journey(
    journeyType = journeyType,
    clientType = Some("personal"),
    clientService = Some("HMRC-MTD-IT"),
    knownFact = Some(testPostcode),
    clientConfirmed = Some(true),
    clientDetailsResponse = Some(clientDetailsWithOutExisting),
    agentType = None
  )

  val agentRoleJourneys: Seq[(Journey, AgentRoleChangeType)] = Seq(
    (journey, AgentRoleChangeType.NewRelationship),
    (journey.copy(clientDetailsResponse = Some(clientDetailsWithExistingMain)), AgentRoleChangeType.MainToSupporting),
    (journey.copy(clientDetailsResponse = Some(clientDetailsWithExistingSupp)), AgentRoleChangeType.SupportingToMain)
  )

  val optionsForItsa: Seq[String] = Seq(mainRole, supportingRole)

  agentRoleJourneys.foreach((journey, agentRoleChangeType) => s"Select agent role view when existing relationship is ${journey.getClientDetailsResponse.hasExistingRelationshipFor.getOrElse("none")}" should {
    implicit val journeyRequest: AgentJourneyRequest[?] =
      new AgentJourneyRequest("", journey, request)
    val form: Form[String] = SelectFromOptionsForm.form("agentRole", optionsForItsa, journeyType.toString, testClientName)
    val view: HtmlFormat.Appendable = viewTemplate(form, optionsForItsa, agentRoleChangeType)
    val expectedHeading: String = if(agentRoleChangeType.equals(AgentRoleChangeType.NewRelationship)) Expected.noExistingRelationshipHeading else Expected.existingRelationshipHeading
    val expectedTitle: String = if(agentRoleChangeType.equals(AgentRoleChangeType.NewRelationship)) Expected.noExistingRelationshipTitle else Expected.existingRelationshipTitle
    val expectedLegend: String = if(agentRoleChangeType.equals(AgentRoleChangeType.NewRelationship)) Expected.noExistingRelationshipLegend else Expected.existingRelationshipLegend
    val expectedRadios = agentRoleChangeType match {
      case AgentRoleChangeType.NewRelationship => TestRadioGroup(expectedLegend, List(
        "As their main agent" -> mainRole,
        "As a supporting agent" -> supportingRole
      ), None)
      case AgentRoleChangeType.MainToSupporting => TestRadioGroup(expectedLegend, List(
        "Yes, I want to become their supporting agent instead" -> supportingRole,
        "No" -> mainRole
      ), None)
      case AgentRoleChangeType.SupportingToMain => TestRadioGroup(expectedLegend, List(
        "Yes, I want to become their main agent instead" -> mainRole,
        "No" -> supportingRole
      ), None)
    }

    val doc: Document = Jsoup.parse(view.body)

    "have the right title" in {
      doc.title() shouldBe expectedTitle
    }

    "have the right heading" in {
      doc.mainContent.extractText(h1, 1).value shouldBe expectedHeading
    }

    "render a radio group to match the scenario" in {
      doc.mainContent.extractRadios().value shouldBe expectedRadios
    }

    "have a submission button" in {
      doc.mainContent.extractText(".govuk-button", 1).value shouldBe Expected.buttonContent
    }

    "render error for the correct journey" in {
      val formWithErrors = form.bind(Map.empty)
      val viewWithErrors: HtmlFormat.Appendable = viewTemplate(formWithErrors, optionsForItsa, agentRoleChangeType)
      val docWithErrors: Document = Jsoup.parse(viewWithErrors.body)
      docWithErrors.mainContent.extractText(errorSummaryList, 1).value shouldBe Expected.formError
    }
  })
}
