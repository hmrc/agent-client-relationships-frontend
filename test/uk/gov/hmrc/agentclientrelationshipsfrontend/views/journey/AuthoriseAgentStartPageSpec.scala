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
import play.api.data.Form
import play.api.test.FakeRequest
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.journey.ConfirmClient

import scala.language.postfixOps

class AuthoriseAgentStartPageSpec extends ViewSpecSupport {

  val viewTemplate: AuthoriseAgentStartPage = app.injector.instanceOf[AuthoriseAgentStartPage]
  val agentName: String = "ABC Accountants"
  val taxServices: List[String] = List("income-tax,income-record-viewer,vat,capital-gains-tax-uk-property,plastic-packaging-tax,country-by-country-reporting,pillar-2,trusts-and-estates")
  val h1Map: Map[String, String] = Map("income-tax", "Authorise $agentName to manage your Income Tax")

  Object expected {
    val incomeTaxH1 = s"Authorise $agentName to manage your Income Tax "
    val personalIncomeRecordH1 = s"Authorise $agentName to view your Income Record"
    val pptH1 = s"Authorise $agentName to manage your Plastic Packaging Tax"
    val cgtH1 = s"Authorise $agentName to manage your Capital Gains Tax on UK property account"
    val vatH1 = s"Authorise $agentName to manage your VAT"
    val pillar2H1 = s"Authorise $agentName to manage your Pillar 2 top-up taxes"
    val trustEstateH1 = s"Authorise $agentName to maintain your Trust or an Estate"
    val cbcH1 = s"Authorise $agentName to manage your Country-by-country Reports"
    val incomeTaxP1 = "You need to sign in with the user ID you use for Income Tax."
    val personalIncomeRecordP1 = "You need to sign in with the user ID you use for your personal tax account."
    val pptP1 = "You need to sign in with the user ID you use for Plastic Packaging Tax."
    val cgtP1 = "You need to sign in with the user ID you use for Capital Gains Tax on UK property account."
    val vatP1 = "You need to sign in with the user ID you use for VAT."
    val pillar2P1 = "You need to sign in with the user ID you use for Pillar 2 top-up taxes."
    val trustsEstateP1 = "You need to sign in with the user ID you use for maintaining your trust or estate."
    val cbcP1 = "You need to sign in with the user ID you use for Country-by-country Reports."
    val p2Content = "If you do not have sign in details,you'll be able to create some."
    val link1Content = "Start now"
    val link2Content = s"I do not want $agentName to act for me."
  }

  "AuthoriseAgentStartPage for authorisation journey view" should {
    val view: HtmlFormat.Appendable = viewTemplate(agentName, "income-tax", "uid")
    val doc: Document = Jsoup.parse(view.body)
    
    "display the correct title and content" when {
      
    }
  }


}