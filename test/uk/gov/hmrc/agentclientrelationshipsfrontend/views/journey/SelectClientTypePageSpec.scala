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
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.journey.SelectFromOptionsForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.journey.SelectClientTypePage

class SelectClientTypePageSpec extends ViewSpecSupport {

  val viewTemplate: SelectClientTypePage = app.injector.instanceOf[SelectClientTypePage]
  //TODO: we need to model the journey session to be able to test this
  object Expected {
    val authorisationRequestHeading = "What type of client is this authorisation for?"
    val cancelAuthorisationHeading = "What type of client do you want to cancel your authorisation for?"
    val authorisationRequestTitle = s"$authorisationRequestHeading - Ask a client to authorise you - GOV.UK"
    val cancelAuthorisationTitle = s"$cancelAuthorisationHeading - Ask a client to authorise you - GOV.UK"
  }

  "SelectClientType for authorisation request view" should {
    val options: Set[String] = Set("personal", "business", "trust")
    val form: Form[String] = SelectFromOptionsForm.form("client-type", options, "authorisation-request")
    val view: HtmlFormat.Appendable = viewTemplate(form, options)
    val doc: Document = Jsoup.parse(view.body)
    "have the right title" in {
      doc.title() shouldBe Expected.authorisationRequestTitle
    }
    "have a language switcher" in {
      doc.hasLanguageSwitch shouldBe true
    }
    "have the right heading" in {
      doc.mainContent.extractText(h1, 1).value shouldBe Expected.authorisationRequestHeading
    }
    "render a radio button for each option" in {
      options.map(option => doc.mainContent.select(s"input[value=$option]")).foreach { radio =>
        radio.size() shouldBe 1
      }
    }
  }

  "SelectClientType for cancel authorisation view" should {
    val options: Set[String] = Set("personal", "business", "trust")
    val form: Form[String] = SelectFromOptionsForm.form("client-type", options, "agent-cancel-authorisation")
    val view: HtmlFormat.Appendable = viewTemplate(form, options)
    val doc: Document = Jsoup.parse(view.body)
    "have the right title" in {
      doc.title() shouldBe Expected.cancelAuthorisationTitle
    }
    "have a language switcher" in {
      doc.hasLanguageSwitch shouldBe true
    }
    "have the right heading" in {
      doc.mainContent.extractText(h1, 1).value shouldBe Expected.cancelAuthorisationHeading
    }
    "render a radio button for each option" in {
      options.map(option => doc.mainContent.select(s"input[value=$option]")).foreach { radio =>
        radio.size() shouldBe 1
      }
    }
  }
}
