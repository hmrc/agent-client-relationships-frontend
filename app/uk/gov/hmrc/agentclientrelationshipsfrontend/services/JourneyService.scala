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
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.{ClientDetailsResponse, CompleteAnswers, JourneyModel}
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

  def getCheckYourAnswersDataFromSession(implicit request: Request[Any]): Future[CompleteAnswers] = 
    for {
      clientType <- getAnswerFromSession(ClientTypeFieldName)
      clientService <- getAnswerFromSession(ClientServiceFieldName)
      clientName <- getAnswerFromSession(ClientNameFieldName)
      agentType <- getAnswerFromSession(AgentTypeFieldName)
      clientConfirmed <- getAnswerFromSession("clientConfirmed")
    } yield CompleteAnswers(clientConfirmed, clientType, clientService, clientName, agentType)

  def deleteAllAnswersInSession(implicit request: Request[Any]): Future[Unit] = {
    journeyRepository.cacheRepo.deleteEntity(request)
  }

  private def sessionToJourneyModel(implicit request: Request[Any]): Future[JourneyModel] = {
    for {
      clientType <- getAnswerFromSession("clientType")(request)
      clientService <- getAnswerFromSession("clientService")(request)
      agentType <- getAnswerFromSession("agentType")(request)
      journeyType <- getAnswerFromSession("journeyType")(request)
      requiredKnownFact <- getAnswerFromSession("requiredKnownFact")(request)
      factValue <- getSeqFromSession("factValue")(request)
      agentTypeRequired <- getAnswerFromSession("agentTypeRequired")(request)
      clientName <- getAnswerFromSession("clientName")(request)
      clientConfirmed <- getAnswerFromSession("clientConfirmed")(request)
      clientId <- getAnswerFromSession("clientId")(request)
    } yield JourneyModel(
      clientType = emptyStringToNone(clientType),
      service = emptyStringToNone(clientService),
      journeyType = JourneyType.toEnum(journeyType),
      clientId = emptyStringToNone(clientId),
      agentType = emptyStringToNone(agentType),
      clientConfirmed = emptyStringToNone(clientConfirmed).contains("true"),
      clientDetailsResponse = ClientDetailsResponse(
        emptyStringToNone(requiredKnownFact).map(s => KnownFactType.toEnum(s)),
        factValue,
        emptyStringToNone(agentTypeRequired).contains("true"),
        emptyStringToNone(clientName)
        emptyStringToNone(relationshipExists).contains("true")
      )
    )
  }

  private def emptyStringToNone(s: String): Option[String] = if (s.isEmpty) None else Some(s)

  def getNextRequiredStep(action: String)(implicit request: Request[Any]): Future[Call] = {
    for {
      journey <- sessionToJourneyModel(request)
    } yield journey

    if (journey.journeyType != JourneyType.toEnum(action)) {
      deleteAllAnswersInSession(request) // reset the journey to be of the new journey type and start again
      routes.SelectClientTypeController.show(action)
    }
    else if (journey.clientConfirmed) routes.CheckYourAnswersController.show
    else if (journey.service.isEmpty) routes.SelectClientTypeController.show(action)
    else if (journey.clientDetailsResponse.isEmpty) routes.EnterClientIdController.show
    else if (journey.clientDetailsResponse.exists(_.requiredKnownFact).nonEmpty) routes.EnterClientVerifierController.show
    else if (journey.clientDetailsResponse.exists(_.agentTypeRequired).contains(true)) routes.SelectAgentTypeController.show
    
  }
  
  def populateJourneyWithFastTrack(action: String, data: Option[Map[String, Seq[String]]])(implicit request: Request[Any]): Future[Unit] =
    data match {
      case Some(d) =>  for {
        _ <- deleteAllAnswersInSession(request)
        _ <- saveAnswerInSession("journeyType", action)(request)
        _ <- d.map((k, v) => saveAnswerInSession(k, v))
        clientDetailsResponse <- journeyService.getClientDetailsResponse(data)
        _ <- saveAnswerInSession("requiredKnownFact", clientDetailsResponse.requiredknownFact)(request)
        _ <- saveAnswerInSession("factValue", clientDetailsResponse.factValue)(request)
        _ <- saveAnswerInSession("clientName", clientDetailsResponse.clientName)(request)
        _ <- saveAnswerInSession("agentTypeRequired", clientDetailsResponse.agentTypeRequired)(request)
        _ <- saveAnswerInSession("relationshipExists", clientDetailsResponse.relationshipExists)(request)
      } yield result 
      case _ => // TODO: Return "failure" or something for caller to act upon - e.g. redirect to start of journey
    }
   
    
    def getClientDetailsResponse(data: Option[Map[String, Seq[String]]]): Future[ClientDetailsResponse] = {
      
      // TODO: HTTP call to get client details response using the client identifier and service from the request body
      //       and return the response as a ClientDetailsResponse
    }

}
