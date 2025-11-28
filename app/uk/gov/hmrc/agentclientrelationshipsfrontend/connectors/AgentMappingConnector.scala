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

import play.api.http.HeaderNames
import play.api.http.Status.*
import play.api.libs.json.Json
import play.api.libs.ws.JsonBodyWritables.*
import play.api.mvc.RequestHeader
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.journey.routes
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.AgentJourneyType.AuthorisationRequest
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.RequestSupport.hc

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AgentMappingConnector @Inject()(appConfig: AppConfig,
                                      httpV2: HttpClientV2
                                     )(implicit executionContext: ExecutionContext):

  private val agentMappingUrl = s"${appConfig.agentMappingFrontendBaseUrl}"

  def startAuthMappingJourney(clientsLegacyRelationships: Seq[String], clientName: String)(implicit rh: RequestHeader): Future[String] = {
    given HeaderCarrier = hc.copy(extraHeaders = hc.headers(Seq(HeaderNames.COOKIE)))

    httpV2
      .post(url"$agentMappingUrl/start-auth-mapping-journey")
      .withBody(Json.obj(
        "clientsLegacyRelationships" -> clientsLegacyRelationships,
        "clientName" -> clientName,
        "backUrl" -> s"${appConfig.appExternalUrl}${routes.DoYouAlreadyManageController.show(AuthorisationRequest).url}",
        "cancelUrl" -> s"${appConfig.appExternalUrl}${routes.DoYouAlreadyManageController.cancelMapping(AuthorisationRequest).url}"
      ))
      .execute[HttpResponse].map { response =>
        response.status match {
          case CREATED => (response.json \ "redirectUrl").as[String]
          case _ => throw new RuntimeException(s"Failed to start a mapping journey, request: ${response.body}")
        }
      }
  }