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

package uk.gov.hmrc.agentclientrelationshipsfrontend.utils

import play.api.libs.json.*

import scala.reflect.{ClassTag, classTag}

object EnumFormatUtil:
  // this will use the .toString method to match json to enum cases, ensure it is overriden to support this
  // if json strings are not expected to match enum names.
  def enumFormat[E <: reflect.Enum : ClassTag](enumValues: Iterable[E]): Format[E] = Format(
    Reads { json =>
      json.validate[String].flatMap { string =>
        enumValues
          .find(_.toString == string).map(JsSuccess(_))
          .getOrElse(JsError(s"Unknown value for enum ${classTag[E].runtimeClass.getSimpleName}: '$string'"))
      }
    },
    Writes(knownFactType => JsString(knownFactType.toString))
  )
