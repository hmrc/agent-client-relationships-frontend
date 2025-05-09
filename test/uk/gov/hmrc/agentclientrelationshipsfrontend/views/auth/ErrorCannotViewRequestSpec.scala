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
import org.scalatestplus.mockito.MockitoSugar.mock
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.auth.ErrorCannotViewRequest

class ErrorCannotViewRequestSpec extends ViewSpecSupport with BeforeAndAfterEach :

  val viewTemplate: ErrorCannotViewRequest = app.injector.instanceOf[ErrorCannotViewRequest]
  override implicit val appConfig: AppConfig = mock[AppConfig]

  object Expected :
    val title = "You cannot view this authorisation request - Appoint someone to deal with HMRC for you - GOV.UK"
    val heading = "You cannot view this authorisation request"
    val para1 = "You have signed in using an agent user ID."
    val para2 = "If you are the agent, ask your client to respond to the authorisation request."
    val link1 = "Manage your recent authorisation requests"
    val link2 = "Finish and sign out"

  "ErrorCannotViewRequest view" when :

    "generated for a business client" should :

      val view: HtmlFormat.Appendable = viewTemplate()
      val doc: Document = Jsoup.parse(view.body)

      "have the right title" in :
        doc.title() shouldBe Expected.title

      "have a language switcher" in :
        doc.hasLanguageSwitch shouldBe true

      "have the right heading" in :
        doc.mainContent.extractText(h1, 1).value shouldBe Expected.heading

      "have the right paras" in :
        doc.mainContent.extractText(p, 1).value shouldBe Expected.para1
        doc.mainContent.extractText(p, 2).value shouldBe Expected.para2

      "have the right links" in :
        doc.mainContent.extractLink(1).value shouldBe TestLink(Expected.link1, "/agent-client-relationships/manage-authorisation-requests")
        doc.mainContent.extractLink(2).value shouldBe TestLink(Expected.link2, "/agent-client-relationships/sign-out?isAgent=true")
