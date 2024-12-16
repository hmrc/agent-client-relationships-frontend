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

package uk.gov.hmrc.agentclientrelationshipsfrontend.models.client

import play.api.libs.json.Format
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.EnumFormatUtil

enum ClientType(clientType: String):
  case personal extends ClientType("personal")
  case business extends ClientType("business")
  case trust extends ClientType("trust")

  override def toString: String = clientType

object ClientType:
  implicit val format: Format[ClientType] = EnumFormatUtil.enumFormat(ClientType.values)
  val validValues: Set[String] = ClientType.values.map(_.toString).toSet