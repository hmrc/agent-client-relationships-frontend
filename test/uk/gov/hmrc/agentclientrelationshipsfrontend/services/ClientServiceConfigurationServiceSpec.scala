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

package uk.gov.hmrc.agentclientrelationshipsfrontend.services

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.common.ClientDetailsConfiguration

class ClientServiceConfigurationServiceSpec extends AnyWordSpecLike with Matchers with ServiceConstants with MockitoSugar :

  implicit val appConfig: AppConfig = mock[AppConfig]
  val services = new ClientServiceConfigurationService
  val serviceNames: List[String] = List(incomeTax, vat, capitalGainsTaxUkProperty, plasticPackagingTax, pillar2, trustsAndEstates, trustsAndEstateNonTaxable, incomeRecordViewer, countryByCountryReporting)

  "getServiceKeysForUrlPart" should :

    s"return enrolments supported by $incomeTax" in :
      services.getServiceKeysForUrlPart(incomeTax) shouldBe Set(HMRCMTDIT, HMRCNI, HMRCPT)

    s"return enrolments supported by $vat" in :
      services.getServiceKeysForUrlPart(vat) shouldBe Set(HMRCMTDVAT)

    s"return enrolments supported by $capitalGainsTaxUkProperty" in :
      services.getServiceKeysForUrlPart(capitalGainsTaxUkProperty) shouldBe Set(HMRCCGTPD)

    s"return enrolments supported by $trustsAndEstates" in :
      services.getServiceKeysForUrlPart(trustsAndEstates) shouldBe Set(HMRCTERSORG, HMRCTERSNTORG)

    s"return enrolments supported by $pillar2" in :
      services.getServiceKeysForUrlPart(pillar2) shouldBe Set(HMRCPILLAR2ORG)

    s"return enrolments supported by $incomeRecordViewer" in :
      services.getServiceKeysForUrlPart(incomeRecordViewer) shouldBe Set(HMRCNI, HMRCPT)

    s"return enrolments supported by $plasticPackagingTax" in :
      services.getServiceKeysForUrlPart(plasticPackagingTax) shouldBe Set(HMRCPPTORG)

    s"return enrolments supported by $countryByCountryReporting" in :
      services.getServiceKeysForUrlPart(countryByCountryReporting) shouldBe Set(HMRCCBCORG, HMRCCBCNONUKORG)

    "return an empty Set when service is unknown" in :
      services.getServiceKeysForUrlPart("unknown") shouldBe Set()

  "inferredClientType" should :

    "return an inferred client type for services with only one type supported" in :
      services.inferredClientType(HMRCMTDIT) shouldBe Some("personal")

    "return personal for personal income record" in :
      services.inferredClientType(PERSONALINCOMERECORD) shouldBe Some("personal")

    "return None for services with more than one client type supported" in :
      services.inferredClientType(HMRCMTDVAT) shouldBe None

  "allClientTypes" should :

    "return the supported client types across all services" in :
      services.allClientTypes shouldBe Set("personal", "business", "trust")

  "clientDetailsFor" should :

    s"provide the correct client details for $HMRCMTDIT" in :
      services.clientDetailsFor(HMRCMTDIT) shouldBe Seq(ClientDetailsConfiguration(
        name = "nino",
        regex = "[[A-Z]&&[^DFIQUV]][[A-Z]&&[^DFIQUVO]] ?\\d{2} ?\\d{2} ?\\d{2} ?[A-D]{1}",
        inputType = "text",
        width = 10,
        clientIdType = "ni"
      ))

    s"provide the correct client details for $HMRCMTDITSUPP" in :
      services.clientDetailsFor(HMRCMTDITSUPP) shouldBe Seq(ClientDetailsConfiguration(
        name = "nino",
        regex = "[[A-Z]&&[^DFIQUV]][[A-Z]&&[^DFIQUVO]] ?\\d{2} ?\\d{2} ?\\d{2} ?[A-D]{1}",
        inputType = "text",
        width = 10,
        clientIdType = "ni"
      ))

    s"provide the correct client details for $PERSONALINCOMERECORD" in :
      services.clientDetailsFor(PERSONALINCOMERECORD) shouldBe Seq(ClientDetailsConfiguration(
        name = "nino",
        regex = "[[A-Z]&&[^DFIQUV]][[A-Z]&&[^DFIQUVO]] ?\\d{2} ?\\d{2} ?\\d{2} ?[A-D]{1}",
        inputType = "text",
        width = 10,
        clientIdType = "ni"
      ))

    s"provide the correct client details for $HMRCMTDVAT" in :
      services.clientDetailsFor(HMRCMTDVAT) shouldBe Seq(ClientDetailsConfiguration(
        name = "vrn",
        regex = "^[0-9]{9}$",
        inputType = "text",
        width = 10,
        clientIdType = "vrn"
      ))

    s"provide the correct client details for $HMRCTERSORG" in :
      services.clientDetailsFor(HMRCTERSORG) shouldBe Seq(ClientDetailsConfiguration(
        name = "utr",
        regex = "^[0-9]{10}$",
        inputType = "text",
        width = 10,
        clientIdType = "utr"
      ))

    s"provide the correct client details for $HMRCTERSNTORG" in :
      services.clientDetailsFor(HMRCTERSNTORG) shouldBe Seq(ClientDetailsConfiguration(
        name = "urn",
        regex = "^[A-Z]{2}TRUST[0-9]{8}$",
        inputType = "text",
        width = 20,
        clientIdType = "urn"
      ))

    s"provide the correct client details for $HMRCCGTPD" in :
      services.clientDetailsFor(HMRCCGTPD) shouldBe Seq(ClientDetailsConfiguration(
        name = "cgtRef",
        regex = "^X[A-Z]CGTP[0-9]{9}$",
        inputType = "text",
        width = 20,
        clientIdType = "CGTPDRef"
      ))

    s"provide the correct client details for $HMRCPPTORG" in :
      services.clientDetailsFor(HMRCPPTORG) shouldBe Seq(ClientDetailsConfiguration(
        name = "pptRef",
        regex = "^X[A-Z]PPT000[0-9]{7}$",
        inputType = "text",
        width = 20,
        clientIdType = "EtmpRegistrationNumber"
      ))

    s"provide the correct client details for $HMRCCBCORG" in :
      services.clientDetailsFor(HMRCCBCORG) shouldBe Seq(ClientDetailsConfiguration(
        name = "cbcId",
        regex = "^X[A-Z]CBC[0-9]{10}$",
        inputType = "text",
        width = 20,
        clientIdType = "cbcId"
      ))

    s"provide the correct client details for $HMRCCBCNONUKORG" in :
      services.clientDetailsFor(HMRCCBCNONUKORG) shouldBe Seq(ClientDetailsConfiguration(
        name = "cbcId",
        regex = "^X[A-Z]CBC[0-9]{10}$",
        inputType = "text",
        width = 20,
        clientIdType = "cbcId"
      ))

    s"provide the correct client details for $HMRCPILLAR2ORG" in :
      services.clientDetailsFor(HMRCPILLAR2ORG) shouldBe Seq(ClientDetailsConfiguration(
        name = "PlrId",
        regex = "^X[A-Z]{1}PLR[0-9]{10}$",
        inputType = "text",
        width = 20,
        clientIdType = "PLRID"
      ))

  "getServiceForFastTrack" should:

    "return the correct service for fast track deauth" in:

      services.getServiceForFastTrack("HMRC-MTD-IT") shouldBe "HMRC-MTD-IT"
      services.getServiceForFastTrack("HMRC-MTD-IT-SUPP") shouldBe "HMRC-MTD-IT"
      services.getServiceForFastTrack("HMRC-MTD-VAT") shouldBe "HMRC-MTD-VAT"
      services.getServiceForFastTrack("HMRC-CGT-PD") shouldBe "HMRC-CGT-PD"
      services.getServiceForFastTrack("HMRC-PPT-ORG") shouldBe "HMRC-PPT-ORG"
      services.getServiceForFastTrack("HMRC-PILLAR2-ORG") shouldBe "HMRC-PILLAR2-ORG"
      services.getServiceForFastTrack("HMRC-TERS-ORG") shouldBe "HMRC-TERS-ORG"
      // HMRC-TERSNT-ORG is arrived at usually via refinement during deauth journey
      // this new method "getServiceForFastTrack" ensures it is preserved when found in
      // an invitation instead of reverted to the parent form option which is HMRC-TERS-ORG
      services.getServiceForFastTrack("HMRC-TERSNT-ORG") shouldBe "HMRC-TERSNT-ORG"
      services.getServiceForFastTrack("PERSONAL-INCOME-RECORD") shouldBe "PERSONAL-INCOME-RECORD"
      services.getServiceForFastTrack("HMRC-CBC-ORG") shouldBe "HMRC-CBC-ORG"
      services.getServiceForFastTrack("HMRC-CBC-NONUK-ORG") shouldBe "HMRC-CBC-ORG"
