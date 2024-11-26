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
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.journey.ClientExitPage

class ClientExitPageSpec extends ViewSpecSupport {

  val viewTemplate: ClientExitPage = app.injector.instanceOf[ClientExitPage]

  case class ExpectedStrings(title: String)
  private val exitPageContent: Map[ClientExitType, ExpectedStrings] = Map(
    ClientExitType.AgentSuspended -> ExpectedStrings(
      title = "You cannot appoint this tax agent - Appoint someone to deal with HMRC for you - GOV.UK"
    ),
    ClientExitType.NoOutstandingRequests ->  ExpectedStrings(
      title = "There are no outstanding authorisation requests for you to respond to - Appoint someone to deal with HMRC for you - GOV.UK"
    ),
    ClientExitType.CannotFindAuthorisationRequest -> ExpectedStrings(
      title = "We cannot find this authorisation request - Appoint someone to deal with HMRC for you - GOV.UK"
    ),
    ClientExitType.AuthorisationRequestExpired -> ExpectedStrings(
      title = "This authorisation request has already expired - Appoint someone to deal with HMRC for you - GOV.UK"
    )
  )

  exitPageContent.map(partialInfo => s"ClientExitPage for ${partialInfo._1} view" should {
      val expectedStrings = partialInfo._2
      val view: HtmlFormat.Appendable = viewTemplate(partialInfo._1)
      val doc: Document = Jsoup.parse(view.body)
      "have the right title" in {
        doc.title() shouldBe expectedStrings.title
      }
      "have a language switcher" in {
//        doc.hasLanguageSwitch shouldBe true
      }
    })
}
