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
import uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.client.routes
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.client.AuthoriseAgentStartPage

import scala.language.postfixOps

class AuthoriseAgentStartPageSpec extends ViewSpecSupport {

  val viewTemplate: AuthoriseAgentStartPage = app.injector.instanceOf[AuthoriseAgentStartPage]
  val agentName: String = "ABC Accountants"
  val taxServiceNames: Map[String, String] = Map(
    "income-tax" -> "Making Tax Digital for Income Tax",
    "income-record-viewer" -> "",
    "vat" -> "VAT",
    "capital-gains-tax-uk-property" -> "Capital Gains Tax on UK property account",
    "plastic-packaging-tax" -> "Plastic Packaging Tax",
    "country-by-country-reporting" -> "Country-by-country Reports",
    "pillar-2" -> "Pillar 2 top-up taxes",
    "trusts-and-estates" -> "Trust or an Estate"
  )

  def h1Expected(taxService: String): String = taxService match {
    case "income-record-viewer" => s"Authorise $agentName to view your Income Record"
    case "trusts-and-estates" => s"Authorise $agentName to maintain your Trust or an Estate"
    case _ => s"Authorise $agentName to manage your ${taxServiceNames(taxService)}"
  }

  def p1Expected(taxService: String): String = taxService match {
    case "income-tax" => "To authorise an agent you need to sign in with the user ID you use for Making Tax Digital for Income tax."
    case "income-record-viewer" => "You need to sign in with the user ID you use for your personal tax account."
    case "trusts-and-estates" => "You need to sign in with the user ID you use for maintaining your trust or estate."
    case _ => s"You need to sign in with the user ID you use for ${taxServiceNames(taxService)}."
  }

  def p2Expected(taxService: String): String = taxService match {
    case "income-tax" => "If you do not have a user ID for Making Tax Digital for Income Tax, you can either:"
    case _ => "If you do not have sign in details, you‘ll be able to create some."
  }


  "AuthoriseAgentStartPage for authorisation journey view" should {

    val uid = "uid"
    for (taxService <- taxServiceNames.keySet.toList) {
      val view: HtmlFormat.Appendable = viewTemplate(agentName, taxService, uid)
      val doc: Document = Jsoup.parse(view.body)
      s"include the correct H1 text for $taxService" in {
        doc.mainContent.extractText(h1, 1).value shouldBe h1Expected(taxService)
      }
      
      s"include the correct p1 text for $taxService" in {
        doc.mainContent.extractText(p, 1).value shouldBe p1Expected(taxService)
      }

      s"include the correct p2 text for $taxService" in {
        doc.mainContent.extractText(p, 2).value shouldBe p2Expected(taxService)
      }
      s"have correct link button for $taxService" in {
        val expectedUrl = s"/agent-client-relationships/authorisation-response/$uid/$taxService/consent-information"
        doc.mainContent.extractLinkButton(1).value shouldBe TestLink("Start now", expectedUrl)
      }
      
      s"have correct 'Decline' link for $taxService" in {
        val expectedUrl = routes.DeclineRequestController.show(uid, taxService).url
        doc.mainContent.extractLink(1).value shouldBe TestLink(s"I do not want $agentName to act for me", expectedUrl)
      }
    }
  }
}

