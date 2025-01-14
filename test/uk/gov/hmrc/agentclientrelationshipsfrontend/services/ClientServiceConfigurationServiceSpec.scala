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

class ClientServiceConfigurationServiceSpec extends AnyWordSpecLike with Matchers with ServiceConstants {

  val services = new ClientServiceConfigurationService

  val serviceNames: List[String] = List(incomeTax, vat, capitalGainsTaxUkProperty, plasticPackagingTax, pillar2, trustsAndEstates, trustsAndEstateNonTaxable, incomeRecordViewer, countryByCountryReporting)

  "getServiceKeys" should {
      s"return enrolments supported by $incomeTax" in {
        services.getServiceKeysForUrlPart(incomeTax) shouldBe Set(HMRCMTDIT, HMRCNI, HMRCPT)
      }
      s"return enrolments supported by $vat" in {
        services.getServiceKeysForUrlPart(vat) shouldBe Set(HMRCMTDVAT)
      }
      s"return enrolments supported by $capitalGainsTaxUkProperty" in {
      services.getServiceKeysForUrlPart(capitalGainsTaxUkProperty) shouldBe Set(HMRCCGTPD)
      }
      s"return enrolments supported by $trustsAndEstates" in {
      services.getServiceKeysForUrlPart(trustsAndEstates) shouldBe Set(HMRCTERSORG, HMRCTERSNTORG)
      }
      s"return enrolments supported by $pillar2" in {
        services.getServiceKeysForUrlPart(pillar2) shouldBe Set(HMRCPILLAR2ORG)
      }
      s"return enrolments supported by $incomeRecordViewer" in {
      services.getServiceKeysForUrlPart(incomeRecordViewer) shouldBe Set(HMRCNI, HMRCPT)
      }
      s"return enrolments supported by $plasticPackagingTax" in {
      services.getServiceKeysForUrlPart(plasticPackagingTax) shouldBe Set(HMRCPPTORG)
      }
      s"return enrolments supported by $countryByCountryReporting" in {
      services.getServiceKeysForUrlPart(countryByCountryReporting) shouldBe Set(HMRCCBCORG, HMRCCBCNONUKORG)
     }
      "throw runtime exception when service is unknown" in {
        intercept[RuntimeException](services.getServiceKeysForUrlPart("unknown"))
    }
  }
  "inferredClientType" should {
    "return an inferred client type for services with only one type supported" in {
      services.inferredClientType(HMRCMTDIT) shouldBe Some("personal")
    }
    "return personal for personal income record" in {
      services.inferredClientType(PERSONALINCOMERECORD) shouldBe Some("personal")
    }
    "return None for services with more than one client type supported" in {
      services.inferredClientType(HMRCMTDVAT) shouldBe None
    }
  }
}
