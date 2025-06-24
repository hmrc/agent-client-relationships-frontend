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
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.KnownFactType.{Country, CountryCode}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.journey.EnterClientFactForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.{ClientDetailsResponse, KnownFactType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.agentJourney.EnterClientFactPage

class CountrySpec extends ViewSpecSupport {

  val viewTemplate: EnterClientFactPage = app.injector.instanceOf[EnterClientFactPage]

  def testCountries(knownFactType: KnownFactType): Seq[(String, String)] = knownFactType match {
    case CountryCode => Seq(("FR", "France"), ("DE", "Germany"))
    case Country => Seq(("France", "France"), ("Germany", "Germany"))
  }

  private val authorisationRequestJourney: AgentJourney = AgentJourney(AgentJourneyType.AuthorisationRequest)
  private val agentCancelAuthorisationJourney: AgentJourney = AgentJourney(AgentJourneyType.AgentCancelAuthorisation)

  List(authorisationRequestJourney, agentCancelAuthorisationJourney).foreach(j =>
    List(CountryCode, Country).foreach(c =>
      s"EnterClientFactPage for ${c.toString} ${j.journeyType.toString} view" should {
        implicit val journeyRequest: AgentJourneyRequest[?] = new AgentJourneyRequest(
          "",
          j.copy(
            clientService = Some("HMRC-CGT-PD"),
            clientDetailsResponse = Some(ClientDetailsResponse("", None, None, Nil, Some(KnownFactType.CountryCode), false, None))
          ),
          request
        )
        val options = testCountries(c)
        val field = c.fieldConfiguration.copy(validOptions = Some(options))
        val form = EnterClientFactForm.form(
          field,
          "HMRC-CGT-PD"
        )
        val view: HtmlFormat.Appendable = viewTemplate(form, field)
        val doc: Document = Jsoup.parse(view.body)
        "have a select element" in {
          doc.select("select").size() shouldBe 1
        }
        "render a select element with country options" in {
          val expectedElement = TestSelect(
            "countryCode",
            Seq(("", "")) ++ options
          )
          doc.extractSelectElement().value shouldBe expectedElement
        }
        "render an error message when form has errors" in {
          val formWithErrors = form.bind(Map("countryCode" -> "invalid"))
          val viewWithErrors: HtmlFormat.Appendable = viewTemplate(formWithErrors, field)
          val docWithErrors: Document = Jsoup.parse(viewWithErrors.body)
          docWithErrors.select("p.govuk-error-message").text() shouldBe "Error: Enter the country of your client’s contact address"
        }
      })
  )
}
