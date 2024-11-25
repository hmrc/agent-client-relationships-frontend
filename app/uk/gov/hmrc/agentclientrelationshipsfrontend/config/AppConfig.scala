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

package uk.gov.hmrc.agentclientrelationshipsfrontend.config

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
class AppConfig @Inject()(servicesConfig: ServicesConfig, config: Configuration):
  // Base Urls (for use by connectors)
  val ivFrontendBaseUrl: String = baseUrl("identity-verification-frontend")
  val agentClientRelationshipsBaseUrl: String = baseUrl("agent-client-relationships")

  // Service Urls
  val appExternalUrl: String = getConfString("agent-client-relationships-frontend.external-url") // Use for redirects back to ACR frontend so they work locally
  val asaFrontendExternalUrl: String = getConfString("agent-services-account-frontend.external-url")
  val acmExternalUrl: String = getConfString("agent-client-management-frontend.external-url")
  val agentServicesAccountLimitedUrl: String = asaFrontendExternalUrl + getConfString("agent-services-account-frontend.account-limited")
  val agentServicesAccountHomeUrl: String = asaFrontendExternalUrl + getConfString("agent-services-account-frontend.home")
  val guidanceUrlSaSignup = s"${govUkExternalUrl}/register-for-self-assessment/self-employed"
  val guidanceUrlForAgentRoles = s"${govUkExternalUrl}/" // TODO: We need this url to be complete
  val guidanceAuthoriseAgent: String =
    s"$govUkExternalUrl/authorise-an-agent-to-deal-with-certain-tax-services-for-you"
  val ivUpliftUrl: String = getConfString("identity-verification-frontend.uplift-url")
  val signInUrl: String = getString("sign-in.url")
  val subscriptionUrl: String = getConfString("agent-subscription-frontend.subscription-url")

  // GovUk Urls
  val govUkUrl: String = getString("gov-uk.url")
  val guidanceUrlSaSignup = s"$govUkUrl/register-for-self-assessment/self-employed"
  val guidanceUrlForAgentRoles = s"$govUkUrl/" // TODO: We need this url to be complete
  val guidanceSa = s"$govUkUrl/guidance/self-assessment-for-agents-online-service"
  val privacyPolicyUrl = s"$govUkUrl/government/publications/data-protection-act-dpa-information-hm-revenue-and-customs-hold-about-you/data-protection-act-dpa-information-hm-revenue-and-customs-hold-about-you"

  // Feature Flags
  val welshLanguageSupportEnabled: Boolean = config.getOptional[Boolean]("features.welsh-language-support").getOrElse(false)

  // Service config
  val appName: String = getString("appName")
  val timeoutDialogTimeoutSeconds: Int = servicesConfig.getInt("timeoutDialog.timeout-seconds")
  val allowedRedirectHosts: Set[String] = config.getOptional[Seq[String]]("allowed-redirect-hosts").getOrElse(Nil).toSet
  val trackRequestsPerPage: Int = servicesConfig.getInt("track-requests-per-page")
  val authorisationRequestExpiryDays: Int = servicesConfig.getDuration("invitation.expiryDuration").toDays.toInt

  // Stub for supported services to be replaced when decision on how to handle this is reached
  val supportedServices: Set[String] =
    Set(
      "HMRC-MTD-IT",
      "PERSONAL-INCOME-RECORD",
      "HMRC-MTD-VAT",
      "HMRC-TERS-ORG",
      "HMRC-TERSNT-ORG",
      "HMRC-CGT-PD",
      "HMRC-PPT-ORG",
      "HMRC-CBC-ORG",
      "HMRC-CBC-NONUK-ORG",
      "HMRC-PILLAR2-ORG"
    )

  val countryListLocation: String = servicesConfig.getString("country.list.location")

  private def getString(key: String) = servicesConfig.getString(key)

  // For config contained in 'microservice.services'
  private def getConfString(key: String) =
    servicesConfig.getConfString(key, throw new RuntimeException(s"config $key not found"))

  private def baseUrl(serviceName: String) = servicesConfig.baseUrl(serviceName)