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
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.{ProcessingYourRequestPage, UserTimedOut}

class ProcessingYourRequestPageSpec extends ViewSpecSupport {

  val viewTemplate: ProcessingYourRequestPage = app.injector.instanceOf[ProcessingYourRequestPage]

  val testUrl = "/url"

  object Expected {
    val title = "We’re processing your request - Appoint someone to deal with HMRC for you - GOV.UK"
    val heading = "We’re processing your request"
    val para1 = "You reached this page because you clicked twice to submit a request."
    val para2  = "To see the confirmation screen, wait a few seconds and then select ‘continue’."
    val button = "Continue"
  }

  "UserTimedOut view" when {
    "viewed as a client" should {
      val view: HtmlFormat.Appendable = viewTemplate(testUrl, isAgent = false)
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
        doc.mainContent.extractLinkButton(1).value shouldBe TestLink(Expected.button, testUrl)
      }
    }
  }
}
