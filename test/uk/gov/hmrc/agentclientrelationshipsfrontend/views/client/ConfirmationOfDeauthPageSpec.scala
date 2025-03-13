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
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.Authorisation
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.client.ConfirmationOfDeauthPage

import java.time.LocalDate
import scala.language.postfixOps

class ConfirmationOfDeauthPageSpec extends ViewSpecSupport {

  val viewTemplate: ConfirmationOfDeauthPage = app.injector.instanceOf[ConfirmationOfDeauthPage]
  val testClientName: String = "Test Client"
  val arn: String = "TARN0000001"
  val mtditid: String = "ABCDEF123456789"
  val authDate: LocalDate = LocalDate.now().minusDays(10)
  val agentName: String = "ABC Accountants"
  val serviceLabels: Map[String, String] = Map(
    "HMRC-MTD-IT" -> "Making Tax Digital for Income Tax",
    "PERSONAL-INCOME-RECORD" -> "Income Record Viewer",
    "HMRC-MTD-VAT" -> "VAT",
    "HMRC-CGT-PD" -> "Capital Gains Tax on UK property account",
    "HMRC-PPT-ORG" -> "Plastic Packaging Tax",
    "HMRC-CBC-ORG" -> "Country-by-country reports",
    "HMRC-PILLAR2-ORG" -> "Pillar 2 Top-up Taxes",
    "HMRC-TERS-ORG" -> "Trusts and Estates",
    "HMRC-TERSNT-ORG" -> "Trusts and Estates"
  )

  val confirmationData: Authorisation = Authorisation(
    uid = "1",
    agentName = agentName,
    service = "HMRC-MTD-IT",
    clientId = mtditid,
    date = authDate,
    arn = arn,
    deauthorised = Some(true)
  )

  "Confirmation page view for removing authorisation for non-ITSA services from an agent" should {
      for (taxService <- serviceLabels.keySet.toList.filterNot(_ == "HMRC-MTD-IT")) {
        val view: HtmlFormat.Appendable = viewTemplate(confirmationData.copy(service = taxService))
        val doc: Document = Jsoup.parse(view.body)
        s"include the correct h1 text for $taxService" in {
          doc.mainContent.extractText("h1.govuk-panel__title", 1).value shouldBe "Authorisation removed"
        }
        s"include the correct p1 text for $taxService" in {
          val expectedContent = s"$agentName is no longer authorised to manage your ${serviceLabels(taxService)}."
          doc.mainContent.extractText(p, 1).value shouldBe expectedContent
        }
      }
  }

  "Confirmation page view removing authorisation for ITSA from an agent" should {
    val view: HtmlFormat.Appendable = viewTemplate(confirmationData)
    val doc: Document = Jsoup.parse(view.body)
    s"include the correct h1 text for ITSA" in {
      doc.mainContent.extractText("h1.govuk-panel__title", 1).value shouldBe "Authorisation removed"
    }
    s"include the correct text for the first subheading" in {
      val expectedH2 = "What this means"
      doc.mainContent.extractText(h2, 1).value shouldBe expectedH2
    }
    s"include the correct p1 text for ITSA" in {
      val expectedContent = s"$agentName is no longer authorised to manage your ${serviceLabels("HMRC-MTD-IT")}."
      doc.mainContent.extractText(p, 1).value shouldBe expectedContent
    }
    s"include extra content for ITSA" in {
      val expectedH2 = s"If $agentName manages your Self Assessment"
      doc.mainContent.extractText(h2, 2).value shouldBe expectedH2
    }
  }

}
