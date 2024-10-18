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

import com.codahale.metrics.MetricRegistry
import play.api.Logging
import play.api.http.Status.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.IvResult
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.HttpAPIMonitor
import uk.gov.hmrc.http.*
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.play.bootstrap.metrics.Metrics

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class IdentityVerificationConnector @Inject()(http: HttpClientV2)
                                             (implicit appConfig: AppConfig,
                                              val metrics: Metrics,
                                              val ec: ExecutionContext)
  extends HttpAPIMonitor with Logging {

  private[connectors] def getIVResultUrl(journeyId: String) =
    s"${appConfig.ivFrontendBaseUrl}/mdtp/journey/journeyId/$journeyId"

  def getIVResult(journeyId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[IvResult]] =
    monitor("ConsumedAPI-Client-Get-IVResult-GET") {
      http
        .get(url"${getIVResultUrl(journeyId)}")
        .execute[HttpResponse]
        .map { response =>
          response.status match {
            case OK =>
              val result = (response.json \ "result").as[IvResult]
              logger.warn(s"identity verification returned result $result for journeyId $journeyId")
              Some(result)
            case NOT_FOUND => None
          }
        }
    }
}
