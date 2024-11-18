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

package uk.gov.hmrc.agentclientrelationshipsfrontend.views.journey.clientFactPartials

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.journey.EnterClientFactForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.{ClientDetailsResponse, KnownFactType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.journey.EnterClientFactPage

class EmailSpec extends ViewSpecSupport {

  val viewTemplate: EnterClientFactPage = app.injector.instanceOf[EnterClientFactPage]

  private val authorisationRequestJourney: Journey = Journey(JourneyType.AuthorisationRequest)
  private val agentCancelAuthorisationJourney: Journey = Journey(JourneyType.AgentCancelAuthorisation)

  List(authorisationRequestJourney, agentCancelAuthorisationJourney).foreach(j =>
    List("HMRC-CBC-ORG","HMRC-CBC-NONUK-ORG").foreach(enrolment =>
      s"EnterClientFactPage for email ${j.journeyType.toString} view for $enrolment" should {
        implicit val journeyRequest: AgentJourneyRequest[?] = new AgentJourneyRequest(
          "",
          j.copy(
            clientService = Some(enrolment),
            clientDetailsResponse = Some(ClientDetailsResponse("", None, None, Nil, Some(KnownFactType.Email), false, None))
          ),
          request
        )
        val form = EnterClientFactForm.form(
          KnownFactType.Email.fieldConfiguration,
          "HMRC-CBC-ORG",
          Set.empty[String]
        )
        val view: HtmlFormat.Appendable = viewTemplate(form, KnownFactType.Email.fieldConfiguration)
        val doc: Document = Jsoup.parse(view.body)
        "have an input element" in {
          doc.select("input").size() shouldBe 1
        }
        "render an input element" in {
          val expectedElement = TestInputField(
            "What is your client’s Country-by-Country contact email address?",Some("This is the email your client gave to HMRC for Country-by-Country reporting."),"email"
          )
          doc.extractInputField().value shouldBe expectedElement
        }

        "render an error message when form has errors" in {
          val formWithErrors = form.bind(Map("email" -> ""))
          val viewWithErrors: HtmlFormat.Appendable = viewTemplate(formWithErrors, KnownFactType.Email.fieldConfiguration)
          val docWithErrors: Document = Jsoup.parse(viewWithErrors.body)
          docWithErrors.select("p.govuk-error-message").text() shouldBe "Error: Enter your client’s email address"
        }
      }))
}
