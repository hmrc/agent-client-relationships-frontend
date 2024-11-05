/*
 * Copyright 2023 HM Revenue & Customs
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

import play.api.http.Status.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.HttpAPIMonitor
import uk.gov.hmrc.http.*
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.play.bootstrap.metrics.Metrics

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SsoConnector @Inject()(http: HttpClientV2)
                            (implicit appConfig: AppConfig,
                              val metrics: Metrics,
                              val ec: ExecutionContext) extends HttpAPIMonitor {
  
  def getAllowlistedDomains()(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Set[String]] =
    monitor("ConsumedAPI-SSO-getExternalDomains-GET") {
      http
        .get(url"${appConfig.ssoBaseUrl}/sso/domains")
        .execute[HttpResponse]
        .map { response =>
          response.status match {
            case OK => (response.json \ "externalDomains").as[Set[String]] ++ (response.json \ "internalDomains").as[Set[String]]
            case s => throw new Exception(s"retrieval of allowlisted domains failed, status: $s")
          }
        }
    }
}

