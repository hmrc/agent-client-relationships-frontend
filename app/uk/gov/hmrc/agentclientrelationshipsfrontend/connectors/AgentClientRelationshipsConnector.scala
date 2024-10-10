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

package uk.gov.hmrc.agentclientrelationshipsfrontend.connectors

import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.{AuthorisationRequest, Invitation, PageInfo}

import java.time.LocalDate
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class AgentClientRelationshipsConnector @Inject()(appConfig: AppConfig):
  def getClientDetails: Future[String] = Future.successful("Troy Barnes")

  def createInvitation: Future[String] = Future.successful("3d358ba5-cc1a-4baa-8965-75f8e7814005")

  def getInvitation(invitationId: String): Future[Invitation] = Future.successful(Invitation(invitationId, LocalDate.now().plusDays(21), "Troy Barnes"))

  def getPagedRequests(arn: String, createdOnOrAfter: LocalDate, pageInfo: PageInfo): Future[List[AuthorisationRequest]] =
    Future.successful(stubbedAuthorisationRequests.slice(pageInfo.offset, pageInfo.offset + appConfig.trackRequestsPerPage))

  def getTotalRequests(arn: String): Future[Int] = Future.successful(stubbedAuthorisationRequests.size)

  def getAllClientNames(arn: String): Future[List[String]] = Future.successful(stubbedAuthorisationRequests.map(_.clientName))

  def getAvailableStatusFilters: Future[List[String]] = Future.successful(availableFilters)


  // stubbing the back end
  private val stubbedAuthorisationRequests: List[AuthorisationRequest] = List(
    AuthorisationRequest("ABC1", Some(LocalDate.now().plusDays(1)), "Troy Barnes", "HMRC-MTD-VAT", "Pending"),
    AuthorisationRequest("ABC2", None, "Sienna Barnes", "HMRC-PPT-ORG", "Accepted"),
    AuthorisationRequest("ABC3", Some(LocalDate.now().plusDays(3)), "Martin Barnes", "HMRC-MTD-VAT", "Pending"),
    AuthorisationRequest("ABC4", Some(LocalDate.now().plusDays(4)), "Bob Barnes", "HMRC-MTD-IT", "Pending"),
    AuthorisationRequest("ABC5", Some(LocalDate.now().plusDays(5)), "Jean Barnes", "HMRC-MTD-IT", "Pending"),
    AuthorisationRequest("ABC6", Some(LocalDate.now().plusDays(6)), "Brian Barnes", "HMRC-MTD-IT", "Pending"),
    AuthorisationRequest("ABC7", None, "Abigail Barnes", "HMRC-MTD-VAT", "Cancelled"),
    AuthorisationRequest("ABC8", Some(LocalDate.now().plusDays(8)), "Francis Barnes", "HMRC-MTD-VAT", "Pending"),
    AuthorisationRequest("ABC9", Some(LocalDate.now().plusDays(9)), "Albert Barnes", "HMRC-MTD-IT", "Pending"),
    AuthorisationRequest("ABCA", Some(LocalDate.now().plusDays(10)), "Paul Barnes", "HMRC-MTD-IT", "Pending"),
    AuthorisationRequest("ABCB", Some(LocalDate.now().plusDays(11)), "Ravi Barnes", "HMRC-MTD-IT", "Pending"),
    AuthorisationRequest("ABCC", Some(LocalDate.now().plusDays(12)), "Diane Barnes", "HMRC-MTD-IT", "Pending"),
    AuthorisationRequest("ABCD", Some(LocalDate.now().plusDays(13)), "Marjorie Barnes", "HMRC-MTD-IT", "Pending"),
    AuthorisationRequest("ABCE", Some(LocalDate.now().plusDays(14)), "Ade Barnes", "HMRC-MTD-IT", "Pending"),
    AuthorisationRequest("ABCF", Some(LocalDate.now().plusDays(15)), "Ewan Barnes", "HMRC-MTD-IT", "Pending"),
    AuthorisationRequest("AABC1", Some(LocalDate.now().plusDays(1)), "Troy Stevens", "HMRC-MTD-IT", "Pending"),
    AuthorisationRequest("AABC2", None, "Sienna Stevens", "HMRC-PPT-ORG", "Accepted"),
    AuthorisationRequest("AABC3", Some(LocalDate.now().plusDays(3)), "Martin Stevens", "HMRC-MTD-IT", "Pending"),
    AuthorisationRequest("AABC4", Some(LocalDate.now().plusDays(4)), "Bob Stevens", "HMRC-MTD-IT", "Pending"),
    AuthorisationRequest("AABC5", Some(LocalDate.now().plusDays(5)), "Jean Stevens", "HMRC-MTD-IT", "Pending"),
    AuthorisationRequest("AABC6", Some(LocalDate.now().plusDays(6)), "Brian Stevens", "HMRC-MTD-IT", "Pending"),
    AuthorisationRequest("AABC7", None, "Abigail Stevens", "HMRC-MTD-VAT", "Cancelled"),
    AuthorisationRequest("AABC8", Some(LocalDate.now().plusDays(8)), "Francis Stevens", "HMRC-MTD-VAT", "Pending"),
    AuthorisationRequest("AABC9", Some(LocalDate.now().plusDays(9)), "Albert Stevens", "HMRC-MTD-IT", "Pending"),
    AuthorisationRequest("AABCA", Some(LocalDate.now().plusDays(10)), "Paul Stevens", "HMRC-MTD-IT", "Pending"),
    AuthorisationRequest("AABCB", Some(LocalDate.now().plusDays(11)), "Ravi Stevens", "HMRC-MTD-IT", "Pending"),
    AuthorisationRequest("AABCC", Some(LocalDate.now().plusDays(12)), "Diane Stevens", "HMRC-MTD-IT", "Pending"),
    AuthorisationRequest("AABCD", Some(LocalDate.now().plusDays(13)), "Marjorie Stevens", "HMRC-MTD-IT", "Pending"),
    AuthorisationRequest("AABCE", Some(LocalDate.now().plusDays(14)), "Ade Stevens", "HMRC-MTD-IT", "Pending"),
    AuthorisationRequest("AABCF", Some(LocalDate.now().plusDays(15)), "Ewan Stevens", "HMRC-MTD-IT", "Pending"),
    AuthorisationRequest("BABC1", Some(LocalDate.now().plusDays(1)), "Troy Singh", "HMRC-MTD-IT", "Pending"),
    AuthorisationRequest("BABC2", None, "Sienna Singh", "HMRC-PPT-ORG", "Accepted"),
    AuthorisationRequest("BABC3", Some(LocalDate.now().plusDays(3)), "Martin Singh", "HMRC-MTD-IT", "Pending"),
    AuthorisationRequest("BABC4", Some(LocalDate.now().plusDays(4)), "Bob Singh", "HMRC-MTD-IT", "Pending"),
    AuthorisationRequest("BABC5", Some(LocalDate.now().plusDays(5)), "Jean Singh", "HMRC-MTD-IT", "Pending"),
    AuthorisationRequest("BABC6", Some(LocalDate.now().plusDays(6)), "Brian Singh", "HMRC-MTD-IT", "Pending"),
    AuthorisationRequest("BABC7", None, "Abigail Singh", "HMRC-MTD-VAT", "Cancelled"),
    AuthorisationRequest("BABC8", Some(LocalDate.now().plusDays(8)), "Francis Singh", "HMRC-MTD-VAT", "Pending"),
    AuthorisationRequest("BABC9", Some(LocalDate.now().plusDays(9)), "Albert Singh", "HMRC-MTD-IT", "Pending"),
    AuthorisationRequest("BABCA", Some(LocalDate.now().plusDays(10)), "Paul Singh", "HMRC-MTD-IT", "Pending"),
    AuthorisationRequest("BABCB", Some(LocalDate.now().plusDays(11)), "Ravi Singh", "HMRC-MTD-IT", "Pending"),
    AuthorisationRequest("BABCC", Some(LocalDate.now().plusDays(12)), "Diane Singh", "HMRC-MTD-IT", "Pending"),
    AuthorisationRequest("BABCD", Some(LocalDate.now().plusDays(13)), "Marjorie Singh", "HMRC-MTD-IT", "Pending"),
    AuthorisationRequest("BABCE", Some(LocalDate.now().plusDays(14)), "Ade Singh", "HMRC-MTD-IT", "Pending"),
    AuthorisationRequest("BABCF", Some(LocalDate.now().plusDays(15)), "Ewan Singh", "HMRC-MTD-IT", "Pending")
  )

  private val availableFilters: List[String] = List(
    "AllStatuses",
    "ExpireInNext5Days",
    "ActivityWithinLast5Days",
    "ClientNotYetResponded",
    "AgentCancelledAuthorisation",
    "DeclinedByClient",
    "AcceptedByClient",
    "Expired",
    "ClientCancelledAuthorisation",
    "HMRCCancelledAuthorisation"
  )

