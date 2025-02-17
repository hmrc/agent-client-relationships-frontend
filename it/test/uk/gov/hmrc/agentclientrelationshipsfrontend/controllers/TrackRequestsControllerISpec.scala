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

package uk.gov.hmrc.agentclientrelationshipsfrontend.controllers

import play.api.http.Status.OK
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.Invitation
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.{InvitationStatus, Pending}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.TrackRequestsService
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.WiremockHelper.stubGet
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AuthStubs, ComponentSpecHelper}

import java.time.{Instant, LocalDate}

class TrackRequestsControllerISpec extends ComponentSpecHelper with AuthStubs {
  val arn = "ARN123456"
  val expiryDate: LocalDate = LocalDate.now().plusDays(10)
  val lastUpdated: Instant = Instant.now()
  def trackRequestsUrl(pageNumber: Int, statusFilter: Option[String], clientName: Option[String]): String =
    s"/agent-client-relationships/agent/$arn/authorisation-requests\\?pageNumber=$pageNumber&pageSize=10" +
      statusFilter.fold("")(status => s"&statusFilter=$status") +
      clientName.fold("")(client => s"&clientName=$client")
  def invitation(status: InvitationStatus, service: String): Invitation =
    Invitation("anyInvitationId", service, expiryDate, "Any Client", status, Instant.now())
  def testTrackRequestsResponseJson(): JsObject = Json.obj(
    "pageNumber" -> 1,
    "requests" -> Json.arr(Json.toJson(invitation(Pending, "HMRC-MTD-IT"))),
    "clientNames" -> Json.arr("Any Client"),
    "availableFilters" -> Json.arr("Pending"),
    "filtersApplied" -> Json.obj(),
    "totalResults" -> 1
  )

  val trackRequestsService: TrackRequestsService = app.injector.instanceOf[TrackRequestsService]

  "GET /track-requests" should {
    "return the track requests page" in {
      authoriseAsAgent()
      stubGet(trackRequestsUrl(1, None, None), OK, testTrackRequestsResponseJson().toString)
      val result = get(routes.TrackRequestsController.show(1).url)
      result.status shouldBe OK
    }
  }

  "POST /track-requests" should {
    "return the track requests page when submitting filters" in {
      authoriseAsAgent()
      stubGet(trackRequestsUrl(1, Some("Pending"), None), OK, testTrackRequestsResponseJson().toString)
      val result = post(routes.TrackRequestsController.submitFilters.url)(Map("statusFilter" -> Seq("Pending")))
      result.status shouldBe OK
    }
  }

}
