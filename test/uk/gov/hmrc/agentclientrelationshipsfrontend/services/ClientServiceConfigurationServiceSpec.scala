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
        services.getServiceKeys(incomeTax).get shouldBe Set(HMRCMTDIT, HMRCNI, HMRCPT)
      }
      s"return enrolments supported by $vat" in {
        services.getServiceKeys(vat).get shouldBe Set(HMRCMTDVAT)
      }
      s"return enrolments supported by $capitalGainsTaxUkProperty" in {
      services.getServiceKeys(capitalGainsTaxUkProperty).get shouldBe Set(HMRCCGTPD)
      }
      s"return enrolments supported by $trustsAndEstates" in {
      services.getServiceKeys(trustsAndEstates).get shouldBe Set(HMRCTERSORG)
      }
      s"return enrolments supported by $trustsAndEstateNonTaxable" in {
      services.getServiceKeys(trustsAndEstateNonTaxable).get shouldBe Set(HMRCTERSNTORG)
      }
      s"return enrolments supported by $pillar2" in {
        services.getServiceKeys(pillar2).get shouldBe Set(HMRCPILLAR2ORG)
      }
      s"return enrolments supported by $incomeRecordViewer" in {
      services.getServiceKeys(incomeRecordViewer).get shouldBe Set(HMRCNI, HMRCPT)
      }
      s"return enrolments supported by $plasticPackagingTax" in {
      services.getServiceKeys(plasticPackagingTax).get shouldBe Set(HMRCPPTORG)
      }
      s"return enrolments supported by $countryByCountryReporting" in {
      services.getServiceKeys(countryByCountryReporting).get shouldBe Set(HMRCCBCORG)
     }
      "return None when service is unknown" in {
      services.getServiceKeys("unknown") shouldBe None
    }
  }
}
