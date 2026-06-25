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
import uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.journey.routes
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.agentJourney.JourneyExitPage

class AgentJourneyExitPageSpec extends ViewSpecSupport {

  val viewTemplate: JourneyExitPage = app.injector.instanceOf[JourneyExitPage]

  private val authorisationRequestJourney: AgentJourney = AgentJourney(AgentJourneyType.AuthorisationRequest)
  private val agentCancelAuthorisationJourney: AgentJourney = AgentJourney(AgentJourneyType.AgentCancelAuthorisation)

  case class ExpectedStrings(authorisationTitle: String, cancelAuthorisationTitle: String)
  private val supportedErrorCodes: Map[JourneyExitType, ExpectedStrings] = Map(
    JourneyExitType.NotFound -> ExpectedStrings(
      authorisationTitle = "We could not find your client - Ask a client to authorise you - GOV.UK",
      cancelAuthorisationTitle = "We could not find your client - Cancel a client’s authorisation - GOV.UK"
    ),
    JourneyExitType.NotRegistered ->  ExpectedStrings(
      authorisationTitle = "Your client needs to register for Self Assessment - Ask a client to authorise you - GOV.UK",
      cancelAuthorisationTitle = "Your client needs to register for Self Assessment - Cancel a client’s authorisation - GOV.UK"
    ),
    JourneyExitType.NotFoundCbc -> ExpectedStrings(
      authorisationTitle = "We could not find your client’s CBC email address - Ask a client to authorise you - GOV.UK",
      cancelAuthorisationTitle = "We could not find your client’s CBC email address - Cancel a client’s authorisation - GOV.UK"
    ),
    JourneyExitType.NotFoundCbcId -> ExpectedStrings(
      authorisationTitle = "We cannot find any clients that match this CBC ID - Ask a client to authorise you - GOV.UK",
      cancelAuthorisationTitle = "We cannot find any clients that match this CBC ID - Cancel a client’s authorisation - GOV.UK"
    ),
    JourneyExitType.AuthorisationAlreadyRemoved -> ExpectedStrings(
      authorisationTitle = "This authorisation has already been removed - Ask a client to authorise you - GOV.UK",
      cancelAuthorisationTitle = "This authorisation has already been removed - Cancel a client’s authorisation - GOV.UK"
    )
  )

  List(authorisationRequestJourney, agentCancelAuthorisationJourney).foreach(j =>
    supportedErrorCodes.map(errorCode => s"JourneyErrorPage for ${errorCode._1} ${j.journeyType.toString} view" should {
      implicit val journeyRequest: AgentJourneyRequest[?] = new AgentJourneyRequest("", j, request)
      val expectedStrings = errorCode._2
      val title = if (j.journeyType == AgentJourneyType.AuthorisationRequest) expectedStrings.authorisationTitle else expectedStrings.cancelAuthorisationTitle
      val view: HtmlFormat.Appendable = viewTemplate(j.journeyType, errorCode._1)
      val doc: Document = Jsoup.parse(view.body)
      "have the right title" in {
        doc.title() shouldBe title
      }
      "have a language switcher" in {
        doc.hasLanguageSwitch shouldBe true
      }
    }))

  private val cbcReportingUrl = "https://www.tax.service.gov.uk/register-to-send-a-country-by-country-report"

  "CBC ID client-not-found view" should {
    implicit val journeyRequest: AgentJourneyRequest[?] = new AgentJourneyRequest("", authorisationRequestJourney, request)
    val view: HtmlFormat.Appendable = viewTemplate(AgentJourneyType.AuthorisationRequest, JourneyExitType.NotFoundCbcId)
    val doc: Document = Jsoup.parse(view.body)

    "have the approved content" in {
      doc.mainContent.extractText(p, 1).value shouldBe "You can go back and enter your client’s CBC ID again."
      doc.mainContent.extractText(p, 2).value shouldBe "If there is still an issue, then your client may need to provide or confirm their details in the country-by-country reporting service."
      doc.mainContent.extractText(p, 3).value shouldBe s"They can use this link to sign in: $cbcReportingUrl"
      doc.mainContent.extractText(p, 4).value shouldBe "Once we receive their details or confirmation, you can come back here and request authorisation."
    }

    "link to the country-by-country service" in {
      doc.mainContent.extractLink(1).value shouldBe TestLink(cbcReportingUrl, cbcReportingUrl)
    }

    "have a start again button" in {
      doc.mainContent.extractLinkButton("showClientTypeButton").value shouldBe TestLink(
        "Start again",
        routes.StartJourneyController.startJourney(AgentJourneyType.AuthorisationRequest).url
      )
    }
  }

  "CBC email client-not-found view" should {
    implicit val journeyRequest: AgentJourneyRequest[?] = new AgentJourneyRequest("", authorisationRequestJourney, request)
    val view: HtmlFormat.Appendable = viewTemplate(AgentJourneyType.AuthorisationRequest, JourneyExitType.NotFoundCbc)
    val doc: Document = Jsoup.parse(view.body)

    "have the approved content" in {
      doc.mainContent.extractText(p, 1).value shouldBe "You can go back and enter your client’s CBC email again."
      doc.mainContent.extractText(p, 2).value shouldBe "If the problem continues, ask your client to check if their details are up to date in the country-by-country reporting service."
      doc.mainContent.extractText(p, 3).value shouldBe s"They can use this link to sign in: $cbcReportingUrl"
      doc.mainContent.extractText(p, 4).value shouldBe "Once we receive their details or confirmation, you can come back here and request authorisation."
    }

    "link to the country-by-country service" in {
      doc.mainContent.extractLink(1).value shouldBe TestLink(cbcReportingUrl, cbcReportingUrl)
    }

    "have a start again button" in {
      doc.mainContent.extractLinkButton("showClientTypeButton").value shouldBe TestLink(
        "Start again",
        routes.StartJourneyController.startJourney(AgentJourneyType.AuthorisationRequest).url
      )
    }
  }
}
