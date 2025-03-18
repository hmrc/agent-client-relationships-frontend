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

package uk.gov.hmrc.agentclientrelationshipsfrontend.views

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.UserTimedOut

class UserTimedOutSpec extends ViewSpecSupport {

  val viewTemplate: UserTimedOut = app.injector.instanceOf[UserTimedOut]

  val testUrl = "/url"

  object ExpectedClient {
    val title = "You have been signed out - Appoint someone to deal with HMRC for you - GOV.UK"
    val heading = "You have been signed out"
    val para1 = "You have not done anything for 15 minutes, so we have signed you out to keep your account secure."
    val linkText1 = "Start again"
  }

  object ExpectedAgent {
    val authorisationRequestTitle = "You have been signed out - Ask a client to authorise you - GOV.UK"
    val cancelAuthorisationTitle = "You have been signed out - Cancel a client’s authorisation - GOV.UK"
    val heading = "You have been signed out"
    val para1 = "You have not done anything for 15 minutes, so we have signed you out to keep your account secure."
    val linkText1 = "Sign in again"
    val para2 = s"$linkText1 to use this service."
  }

  "UserTimedOut view" when {
    "viewed as a client" should {
      val view: HtmlFormat.Appendable = viewTemplate(Some(testUrl), isAgent = true)
      val doc: Document = Jsoup.parse(view.body)
      "have the right title" in {
        doc.title() shouldBe ExpectedClient.title
      }
      "have a language switcher" in {
        doc.hasLanguageSwitch shouldBe true
      }
      "have the right heading" in {
        doc.mainContent.extractText(h1, 1).value shouldBe ExpectedClient.heading
      }
      "have the right paras" in {
        doc.mainContent.extractText(p, 1).value shouldBe ExpectedClient.para1
      }
      "have a link button" in {
        doc.mainContent.extractLinkButton(1).value shouldBe TestLink(ExpectedClient.linkText1, testUrl)
      }
    }
    "viewed as an agent on an authorisation request journey" should {
      val view: HtmlFormat.Appendable = viewTemplate(Some(testUrl), Some("Ask a client to authorise you"), isAgent = true)
      val doc: Document = Jsoup.parse(view.body)
      "have the right title" in {
        doc.title() shouldBe ExpectedAgent.authorisationRequestTitle
      }
      "have a language switcher" in {
        doc.hasLanguageSwitch shouldBe true
      }
      "have the right heading" in {
        doc.mainContent.extractText(h1, 1).value shouldBe ExpectedAgent.heading
      }
      "have the right paras" in {
        doc.mainContent.extractText(p, 1).value shouldBe ExpectedAgent.para1
        doc.mainContent.extractText(p, 2).value shouldBe ExpectedAgent.para2
      }
      "have a link" in {
        doc.mainContent.extractLink(1).value shouldBe TestLink(ExpectedAgent.linkText1, testUrl)
      }
    }
    "viewed as an agent on a cancel authorisation journey" should {
      val view: HtmlFormat.Appendable = viewTemplate(Some(testUrl), Some("Cancel a client’s authorisation"), isAgent = true)
      val doc: Document = Jsoup.parse(view.body)
      "have the right title" in {
        doc.title() shouldBe ExpectedAgent.cancelAuthorisationTitle
      }
      "have a language switcher" in {
        doc.hasLanguageSwitch shouldBe true
      }
      "have the right heading" in {
        doc.mainContent.extractText(h1, 1).value shouldBe ExpectedAgent.heading
      }
      "have the right paras" in {
        doc.mainContent.extractText(p, 1).value shouldBe ExpectedAgent.para1
        doc.mainContent.extractText(p, 2).value shouldBe ExpectedAgent.para2
      }
      "have a link" in {
        doc.mainContent.extractLink(1).value shouldBe TestLink(ExpectedAgent.linkText1, testUrl)
      }
    }
  }
}
