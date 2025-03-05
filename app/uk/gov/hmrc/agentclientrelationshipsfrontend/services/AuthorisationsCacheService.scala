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

import play.api.libs.json.{Reads, Writes}
import play.api.mvc.Request
import uk.gov.hmrc.agentclientrelationshipsfrontend.repositories.AuthorisationsCacheRepository
import uk.gov.hmrc.mongo.cache.DataKey

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class AuthorisationsCacheService @Inject()(sessionCacheRepository: AuthorisationsCacheRepository) {

  def get[T](dataKey: DataKey[T])
            (implicit reads: Reads[T], request: Request[?]): Future[Option[T]] = {
        sessionCacheRepository.getFromSession(dataKey)
  }

  def put[T](dataKey: DataKey[T], value: T)
            (implicit writes: Writes[T], request: Request[?]): Future[(String, String)] = {
        sessionCacheRepository.putSession(dataKey, value)
  }

  def delete[T](dataKey: DataKey[T])
               (implicit request: Request[?]): Future[Unit] = {
    sessionCacheRepository.deleteFromSession(dataKey)
  }

}
