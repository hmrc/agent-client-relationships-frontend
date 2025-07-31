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

package uk.gov.hmrc.agentclientrelationshipsfrontend.views.agentJourney

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.KnownFactType.Country
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.journey.EnterClientFactForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.{ClientDetailsResponse, KnownFactType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.agentJourney.EnterClientFactPage

class EnterClientFactPageSpec extends ViewSpecSupport {

  val viewTemplate: EnterClientFactPage = app.injector.instanceOf[EnterClientFactPage]

  private val authorisationRequestJourney: AgentJourney = AgentJourney(AgentJourneyType.AuthorisationRequest)
  private val agentCancelAuthorisationJourney: AgentJourney = AgentJourney(AgentJourneyType.AgentCancelAuthorisation)

  case class ExpectedStrings(
                              authorisationTitle: String,
                              cancelAuthorisationTitle: String,
                              errorRequired: String
                            )

  private def testCountries(knownFactType: KnownFactType): Seq[(String, String)] = knownFactType match {
    case Country => Seq(("France", "France"), ("Germany", "Germany"))
    case _ => Seq(("FR", "France"), ("DE", "Germany"))
  }

  private val knownFactContentVariants: Map[(String, KnownFactType), ExpectedStrings] = Map(
    ("HMRC-MTD-IT", KnownFactType.PostalCode) -> ExpectedStrings(
      authorisationTitle = "What is your client’s postcode? - Ask a client to authorise you - GOV.UK",
      cancelAuthorisationTitle = "What is your client’s postcode? - Cancel a client’s authorisation - GOV.UK",
      errorRequired = "Enter your client’s postcode"
    ),
    ("PERSONAL-INCOME-RECORD", KnownFactType.Date) -> ExpectedStrings(
      authorisationTitle = "What is your client’s date of birth? - Ask a client to authorise you - GOV.UK",
      cancelAuthorisationTitle = "What is your client’s date of birth? - Cancel a client’s authorisation - GOV.UK",
      errorRequired = "Enter your client’s date of birth"
    ),
    ("HMRC-MTD-VAT", KnownFactType.Date) -> ExpectedStrings(
      authorisationTitle = "When did the client’s VAT registration start? - Ask a client to authorise you - GOV.UK",
      cancelAuthorisationTitle = "When did the client’s VAT registration start? - Cancel a client’s authorisation - GOV.UK",
      errorRequired = "Enter your client’s VAT registration date"
    ),
    ("HMRC-CGT-PD", KnownFactType.PostalCode) -> ExpectedStrings(
      authorisationTitle = "What is your client’s postcode? - Ask a client to authorise you - GOV.UK",
      cancelAuthorisationTitle = "What is your client’s postcode? - Cancel a client’s authorisation - GOV.UK",
      errorRequired = "Enter your client’s postcode"
    ),
    ("HMRC-CGT-PD", KnownFactType.CountryCode) -> ExpectedStrings(
      authorisationTitle = "Which country is your client’s contact address in? - Ask a client to authorise you - GOV.UK",
      cancelAuthorisationTitle = "Which country is your client’s contact address in? - Cancel a client’s authorisation - GOV.UK",
      errorRequired = "Enter the country of your client’s contact address"
    ),
    ("HMRC-PPT-ORG", KnownFactType.Date) -> ExpectedStrings(
      authorisationTitle = "When did your client’s registration start for Plastic Packaging Tax? - Ask a client to authorise you - GOV.UK",
      cancelAuthorisationTitle = "When did your client’s registration start for Plastic Packaging Tax? - Cancel a client’s authorisation - GOV.UK",
      errorRequired = "Enter your client’s Plastic Packaging Tax registration date"
    ),
    ("HMRC-CBC-ORG", KnownFactType.Email) -> ExpectedStrings(
      authorisationTitle = "What is your client’s Country-by-Country contact email address? - Ask a client to authorise you - GOV.UK",
      cancelAuthorisationTitle = "What is your client’s Country-by-Country contact email address? - Cancel a client’s authorisation - GOV.UK",
      errorRequired = "Enter your client’s email address"
    ),
    ("HMRC-PILLAR2-ORG", KnownFactType.Date) -> ExpectedStrings(
      authorisationTitle = "What is your client’s registration date for Pillar 2 top-up taxes? - Ask a client to authorise you - GOV.UK",
      cancelAuthorisationTitle = "What is your client’s registration date for Pillar 2 top-up taxes? - Cancel a client’s authorisation - GOV.UK",
      errorRequired = "Enter your client’s date of registration to report Pillar 2 top-up taxes"
    )
  )

  List(authorisationRequestJourney, agentCancelAuthorisationJourney).foreach(j =>
    knownFactContentVariants.foreach { case ((service, knownFactType), expectedStrings) =>
      s"EnterClientFactPage for $service $knownFactType ${j.journeyType.toString} view" should {
        implicit val journeyRequest: AgentJourneyRequest[?] = new AgentJourneyRequest(
          "",
          j.copy(
            clientService = Some(service),
            clientDetailsResponse = Some(ClientDetailsResponse("", None, None, Nil, Some(knownFactType), false, None))
          ),
          request
        )
        val title = if j.journeyType == AgentJourneyType.AuthorisationRequest then expectedStrings.authorisationTitle else expectedStrings.cancelAuthorisationTitle
        val field = knownFactType.fieldConfiguration.copy(validOptions = Some(testCountries(knownFactType)))
        val form = EnterClientFactForm.form(
          field,
          service
        )
        val blankForm = form.bindFromRequest(Map.empty)
        val view: HtmlFormat.Appendable = viewTemplate(form, field)
        val viewWithRequiredError: HtmlFormat.Appendable = viewTemplate(blankForm, field)
        val doc: Document = Jsoup.parse(view.body)
        val requiredErrorDoc: Document = Jsoup.parse(viewWithRequiredError.body)
        "have the right title" in {
          doc.title() shouldBe title
          requiredErrorDoc.title() shouldBe "Error: " + title
        }
        "have a language switcher" in {
          doc.hasLanguageSwitch shouldBe true
        }
        "shows the correct required error" in {
          requiredErrorDoc.extractText(errorSummaryList, 1).value shouldBe expectedStrings.errorRequired
        }
      }
    })
}
