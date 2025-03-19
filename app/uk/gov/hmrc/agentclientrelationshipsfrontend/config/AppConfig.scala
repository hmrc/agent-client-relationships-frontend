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
  val agentServicesAccountLimitedUrl: String = asaFrontendExternalUrl + getConfString("agent-services-account-frontend.account-limited")
  val agentServicesAccountHomeUrl: String = asaFrontendExternalUrl + getConfString("agent-services-account-frontend.home")
  val ivUpliftUrl: String = getConfString("identity-verification-frontend.uplift-url")
  val signInUrl: String = getString("sign-in.url")
  val subscriptionUrl: String = getConfString("agent-subscription-frontend.subscription-url")
  val clientLinkBaseUrl: String = s"$appExternalUrl/agent-client-relationships/appoint-someone-to-deal-with-HMRC-for-you"
  val feedbackSurveyUrl: String = getConfString("feedback-frontend.external-url")
  def surveyUrl(isAgent: Boolean): String = {
    if isAgent then s"$feedbackSurveyUrl/$agentOriginToken"
    else s"$feedbackSurveyUrl/$clientOriginToken"
  }

  // GovUk Urls
  val govUkUrl: String = getString("gov-uk.url")
  val guidanceUrlSaSignup = s"$govUkUrl/register-for-self-assessment/self-employed"
  val guidanceUrlForAgentRoles = s"$govUkUrl/" // TODO: We need this url to be complete
  val guidanceSa = s"$govUkUrl/guidance/self-assessment-for-agents-online-service"
  val guidanceAuthoriseAgent = s"$govUkUrl/guidance/authorise-an-agent-to-deal-with-certain-tax-services-for-you"
  val privacyPolicyUrl = s"$govUkUrl/government/publications/data-protection-act-dpa-information-hm-revenue-and-customs-hold-about-you/data-protection-act-dpa-information-hm-revenue-and-customs-hold-about-you"
  val signupClientUrl: String = s"$govUkUrl/guidance/sign-up-your-client-for-making-tax-digital-for-income-tax"
  val mytaOtherTaxServicesGuidanceUrl: String = s"$govUkUrl/guidance/client-authorisation-an-overview#how-to-change-or-cancel-authorisations-as-an-agent"

  // Feature Flags
  val welshLanguageSupportEnabled: Boolean = config.getOptional[Boolean]("features.welsh-language-support").getOrElse(false)
  val emaEnabled: Boolean = config.get[Boolean]("features.enable-ema")
  val cbcEnabled: Boolean = config.get[Boolean]("features.enable-cbc")

  // Service config
  val appName: String = getString("appName")
  val timeoutDialogTimeoutSeconds: Int = servicesConfig.getInt("timeoutDialog.timeout-seconds")
  val allowedRedirectHosts: Set[String] = config.getOptional[Seq[String]]("allowed-redirect-hosts").getOrElse(Nil).toSet
  val trackRequestsPageSize: Int = servicesConfig.getInt("track-requests-per-page")
  val authorisationRequestExpiryDays: Int = servicesConfig.getDuration("invitation.expiryDuration").toDays.toInt
  val agentOriginToken = "INVITAGENT"
  val clientOriginToken = "INVITCLIENT"

  val countryListLocation: String = servicesConfig.getString("country.list.location")

  private def getString(key: String) = servicesConfig.getString(key)

  // For config contained in 'microservice.services'
  private def getConfString(key: String) =
    servicesConfig.getConfString(key, throw new RuntimeException(s"config $key not found"))

  private def baseUrl(serviceName: String) = servicesConfig.baseUrl(serviceName)