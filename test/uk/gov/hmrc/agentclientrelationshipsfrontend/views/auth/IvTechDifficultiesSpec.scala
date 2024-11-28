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
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.auth.IvTechDifficulties

class IvTechDifficultiesSpec extends ViewSpecSupport {

  val viewTemplate: IvTechDifficulties = app.injector.instanceOf[IvTechDifficulties]

  object Expected {
    val title = "Sorry, there is a problem with the service - Appoint someone to deal with HMRC for you - GOV.UK"
    val heading = "Sorry, there is a problem with the service"
    val para1 = "Try again later."
    val para2 = "We may not have saved your answers. When the service is available, you may have to start again."
    val linkText1 = "Call the VAT online services helpline"
    val linkText2 = "Call the HMRC Self Assessment online services helpline"
    val linkUrl1 = "https://www.gov.uk/government/organisations/hm-revenue-customs/contact/vat-online-services-helpdesk"
    val linkUrl2 = "https://www.gov.uk/government/organisations/hm-revenue-customs/contact/self-assessment-online-services-helpdesk"
    val para3 = s"$linkText1 if you need help with VAT."
    val para4 = s"$linkText2 if you need help with Making Tax Digital for Income Tax."
  }

  "IvTechDifficulties view" should {
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
      doc.mainContent.extractText(p, 3).value shouldBe Expected.para3
      doc.mainContent.extractText(p, 4).value shouldBe Expected.para4
    }
    "have the right links" in {
      doc.mainContent.extractLink(1).value shouldBe TestLink(Expected.linkText1, Expected.linkUrl1)
      doc.mainContent.extractLink(2).value shouldBe TestLink(Expected.linkText2, Expected.linkUrl2)
    }
  }
}
