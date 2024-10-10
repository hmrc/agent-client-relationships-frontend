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

import uk.gov.hmrc.agentclientrelationshipsfrontend.models.Invitation

import java.time.LocalDate
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class AgentClientRelationshipsConnector @Inject():
  def getClientDetails: Future[String] = Future.successful("Troy Barnes")
  def createInvitation: Future[String] = Future.successful("3d358ba5-cc1a-4baa-8965-75f8e7814005")
  def getInvitation(invitationId: String): Future[Invitation] = Future.successful(Invitation(invitationId, LocalDate.now().plusDays(21), "Troy Barnes"))

  def getAllRequests(arn: String, createdOnOrAfter: LocalDate): Future[List[Invitation]] = Future.successful(List(
    Invitation("ABC1", LocalDate.now().plusDays(1), "Troy Barnes"),
    Invitation("ABC2", LocalDate.now().plusDays(2), "Sienna Barnes"),
    Invitation("ABC3", LocalDate.now().plusDays(3), "Martin Barnes"),
    Invitation("ABC4", LocalDate.now().plusDays(4), "Bob Barnes"),
    Invitation("ABC5", LocalDate.now().plusDays(5), "Jean Barnes"),
    Invitation("ABC6", LocalDate.now().plusDays(6), "Brian Barnes"),
    Invitation("ABC7", LocalDate.now().plusDays(7), "Abigail Barnes"),
    Invitation("ABC8", LocalDate.now().plusDays(8), "Francis Barnes"),
    Invitation("ABC9", LocalDate.now().plusDays(9), "Albert Barnes"),
    Invitation("ABCA", LocalDate.now().plusDays(10), "Paul Barnes"),
    Invitation("ABCB", LocalDate.now().plusDays(11), "Ravi Barnes"),
    Invitation("ABCC", LocalDate.now().plusDays(12), "Diane Barnes"),
    Invitation("ABCD", LocalDate.now().plusDays(13), "Marjorie Barnes"),
    Invitation("ABCE", LocalDate.now().plusDays(14), "Ade Barnes"),
    Invitation("ABCF", LocalDate.now().plusDays(15), "Ewan Barnes")
  ))

