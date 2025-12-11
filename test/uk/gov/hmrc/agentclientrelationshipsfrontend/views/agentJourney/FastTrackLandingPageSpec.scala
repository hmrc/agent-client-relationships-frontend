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
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.{ClientDetailsResponse, KnownFactType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.common.ClientDetailsConfiguration
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.agentJourney.FastTrackLandingPage

class FastTrackLandingPageSpec extends ViewSpecSupport {

  val viewTemplate: FastTrackLandingPage = app.injector.instanceOf[FastTrackLandingPage]

  private val journeyType = AgentJourneyType.AuthorisationRequest
  private val exampleClientId: String = "1234567890"
  private val clientName = "Test Name"
  private val basicClientDetails = ClientDetailsResponse(clientName, None, None, Seq.empty, Some(KnownFactType.PostalCode), false, None)
  private val basicJourney: AgentJourney = AgentJourney(
    journeyType = journeyType,
    clientType = Some("personal"),
    clientService = Some("HMRC-MTD-IT"),
    clientId = Some(exampleClientId),
    clientDetailsResponse = Some(basicClientDetails)
  )

  def agentRoleBasedRequestJourney(service: String, role: String): AgentJourney = basicJourney.copy(
    clientService = Some(service),
    agentType = Some(role)
  )

  private val authorisationRequestJourney: AgentJourney = AgentJourney(AgentJourneyType.AuthorisationRequest)

  val ninoField: ClientDetailsConfiguration = ClientDetailsConfiguration(
    name = "nino",
    regex = "[[A-Z]&&[^DFIQUV]][[A-Z]&&[^DFIQUVO]] ?\\d{2} ?\\d{2} ?\\d{2} ?[A-D]{1}",
    inputType = "text",
    width = 10,
    clientIdType = "ni"
  )

  val mapOfFieldConfiguration: Map[String, ClientDetailsConfiguration] =
    Map(
      "nino" -> ninoField
    )

  def authorisationRequestTitle(startOfTitle: String): String = s"$startOfTitle - Ask a client to authorise you - GOV.UK"

  List("HMRC-MTD-IT").foreach(role =>
    val serviceName = "HMRC-MTD-IT"
    List(authorisationRequestJourney).foreach(j =>
      mapOfFieldConfiguration.keys.map(field => s"FastTrackLanding for a $field ${j.journeyType.toString} view" should {
        val agentJourney = agentRoleBasedRequestJourney(serviceName, role)
        implicit val journeyRequest: AgentJourneyRequest[?] = new AgentJourneyRequest("", agentJourney, request)

        val key = "fasttrackLanding"

        val view: HtmlFormat.Appendable = viewTemplate(mapOfFieldConfiguration(field))
        val doc: Document = Jsoup.parse(view.body)
        "have the right title" in {
          doc.title() shouldBe authorisationRequestTitle(messages(s"$key.$serviceName.header"))
        }
        "have a language switcher" in {
          doc.hasLanguageSwitch shouldBe true
        }
        "have the right inset text" in {
          doc.select(".govuk-inset-text").get(0).text() shouldBe messages(s"$key.$serviceName.inset")
        }
        "have the right paragraph text" in {
          doc.select(".govuk-body").get(0).text() shouldBe messages(s"$key.$serviceName.paragraph")
        }
        "have a summary list containing the client id type and value" in {
          val summaryList = doc.select(".govuk-summary-list").get(0)
          summaryList.select(".govuk-summary-list__row").size() shouldEqual 1
          summaryList.select(".govuk-summary-list__key").get(0).text() shouldBe messages(s"$key.$field.key")
          summaryList.select(".govuk-summary-list__value").get(0).text() shouldBe agentJourney.clientId.get
        }
        "have a submission button" in {
          doc.mainContent.extractText(button, 1).value shouldBe messages("continue.button")
        }
      }))
  )
}
