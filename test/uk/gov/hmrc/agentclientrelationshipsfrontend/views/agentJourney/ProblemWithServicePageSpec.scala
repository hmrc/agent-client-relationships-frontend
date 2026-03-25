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
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.agentJourney.ProblemWithServicePage

class ProblemWithServicePageSpec extends ViewSpecSupport:

  val viewTemplate: ProblemWithServicePage = app.injector.instanceOf[ProblemWithServicePage]

  private val journey: AgentJourney = AgentJourney(
    AgentJourneyType.AuthorisationRequest,
    clientType = Some("personal"),
    clientService = Some("HMRC-MTD-IT"),
    clientId = Some("AB123"),
    clientDetailsResponse = Some(ClientDetailsResponse("Test Name", None, None, Seq("AA11AA"), Some(KnownFactType.PostalCode), false, None, isMissingEacdKnownFacts = Some(true)))
  )

  object Expected {
    val heading = "HMRC is currently having a problem with this service"
    val title = s"$heading - Ask a client to authorise you - GOV.UK"
    val para1 = "Some customers are experiencing authorisation issues on the country-by-country service."
    val para2 = "We are trying to fix the issue as soon as possible."
    val para3 = "You can try again later."
  }

  s"ProblemWithServicePage view" should :
    implicit val journeyRequest: AgentJourneyRequest[?] = new AgentJourneyRequest("", journey, request)
    val view: HtmlFormat.Appendable = viewTemplate()
    val doc: Document = Jsoup.parse(view.body)
    "have the right title" in :
      doc.title() shouldBe Expected.title

    "have a language switcher" in :
      doc.hasLanguageSwitch shouldBe true

    "have the right heading" in :
      doc.mainContent.extractText(h1, 1).value shouldBe Expected.heading

    "have the right paragraphs" in :
      doc.mainContent.extractText(p, 1).value shouldBe Expected.para1
      doc.mainContent.extractText(p, 2).value shouldBe Expected.para2
      doc.mainContent.extractText(p, 3).value shouldBe Expected.para3


