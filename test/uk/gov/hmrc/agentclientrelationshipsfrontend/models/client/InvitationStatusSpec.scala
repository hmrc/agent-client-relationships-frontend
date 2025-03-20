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

package uk.gov.hmrc.agentclientrelationshipsfrontend.models.client

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.libs.json.{JsString, Json}

class InvitationStatusSpec extends AnyWordSpecLike with Matchers :

  "InvitationStatus" should :

    "instantiate from a recognised String" in :
      InvitationStatus("Pending") shouldBe Pending
      InvitationStatus("Rejected") shouldBe Rejected
      InvitationStatus("Accepted") shouldBe Accepted
      InvitationStatus("Cancelled") shouldBe Cancelled
      InvitationStatus("Expired") shouldBe Expired
      InvitationStatus("Deauthorised") shouldBe DeAuthorised
      InvitationStatus("Partialauth") shouldBe PartialAuth

    "fail to instantiate when the provided value is not recognised" in :
      intercept[IllegalArgumentException](InvitationStatus("Destroyed"))

    "unapply to a String" in :
      InvitationStatus.unapply(Pending) shouldBe "Pending"
      InvitationStatus.unapply(Rejected) shouldBe "Rejected"
      InvitationStatus.unapply(Accepted) shouldBe "Accepted"
      InvitationStatus.unapply(Cancelled) shouldBe "Cancelled"
      InvitationStatus.unapply(Expired) shouldBe "Expired"
      InvitationStatus.unapply(DeAuthorised) shouldBe "Deauthorised"
      InvitationStatus.unapply(PartialAuth) shouldBe "Partialauth"

    "read from JSON when the status is recognised" in :
      JsString("Pending").as[InvitationStatus] shouldBe Pending
      JsString("Rejected").as[InvitationStatus] shouldBe Rejected
      JsString("Accepted").as[InvitationStatus] shouldBe Accepted
      JsString("Cancelled").as[InvitationStatus] shouldBe Cancelled
      JsString("Expired").as[InvitationStatus] shouldBe Expired
      JsString("Deauthorised").as[InvitationStatus] shouldBe DeAuthorised
      JsString("Partialauth").as[InvitationStatus] shouldBe PartialAuth

    "fail to read from JSON when the status is not recognised" in :
      intercept[IllegalArgumentException](JsString("Destroyed").as[InvitationStatus])

    "write to JSON" in :
      Json.toJson(InvitationStatus("Pending")) shouldBe JsString("Pending")
      Json.toJson(InvitationStatus("Rejected")) shouldBe JsString("Rejected")
      Json.toJson(InvitationStatus("Accepted")) shouldBe JsString("Accepted")
      Json.toJson(InvitationStatus("Cancelled")) shouldBe JsString("Cancelled")
      Json.toJson(InvitationStatus("Expired")) shouldBe JsString("Expired")
      Json.toJson(InvitationStatus("Deauthorised")) shouldBe JsString("Deauthorised")
      Json.toJson(InvitationStatus("Partialauth")) shouldBe JsString("Partialauth")
