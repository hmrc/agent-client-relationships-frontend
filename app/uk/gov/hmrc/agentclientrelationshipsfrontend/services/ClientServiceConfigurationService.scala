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

import uk.gov.hmrc.agentclientrelationshipsfrontend.models.common.{ClientIdType, FieldConfiguration, ServiceData}

import javax.inject.{Inject, Singleton}

@Singleton
class ClientServiceConfigurationService @Inject() {
  def allClientTypes: Set[String] = services.flatMap(_._2.clientTypes).toSet[String]
  def clientServicesFor(clientType: String): Set[String] = services.filter(_._2.clientTypes.contains(clientType)).keySet
  def clientDetailsFor(clientService: String): Seq[FieldConfiguration] = services(clientService).clientDetails
  def fieldConfigurationFor(clientService: String, fieldName: String): FieldConfiguration = services(clientService).clientDetails.filter(_.name.equalsIgnoreCase(fieldName)).head
  def firstClientDetailsFieldFor(clientService: String): FieldConfiguration = services(clientService).clientDetails.head
  def lastClientDetailsFieldFor(clientService: String): FieldConfiguration = services(clientService).clientDetails.last
  def allServices: Set[String] = services.map(_._2.serviceName).toSet[String]
  def allClientIdTypes: Set[String] = services.map(_._2.clientTypesId.id).toSet[String]
  def allClientIdRegs: Set[String] = services.map(_._2.clientDetails.head.regex).toSet[String]
  def allKnowFactsRegs: Set[String] = services.map(_._2.clientDetails.last.regex).toSet[String]


  private val services: Map[String, ServiceData] = Map(
    "HMRC-MTD-IT" -> ServiceData(
      serviceName = "HMRC-MTD-IT",
      clientTypes = Set("personal"),
      ClientIdType(
        id = "ni",
        enrolmentId = "NINO"
      ),
      clientDetails = Seq(
        FieldConfiguration(
          name = "nino",
          regex = "[[A-Z]&&[^DFIQUV]][[A-Z]&&[^DFIQUVO]] ?\\d{2} ?\\d{2} ?\\d{2} ?[A-D]{1}",
          inputType = "text",
          width = 10
        ),
        FieldConfiguration(
          name = "postcode",
          regex = "^[A-Z]{1,2}[0-9][0-9A-Z]?\\s?[0-9][A-Z]{2}$|BFPO\\s?[0-9]{1,5}$",
          inputType = "text",
          width = 20
        )
      )
    ),
    "HMRC-PPT-ORG" -> ServiceData(
      serviceName = "HMRC-PPT-ORG",
      clientTypes = Set("personal", "business", "trust"),
      ClientIdType(
        id = "EtmpRegistrationNumber",
        enrolmentId = "EtmpRegistrationNumber"
      ),
      clientDetails = Seq(
        FieldConfiguration(
          name = "pptRef",
          regex = "^[0-9A-Za-z]{15}$",
          inputType = "text",
          width = 10
        ),
        FieldConfiguration(
          name = "registrationDate",
          regex = "",
          inputType = "date",
          width = 8
        )
      )
    ),
    "HMRC-CGT-PD" -> ServiceData(
      serviceName = "HMRC-CGT-PD",
      clientTypes = Set("personal", "trust"),
      ClientIdType(
        id = "CGTPDRef",
        enrolmentId = "CGTPDRef"
      ),
      clientDetails = Seq(
        FieldConfiguration(
          name = "cgtRef",
          regex = "^X[A-Z]CGTP[0-9]{9}$",
          inputType = "text",
          width = 20
        )
      )
    ),
    "PERSONAL-INCOME-RECORD" -> ServiceData(
      serviceName = "PERSONAL-INCOME-RECORD",
      clientTypes = Set("personal"),
      ClientIdType(
        id = "ni",
        enrolmentId = "NINO"
      ),
      clientDetails = Seq(
        FieldConfiguration(
          name = "nino",
          regex = "[[A-Z]&&[^DFIQUV]][[A-Z]&&[^DFIQUVO]] ?\\d{2} ?\\d{2} ?\\d{2} ?[A-D]{1}",
          inputType = "text",
          width = 10
        ),
        FieldConfiguration(
          name = "dob",
          regex = "",
          inputType = "date",
          width = 8
        )
      )
    )
  )
}
