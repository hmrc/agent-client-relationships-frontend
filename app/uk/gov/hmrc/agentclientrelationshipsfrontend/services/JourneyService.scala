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

import play.api.mvc.Request
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.{AgentTypeFieldName, ClientNameFieldName, ClientServiceFieldName, ClientTypeFieldName}
import uk.gov.hmrc.agentclientrelationshipsfrontend.repositories.CreateInvitationJourneyRepository
import uk.gov.hmrc.mongo.cache.DataKey

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class JourneyService @Inject()(createInvitationJourneyRepository: CreateInvitationJourneyRepository
                                       )(implicit executionContext: ExecutionContext) {
  
  def saveAnswerInSession(fieldName: String, answer: String)(implicit request: Request[Any]): Future[(String, String)] = {
    createInvitationJourneyRepository.putSession[String](DataKey[String](fieldName), answer)
  }

  def getAnswerFromSession(fieldName: String)(implicit request: Request[Any]): Future[String] = {
    createInvitationJourneyRepository.getFromSession[String](DataKey[String](fieldName)).map(_.getOrElse(""))
  }

  def getCheckYourAnswersDataFromSession(implicit request: Request[Any]): Future[(String, String, String, String)] = {
    for {
      clientType <- getAnswerFromSession(ClientTypeFieldName)
      clientService <- getAnswerFromSession(ClientServiceFieldName)
      clientName <- getAnswerFromSession(ClientNameFieldName)
      agentType <- getAnswerFromSession(AgentTypeFieldName)
    } yield (clientType, clientService, clientName, agentType)
  }

  def deleteAllAnswersInSession(implicit request: Request[Any]): Future[Unit] = {
    createInvitationJourneyRepository.cacheRepo.deleteEntity(request)
  }

}
