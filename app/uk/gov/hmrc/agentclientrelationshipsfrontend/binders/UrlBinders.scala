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

package uk.gov.hmrc.agentclientrelationshipsfrontend.binders

import play.api.mvc.PathBindable
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{JourneyExitType, JourneyType}

object UrlBinders:
  implicit val journeyTypeBinder: PathBindable[JourneyType] = getJourneyTypeBinder

  private def getJourneyTypeBinder = new PathBindable[JourneyType]:

    override def bind(key: String, value: String): Either[String, JourneyType] =
      JourneyType.mapping
        .get(value).map(Right(_))
        .getOrElse(Left(s"Invalid journey type: $value"))

    override def unbind(key: String, value: JourneyType): String =
      value.toString

  implicit val journeyExitTypeBinder: PathBindable[JourneyExitType] = getJourneyExitTypeBinder

  private def getJourneyExitTypeBinder = new PathBindable[JourneyExitType]:

    override def bind(key: String, value: String): Either[String, JourneyExitType] =
      JourneyExitType.values
        .find(_.toString == value).map(Right(_))
        .getOrElse(Left(s"Invalid journey exit type: $value"))

    override def unbind(key: String, value: JourneyExitType): String =
      value.toString

