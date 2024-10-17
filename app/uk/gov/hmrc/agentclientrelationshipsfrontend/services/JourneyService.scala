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
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.{AgentTypeFieldName, ClientNameFieldName, ClientServiceFieldName, ClientTypeFieldName, JourneyTypeFieldName}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.JourneyModel
import uk.gov.hmrc.agentclientrelationshipsfrontend.repositories.JourneyRepository
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.mongo.cache.DataKey

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class JourneyService @Inject()(journeyRepository: JourneyRepository
                                       )(implicit executionContext: ExecutionContext) {

  def saveAnswerInSession(fieldName: String, answer: String)(implicit request: Request[Any]): Future[(String, String)] = {
    journeyRepository.putSession[String](DataKey[String](fieldName), answer)
  }

  def getAnswerFromSession(fieldName: String)(implicit request: Request[Any]): Future[String] = {
    journeyRepository.getFromSession[String](DataKey[String](fieldName)).map(_.getOrElse(""))
  }

  def getSeqFromSession(fieldName: String)(implicit request: Request[Any]): Future[Seq[String]] = {
    journeyRepository.getFromSession[Seq[String]](DataKey[Seq[String]](fieldName)).map(_.getOrElse(Seq.empty))
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
    journeyRepository.cacheRepo.deleteEntity(request)
  }

  private def sessionToJourneyModel(implicit request: Request[Any]): Future[JourneyModel] = {
    for {
      clientType <- getAnswerFromSession(ClientTypeFieldName)
      clientService <- getAnswerFromSession(ClientServiceFieldName)
      agentType <- getAnswerFromSession(AgentTypeFieldName)
      journeyType <- getAnswerFromSession("journeyType")
      requiredKnownFact <- getAnswerFromSession("requiredKnownFact")
      factValue <- getSeqFromSession("factValue")
      agentTypeRequired <- getAnswerFromSession("agentTypeRequired")
      clientName <- getAnswerFromSession("clientName")
      clientConfirmed <- getAnswerFromSession("clientConfirmed")
      clientId <- getAnswerFromSession("clientId")
    } yield JourneyModel(
      clientType = emptyStringToNone(clientType),
      service = emptyStringToNone(clientService),
      journeyType = JourneyType.toEnum(journeyType),
      clientId = emptyStringToNone(clientId),
      agentType = emptyStringToNone(agentType),
      clientConfirmed = emptyStringToNone(clientConfirmed).contains("true"),
      clientDetailsResponse = ClientDetailsResponse(
        requiredKnownFact = emptyStringToNone(requiredKnownFact).map(KnownFactType.toEnum),
        factValue = factValue,
        agentTypeRequired = emptyStringToNone(agentTypeRequired).contains("true"),
        clientName = emptyStringToNone(clientName)
      )
    )
  }

  private def emptyStringToNone(s: String): Option[String] = if (s.isEmpty) None else Some(s)

  def getNextRequiredStep(action: String)(implicit request: Request[Any]): Future[Call] = {
    val journey: JourneyModel = sessionToJourneyModel
    if (journey.journeyType != JourneyType.toEnum(action)) routes.SelectClientTypeController.show(action)
    else if (journey.clientConfirmed) routes.CheckYourAnswersController.show
    else if (journey.service.isEmpty) routes.SelectClientTypeController.show(action)
    else if (journey.clientDetailsResponse.isEmpty) routes.EnterClientIdController.show
    else if (journey.clientDetailsResponse.flatMap(_.requiredKnownFact).nonEmpty) routes.EnterKnownFactController.show
    else if (journey.clientDetailsResponse.exists(_.agentTypeRequired)) routes.SelectAgentTypeController.show
  }

}
