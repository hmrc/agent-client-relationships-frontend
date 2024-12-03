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

trait ServiceConstants:

  val incomeTax = "income-tax"
  val vat = "vat"
  val incomeRecordViewer = "income-record-viewer"
  val trustsAndEstates = "trusts-and-estates"
  val trustsAndEstateNonTaxable = "trusts-and-estates-non-taxable"
  val capitalGainsTaxUkProperty = "capital-gains-tax-uk-property"
  val plasticPackagingTax = "plastic-packaging-tax"
  val countryByCountryReporting = "country-by-country-reporting"
  val pillar2 = "pillar-2"
  
  val HMRCMTDIT: String = "HMRC-MTD-IT"
  val HMRCNI: String = "HMRC-NI"
  val HMRCPT: String = "HMRC-PT"
  val HMRCMTDITSUPP: String = "HMRC-MTD-IT-SUPP"
  val PERSONALINCOMERECORD: String = "PERSONAL-INCOME-RECORD"
  val HMRCMTDVAT: String = "HMRC-MTD-VAT"
  val HMRCTERSORG: String = "HMRC-TERS-ORG"
  val HMRCTERSNTORG: String = "HMRC-TERSNT-ORG"
  val HMRCCGTPD: String = "HMRC-CGT-PD"
  val HMRCPPTORG: String = "HMRC-PPT-ORG"
  val HMRCCBCORG: String = "HMRC-CBC-ORG"
  val HMRCCBCNONUKORG: String = "HMRC-CBC-NONUK-ORG"
  val HMRCPILLAR2ORG: String = "HMRC-PILLAR2-ORG"
