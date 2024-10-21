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

package uk.gov.hmrc.agentclientrelationshipsfrontend.controllers

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, MessagesRequest}
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.PageInfo
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.TrackRequestsService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.TrackRequestsPage
@Singleton
class TrackRequestsController @Inject()(
                                         mcc: MessagesControllerComponents,
                                         trackRequestsService: TrackRequestsService,
                                         trackRequestsPage: TrackRequestsPage
                                       )(implicit val executionContext: ExecutionContext, appConfig: AppConfig) extends FrontendController(mcc) {
  
  def show(pageNumber: Int, filtersApplied: Option[Map[String, Seq[String]]] = None): Action[AnyContent] = Action.async:
    request =>
      given MessagesRequest[AnyContent] = request

      for{
        totalResults <- trackRequestsService.getTotalRequests("arn", filtersApplied)
        pageInfo = PageInfo(pageNumber, appConfig.trackRequestsPerPage, totalResults)
        requests <- trackRequestsService.getRequests("arn", pageInfo, filtersApplied)
        clientNames <- trackRequestsService.getAllClientNames("arn", filtersApplied)
        statusFilters <- trackRequestsService.getStatusFilters
      } yield {
        Ok(trackRequestsPage(requests, pageInfo, clientNames, statusFilters, filtersApplied))
      }
      
   def submitFilters: Action[AnyContent] = Action.async:
    request => 
      given MessagesRequest[AnyContent] = request
      val filtersApplied: Option[Map[String, Seq[String]]] = request.body.asFormUrlEncoded
      for{
        totalResults <- trackRequestsService.getTotalRequests("arn", filtersApplied)
        pageInfo = PageInfo(1, appConfig.trackRequestsPerPage, totalResults)
        requests <- trackRequestsService.getRequests("arn", pageInfo, filtersApplied)
        clientNames <- trackRequestsService.getAllClientNames("arn", filtersApplied)
        statusFilters <- trackRequestsService.getStatusFilters
      } yield {
        Ok(trackRequestsPage(requests, pageInfo, clientNames, statusFilters, filtersApplied))
      }
}
