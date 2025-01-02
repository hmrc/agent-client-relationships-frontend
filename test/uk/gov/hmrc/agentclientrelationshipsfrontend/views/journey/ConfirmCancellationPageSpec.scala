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
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.ConfirmCancellationFieldName
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.journey.ConfirmCancellationForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.{ClientDetailsResponse, KnownFactType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.journey.ConfirmCancellationPage

class ConfirmCancellationPageSpec extends ViewSpecSupport {

  val viewTemplate: ConfirmCancellationPage = app.injector.instanceOf[ConfirmCancellationPage]
  private val journeyType = AgentJourneyType.AgentCancelAuthorisation
  val exampleClientId: String = "1234567890"
  val exampleKnownFact: String = "AA11AA"
  val clientName = "Test Name"

  private val services = Seq("HMRC-MTD-IT", "PERSONAL-INCOME-RECORD", "HMRC-MTD-VAT", "HMRC-CGT-PD", "HMRC-PPT-ORG", "HMRC-CBC-ORG", "HMRC-PILLAR2-ORG", "HMRC-TERS-ORG")
  private val basicClientDetails = ClientDetailsResponse(clientName, None, None, Seq(exampleKnownFact), Some(KnownFactType.PostalCode), false, None)
  private val basicJourney: AgentJourney = AgentJourney(
    journeyType = journeyType,
    clientType = Some("personal"),
    clientService = Some("HMRC-MTD-IT"),
    clientId = Some(exampleClientId),
    clientDetailsResponse = Some(basicClientDetails),
    knownFact = Some(exampleKnownFact),
    clientConfirmed = Some(true),
    agentType = None
  )

  private def cancellationJourney(service: String, supporting: Boolean) = basicJourney.copy(
    clientService = Some(service),
    clientDetailsResponse = Some(basicClientDetails.copy(hasExistingRelationshipFor = Some(if supporting then "HMRC-MTD-IT-SUPP" else service)))
  )

  services.foreach(service =>
    s"Confirm cancellation view for $service" should {
      val j = cancellationJourney(service, supporting = false)
      implicit val journeyRequest: AgentJourneyRequest[?] = new AgentJourneyRequest("", j, request)
      val form: Form[Boolean] = ConfirmCancellationForm.form(ConfirmCancellationFieldName, journeyType.toString)
      val view: HtmlFormat.Appendable = viewTemplate(form)
      val doc: Document = Jsoup.parse(view.body)

      "have the right title" in {
        doc.title() shouldBe "Confirm cancellation - Cancel a client’s authorisation - GOV.UK"
      }

      "have a language switcher" in {
        doc.hasLanguageSwitch shouldBe true
      }

      "render input Radio form for confirm cancellation page" in {
        doc.mainContent.extractRadios() shouldBe Some(TestRadioGroup(
          legend = "Do you want to cancel your authorisation for this client?",
          options = List(("Yes", "true"), ("No - I need to start again", "false")),
          hint = None
        ))
      }

      "have a submission button" in {
        doc.mainContent.extractText(button, 1).value shouldBe "Continue"
      }

      "render error for the correct journey" in {
        val formWithErrors = form.bind(Map.empty)
        val viewWithErrors: HtmlFormat.Appendable = viewTemplate(formWithErrors)
        val docWithErrors: Document = Jsoup.parse(viewWithErrors.body)
        docWithErrors.mainContent.extractText(errorSummaryList, 1).value shouldBe "Select ‘yes’ if you want to cancel authorisation for this client"
      }
    })
}
