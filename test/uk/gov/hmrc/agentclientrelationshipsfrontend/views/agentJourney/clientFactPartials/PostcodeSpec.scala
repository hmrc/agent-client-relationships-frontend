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

package uk.gov.hmrc.agentclientrelationshipsfrontend.views.agentJourney.clientFactPartials

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.journey.EnterClientFactForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.{ClientDetailsResponse, KnownFactType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.agentJourney.EnterClientFactPage

class PostcodeSpec extends ViewSpecSupport {

  val viewTemplate: EnterClientFactPage = app.injector.instanceOf[EnterClientFactPage]

  private val authorisationRequestJourney: AgentJourney = AgentJourney(AgentJourneyType.AuthorisationRequest)
  private val agentCancelAuthorisationJourney: AgentJourney = AgentJourney(AgentJourneyType.AgentCancelAuthorisation)

  List(authorisationRequestJourney, agentCancelAuthorisationJourney).foreach(j =>
      s"EnterClientFactPage for postcode ${j.journeyType.toString} view" should {
        implicit val journeyRequest: AgentJourneyRequest[?] = new AgentJourneyRequest(
          "",
          j.copy(
            clientService = Some("HMRC-CGT-PD"),
            clientDetailsResponse = Some(ClientDetailsResponse("", None, None, Nil, Some(KnownFactType.PostalCode), false, None))
          ),
          request
        )
        val form = EnterClientFactForm.form(
          KnownFactType.PostalCode.fieldConfiguration,
          "HMRC-CGT-PD"
        )
        val view: HtmlFormat.Appendable = viewTemplate(form, KnownFactType.PostalCode.fieldConfiguration)
        val doc: Document = Jsoup.parse(view.body)
        "have a text input element" in {
          doc.select("input").size() shouldBe 1
        }
      })
}
