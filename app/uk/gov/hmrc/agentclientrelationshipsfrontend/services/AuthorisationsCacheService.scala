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

import play.api.libs.json.Writes
import play.api.mvc.RequestHeader
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.{Authorisation, AuthorisationsCache}
import uk.gov.hmrc.agentclientrelationshipsfrontend.repositories.AuthorisationsCacheRepository
import uk.gov.hmrc.mongo.cache.DataKey

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthorisationsCacheService @Inject()(sessionCacheRepository: AuthorisationsCacheRepository)
                                          (implicit executionContext: ExecutionContext) {

  def getAuthorisation(id: String)
                      (implicit request: RequestHeader): Future[Option[Authorisation]] =
    sessionCacheRepository.getFromSession[AuthorisationsCache](DataKey("authorisationsCache")).map {
      case Some(records) => records.authorisations.find(_.uid == id)
      case None => None
    }

  def getAuthorisations(implicit request: RequestHeader): Future[Option[AuthorisationsCache]] =
    sessionCacheRepository.getFromSession[AuthorisationsCache](DataKey("authorisationsCache"))

  def putAuthorisations(value: AuthorisationsCache)
                       (implicit writes: Writes[AuthorisationsCache], request: RequestHeader): Future[(String, String)] =
    sessionCacheRepository.putSession[AuthorisationsCache](DataKey("authorisationsCache"), value)

}
