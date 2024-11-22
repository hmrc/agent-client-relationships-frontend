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
import play.api.test.FakeRequest
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.journey.AuthoriseAgentStartPage

import scala.language.postfixOps

class AuthoriseAgentStartPageSpec extends ViewSpecSupport {

  val viewTemplate: AuthoriseAgentStartPage = app.injector.instanceOf[AuthoriseAgentStartPage]
  val agentName: String = "ABC Accountants"
  val taxServiceNames: Map[String, String] = Map(
    "income-tax" -> "Income Tax",
    "income-record-viewer" -> "",
    "vat" -> "VAT",
    "capital-gains-tax-uk-property" -> "Capital Gains Tax on UK property account",
    "plastic-packaging-tax" -> "Plastic Packaging Tax",
    "country-by-country-reporting" -> "Country-by-country Reports",
    "pillar-2" -> "Pillar 2 top-up taxes",
    "trusts-and-estates" -> "Trust or an Estate"
  ) 



    def h1Expected(taxService: String): String = taxService match {
      case "income-record-viewer"   => s"Authorise $agentName to view your Income Record"
      case "trusts-and-estates"     => s"Authorise $agentName to maintain your Trust or an Estate"
      case _                        => s"Authorise $agentName to manage your ${taxServiceNames(taxService)}"
    }
    
//    val incomeTaxH1 = s"Authorise $agentName to manage your Income Tax "
//    val personalIncomeRecordH1 = s"Authorise $agentName to view your Income Record"
//    val pptH1 = s"Authorise $agentName to manage your Plastic Packaging Tax"
//    val cgtH1 = s"Authorise $agentName to manage your Capital Gains Tax on UK property account"
//    val vatH1 = s"Authorise $agentName to manage your VAT"
//    val pillar2H1 = s"Authorise $agentName to manage your Pillar 2 top-up taxes"
//    val trustEstateH1 = s"Authorise $agentName to maintain your Trust or an Estate"
//    val cbcH1 = s"Authorise $agentName to manage your Country-by-country Reports"
//    val incomeTaxP1 = "You need to sign in with the user ID you use for Income Tax."
//    val personalIncomeRecordP1 = "You need to sign in with the user ID you use for your personal tax account."
//    val pptP1 = "You need to sign in with the user ID you use for Plastic Packaging Tax."
//    val cgtP1 = "You need to sign in with the user ID you use for Capital Gains Tax on UK property account."
//    val vatP1 = "You need to sign in with the user ID you use for VAT."
//    val pillar2P1 = "You need to sign in with the user ID you use for Pillar 2 top-up taxes."
//    val trustsEstateP1 = "You need to sign in with the user ID you use for maintaining your trust or estate."
//    val cbcP1 = "You need to sign in with the user ID you use for Country-by-country Reports."
//    val p2Content = "If you do not have sign in details,you'll be able to create some."
//    val link1Content = "Start now"
//    val link2Content = s"I do not want $agentName to act for me."

  "AuthoriseAgentStartPage for authorisation journey view" should {


    for (elem <- taxServiceNames.keySet.toList) {
      s"include the correct H1 text for $elem" in {

        val view: HtmlFormat.Appendable = viewTemplate(agentName, elem, "uid")
        val doc: Document = Jsoup.parse(view.body)
        doc.mainContent.extractText(h1, 1).get shouldBe h1Expected(elem)
      }
    }


    "have the right title" in {
    }

    "have the right p1" in {

    }

    "have the right p2" in {

    }

    "have a correctLink content" in {

    }


    //    def pMessage(taxService: String): String = taxService match {
    //      case "income-tax" => expected.incomeTaxP1
    //      case "personal-income-record" => expected.personalIncomeRecordP1
    //      case "plastic-packaging-tax" => expected.pptP1
    //      case "capital-gains-tax-uk-property" => expected.cgtP1
    //      case "vat" => expected.vatP1
    //      case "pillar-2" => expected.pillar2P1
    //      case "trusts-and-estates" => expected.trustsEstateP1
    //      case "country-by-country-reporting" => expected.cbcP1
    //    }

    //    def p2Message(): String = expected.p2Content
    //
    //    def link1Message(): String = expected.link1Content
    //
    //    def link2Message(): String = expected.link2Content
  }


}