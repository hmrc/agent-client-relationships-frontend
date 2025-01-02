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
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.ClientType.personal
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.ExistingMainAgent
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.client.ConfirmConsentForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{ClientJourney, ClientJourneyRequest}
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.client.ConfirmConsentPage

class ConfirmConsentPageSpec extends ViewSpecSupport:

  val viewTemplate: ConfirmConsentPage = app.injector.instanceOf[ConfirmConsentPage]
  val newAgentName = "ABC Agents"
  val existingAgent: ExistingMainAgent = ExistingMainAgent("XYZ Agents", false)
  val mainRole = "mainAgent"
  val suppRole = "suppAgent"
  val genericRole = "agent"
  val caption = "Making Tax Digital for Income Tax"

  val genericRoleServices: Seq[String] = Seq(
    "PERSONAL-INCOME-RECORD", "HMRC-MTD-VAT", "HMRC-CGT-PD",
    "HMRC-PPT-ORG", "HMRC-CBC-ORG", "HMRC-PILLAR2-ORG", "HMRC-TERS-ORG"
  )
  val mainRoleServices: Seq[String] = Seq("HMRC-MTD-IT")
  val suppRoleServices: Seq[String] = Seq("HMRC-MTD-IT-SUPP")

  val baseJourneyModel: ClientJourney = ClientJourney(
    "authorisation-response",
    invitationId = Some("ABC123"),
    agentName = Some(newAgentName),
    clientType = Some(personal)
  )

  val bulletListItemMap: Map[String, Int] = Map(
    "HMRC-MTD-IT" -> 12,
    "HMRC-MTD-IT-SUPP" -> 8,
    "PERSONAL-INCOME-RECORD" -> 4,
    "HMRC-MTD-VAT" -> 12,
    "HMRC-TERS-ORG" -> 2,
    "HMRC-TERSNT-ORG" -> 2,
    "HMRC-CGT-PD" -> 10,
    "HMRC-PPT-ORG" -> 8,
    "HMRC-CBC-ORG" -> 11,
    "HMRC-PILLAR2-ORG" -> 6,
  )

  object Expected:
    val title = "Authorise an agent - Appoint someone to deal with HMRC for you - GOV.UK"
    val heading = "Authorise an agent"
    def caption(service: String): String = messages(service)
    def introductionParagraph(role: String, service: String) =
      s"$newAgentName want to be authorised as your ${messages(s"confirmConsent.form.$role")} for ${messages(service)}."
    val subHeading = "Read carefully: we need your consent"
    def agreeParagraph(role: String, service: String) =
      s"If you agree that $newAgentName can act as your ${messages(s"confirmConsent.form.$role")} for ${messages(service)}, they will be able to:"
    def legend(role: String, service: String) =
      s"Do you want $newAgentName to be authorised as your ${messages(s"confirmConsent.form.$role")} for ${messages(service)}?"
    val yesOption = "Yes"
    val noOption = "No"
    def formError(role: String, service: String) =
      s"Select yes if you want $newAgentName to be your $role for ${messages(service)}"
    val continue = "Continue"
    def warningGeneric(service: String) =
      s"Warning You can have 1 agent for ${messages(service)}. If you authorise $newAgentName, we will remove ${existingAgent.agencyName} as your existing agent."
    def warningMain(service: String) =
      s"Warning You can have 1 main agent for ${messages(service)}. If you authorise $newAgentName, we will remove ${existingAgent.agencyName} as your existing main agent."
    def warningMainToSupp(service: String) =
      s"Warning If you authorise $newAgentName as your supporting agent, you will no longer have a main agent for ${messages(service)}."
    val suppDetailsHeading = "What a supporting agent cannot do"
    val suppDetailsP1 = "A supporting agent can only carry out limited tax-related tasks for you. For example, they cannot:"
    val suppDetailsBullet1 = "submit end-of-year tax returns"
    val suppDetailsBullet2 = "estimate tax and view final calculation"
    val suppDetailsBullet3 = "view tax amounts owed and paid"
    val suppDetailsBullet4 = "deal with penalties for late payment and late submission"
    val bulletListMap: Map[String, String] = Map(
      "HMRC-MTD-IT-1" -> "sign you up or opt you out",
      "HMRC-MTD-IT-2" -> "contact HMRC about your current and previous returns",
      "HMRC-MTD-IT-3" -> "view and change your details, such as sources of income",
      "HMRC-MTD-IT-4" -> "provide repayment bank details to HMRC when repayment is due",
      "HMRC-MTD-IT-5" -> "view and submit your updates (income and expenses)",
      "HMRC-MTD-IT-6" -> "submit your end-of-year tax return",
      "HMRC-MTD-IT-7" -> "view your calculations and amounts owed and paid",
      "HMRC-MTD-IT-8" -> "manage your Making Tax Digital for Income Tax using software",
      "HMRC-MTD-IT-9" -> "access your Self Assessment details, such as your name, address, National Insurance number and Unique Taxpayer Reference",
      "HMRC-MTD-IT-10" -> "receive copies of penalty notices for late payment or late submission",
      "HMRC-MTD-IT-11" -> "view and appeal against penalties issued for late payment or late submission",
      "HMRC-MTD-IT-12" -> "cancel your Self Assessment registration",
      "HMRC-MTD-IT-SUPP-1" -> "sign you up or opt you out",
      "HMRC-MTD-IT-SUPP-2" -> "contact HMRC about your current and previous returns",
      "HMRC-MTD-IT-SUPP-3" -> "view your personal and business details, including name, address, National Insurance number and Unique Taxpayer Reference",
      "HMRC-MTD-IT-SUPP-4" -> "view and amend your designatory business details",
      "HMRC-MTD-IT-SUPP-5" -> "view and amend your sources of income",
      "HMRC-MTD-IT-SUPP-6" -> "find and choose compatible software and use it to manage your Making Tax Digital for Income Tax",
      "HMRC-MTD-IT-SUPP-7" -> "send quarterly updates",
      "HMRC-MTD-IT-SUPP-8" -> "make end of year adjustments",
      "PERSONAL-INCOME-RECORD-1" -> "access information about",
      "PERSONAL-INCOME-RECORD-2" -> "who you have worked for in the past",
      "PERSONAL-INCOME-RECORD-3" -> "taxable benefits like medical insurance and company car",
      "PERSONAL-INCOME-RECORD-4" -> "your pensions",
      "PERSONAL-INCOME-RECORD-5" -> "your PAYE involvement start and end dates",
      "HMRC-MTD-VAT-1" -> "contact HMRC about your VAT",
      "HMRC-MTD-VAT-2" -> "view and change your VAT and bank details",
      "HMRC-MTD-VAT-3" -> "view, change and submit your VAT details",
      "HMRC-MTD-VAT-4" -> "view your VAT payments and liabilities",
      "HMRC-MTD-VAT-5" -> "manage your VAT through software",
      "HMRC-MTD-VAT-6" -> "cancel your VAT registration",
      "HMRC-MTD-VAT-7" -> "appeal a late submission or late payment penalty",
      "HMRC-MTD-VAT-8" -> "access your",
      "HMRC-MTD-VAT-9" -> "VAT registration details, such as your business and contact details",
      "HMRC-MTD-VAT-10" -> "submitted VAT Returns",
      "HMRC-MTD-VAT-11" -> "VAT Return calculations",
      "HMRC-MTD-VAT-12" -> "amounts owed or paid",
      "HMRC-TERS-ORG-1" -> "report changes relating to trustees, settlors, beneficiaries, protectors and other individuals in the trust, such as names and addresses, and declare the trust is up to date",
      "HMRC-TERS-ORG-2" -> "report changes relating to the estate’s personal representative, such as name and address, and declare the estate is up to date",
      "HMRC-CGT-PD-1" -> "view, change and submit your current returns",
      "HMRC-CGT-PD-2" -> "view and change your previous return details",
      "HMRC-CGT-PD-3" -> "view and change your contact preferences",
      "HMRC-CGT-PD-4" -> "contact HMRC about your returns, payments and any penalties",
      "HMRC-CGT-PD-5" -> "access and update your",
      "HMRC-CGT-PD-6" -> "name and contact details",
      "HMRC-CGT-PD-7" -> "residence status",
      "HMRC-CGT-PD-8" -> "liability for current and previous tax years",
      "HMRC-CGT-PD-9" -> "amounts due and paid",
      "HMRC-CGT-PD-10" -> "penalty details",
      "HMRC-PPT-ORG-1" -> "view your PPT returns",
      "HMRC-PPT-ORG-2" -> "submit your PPT returns",
      "HMRC-PPT-ORG-3" -> "adjust your PPT returns",
      "HMRC-PPT-ORG-4" -> "contact HMRC about your returns",
      "HMRC-PPT-ORG-5" -> "provide repayment bank details to HMRC when repayment is due",
      "HMRC-PPT-ORG-6" -> "finalise your overall tax position",
      "HMRC-PPT-ORG-7" -> "view your calculations and amounts owed and paid",
      "HMRC-PPT-ORG-8" -> "manage your PPT using software",
      "HMRC-CBC-ORG-1" -> "send new country-by-country reports",
      "HMRC-CBC-ORG-2" -> "send additional information, corrections or deletions for previous country-by-country reports",
      "HMRC-CBC-ORG-3" -> "access a list of any country-by-country reports sent in the last 28 days.",
      "HMRC-CBC-ORG-4" -> "contact HMRC about your current and previous reports",
      "HMRC-CBC-ORG-5" -> "share information with HMRC about your current or previous country-by-country reports",
      "HMRC-CBC-ORG-6" -> "receive information from HMRC about your current or previous country-by-country reports",
      "HMRC-CBC-ORG-7" -> "access your CBC ID (country-by-country ID)",
      "HMRC-CBC-ORG-8" -> "access and change your country-by-country contact details, such as your contact names, email addresses and telephone numbers",
      "HMRC-CBC-ORG-9" -> "access details of any penalties charged",
      "HMRC-CBC-ORG-10" -> "appeal late submission penalties",
      "HMRC-CBC-ORG-11" -> "provide additional information when appealing late submission penalties",
      "HMRC-PILLAR2-ORG-1" -> "view, change, and submit your Pillar 2 top-up taxes returns",
      "HMRC-PILLAR2-ORG-2" -> "contact HMRC about your current and previous Pillar 2 top-up taxes returns",
      "HMRC-PILLAR2-ORG-3" -> "view and change your Pillar 2 top-up taxes details such as your contact details, accounting period and organisation type",
      "HMRC-PILLAR2-ORG-4" -> "provide repayment bank details to HMRC when repayment is due",
      "HMRC-PILLAR2-ORG-5" -> "finalise your overall tax position",
      "HMRC-PILLAR2-ORG-6" -> "view your calculations and amounts owed and paid"
    )
    val errorMessage = s"Error: Select yes if you want $newAgentName to be your agent for VAT"
    val errorMessageMain = s"Error: Select yes if you want $newAgentName to be your main agent for Making Tax Digital for Income Tax"
    val errorMessageSupp = s"Error: Select yes if you want $newAgentName to be your supporting agent for Making Tax Digital for Income Tax"

  "The Confirm Consent page" when:

    "the request is for a tax type that does not allow for main/supporting agents" when:

      "there is no existing agent" when:

        genericRoleServices.foreach: service =>

          s"the tax service is $service" should:

            val form = ConfirmConsentForm.form(newAgentName, genericRole, service)
            implicit val journeyRequest: ClientJourneyRequest[?] =
              ClientJourneyRequest(baseJourneyModel.copy(serviceKey = Some(service)), request)
            val view = viewTemplate(form, genericRole)
            val doc: Document = Jsoup.parse(view.body)

            "have the correct title" in:
              doc.title() shouldBe Expected.title

            "have the correct heading" in:
              doc.mainContent.extractText(h1, 1).value shouldBe Expected.heading

            "have the correct caption" in:
              doc.mainContent.extractText(h2, 1).value shouldBe Expected.caption(service)

            "have the correct first paragraph" in:
              doc.mainContent.extractText(p, 1).value shouldBe Expected.introductionParagraph(genericRole, service)

            "not render a warning message" in:
              doc.mainContent.extractText(".govuk-warning-text__text", 1) shouldBe None

            "have the correct subheading" in:
              doc.mainContent.extractText(h2, 2).value shouldBe Expected.subHeading

            "have the correct second paragraph" in:
              doc.mainContent.extractText(p, 2).value shouldBe Expected.agreeParagraph(genericRole, service)

            "have the correct consent bullet list" in:
              val numberOfBulletItems = bulletListItemMap(service)
              (1 to numberOfBulletItems).foreach: i =>
                doc.mainContent.extractText("li", i).value.takeWhile(_ != ':') shouldBe Expected.bulletListMap(s"$service-$i")

            "have the correct form legend" in:
              doc.mainContent.extractText("legend", 1).value shouldBe Expected.legend(genericRole, service)

            "have the correct radio options" in:
              val expectedRadioGroup = TestRadioGroup(
                Expected.legend(genericRole, service),
                List(
                  Expected.yesOption -> "true",
                  Expected.noOption -> "false"
                ),
                None
              )
              doc.mainContent.extractRadios().value shouldBe expectedRadioGroup

            "have the correct button" in:
              doc.mainContent.extractText(".govuk-button", 1).value shouldBe Expected.continue

      "there is an existing agent" when:

        genericRoleServices.foreach: service =>

          s"the tax service is $service" should:

            val form = ConfirmConsentForm.form(newAgentName, genericRole, service)
            implicit val journeyRequest: ClientJourneyRequest[?] = ClientJourneyRequest(
              baseJourneyModel.copy(serviceKey = Some(service), existingMainAgent = Some(existingAgent)), request)
            val view = viewTemplate(form, genericRole)
            val doc: Document = Jsoup.parse(view.body)

            s"have the correct warning message for $service" in:
             doc.mainContent.extractText(".govuk-warning-text__text", 1).value shouldBe Expected.warningGeneric(service)

      "there are errors in the form" should :

        val form = ConfirmConsentForm.form(newAgentName, genericRole, "HMRC-MTD-VAT").bind(Map())
        implicit val journeyRequest: ClientJourneyRequest[?] =
          ClientJourneyRequest(baseJourneyModel.copy(serviceKey = Some("HMRC-MTD-VAT")), request)
        val view = viewTemplate(form, genericRole)
        val doc: Document = Jsoup.parse(view.body)

        "have the correct error message" in :
          doc.mainContent.extractText(".govuk-error-message", 1).value shouldBe Expected.errorMessage

    "the request is for a tax type that allows for main/supporting agents" when:

      "the authorisation is for a main agent" when:

        "there is no existing main agent" when:

          mainRoleServices.foreach: service =>

            s"the tax service is $service" should:

              val form = ConfirmConsentForm.form(newAgentName, mainRole, service)
              implicit val journeyRequest: ClientJourneyRequest[?] =
                ClientJourneyRequest(baseJourneyModel.copy(serviceKey = Some(service)), request)
              val view = viewTemplate(form, mainRole)
              val doc: Document = Jsoup.parse(view.body)

              "have the correct title" in:
                doc.title() shouldBe Expected.title

              "have the correct heading" in:
                doc.mainContent.extractText(h1, 1).value shouldBe Expected.heading

              "have the correct caption" in:
                doc.mainContent.extractText(h2, 1).value shouldBe Expected.caption(service)

              "have the correct first paragraph" in:
                doc.mainContent.extractText(p, 1).value shouldBe Expected.introductionParagraph(mainRole, service)

              "not render a warning message" in:
                doc.mainContent.extractText(".govuk-warning-text__text", 1) shouldBe None

              "have the correct subheading" in:
                doc.mainContent.extractText(h2, 2).value shouldBe Expected.subHeading

              "have the correct consent bullet list" in :
                val numberOfBulletItems = bulletListItemMap(service)
                (1 to numberOfBulletItems).foreach: i =>
                  doc.mainContent.extractText("li", i).value.takeWhile(_ != ':') shouldBe Expected.bulletListMap(s"$service-$i")

              "have the correct second paragraph" in:
                doc.mainContent.extractText(p, 2).value shouldBe Expected.agreeParagraph(mainRole, service)

              "have the correct form legend" in:
                doc.mainContent.extractText("legend", 1).value shouldBe Expected.legend(mainRole, service)

              "have the correct radio options" in:
                val expectedRadioGroup = TestRadioGroup(
                  Expected.legend(mainRole, service),
                  List(
                    Expected.yesOption -> "true",
                    Expected.noOption -> "false"
                  ),
                  None
                )
                doc.mainContent.extractRadios().value shouldBe expectedRadioGroup

              "have the correct button" in:
                doc.mainContent.extractText(".govuk-button", 1).value shouldBe Expected.continue

        "there is an existing main agent" when:

          genericRoleServices.foreach: service =>

            s"the tax service is $service" should:

              val form = ConfirmConsentForm.form(newAgentName, mainRole, service)
              implicit val journeyRequest: ClientJourneyRequest[?] = ClientJourneyRequest(
                baseJourneyModel.copy(serviceKey = Some(service), existingMainAgent = Some(existingAgent)), request)
              val view = viewTemplate(form, mainRole)
              val doc: Document = Jsoup.parse(view.body)

              s"have the correct warning message for $service" in:
                doc.mainContent.extractText(".govuk-warning-text__text", 1).value shouldBe Expected.warningMain(service)

        "there are errors in the form" should:

          val form = ConfirmConsentForm.form(newAgentName, mainRole, "HMRC-MTD-IT").bind(Map())
          implicit val journeyRequest: ClientJourneyRequest[?] =
            ClientJourneyRequest(baseJourneyModel.copy(serviceKey = Some("HMRC-MTD-IT")), request)
          val view = viewTemplate(form, mainRole)
          val doc: Document = Jsoup.parse(view.body)

          "have the correct error message" in :
            doc.mainContent.extractText(".govuk-error-message", 1).value shouldBe Expected.errorMessageMain

      "the authorisation is for a supp agent" when:

        "there is no existing main agent" when:

          suppRoleServices.foreach: service =>

            s"the tax service is $service" should:

              val form = ConfirmConsentForm.form(newAgentName, suppRole, service)
              implicit val journeyRequest: ClientJourneyRequest[?] =
                ClientJourneyRequest(baseJourneyModel.copy(serviceKey = Some(service)), request)
              val view = viewTemplate(form, suppRole)
              val doc: Document = Jsoup.parse(view.body)

              "have the correct title" in:
                doc.title() shouldBe Expected.title

              "have the correct heading" in:
                doc.mainContent.extractText(h1, 1).value shouldBe Expected.heading

              "have the correct caption" in:
                doc.mainContent.extractText(h2, 1).value shouldBe Expected.caption(service)

              "have the correct first paragraph" in:
                doc.mainContent.extractText(p, 1).value shouldBe Expected.introductionParagraph(suppRole, service)

              "not render a warning message" in:
                doc.mainContent.extractText(".govuk-warning-text__text", 1) shouldBe None

              "have the correct details component with supporting agent information" which:

                "has the correct heading" in:
                  doc.mainContent.extractText(".govuk-details__summary-text", 1).value shouldBe Expected.suppDetailsHeading

                "has the correct paragraph" in :
                  doc.mainContent.extractText(".govuk-details__text > p", 1).value shouldBe Expected.suppDetailsP1

                "has the correct bullet point list" in :
                  doc.mainContent.extractText(".govuk-details__text li", 1).value shouldBe Expected.suppDetailsBullet1
                  doc.mainContent.extractText(".govuk-details__text li", 2).value shouldBe Expected.suppDetailsBullet2
                  doc.mainContent.extractText(".govuk-details__text li", 3).value shouldBe Expected.suppDetailsBullet3
                  doc.mainContent.extractText(".govuk-details__text li", 4).value shouldBe Expected.suppDetailsBullet4

              "have the correct subheading" in:
                doc.mainContent.extractText(h2, 2).value shouldBe Expected.subHeading

              "have the correct consent bullet list" in :
                val numberOfBulletItems = bulletListItemMap(service)
                (1 to numberOfBulletItems).foreach: i =>
                  doc.mainContent.extractText("div > div > ul > li", i).value.takeWhile(_ != ':') shouldBe Expected.bulletListMap(s"$service-$i")

              "have the correct third paragraph" in:
                doc.mainContent.extractText(p, 3).value shouldBe Expected.agreeParagraph(suppRole, service)

              "have the correct form legend" in:
                doc.mainContent.extractText("legend", 1).value shouldBe Expected.legend(suppRole, service)

              "have the correct radio options" in:
                val expectedRadioGroup = TestRadioGroup(
                  Expected.legend(suppRole, service),
                  List(
                    Expected.yesOption -> "true",
                    Expected.noOption -> "false"
                  ),
                  None
                )
                doc.mainContent.extractRadios().value shouldBe expectedRadioGroup

              "have the correct button" in:
                doc.mainContent.extractText(".govuk-button", 1).value shouldBe Expected.continue

        "there is an existing main agent" when:

          "the existing agent is the same as the requesting agent" when:

            genericRoleServices.foreach: service =>

              s"the tax service is $service" should:

                val form = ConfirmConsentForm.form(newAgentName, suppRole, service)
                val sameAgent = existingAgent.copy(sameAgent = true)
                implicit val journeyRequest: ClientJourneyRequest[?] = ClientJourneyRequest(
                  baseJourneyModel.copy(serviceKey = Some(service), existingMainAgent = Some(sameAgent)), request)
                val view = viewTemplate(form, suppRole)
                val doc: Document = Jsoup.parse(view.body)

                s"have the correct warning message for $service" in:
                  doc.mainContent.extractText(".govuk-warning-text__text", 1).value shouldBe Expected.warningMainToSupp(service)

          "the existing agent is different to the requesting agent" when:

            genericRoleServices.foreach: service =>

              s"the tax service is $service" should:

                val form = ConfirmConsentForm.form(newAgentName, suppRole, service)
                implicit val journeyRequest: ClientJourneyRequest[?] = ClientJourneyRequest(
                  baseJourneyModel.copy(serviceKey = Some(service), existingMainAgent = Some(existingAgent)), request)
                val view = viewTemplate(form, suppRole)
                val doc: Document = Jsoup.parse(view.body)

                "not render a warning message" in:
                  doc.mainContent.extractText(".govuk-warning-text__text", 1) shouldBe None

        "there are errors in the form" should:

          val form = ConfirmConsentForm.form(newAgentName, suppRole, "HMRC-MTD-IT-SUPP").bind(Map())
          implicit val journeyRequest: ClientJourneyRequest[?] =
            ClientJourneyRequest(baseJourneyModel.copy(serviceKey = Some("HMRC-MTD-IT-SUPP")), request)
          val view = viewTemplate(form, suppRole)
          val doc: Document = Jsoup.parse(view.body)

          "have the correct error message" in:
            doc.mainContent.extractText(".govuk-error-message", 1).value shouldBe Expected.errorMessageSupp
