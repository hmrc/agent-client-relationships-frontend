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

package uk.gov.hmrc.agentclientrelationshipsfrontend.models

import play.api.libs.json.Format
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.common.FieldConfiguration
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.EnumFormatUtil

enum KnownFactType(val name: String):
  case PostalCode extends KnownFactType("PostalCode")
  case CountryCode extends KnownFactType("CountryCode")
  case Email extends KnownFactType("Email")
  case Date extends KnownFactType("Date")

  override def toString: String = name

object KnownFactType:
  implicit val format: Format[KnownFactType] = EnumFormatUtil.enumFormat(KnownFactType.values)

  def getFieldConfiguration: FieldConfiguration = this match {
    case PostalCode => FieldConfiguration("postcode", "^[A-Z]{1,2}[0-9R][0-9A-Z]? [0-9][A-Z]{2}$", "text", 10, "Postcode")
    case CountryCode => FieldConfiguration("countryCode", "^[A-Z]{2}$", "select", 20, "Country")
    case Email => FieldConfiguration("email", "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", "text", 20, "Email address")
    case Date => FieldConfiguration("date", "^[0-9]{4}-[0-9]{2}-[0-9]{2}$", "date", 4, "Date")
    case _ => throw new IllegalArgumentException("Unknown KnownFactType")
  }
