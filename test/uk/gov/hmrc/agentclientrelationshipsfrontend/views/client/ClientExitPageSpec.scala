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

    val agentSuspendedLabel = "You cannot appoint this tax agent - Appoint someone to deal with HMRC for you - GOV.UK"
    val agentSuspendedParagraphOne = "This tax agent cannot manage your Making Tax Digital for Income Tax at this time."
    val agentSuspendedParagraphTwo = "If you have any questions, contact the tax agent who sent you this request."

    val noOutstandingRequestsLabel = "There are no outstanding authorisation requests for you to respond to - Appoint someone to deal with HMRC for you - GOV.UK"
    val noOutstandingRequestsParagraphOne = "If you think this is wrong, contact the agent who sent you the request or view your request history."
  }

  "ClientExitPage for CannotFindAuthorisationRequest view" should {
    val view: HtmlFormat.Appendable = viewTemplate(CannotFindAuthorisationRequest, userIsLoggedIn = true, Some(testLastModifiedDate), Some(RedirectUrl("/url")))
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
        Expected.cannotFindAuthorisationRequestSignIn,
        routes.SignOutController.signOut(isAgent = false, continueUrl = Some(RedirectUrl("/url"))).url
      )
      doc.mainContent.extractLink(2).value shouldBe TestLink(
        Expected.cannotFindAuthorisationRequestSignOut,
        routes.SignOutController.signOut(isAgent = false, continueUrl = None).url
      )
    }

    "have a language switcher" in {
      doc.hasLanguageSwitch shouldBe true
    }
  }

  "ClientExitPage for AuthorisationRequestExpired view" should {
    val view: HtmlFormat.Appendable = viewTemplate(AuthorisationRequestExpired, userIsLoggedIn = true, Some(testLastModifiedDate))
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

      doc.mainContent.extractLink(1).value shouldBe TestLink("view your history", "/agent-client-relationships/manage-your-tax-agents#history")
    }
  }

  "ClientExitPage for AuthorisationRequestCancelled view" should {
    val view: HtmlFormat.Appendable = viewTemplate(AuthorisationRequestCancelled, userIsLoggedIn = true, Some(testLastModifiedDate))
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

      doc.mainContent.extractLink(1).value shouldBe TestLink("view your history", "/agent-client-relationships/manage-your-tax-agents#history")
    }
  }

  "ClientExitPage for AlreadyRespondedToAuthorisationRequest view" should {
    val view: HtmlFormat.Appendable = viewTemplate(AlreadyRespondedToAuthorisationRequest, userIsLoggedIn = true, Some(testLastModifiedDate))
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

      doc.mainContent.extractLink(2).value shouldBe TestLink("make sure you have signed up to the tax service you need.", "https://www.gov.uk/guidance/authorise-an-agent-to-deal-with-certain-tax-services-for-you")
    }
  }

  "ClientExitPage for AgentSuspended view" should {
    val view: HtmlFormat.Appendable = viewTemplate(AgentSuspended, userIsLoggedIn = false)
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

      doc.mainContent.extractLink(1).value shouldBe TestLink("Finish and sign out", "/agent-client-relationships/sign-out?isAgent=false")
    }
  }

  "ClientExitPage for NoOutstandingRequests view" should {
    val view: HtmlFormat.Appendable = viewTemplate(NoOutstandingRequests, userIsLoggedIn = false)
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

      doc.mainContent.extractLink(1).value shouldBe TestLink("view your request history", "/agent-client-relationships/manage-your-tax-agents#history")
    }
  }
}