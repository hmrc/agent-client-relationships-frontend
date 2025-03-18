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

package uk.gov.hmrc.agentclientrelationshipsfrontend.views.auth

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatest.BeforeAndAfterEach
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.routes
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.auth.NotAuthorisedAsClient

class NotAuthorisedAsClientSpec extends ViewSpecSupport with BeforeAndAfterEach {

  val viewTemplate: NotAuthorisedAsClient = app.injector.instanceOf[NotAuthorisedAsClient]

  val testUrl = "/url"

  object Expected {
    val title = "You cannot access this page - Appoint someone to deal with HMRC for you - GOV.UK"
    val heading = "You cannot access this page"
    val para1 = "This page can only be accessed by your client."
    val para2 = "Ask your client to go to the link that you tried to access, so they can accept your authorisation request."
    val button = "Sign out"
  }

  "NotAuthorisedAsClient view" when {
    val view: HtmlFormat.Appendable = viewTemplate()
    val doc: Document = Jsoup.parse(view.body)
    "have the right title" in {
      doc.title() shouldBe Expected.title
    }
    "have a language switcher" in {
      doc.hasLanguageSwitch shouldBe true
    }
    "have the right heading" in {
      doc.mainContent.extractText(h1, 1).value shouldBe Expected.heading
    }
    "have the right paras" in {
      doc.mainContent.extractText(p, 1).value shouldBe Expected.para1
      doc.mainContent.extractText(p, 2).value shouldBe Expected.para2
    }
    "have a link button" in {
      doc.mainContent.extractLinkButton(1).value shouldBe TestLink(Expected.button, routes.SignOutController.signOut(isAgent = false).url)
    }
  }
}
