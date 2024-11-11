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

import uk.gov.hmrc.agentclientrelationshipsfrontend.models.common.{ClientDetailsConfiguration, ServiceData}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{JourneyErrorType, JourneyErrors, JourneyType}

import javax.inject.{Inject, Singleton}
import scala.collection.immutable.ListMap

@Singleton
class ClientServiceConfigurationService @Inject() {
  def orderedClientTypes: Seq[String] = Seq("personal", "business", "trust")

  def allClientTypes: Set[String] = services.flatMap(_._2.clientTypes).toSet[String]
  
  def getService(serviceName: String): Option[ServiceData] = services.get(serviceName)

  def clientServicesFor(clientType: String): Seq[String] = services.filter(_._2.serviceOption == true).filter(_._2.clientTypes.contains(clientType)).keys.toSeq

  def allSupportedServices: Set[String] = services.map(_._2.serviceName).toSet[String]

  def clientDetailsFor(clientService: String): Seq[ClientDetailsConfiguration] = services(clientService).clientDetails

  def allClientIdRegex: Set[String] = services.flatMap(_._2.clientDetails.map(_.regex)).toSet[String]

  def allSupportedClientTypeIds: Set[String] = services.flatMap(_._2.clientDetails.map(_.clientIdType)).toSet[String]

  def firstClientDetailsFieldFor(clientService: String): ClientDetailsConfiguration = services(clientService).clientDetails.head

  def requiresRefining(clientService: String): Boolean = services(clientService).supportedEnrolments.size > 1

  def getSupportedEnrolments(clientService: String): Seq[String] = services(clientService).supportedEnrolments
  
  def getServiceForForm(clientService: String): String = if clientService.nonEmpty then getSupportedEnrolments(clientService) match {
    case enrols: Seq[String] if enrols.size > 1 => enrols.head // the head of the list is the parent service
    case _ => clientService
  } else ""

  def getNotFoundError(journeyType: JourneyType, clientService: String): JourneyErrorType = services(clientService).journeyErrors(journeyType).notFound

  val utrRegex = "^[0-9]{10}$"
  val urnRegex = "^[A-Z]{2}TRUST[0-9]{8}$"

  def clientDetailForServiceAndClientIdType(clientService: String, clientIdType: String): Option[ClientDetailsConfiguration] = services(clientService).clientDetails.find(_.clientIdType == clientIdType)

  private val services: ListMap[String, ServiceData] = ListMap(
    "HMRC-MTD-IT" -> ServiceData(
      serviceName = "HMRC-MTD-IT",
      serviceOption = true,
      clientTypes = Set("personal"),
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
        JourneyType.AuthorisationRequest -> JourneyErrors(
          notFound = JourneyErrorType.NotRegistered
        ),
        JourneyType.AgentCancelAuthorisation -> JourneyErrors()
      )
    ),
    "PERSONAL-INCOME-RECORD" -> ServiceData(
      serviceName = "PERSONAL-INCOME-RECORD",
      serviceOption = true,
      clientTypes = Set("personal"),
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
    "HMRC-MTD-VAT" -> ServiceData(
      serviceName = "HMRC-MTD-VAT",
      serviceOption = true,
      clientTypes = Set("personal", "business"),
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
    "HMRC-TERS-ORG" -> ServiceData(
      serviceName = "HMRC-TERS-ORG",
      serviceOption = true,
      supportedEnrolments = Seq("HMRC-TERS-ORG", "HMRC-TERSNT-ORG"), // parent service is always head of the list
      clientTypes = Set("trust"),
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
    "HMRC-TERSNT-ORG" -> ServiceData(
      serviceName = "HMRC-TERSNT-ORG",
      serviceOption = false,
      supportedEnrolments = Seq("HMRC-TERS-ORG", "HMRC-TERSNT-ORG"), // parent service is always head of the list
      clientTypes = Set("trust"),
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
    "HMRC-CGT-PD" -> ServiceData(
      serviceName = "HMRC-CGT-PD",
      serviceOption = true,
      clientTypes = Set("personal", "trust"),
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
    "HMRC-PPT-ORG" -> ServiceData(
      serviceName = "HMRC-PPT-ORG",
      serviceOption = true,
      clientTypes = Set("personal", "business", "trust"),
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
    "HMRC-CBC-ORG" -> ServiceData(
      serviceName = "HMRC-CBC-ORG",
      serviceOption = true,
      clientTypes = Set("business", "trust"),
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
    "HMRC-PILLAR2-ORG" -> ServiceData(
      serviceName = "HMRC-PILLAR2-ORG",
      serviceOption = true,
      clientTypes = Set("business", "trust"),
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
