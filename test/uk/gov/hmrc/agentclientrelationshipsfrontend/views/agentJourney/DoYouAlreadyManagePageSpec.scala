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

package uk.gov.hmrc.agentclientrelationshipsfrontend.views.agentJourney

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.ClientDetailsResponse
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.journey.DoYouAlreadyManageForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{AgentJourney, AgentJourneyRequest, AgentJourneyType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.agentJourney.DoYouAlreadyManagePage

class DoYouAlreadyManagePageSpec extends ViewSpecSupport:

  val viewTemplate: DoYouAlreadyManagePage = app.injector.instanceOf[DoYouAlreadyManagePage]

  private val journey: AgentJourney = AgentJourney(
    AgentJourneyType.AuthorisationRequest,
    clientDetailsResponse = Some(ClientDetailsResponse("TestName", None, None, Nil, None, false, None))
  )

  val clientName = "TestName"

  object Expected {
    val label = s"Do you already manage Self Assessment for $clientName?"
    val title = s"$label - Ask a client to authorise you - GOV.UK"
    val radioTrue = "Yes"
    val radioFalse = "No"
    val errorRequired = s"Select ‘yes’ if you already manage Self Assessment for $clientName"
    val buttonContent = "Continue"
  }

  s"Confirm client view for ${journey.journeyType}" should :
    implicit val journeyRequest: AgentJourneyRequest[?] = new AgentJourneyRequest("", journey, request)

    val title = Expected.title
    val form: Form[Boolean] = DoYouAlreadyManageForm.form(clientName)
    val view: HtmlFormat.Appendable = viewTemplate(form)
    val doc: Document = Jsoup.parse(view.body)

    "have the right title" in :
      doc.title() shouldBe title

    "have a language switcher" in :
      doc.hasLanguageSwitch shouldBe true

    "render input Radio form for confirm client page" in :
      doc.mainContent.extractRadios() shouldBe Some(TestRadioGroup(
        legend = Expected.label,
        options = List((Expected.radioTrue, "true"), (Expected.radioFalse, "false")),
        hint = None
      ))

    "have a submission button" in :
      doc.mainContent.extractText(button, 1).value shouldBe Expected.buttonContent

    "render error for the correct journey" in :
      val expectedError = Expected.errorRequired
      val formWithErrors = form.bind(Map.empty)
      val viewWithErrors: HtmlFormat.Appendable = viewTemplate(formWithErrors)
      val docWithErrors: Document = Jsoup.parse(viewWithErrors.body)
      docWithErrors.mainContent.extractText(errorSummaryList, 1).value shouldBe expectedError
