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

package uk.gov.hmrc.agentclientrelationshipsfrontend.views.agentJourney.clientFactPartials

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.common.KnownFactsConfiguration
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.journey.EnterClientFactForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{AgentJourneyRequest, AgentJourney}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.AgentJourneyType.AuthorisationRequest
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.agentJourney.clientFactPartials.Date

class DateSpec extends ViewSpecSupport {

  val template: Date = app.injector.instanceOf[Date]
  val fieldConfig: KnownFactsConfiguration = KnownFactsConfiguration("date", "", "text", 20)
  implicit val journeyRequest: AgentJourneyRequest[?] =
    new AgentJourneyRequest("", AgentJourney(journeyType = AuthorisationRequest), request)

  val listOfServices: Seq[String] = Seq("PERSONAL-INCOME-RECORD", "HMRC-MTD-VAT", "HMRC-PPT-ORG", "HMRC-PILLAR2-ORG")

  val h1ContentMap: Map[String, String] = Map(
    "PERSONAL-INCOME-RECORD" -> "What is your client’s date of birth?",
    "HMRC-MTD-VAT" -> "When did the client’s VAT registration start?",
    "HMRC-PPT-ORG" -> "When did your client’s registration start for Plastic Packaging Tax?",
    "HMRC-PILLAR2-ORG" -> "What is your client’s registration date for Pillar 2 top-up taxes?"
  )

  val hintContentMap: Map[String, String] = Map(
    "PERSONAL-INCOME-RECORD" -> "For example, 22 7 1981.",
    "HMRC-MTD-VAT" -> "The date of registration is on the client’s VAT certificate. For example, 31 8 2015.",
    "HMRC-PPT-ORG" -> "For example, 31 8 2022.",
    "HMRC-PILLAR2-ORG" -> "The current filing member can find it on their Pillar 2 top-up taxes homepage. For example, 27 3 2026."
  )

  val errorContentMap: Map[String, String] = Map(
    "PERSONAL-INCOME-RECORD" -> "Enter your client’s date of birth",
    "HMRC-MTD-VAT" -> "Enter your client’s VAT registration date",
    "HMRC-PPT-ORG" -> "Enter your client’s Plastic Packaging Tax registration date",
    "HMRC-PILLAR2-ORG" -> "Enter your client’s date of registration to report Pillar 2 top-up taxes"
  )

  "The Date partial" should {

    "render base components of a date form" which {

      val form = EnterClientFactForm.form(fieldConfig, "")
      val view: HtmlFormat.Appendable = template(form, fieldConfig, "")
      val doc: Document = Jsoup.parse(view.body)

      "include a day input" in {
        doc.extractByIndex("input", 1).value.attr("id") shouldBe "date.day"
      }

      "include a month input" in {
        doc.extractByIndex("input", 2).value.attr("id") shouldBe "date.month"
      }

      "include a year input" in {
        doc.extractByIndex("input", 3).value.attr("id") shouldBe "date.year"
      }

      "include a submission button" in {
        doc.extractText(".govuk-button", 1).value shouldBe "Continue"
      }
    }

    "render a page heading and form hint based on the service" which {

      listOfServices.foreach { service =>

        val msgPrefix = s"clientFact.$service.date"
        val form = EnterClientFactForm.form(fieldConfig, service)
        val view: HtmlFormat.Appendable = template(form, fieldConfig, msgPrefix)
        val doc: Document = Jsoup.parse(view.body)

        s"has a legend containing the h1 for $service" in {
          doc.extractText("legend > h1", 1).value shouldBe h1ContentMap(service)
        }

        s"has a form hint for $service" in {
          doc.extractText(".govuk-hint", 1).value shouldBe hintContentMap(service)
        }
      }
    }

    "render an error when there are errors in the form" which {

      listOfServices.foreach { service =>

        val msgPrefix = s"clientFact.$service.date"
        val form = EnterClientFactForm.form(fieldConfig, service).bind(Map("date" -> ""))
        val view: HtmlFormat.Appendable = template(form, fieldConfig, msgPrefix)
        val doc: Document = Jsoup.parse(view.body)

        s"includes an error message specific for $service" in {
          doc.extractText(".govuk-error-message", 1).value shouldBe s"Error: ${errorContentMap(service)}"
        }
      }
    }
  }
}
