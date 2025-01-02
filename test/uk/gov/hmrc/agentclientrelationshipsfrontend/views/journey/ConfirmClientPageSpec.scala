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
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.journey.ConfirmClientForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.journey.ConfirmClientPage
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.ClientConfirmationFieldName
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.ClientDetailsResponse

class ConfirmClientPageSpec extends ViewSpecSupport {

  val viewTemplate: ConfirmClientPage = app.injector.instanceOf[ConfirmClientPage]

  private val authorisationRequestJourney: AgentJourney = AgentJourney(
    AgentJourneyType.AuthorisationRequest,
    clientDetailsResponse = Some(ClientDetailsResponse("TestName", None, None, Nil, None, false, None))
  )
  private val agentCancelAuthorisationJourney: AgentJourney = AgentJourney(
    AgentJourneyType.AgentCancelAuthorisation,
    clientDetailsResponse = Some(ClientDetailsResponse("TestName", None, None, Nil, None, false, None))
  )

  val clientName = "TestName"

  object Expected {
    val authorisationLabel = s"Is $clientName the client you want authorisation from?"
    val deAuthorisationLabel = s"Is your cancellation request for $clientName?"
    val authorisationTitle = s"$authorisationLabel - Ask a client to authorise you - GOV.UK"
    val deAuthorisationTitle = s"$deAuthorisationLabel - Cancel a client’s authorisation - GOV.UK"
    val radioTrue = "Yes"
    val radioFalse = "No - I need to start again"
    val authorisationErrorRequired = s"Select ‘yes’ if you want $clientName to authorise you as an agent"
    val deAuthorisationErrorRequired = s"Select ‘yes’ if $clientName is the client you want to cancel authorisation for"
    val buttonContent = "Continue"
  }

  List(authorisationRequestJourney, agentCancelAuthorisationJourney).foreach(j =>
    s"Confirm client view for ${j.journeyType}" should {
      implicit val journeyRequest: AgentJourneyRequest[?] = new AgentJourneyRequest("", j, request)

      val title = if(j.journeyType == AgentJourneyType.AuthorisationRequest) Expected.authorisationTitle else
        Expected.deAuthorisationTitle
      val form: Form[Boolean] = ConfirmClientForm.form(ClientConfirmationFieldName, clientName, j.journeyType.toString)
      val view: HtmlFormat.Appendable = viewTemplate(form)
      val doc: Document = Jsoup.parse(view.body)

      "have the right title" in {
        doc.title() shouldBe title
      }

      "have a language switcher" in {
        doc.hasLanguageSwitch shouldBe true
      }

      "render input Radio form for confirm client page" in {
        doc.mainContent.extractRadios() shouldBe Some(TestRadioGroup(
          legend = if(j.journeyType == AgentJourneyType.AuthorisationRequest) Expected.authorisationLabel else
            Expected.deAuthorisationLabel,
          options = List((Expected.radioTrue, "true"), (Expected.radioFalse, "false")),
          hint = None
        ))
      }

      "have a submission button" in {
        doc.mainContent.extractText(button, 1).value shouldBe Expected.buttonContent
      }

      "render error for the correct journey" in {
        val expectedError = if(j.journeyType == AgentJourneyType.AuthorisationRequest) Expected.authorisationErrorRequired else
          Expected.deAuthorisationErrorRequired
        val formWithErrors = form.bind(Map.empty)
        val viewWithErrors: HtmlFormat.Appendable = viewTemplate(formWithErrors)
        val docWithErrors: Document = Jsoup.parse(viewWithErrors.body)
        docWithErrors.mainContent.extractText(errorSummaryList, 1).value shouldBe expectedError
      }
    })
}
