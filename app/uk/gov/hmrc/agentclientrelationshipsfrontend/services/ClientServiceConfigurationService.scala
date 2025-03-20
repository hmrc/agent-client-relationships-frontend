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

import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.ClientType
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.ClientType.{business, personal, trust}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.common.{ClientDetailsConfiguration, ServiceData}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{AgentJourneyType, JourneyErrors, JourneyExitType}

import javax.inject.{Inject, Singleton}
import scala.collection.immutable.ListMap

@Singleton
class ClientServiceConfigurationService @Inject()(implicit appConfig: AppConfig) extends ServiceConstants {

  def orderedClientTypes: Seq[String] = Seq("personal", "business", "trust")

  def allClientTypes: Set[String] = services.flatMap(_._2.clientTypes).toSet.map(_.toString)
  
  def getService(serviceName: String): Option[ServiceData] = services.get(serviceName)
  
  def validateUrlPart(urlPartKey: String): Boolean = getServiceKeysForUrlPart(urlPartKey).nonEmpty

  // services that are enabled and have the service option enabled meaning they will appear in the user facing service
  // options list for the given user type
  def clientServicesFor(clientType: String): Seq[String] = services
    .filter(_._2.serviceOption == true)
    .filter(_._2.clientTypes.contains(ClientType.valueOf(clientType)))
    .keys.toSeq

  def allEnabledServices: Set[String] = services.filter(_._2.serviceOption == true).map(_._2.serviceName).toSet
  def allSupportedServices: Set[String] = services.map(_._2.serviceName).toSet[String]

  def clientDetailsFor(clientService: String): Seq[ClientDetailsConfiguration] = services(clientService).clientDetails

  def allClientIdRegex: Set[String] = services.flatMap(_._2.clientDetails.map(_.regex)).toSet[String]

  def allSupportedClientTypeIds: Set[String] = services.flatMap(_._2.clientDetails.map(_.clientIdType)).toSet[String]

  def firstClientDetailsFieldFor(clientService: String): ClientDetailsConfiguration = services(clientService).clientDetails.head

  def requiresRefining(clientService: String): Boolean = services(clientService).supportedEnrolments.size > 1 && services(clientService).overseasServiceName.isEmpty

  def getSupportedEnrolments(clientService: String): Seq[String] = services(clientService).supportedEnrolments
  
  // this method normalises the service name to the parent service name if the service supports multiple service keys for enrolments
  def getServiceForForm(clientService: String): String = if clientService.nonEmpty then (getSupportedAgentRoles(clientService), getSupportedEnrolments(clientService)) match {
    case (_, enrols) if enrols.size > 1 => enrols.head // the head of the list is the parent service
    case (roles, _) if roles.size > 1 => roles.head // the head of the list is the parent service
    case _ => clientService
  } else ""

  // some services may have custom not found errors
  def getNotFoundError(journeyType: AgentJourneyType, clientService: String): JourneyExitType =
    services(clientService).journeyErrors(journeyType).notFound

  def supportsAgentRoles(clientService: String): Boolean = services.get(clientService).exists(_.supportedAgentRoles.size > 1)

  def getSupportedAgentRoles(clientService: String): Seq[String] = services(clientService).supportedAgentRoles

  val utrRegex = "^[0-9]{10}$"
  val urnRegex = "^[A-Z]{2}TRUST[0-9]{8}$"

  def clientDetailForServiceAndClientIdType(clientService: String, clientIdType: String): Option[ClientDetailsConfiguration] =
    services(clientService).clientDetails.find(_.clientIdType == clientIdType)

  def getServiceKeysForUrlPart(taxService: String): Set[String] = services
    .find(_._2.urlPart.keySet.contains(taxService))
    .map((_, serviceData) => serviceData.urlPart(taxService))
    .getOrElse(Set())

  // url parts are used in public urls to determine which service
  def getUrlPart(clientService: String): String = services(getServiceForForm(clientService)).urlPart.keys.head

  // when a service only supports one client type we can infer the client type from the service when it's missing
  // from any fast track requests
  def inferredClientType(clientService: String): Option[String] = {
    val clientTypes = services(getServiceForForm(clientService)).clientTypes
    if clientTypes.size == 1 then Some(clientTypes.head.toString) else None
  }

  val supportingAgentServices: Seq[String] = Seq(HMRCMTDITSUPP)
  
  private val services: ListMap[String, ServiceData] = ListMap(
    HMRCMTDIT -> ServiceData(
      serviceName = HMRCMTDIT,
      urlPart = Map(incomeTax -> Set(HMRCMTDIT, HMRCNI, HMRCPT)),
      serviceOption = true,
      supportedAgentRoles = if appConfig.emaEnabled then Seq(HMRCMTDIT, HMRCMTDITSUPP) else Seq.empty,
      clientTypes = Set(personal),
      clientDetails = Seq(
        ClientDetailsConfiguration(
          name = "nino",
          regex = "[[A-Z]&&[^DFIQUV]][[A-Z]&&[^DFIQUVO]] ?\\d{2} ?\\d{2} ?\\d{2} ?[A-D]{1}",
          inputType = "text",
          width = 10,
          clientIdType = "ni"
        )
      ),
      journeyErrors = Map(
        AgentJourneyType.AuthorisationRequest -> JourneyErrors(
          notFound = JourneyExitType.NotRegistered
        ),
        AgentJourneyType.AgentCancelAuthorisation -> JourneyErrors()
      )
    ),
    HMRCMTDITSUPP -> ServiceData(
      serviceName = HMRCMTDITSUPP,
      urlPart = Map(incomeTax -> Set(HMRCMTDIT, HMRCNI, HMRCPT)),
      serviceOption = false,
      supportedAgentRoles = Seq(HMRCMTDIT, HMRCMTDITSUPP),
      clientTypes = Set(personal),
      clientDetails = Seq(
        ClientDetailsConfiguration(
          name = "nino",
          regex = "[[A-Z]&&[^DFIQUV]][[A-Z]&&[^DFIQUVO]] ?\\d{2} ?\\d{2} ?\\d{2} ?[A-D]{1}",
          inputType = "text",
          width = 10,
          clientIdType = "ni"
        )
      )
    ),
    PERSONALINCOMERECORD -> ServiceData(
      serviceName = PERSONALINCOMERECORD,
      urlPart = Map(incomeRecordViewer -> Set(HMRCNI, HMRCPT)),
      serviceOption = true,
      clientTypes = Set(personal),
      clientDetails = Seq(
        ClientDetailsConfiguration(
          name = "nino",
          regex = "[[A-Z]&&[^DFIQUV]][[A-Z]&&[^DFIQUVO]] ?\\d{2} ?\\d{2} ?\\d{2} ?[A-D]{1}",
          inputType = "text",
          width = 10,
          clientIdType = "ni"
        )
      )
    ),
    HMRCMTDVAT -> ServiceData(
      serviceName = HMRCMTDVAT,
      urlPart = Map(vat -> Set(HMRCMTDVAT)),
      serviceOption = true,
      clientTypes = Set(personal, business),
      clientDetails = Seq(
        ClientDetailsConfiguration(
          name = "vrn",
          regex = "^[0-9]{9}$",
          inputType = "text",
          width = 10,
          clientIdType = "vrn"
        )
      )
    ),
    HMRCTERSORG -> ServiceData(
      serviceName = HMRCTERSORG,
      urlPart = Map(trustsAndEstates -> Set(HMRCTERSORG, HMRCTERSNTORG)),
      serviceOption = true,
      supportedEnrolments = Seq(HMRCTERSORG, HMRCTERSNTORG), // parent service is always head of the list
      clientTypes = Set(trust),
      clientDetails = Seq(
        ClientDetailsConfiguration(
          name = "utr",
          regex = utrRegex,
          inputType = "text",
          width = 10,
          clientIdType = "utr"
        )
      )
    ),
    HMRCTERSNTORG -> ServiceData(
      serviceName = HMRCTERSNTORG,
      urlPart = Map(trustsAndEstates -> Set(HMRCTERSORG, HMRCTERSNTORG)),
      serviceOption = false,
      supportedEnrolments = Seq(HMRCTERSORG, HMRCTERSNTORG), // parent service is always head of the list
      clientTypes = Set(trust),
      clientDetails = Seq(
        ClientDetailsConfiguration(
          name = "urn",
          regex = urnRegex,
          inputType = "text",
          width = 20,
          clientIdType = "urn"
        )
      )
    ),
    HMRCCGTPD -> ServiceData(
      serviceName = HMRCCGTPD,
      urlPart = Map(capitalGainsTaxUkProperty -> Set(HMRCCGTPD)),
      serviceOption = true,
      clientTypes = Set(personal, trust),
      clientDetails = Seq(
        ClientDetailsConfiguration(
          name = "cgtRef",
          regex = "^X[A-Z]CGTP[0-9]{9}$",
          inputType = "text",
          width = 20,
          clientIdType = "CGTPDRef"
        )
      )
    ),
    HMRCPPTORG -> ServiceData(
      serviceName = HMRCPPTORG,
      urlPart = Map(plasticPackagingTax -> Set(HMRCPPTORG)),
      serviceOption = true,
      clientTypes = Set(personal, business, trust),
      clientDetails = Seq(
        ClientDetailsConfiguration(
          name = "pptRef",
          regex = "^X[A-Z]PPT000[0-9]{7}$",
          inputType = "text",
          width = 20,
          clientIdType = "EtmpRegistrationNumber"
        )
      )
    ),
    HMRCCBCORG -> ServiceData(
      serviceName = HMRCCBCORG,
      overseasServiceName = Some(HMRCCBCNONUKORG),
      supportedEnrolments = Seq(HMRCCBCORG, HMRCCBCNONUKORG), // parent service is always head of the list
      urlPart = Map(countryByCountryReporting -> Set(HMRCCBCORG, HMRCCBCNONUKORG)),
      serviceOption = appConfig.cbcEnabled,
      clientTypes = Set(business, trust),
      clientDetails = Seq(
        ClientDetailsConfiguration(
          name = "cbcId",
          regex = "^X[A-Z]CBC[0-9]{10}$",
          inputType = "text",
          width = 20,
          clientIdType = "cbcId"
        )
      )
    ),
    HMRCCBCNONUKORG -> ServiceData(
      serviceName = HMRCCBCNONUKORG,
      overseasServiceName = Some(HMRCCBCNONUKORG),
      supportedEnrolments = Seq(HMRCCBCORG, HMRCCBCNONUKORG), // parent service is always head of the list
      urlPart = Map(countryByCountryReporting -> Set(HMRCCBCORG, HMRCCBCNONUKORG)),
      serviceOption = false,
      clientTypes = Set(business, trust),
      clientDetails = Seq(
        ClientDetailsConfiguration(
          name = "cbcId",
          regex = "^X[A-Z]CBC[0-9]{10}$",
          inputType = "text",
          width = 20,
          clientIdType = "cbcId"
        )
      )
    ),
    HMRCPILLAR2ORG -> ServiceData(
      serviceName = HMRCPILLAR2ORG,
      urlPart = Map(pillar2 -> Set(HMRCPILLAR2ORG)),
      serviceOption = true,
      clientTypes = Set(business, trust),
      clientDetails = Seq(
        ClientDetailsConfiguration(
          name = "PlrId",
          regex = "^X[A-Z]{1}PLR[0-9]{10}$",
          inputType = "text",
          width = 20,
          clientIdType = "PLRID"
        )
      )
    )
  )
}
