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
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.journey.PageNotFound

import scala.language.postfixOps

class PageNotFoundPageSpec extends ViewSpecSupport {

  val viewTemplate: PageNotFound = app.injector.instanceOf[PageNotFound]

  object Expected {
    val heading = "Page not found"
    val title = s"$heading - Ask a client to authorise you - GOV.UK"
    val p1 = s"If you typed the web address, check it is correct."
    val p2 = s"If you pasted the web address, check you copied the entire address."
    val p3 = s"If the web address is correct, contact the tax agent who sent you the request if you still want to authorise them."
  }

  "PageNotFound view" should {
        val view: HtmlFormat.Appendable = viewTemplate()
        val doc: Document = Jsoup.parse(view.body)
        s"include the correct title" in {
          doc.title() shouldBe Expected.title
        }

        s"include the correct H1 text" in {
          doc.mainContent.extractText(h1, 1).value shouldBe Expected.heading
        }

        s"include the correct text" in {
          doc.mainContent.extractText(p, 1).value shouldBe Expected.p1
          doc.mainContent.extractText(p, 2).value shouldBe Expected.p2
          doc.mainContent.extractText(p, 3).value shouldBe Expected.p3
        }
  }
}
