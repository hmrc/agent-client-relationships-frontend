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

package uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.libs.json.{JsError, JsString, JsSuccess, Json}

class AgentJourneyStateSpec extends AnyWordSpecLike with Matchers {

  "JourneyState format" should {
    JourneyState.values.foreach(value => s"write $value to a json string and read it back" in {
      val jsString = JsString(value.toString)
      Json.toJson[JourneyState](value) shouldBe jsString
      Json.fromJson[JourneyState](jsString) shouldBe JsSuccess(value)
    })

    "fail to read an unknown value" in {
      Json.fromJson[JourneyState](JsString("invalid")) shouldBe JsError("Invalid JourneyState")
    }
  }

}
