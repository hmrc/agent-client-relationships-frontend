/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.agentclientrelationshipsfrontend.repositories

import play.api.libs.json.{Reads, Writes}
import play.api.mvc.RequestHeader
import uk.gov.hmrc.crypto.json.JsonEncryption.{sensitiveDecrypter, sensitiveEncrypter}
import uk.gov.hmrc.crypto.{Decrypter, Encrypter, Sensitive}
import uk.gov.hmrc.mongo.cache.{DataKey, SessionCacheRepository}

import scala.concurrent.{ExecutionContext, Future}

trait EncryptedSessionCacheRepository extends SessionCacheRepository:

  implicit val ec: ExecutionContext
  implicit val crypto: Encrypter & Decrypter

  override def putSession[T: Writes](dataKey: DataKey[T], data: T)
                                    (implicit request: RequestHeader): Future[(String, String)] =
    super.putSession(DataKey[SensitiveWrapper[T]](dataKey.unwrap), SensitiveWrapper(data))

  override def getFromSession[T: Reads](dataKey: DataKey[T])
                                       (implicit request: RequestHeader): Future[Option[T]] =
    super.getFromSession(DataKey[SensitiveWrapper[T]](dataKey.unwrap)).map(_.map(_.decryptedValue))

  override def deleteFromSession[T](dataKey: DataKey[T])
                                   (implicit request: RequestHeader): Future[Unit] =
    super.deleteFromSession(DataKey[SensitiveWrapper[T]](dataKey.unwrap))


case class SensitiveWrapper[T](override val decryptedValue: T) extends Sensitive[T]

object SensitiveWrapper:
  implicit def reads[T](implicit reads: Reads[T], crypto: Encrypter & Decrypter): Reads[SensitiveWrapper[T]] =
    sensitiveDecrypter(SensitiveWrapper(_))
  implicit def writes[T](implicit writes: Writes[T], crypto: Encrypter & Decrypter): Writes[SensitiveWrapper[T]] =
    sensitiveEncrypter
