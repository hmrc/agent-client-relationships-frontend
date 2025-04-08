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

package uk.gov.hmrc.agentclientrelationshipsfrontend.views.client

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.HMRCMTDIT
import uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.routes
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.ClientExitType.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.client.ClientExitPage
import uk.gov.hmrc.play.bootstrap.binders.RedirectUrl

import java.time.{Instant, LocalDateTime, ZoneOffset}

class ClientExitPageSpec extends ViewSpecSupport {
  val testStatus: InvitationStatus = Accepted
  val testLastModifiedDate: Instant = LocalDateTime.of(2024, 1, 10, 0, 0).toInstant(ZoneOffset.UTC)
  val testAgentName = "Agent Name"

  val viewTemplate: ClientExitPage = app.injector.instanceOf[ClientExitPage]

  object Expected {
    val cannotFindAuthorisationRequestLabel = "There are no requests linked to the sign-in details you used - Appoint someone to deal with HMRC for you - GOV.UK"
    val cannotFindAuthorisationRequestParagraphOne = "You may have signed in with the wrong user ID."
    val cannotFindAuthorisationRequestParagraphTwo = "You need to use the sign-in details which match the tax service the agent wants to manage for you."
    val cannotFindAuthorisationRequestParagraphThree = "For example, if an agent wants to manage your VAT you should:"
    val cannotFindAuthorisationRequestBulletOne = "make sure you have registered for VAT and have an online VAT account"
    val cannotFindAuthorisationRequestBulletTwo = "use your VAT sign-in details when responding to the authorisation request"
    val cannotFindAuthorisationRequestSignIn = "Sign in with a different user ID"
    val cannotFindAuthorisationRequestSignOut = "Finish and sign out"

    val authorisationRequestExpiredLabel = "This authorisation request has expired - Appoint someone to deal with HMRC for you - GOV.UK"
    val authorisationRequestExpiredParagraphOne = "This request expired on 10 January 2024. Authorisation requests expire after 21 days."
    val authorisationRequestExpiredParagraphTwo = "If you want this agent to manage your Making Tax Digital for Income Tax, ask them to send you another authorisation request."
    val authorisationRequestExpiredLinkOne = "Manage your tax agents"
    val authorisationRequestExpiredLinkTwo = "Finish and sign out"

    val authorisationRequestCancelledLabel = "This authorisation request request has been cancelled - Appoint someone to deal with HMRC for you - GOV.UK"
    val authorisationRequestCancelledParagraphOne = "This request was cancelled by the agent on 10 January 2024."
    val authorisationRequestCancelledParagraphTwo = "If you want this agent to manage your Making Tax Digital for Income Tax, ask them to send you another authorisation request."
    val authorisationRequestCancelledLinkOne = "Manage your tax agents"
    val authorisationRequestCancelledLinkTwo = "Finish and sign out"

    val alreadyAcceptedAuthorisationRequestLabel = "You have already accepted this authorisation request - Appoint someone to deal with HMRC for you - GOV.UK"
    val alreadyAcceptedAuthorisationRequestParagraphOne = "We received a response to this request on 10 January 2024."
    val alreadyAcceptedAuthorisationRequestParagraphTwo = "To check the details, or to cancel this authorisation, go to Manage your tax agents."

    val alreadyRefusedAuthorisationRequestLabel = "You already refused this authorisation request - Appoint someone to deal with HMRC for you - GOV.UK"
    val alreadyRefusedAuthorisationRequestParagraphOne = "This request was refused on 10 January 2024."
    val alreadyRefusedAuthorisationRequestParagraphTwo = "If you want this agent to manage your Making Tax Digital for Income Tax, ask them to send you another authorisation request."
    val alreadyRefusedAuthorisationRequestLinkOne = "Manage your tax agents"
    val alreadyRefusedAuthorisationRequestLinkTwo = "Finish and sign out"

    val agentSuspendedLabel = "You cannot appoint this tax agent - Appoint someone to deal with HMRC for you - GOV.UK"
    val agentSuspendedParagraphOne = "This tax agent cannot manage your Making Tax Digital for Income Tax at this time."
    val agentSuspendedParagraphTwo = "If you have any questions, contact the tax agent who sent you this request."

    val noOutstandingRequestsLabel = "There are no outstanding authorisation requests for you to respond to - Appoint someone to deal with HMRC for you - GOV.UK"
    val noOutstandingRequestsParagraphOne = "If you think this is wrong, contact the agent who sent you the request or view your request history."
  }

  "ClientExitPage for CannotFindAuthorisationRequest view" should {
    val view: HtmlFormat.Appendable = viewTemplate(
      exitType = CannotFindAuthorisationRequest,
      userIsLoggedIn = true,
      lastModifiedDate = Some(testLastModifiedDate),
      continueUrl = Some(RedirectUrl("/url")),
      service = HMRCMTDIT
    )
    val doc: Document = Jsoup.parse(view.body)

    "have the right title" in {
      doc.title() shouldBe Expected.cannotFindAuthorisationRequestLabel
    }

    "display paragraph content" in {
      doc.mainContent.extractText(p, 1).value shouldBe Expected.cannotFindAuthorisationRequestParagraphOne
      doc.mainContent.extractText(p, 2).value shouldBe Expected.cannotFindAuthorisationRequestParagraphTwo
      doc.mainContent.extractText(p, 3).value shouldBe Expected.cannotFindAuthorisationRequestParagraphThree
    }

    "display bullets" in {
      doc.extractList(1) shouldBe List(
        Expected.cannotFindAuthorisationRequestBulletOne,
        Expected.cannotFindAuthorisationRequestBulletTwo
      )
    }

    "display correct links" in {
      doc.mainContent.extractLink(1).value shouldBe TestLink(
        text = Expected.cannotFindAuthorisationRequestSignIn,
        href = routes.SignOutController.signOut(
          isAgent = false,
          continueUrl = Some(RedirectUrl("/url"))
        ).url
      )
      doc.mainContent.extractLink(2).value shouldBe TestLink(
        text = Expected.cannotFindAuthorisationRequestSignOut,
        href = routes.SignOutController.signOut(
          isAgent = false,
          continueUrl = None
        ).url
      )
    }

    "have a language switcher" in {
      doc.hasLanguageSwitch shouldBe true
    }
  }

  "ClientExitPage for AuthorisationRequestExpired view" should {
    val view: HtmlFormat.Appendable = viewTemplate(
      exitType = AuthorisationRequestExpired,
      userIsLoggedIn = true,
      lastModifiedDate = Some(testLastModifiedDate),
      service = HMRCMTDIT
    )
    val doc: Document = Jsoup.parse(view.body)

    "have the right title" in {
      doc.title() shouldBe Expected.authorisationRequestExpiredLabel
    }

    "display paragraph content" in {
      doc.select(".govuk-body").get(0).text() shouldBe Expected.authorisationRequestExpiredParagraphOne
      doc.select(".govuk-body").get(1).text() shouldBe Expected.authorisationRequestExpiredParagraphTwo
    }

    "have the correct link text and hrefs" in {
      doc.mainContent.extractLink(1).value shouldBe TestLink(Expected.authorisationRequestExpiredLinkOne, "/agent-client-relationships/manage-your-tax-agents")
      doc.mainContent.extractLink(2).value shouldBe TestLink(Expected.authorisationRequestExpiredLinkTwo, "/agent-client-relationships/sign-out?isAgent=false")
    }

    "have a language switcher" in {
      doc.hasLanguageSwitch shouldBe true
    }
  }

  "ClientExitPage for AuthorisationRequestCancelled view" should {
    val view: HtmlFormat.Appendable = viewTemplate(
      exitType = AuthorisationRequestCancelled,
      userIsLoggedIn = true,
      lastModifiedDate = Some(testLastModifiedDate),
      service = HMRCMTDIT
    )
    val doc: Document = Jsoup.parse(view.body)

    "have the right title" in {
      doc.title() shouldBe Expected.authorisationRequestCancelledLabel
    }

    "display paragraph content" in {
      doc.select(".govuk-body").get(0).text() shouldBe Expected.authorisationRequestCancelledParagraphOne
      doc.select(".govuk-body").get(1).text() shouldBe Expected.authorisationRequestCancelledParagraphTwo
    }

    "have the correct link text and hrefs" in {
      doc.mainContent.extractLink(1).value shouldBe TestLink(
        text = Expected.authorisationRequestCancelledLinkOne,
        href = "/agent-client-relationships/manage-your-tax-agents"
      )
      doc.mainContent.extractLink(2).value shouldBe TestLink(
        text = Expected.authorisationRequestCancelledLinkTwo,
        href = "/agent-client-relationships/sign-out?isAgent=false"
      )
    }

    "have a language switcher" in {
      doc.hasLanguageSwitch shouldBe true
    }
  }

  "ClientExitPage for AlreadyAcceptedAuthorisationRequest view" should {
    val view: HtmlFormat.Appendable = viewTemplate(
      exitType = AlreadyAcceptedAuthorisationRequest,
      userIsLoggedIn = true,
      lastModifiedDate = Some(testLastModifiedDate),
      service = HMRCMTDIT
    )
    val doc: Document = Jsoup.parse(view.body)

    "have the right title" in {
      doc.title() shouldBe Expected.alreadyAcceptedAuthorisationRequestLabel
    }

    "display paragraph content" in {
      doc.select(".govuk-body").get(0).text() shouldBe Expected.alreadyAcceptedAuthorisationRequestParagraphOne
      doc.select(".govuk-body").get(1).text() shouldBe Expected.alreadyAcceptedAuthorisationRequestParagraphTwo
    }

    "have a language switcher" in {
      doc.hasLanguageSwitch shouldBe true
    }

    "have the correct link text and hrefs" in {
      doc.mainContent.extractLink(1).value shouldBe TestLink(
        text = "Manage your tax agents",
        href = "/agent-client-relationships/manage-your-tax-agents"
      )
      doc.mainContent.extractLink(2).value shouldBe TestLink(
        text = "Finish and sign out",
        href = "/agent-client-relationships/sign-out?isAgent=false"
      )
    }
  }

  "ClientExitPage for AlreadyRefusedAuthorisationRequest view" should {
    val view: HtmlFormat.Appendable = viewTemplate(
      exitType = AlreadyRefusedAuthorisationRequest,
      userIsLoggedIn = true,
      lastModifiedDate = Some(testLastModifiedDate),
      service = HMRCMTDIT
    )
    val doc: Document = Jsoup.parse(view.body)

    "have the right title" in {
      doc.title() shouldBe Expected.alreadyRefusedAuthorisationRequestLabel
    }

    "display paragraph content" in {
      doc.select(".govuk-body").get(0).text() shouldBe Expected.alreadyRefusedAuthorisationRequestParagraphOne
      doc.select(".govuk-body").get(1).text() shouldBe Expected.alreadyRefusedAuthorisationRequestParagraphTwo
    }

    "have the correct link text and hrefs" in {
      doc.mainContent.extractLink(1).value shouldBe TestLink(
        text = Expected.alreadyRefusedAuthorisationRequestLinkOne,
        href = "/agent-client-relationships/manage-your-tax-agents"
      )
      doc.mainContent.extractLink(2).value shouldBe TestLink(
        text = Expected.alreadyRefusedAuthorisationRequestLinkTwo,
        href = "/agent-client-relationships/sign-out?isAgent=false"
      )
    }

    "have a language switcher" in {
      doc.hasLanguageSwitch shouldBe true
    }
  }

  "ClientExitPage for AgentSuspended view" should {
    val view: HtmlFormat.Appendable = viewTemplate(
      exitType = AgentSuspended,
      userIsLoggedIn = false,
      service = HMRCMTDIT
    )
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
    // there should not be a sign out link as the user is not signed in
  }

  "ClientExitPage for NoOutstandingRequests view" should {
    val view: HtmlFormat.Appendable = viewTemplate(
      exitType = NoOutstandingRequests,
      userIsLoggedIn = false,
      service = HMRCMTDIT
    )
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

      doc.mainContent.extractLink(1).value shouldBe TestLink(
        text = "view your request history",
        href = "/agent-client-relationships/manage-your-tax-agents#history"
      )
    }
  }
}