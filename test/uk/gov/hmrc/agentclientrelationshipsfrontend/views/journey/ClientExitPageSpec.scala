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
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.ClientExitType
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.client.ClientExitPage

class ClientExitPageSpec extends ViewSpecSupport {

  val viewTemplate: ClientExitPage = app.injector.instanceOf[ClientExitPage]

  case class ExpectedStrings(title: String, paragraphs: List[String])

  private val exitPageContent: Map[ClientExitType, ExpectedStrings] = Map(
    ClientExitType.AgentSuspended -> ExpectedStrings(
      title = "You cannot appoint this tax agent - Appoint someone to deal with HMRC for you - GOV.UK",

      List("This tax agent cannot manage your Making Tax Digital for Income Tax at this time.",
        "If you have any questions, contact the tax agent who sent you this request.")
    ),
    ClientExitType.NoOutstandingRequests -> ExpectedStrings(
      title = "There are no outstanding authorisation requests for you to respond to - Appoint someone to deal with HMRC for you - GOV.UK",

      List("If you think this is wrong, contact the agent who sent you the request or view your request history.")
    ),
    ClientExitType.CannotFindAuthorisationRequest -> ExpectedStrings(
      title = "We cannot find this authorisation request - Appoint someone to deal with HMRC for you - GOV.UK",

      List("We cannot find a request from Agent Name.",
        "Make sure you have signed up for the tax service you need. Ask your agent if you are not sure.",
        "You need to sign in with the correct Government Gateway user ID. It is possible to have more than one, " +
          "so make sure it is the same one you used to sign up to the tax service the authorisation request is for. " +
          "Try signing in with a different Government Gateway user ID (the one that you use for managing your personal tax affairs).")
    ),
    ClientExitType.AuthorisationRequestExpired -> ExpectedStrings(
      title = "This authorisation request has already expired - Appoint someone to deal with HMRC for you - GOV.UK",

      List("This request expired on 11.11.2024. For details, view your history to check for any expired, cancelled or" +
        " outstanding requests.",
        "If your agent has sent you a recent request, make sure you have signed up to the tax service you need.",
        "You could also check you have signed in with the correct Government Gateway user ID. It must be the same one" +
          " you used to sign up to the tax service the authorisation request is for.",
        "Sign in with the Government Gateway user ID you use for managing your personal tax affairs.")
    ),
    ClientExitType.AuthorisationRequestCancelled -> ExpectedStrings(
      title = "This authorisation request request has been cancelled - Appoint someone to deal with HMRC for you - GOV.UK",

      List("This request was cancelled by your agent on 11.11.2024. For details, view your history to check for any expired," +
        " cancelled or outstanding requests.",
        "If your agent has sent you a recent request, make sure you have signed up to the tax service you need.",
        "You could also check you have signed in with the correct Government Gateway user ID. It must be the same one you" +
          " used to sign up to the tax service the authorisation request is for.",
        "Sign in with the Government Gateway user ID you use for managing your personal tax affairs."),
    ),
    ClientExitType.AlreadyRespondedToAuthorisationRequest -> ExpectedStrings(
      title = "This authorisation request has already been responded to - Appoint someone to deal with HMRC for you - GOV.UK",

      List("This request has already been responded to on 11.11.2024. For details, view your history to check for any" +
        " expired, cancelled or outstanding requests.",
        "If your agent has sent you a recent request, make sure you have signed up to the tax service you need.",
        "You could also check you have signed in with the correct Government Gateway user ID. It must be the same one" +
          " you used to sign up to the tax service the authorisation request is for.",
        "Sign in with the Government Gateway user ID you use for managing your personal tax affairs.")
    )
  )

  exitPageContent.map(partialInfo => s"ClientExitPage for ${partialInfo._1} view" should {
    val expectedStrings = partialInfo._2
    val view: HtmlFormat.Appendable = viewTemplate(partialInfo._1, Some("Agent Name"), Some("11.11.2024"))
    val doc: Document = Jsoup.parse(view.body)

    "have the right title" in {
      doc.title() shouldBe expectedStrings.title
    }

    "display paragraph content" in {
      expectedStrings.paragraphs.indices.foreach {
        index =>
          doc.select(".govuk-body").get(index).text() shouldBe expectedStrings.paragraphs(index)
      }
    }

    "have a language switcher" in {
      doc.hasLanguageSwitch shouldBe true
    }
  })
}
