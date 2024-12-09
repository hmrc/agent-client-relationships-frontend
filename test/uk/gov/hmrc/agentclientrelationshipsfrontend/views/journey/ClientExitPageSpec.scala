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
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.ClientExitType.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.JourneyType.ClientResponse
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{ClientJourney, ClientJourneyRequest}
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.client.ClientExitPage

import java.time.{Instant, LocalDateTime, ZoneOffset}

class ClientExitPageSpec extends ViewSpecSupport {
  val testStatus: InvitationStatus = Accepted
  val testLastModifiedDate: Instant = LocalDateTime.of(2024, 1, 10, 0, 0).toInstant(ZoneOffset.UTC)
  val testAgentName = "Agent Name"

  val viewTemplate: ClientExitPage = app.injector.instanceOf[ClientExitPage]

  case class ExpectedStrings(title: String, paragraphs: List[String])

  private def clientJourney(status:Option[InvitationStatus]): ClientJourney = ClientJourney(
    journeyType = ClientResponse, agentName = Some(testAgentName), status = status, lastModifiedDate = Some(testLastModifiedDate)
  )

  object Expected {
    val agentSuspendedLabel = "You cannot appoint this tax agent - Appoint someone to deal with HMRC for you - GOV.UK"
    val agentSuspendedParagraphOne = "This tax agent cannot manage your Making Tax Digital for Income Tax at this time."
    val agentSuspendedParagraphTwo = "If you have any questions, contact the tax agent who sent you this request."

    val noOutstandingRequestsLabel = "There are no outstanding authorisation requests for you to respond to - Appoint someone to deal with HMRC for you - GOV.UK"
    val noOutstandingRequestsParagraphOne = "If you think this is wrong, contact the agent who sent you the request or view your request history."

    val cannotFindAuthorisationRequestLabel = "We cannot find this authorisation request - Appoint someone to deal with HMRC for you - GOV.UK"
    val cannotFindAuthorisationRequestParagraphOne = "We cannot find a request from Agent Name."
    val cannotFindAuthorisationRequestParagraphTwo = "Make sure you have signed up for the tax service you need. Ask your agent if you are not sure."
    val cannotFindAuthorisationRequestParagraphThree = "You need to sign in with the correct Government Gateway user ID. It is possible to have more than one, so make sure it is the same one you used to sign up to the tax service the authorisation request is for. Try signing in with a different Government Gateway user ID (the one that you use for managing your personal tax affairs)."

    val authorisationRequestExpiredLabel = "This authorisation request has already expired - Appoint someone to deal with HMRC for you - GOV.UK"
    val authorisationRequestExpiredParagraphOne = "This request expired on 10/01/2024. For details, view your history to check for any expired, cancelled or outstanding requests."
    val authorisationRequestExpiredParagraphTwo = "If your agent has sent you a recent request, make sure you have signed up to the tax service you need."
    val authorisationRequestExpiredParagraphThree = "You could also check you have signed in with the correct Government Gateway user ID. It must be the same one you used to sign up to the tax service the authorisation request is for."
    val authorisationRequestExpiredParagraphFour = "Sign in with the Government Gateway user ID you use for managing your personal tax affairs."

    val authorisationRequestCancelledLabel = "This authorisation request request has been cancelled - Appoint someone to deal with HMRC for you - GOV.UK"
    val authorisationRequestCancelledParagraphOne = "This request was cancelled by your agent on 10/01/2024. For details, view your history to check for any expired, cancelled or outstanding requests."
    val authorisationRequestCancelledParagraphTwo = "If your agent has sent you a recent request, make sure you have signed up to the tax service you need."
    val authorisationRequestCancelledParagraphThree = "You could also check you have signed in with the correct Government Gateway user ID. It must be the same one you used to sign up to the tax service the authorisation request is for."
    val authorisationRequestCancelledParagraphFour = "Sign in with the Government Gateway user ID you use for managing your personal tax affairs."

    val alreadyRespondedToAuthorisationRequestLabel = "This authorisation request has already been responded to - Appoint someone to deal with HMRC for you - GOV.UK"
    val alreadyRespondedToAuthorisationRequestParagraphOne = s"This request has already been responded to on 10/01/2024. For details, view your history to check for any expired, cancelled or outstanding requests."
    val alreadyRespondedToAuthorisationRequestParagraphTwo = "If your agent has sent you a recent request, make sure you have signed up to the tax service you need."
    val alreadyRespondedToAuthorisationRequestParagraphThree = "You could also check you have signed in with the correct Government Gateway user ID. It must be the same one you used to sign up to the tax service the authorisation request is for."
    val alreadyRespondedToAuthorisationRequestParagraphFour = "Sign in with the Government Gateway user ID you use for managing your personal tax affairs."
  }

  "ClientExitPage for AgentSuspended view" should {
    implicit val clientJourneyRequest: ClientJourneyRequest[?] = new ClientJourneyRequest(clientJourney(Some(Pending)), request)

    val view: HtmlFormat.Appendable = viewTemplate(AgentSuspended)
    val doc: Document = Jsoup.parse(view.body)

    "have the right title" in {
      doc.title() shouldBe Expected.agentSuspendedLabel
    }

    "display paragraph content" in {
          doc.select(".govuk-body").get(0).text() shouldBe Expected.agentSuspendedParagraphOne
          doc.select(".govuk-body").get(1).text() shouldBe Expected.agentSuspendedParagraphTwo
    }

    "have a language switcher" in {
      doc.hasLanguageSwitch shouldBe true
    }

    "have the correct link in the details component content" in {

      doc.mainContent.extractLink(1).value shouldBe TestLink("Finish and sign out", "/agent-client-relationships/sign-out")
    }
  }

  "ClientExitPage for NoOutstandingRequests view" should {

    implicit val clientJourneyRequest: ClientJourneyRequest[?] = new ClientJourneyRequest(clientJourney(None), request)
    val view: HtmlFormat.Appendable = viewTemplate(NoOutstandingRequests)
    val doc: Document = Jsoup.parse(view.body)

    "have the right title" in {
      doc.title() shouldBe Expected.noOutstandingRequestsLabel
    }

    "display paragraph content" in {
      doc.select(".govuk-body").get(0).text() shouldBe Expected.noOutstandingRequestsParagraphOne
    }

    "have a language switcher" in {
      doc.hasLanguageSwitch shouldBe true
    }

    "have the correct link in the details component content" in {

      doc.mainContent.extractLink(1).value shouldBe TestLink("view your request history", "http://localhost:9568#history")
    }
  }

  "ClientExitPage for CannotFindAuthorisationRequest view" should {
    implicit val clientJourneyRequest: ClientJourneyRequest[?] = new ClientJourneyRequest(clientJourney(None), request)

    val view: HtmlFormat.Appendable = viewTemplate(CannotFindAuthorisationRequest)
    val doc: Document = Jsoup.parse(view.body)

    "have the right title" in {
      doc.title() shouldBe Expected.cannotFindAuthorisationRequestLabel
    }

    "display paragraph content" in {
      doc.select(".govuk-body").get(0).text() shouldBe Expected.cannotFindAuthorisationRequestParagraphOne
      doc.select(".govuk-body").get(1).text() shouldBe Expected.cannotFindAuthorisationRequestParagraphTwo
      doc.select(".govuk-body").get(2).text() shouldBe Expected.cannotFindAuthorisationRequestParagraphThree
    }

    "have a language switcher" in {
      doc.hasLanguageSwitch shouldBe true
    }

    "have the correct link in the details component content" in {

      doc.mainContent.extractLink(1).value shouldBe TestLink("Make sure you have signed up for the tax service you need.", "https://www.gov.uk/authorise-an-agent-to-deal-with-certain-tax-services-for-you")
    }
  }

  "ClientExitPage for AuthorisationRequestExpired view" should {
    implicit val clientJourneyRequest: ClientJourneyRequest[?] = new ClientJourneyRequest(clientJourney(Some(Expired)), request)

    val view: HtmlFormat.Appendable = viewTemplate(AuthorisationRequestExpired)
    val doc: Document = Jsoup.parse(view.body)

    "have the right title" in {
      doc.title() shouldBe Expected.authorisationRequestExpiredLabel
    }

    "display paragraph content" in {
      doc.select(".govuk-body").get(0).text() shouldBe Expected.authorisationRequestExpiredParagraphOne
      doc.select(".govuk-body").get(1).text() shouldBe Expected.authorisationRequestExpiredParagraphTwo
      doc.select(".govuk-body").get(2).text() shouldBe Expected.authorisationRequestExpiredParagraphThree
      doc.select(".govuk-body").get(3).text() shouldBe Expected.authorisationRequestExpiredParagraphFour
    }

    "have a language switcher" in {
      doc.hasLanguageSwitch shouldBe true
    }

    "have the correct link in the details component content" in {

      doc.mainContent.extractLink(1).value shouldBe TestLink("view your history", "http://localhost:9568#history")
    }
  }

  "ClientExitPage for AuthorisationRequestCancelled view" should {
    implicit val clientJourneyRequest: ClientJourneyRequest[?] = new ClientJourneyRequest(clientJourney(Some(Cancelled)), request)

    val view: HtmlFormat.Appendable = viewTemplate(AuthorisationRequestCancelled)
    val doc: Document = Jsoup.parse(view.body)

    "have the right title" in {
      doc.title() shouldBe Expected.authorisationRequestCancelledLabel
    }

    "display paragraph content" in {
      doc.select(".govuk-body").get(0).text() shouldBe Expected.authorisationRequestCancelledParagraphOne
      doc.select(".govuk-body").get(1).text() shouldBe Expected.authorisationRequestCancelledParagraphTwo
      doc.select(".govuk-body").get(2).text() shouldBe Expected.authorisationRequestCancelledParagraphThree
      doc.select(".govuk-body").get(3).text() shouldBe Expected.authorisationRequestCancelledParagraphFour
    }

    "have a language switcher" in {
      doc.hasLanguageSwitch shouldBe true
    }

    "have the correct link in the details component content" in {

      doc.mainContent.extractLink(1).value shouldBe TestLink("view your history", "http://localhost:9568#history")
    }
  }

  "ClientExitPage for AlreadyRespondedToAuthorisationRequest view" should {
    implicit val clientJourneyRequest: ClientJourneyRequest[?] = new ClientJourneyRequest(clientJourney(Some(Rejected)), request)

    val view: HtmlFormat.Appendable = viewTemplate(AlreadyRespondedToAuthorisationRequest)()
    val doc: Document = Jsoup.parse(view.body)

    "have the right title" in {
      doc.title() shouldBe Expected.alreadyRespondedToAuthorisationRequestLabel
    }

    "display paragraph content" in {
      doc.select(".govuk-body").get(0).text() shouldBe Expected.alreadyRespondedToAuthorisationRequestParagraphOne
      doc.select(".govuk-body").get(1).text() shouldBe Expected.alreadyRespondedToAuthorisationRequestParagraphTwo
      doc.select(".govuk-body").get(2).text() shouldBe Expected.alreadyRespondedToAuthorisationRequestParagraphThree
      doc.select(".govuk-body").get(3).text() shouldBe Expected.alreadyRespondedToAuthorisationRequestParagraphFour
    }

    "have a language switcher" in {
      doc.hasLanguageSwitch shouldBe true
    }

    "have the correct link in the details component content" in {

      doc.mainContent.extractLink(2).value shouldBe TestLink("view your history", "http://localhost:9568#history")
    }
  }
}