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

import uk.gov.hmrc.agentclientrelationshipsfrontend.connectors.AgentClientRelationshipsConnector
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.{AuthorisationRequest, PageInfo}

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.Future

class TrackRequestsService @Inject()(acrConnector: AgentClientRelationshipsConnector) {
  def getRequests(arn: String, pageInfo: PageInfo, filtersApplied: Option[Map[String, Seq[String]]]): Future[List[AuthorisationRequest]] =
    acrConnector.getPagedRequests(arn, LocalDate.now(), pageInfo, filtersApplied)
    
  def getTotalRequests(arn: String, filtersApplied: Option[Map[String, Seq[String]]]): Future[Int] = acrConnector.getTotalRequests(arn, filtersApplied)
  
  def getAllClientNames(arn: String, filtersApplied: Option[Map[String, Seq[String]]]): Future[List[String]] = acrConnector.getAllClientNames(arn, filtersApplied)
  
  def getStatusFilters: Future[List[String]] = acrConnector.getAvailableStatusFilters

}
