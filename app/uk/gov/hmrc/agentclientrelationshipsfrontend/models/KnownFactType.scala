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
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.common.KnownFactsConfiguration
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.EnumFormatUtil

enum KnownFactType(val knownFactTypeName: String, val fieldConfiguration: KnownFactsConfiguration):
  case PostalCode extends KnownFactType(knownFactTypeName = "PostalCode", fieldConfiguration= KnownFactsConfiguration("postcode", "^[A-Z]{1,2}[0-9][0-9A-Z]?\\s?[0-9][A-Z]{2}$|BFPO\\s?[0-9]{1,5}$", "text", 10))
  case CountryCode extends KnownFactType(knownFactTypeName = "CountryCode",  fieldConfiguration= KnownFactsConfiguration("countryCode", "^[A-Z]{2}$", "select", 20))
  case Email extends KnownFactType(knownFactTypeName = "Email", fieldConfiguration= KnownFactsConfiguration("email", "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", "text", 20))
  case Date extends KnownFactType(knownFactTypeName = "Date", fieldConfiguration= KnownFactsConfiguration("date", "^[0-9]{4}-[0-9]{2}-[0-9]{2}$", "date", 4))

  override def toString: String = knownFactTypeName

  def getFieldConfiguration: FieldConfiguration = this match {
    case PostalCode => FieldConfiguration("postcode", "^[A-Z]{1,2}[0-9R][0-9A-Z]? [0-9][A-Z]{2}$", "text", 10, "Postcode")
    case CountryCode => FieldConfiguration("countryCode", "^[A-Z]{2}$", "select", 20, "Country")
    case Email => FieldConfiguration("email", "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", "text", 20, "Email address")
    case Date => FieldConfiguration("date", "^[0-9]{4}-[0-9]{2}-[0-9]{2}$", "date", 4, "Date")
    case _ => throw new IllegalArgumentException("Unknown KnownFactType")
  }

object KnownFactType:
  implicit val format: Format[KnownFactType] = EnumFormatUtil.enumFormat(KnownFactType.values)

