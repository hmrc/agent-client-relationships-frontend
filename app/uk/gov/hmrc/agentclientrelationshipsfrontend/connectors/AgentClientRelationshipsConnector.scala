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

package uk.gov.hmrc.agentclientrelationshipsfrontend.connectors

import play.api.http.Status.*
import play.api.libs.json.Json
import play.api.libs.ws.JsonBodyWritables.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.actions.AgentRequest
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.ManageYourTaxAgentsData
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.invitationLink.{ValidateLinkPartsError, ValidateLinkPartsResponse}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{AgentJourney, AgentJourneyRequest}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.ClientServiceConfigurationService
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import views.html.helper.urlEncode

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AgentClientRelationshipsConnector @Inject()(appConfig: AppConfig,
                                                  serviceConfig: ClientServiceConfigurationService,
                                                  httpV2: HttpClientV2
                                                 )(implicit executionContext: ExecutionContext):

  private val agentClientRelationshipsUrl = s"${appConfig.agentClientRelationshipsBaseUrl}/agent-client-relationships"

  def getClientDetails(service: String, clientId: String)(implicit hc: HeaderCarrier): Future[Option[ClientDetailsResponse]] = httpV2
    .get(url"$agentClientRelationshipsUrl/client/${serviceConfig.getServiceForFastTrack(service)}/details/$clientId")
    .execute[Option[ClientDetailsResponse]]

  def createAuthorisationRequest(journey: AgentJourney)(implicit hc: HeaderCarrier, request: AgentJourneyRequest[?]): Future[String] = {
    val clientIdType = serviceConfig.firstClientDetailsFieldFor(journey.getService).clientIdType
    httpV2
      .post(url"$agentClientRelationshipsUrl/agent/${request.arn}/authorisation-request")
      .withBody(Json.toJson(CreateAuthorisationRequest(journey.clientId.get, clientIdType, journey.getClientDetailsResponse.name, journey.agentType.getOrElse(journey.getService), journey.getClientType)))
      .execute[HttpResponse].map { response =>
        response.status match {
          case CREATED => (response.json \ "invitationId").as[String]
          case _ => throw new RuntimeException(s"Failed to create authorisation request: ${response.body}")
        }
      }
  }

  def removeAuthorisation(journey: AgentJourney)(implicit hc: HeaderCarrier, request: AgentJourneyRequest[?]): Future[Unit] = httpV2
    .post(url"$agentClientRelationshipsUrl/agent/${request.arn}/remove-authorisation")
    .withBody(Json.obj("clientId" -> journey.getClientId, "service" -> journey.getActiveRelationship))
    .execute[HttpResponse].map { response =>
      response.status match {
        case NO_CONTENT => ()
        case _ => throw new RuntimeException(s"Failed to cancel authorisation on behalf of agent: ${response.body}")
      }
    }

  def clientCancelAuthorisation(clientId: String, service: String, arn: String)(implicit hc: HeaderCarrier): Future[Unit] = httpV2
    .post(url"$agentClientRelationshipsUrl/agent/$arn/remove-authorisation")
    .withBody(Json.obj("clientId" -> clientId, "service" -> service))
    .execute[HttpResponse].map { response =>
      response.status match {
        case NO_CONTENT => Future.successful(())
        case _ => throw new RuntimeException(s"Failed to cancel authorisation on behalf of client: ${response.body}")
      }
    }

  def getAuthorisationRequest(invitationId: String)(implicit headerCarrier: HeaderCarrier, request: AgentRequest[?]): Future[Option[AuthorisationRequestInfo]] = {
    httpV2.get(url"$agentClientRelationshipsUrl/agent/${request.arn}/authorisation-request-info/$invitationId")
      .execute[Option[AuthorisationRequestInfo]]
  }

  def getAuthorisationRequestForClient(invitationId: String)(implicit headerCarrier: HeaderCarrier): Future[Option[AuthorisationRequestInfoForClient]] = {
    httpV2.get(url"$agentClientRelationshipsUrl/client/authorisation-request-info/$invitationId")
      .execute[Option[AuthorisationRequestInfoForClient]]
  }

  def getAgentDetails()(implicit hc: HeaderCarrier, request: AgentJourneyRequest[?]): Future[Option[AgentDetails]] = httpV2
    .get(url"$agentClientRelationshipsUrl/agent/${request.arn}/details")
    .execute[Option[AgentDetails]]

  def trackRequests(
                     arn: String,
                     pageNumber: Int,
                     statusFilter: Option[String],
                     clientName: Option[String]
                   )(implicit hc: HeaderCarrier): Future[TrackRequestsResult] = {
    val queryParams: List[(String, String)] =
        List("pageNumber" -> s"$pageNumber") ++
        List("pageSize" -> s"${appConfig.trackRequestsPageSize}") ++
        statusFilter.fold[List[(String, String)]](List.empty)(statusFilter => List("statusFilter" -> statusFilter)) ++
        clientName.fold[List[(String, String)]](List.empty)(clientName => List("clientName" -> urlEncode(clientName)))
    httpV2
      .get(url"$agentClientRelationshipsUrl/agent/$arn/authorisation-requests?$queryParams")
      .execute[HttpResponse]
      .map(response => response.status match {
        case OK => response.json.as[TrackRequestsResult]
        case status => throw new Exception(s"Unexpected status $status received when calling track requests")
      })
  }

  def validateLinkParts(uid: String, normalizedAgentName: String)(implicit hc: HeaderCarrier): Future[Either[ValidateLinkPartsError, ValidateLinkPartsResponse]] = {
    import uk.gov.hmrc.agentclientrelationshipsfrontend.models.invitationLink.{AgentNotFoundError, AgentSuspendedError}
    httpV2
      .get(url"$agentClientRelationshipsUrl/agent/agent-reference/uid/$uid/$normalizedAgentName")
      .execute[HttpResponse]
      .map(response => response.status match {
        case OK => Right(response.json.as[ValidateLinkPartsResponse])
        case NOT_FOUND => Left(AgentNotFoundError)
        case FORBIDDEN => Left(AgentSuspendedError)
        case status => throw new Exception(s"Unexpected status $status received when validating link")
      })
  }

  def acceptAuthorisation(invitationId: String)(implicit hc: HeaderCarrier): Future[Unit] =
    val url = s"$agentClientRelationshipsUrl/authorisation-response/accept/$invitationId"
    httpV2.put(url"$url").execute[HttpResponse].map(response => response.status match {
      case NO_CONTENT => ()
      case status => throw new Exception(s"Unexpected status $status received when accepting invitation")
    })

  def rejectAuthorisation(invitationId: String)(implicit hc: HeaderCarrier): Future[Unit] =
    val url = s"$agentClientRelationshipsUrl/client/authorisation-response/reject/$invitationId"
    httpV2.put(url"$url").execute[HttpResponse].map(response => response.status match {
      case NO_CONTENT => ()
      case status => throw new Exception(s"Unexpected status $status received when rejecting invitation")
    })

  def cancelInvitation(invitationId: String)(implicit hc: HeaderCarrier): Future[Unit] =
    val url = s"$agentClientRelationshipsUrl/agent/cancel-invitation/$invitationId"
    httpV2.put(url"$url").execute[HttpResponse].map(response => response.status match {
      case NO_CONTENT => Future.successful(())
      case status => throw new Exception(s"Unexpected status $status received when cancelling invitation")
    })

  def validateInvitation(uid: String, serviceKeys: Set[String])(implicit hc: HeaderCarrier): Future[Either[ValidateInvitationError, ValidateInvitationResponse]] = {
    httpV2
      .post(url"$agentClientRelationshipsUrl/client/validate-invitation")
      .withBody(Json.obj(
        "uid" -> uid,
        "serviceKeys" -> serviceKeys
      ))
      .execute[HttpResponse]
      .map(response => response.status match {
        case OK => Right(response.json.as[ValidateInvitationResponse])
        case FORBIDDEN => Left(InvitationAgentSuspendedError)
        case NOT_FOUND => Left(InvitationOrAgentNotFoundError)
        case status => throw new Exception(s"Unexpected status $status received when fetching invitation")
      })
  }

  def getManageYourTaxAgentsData()(implicit hc: HeaderCarrier): Future[ManageYourTaxAgentsData] =
    httpV2
      .get(url"$agentClientRelationshipsUrl/client/authorisations-relationships")
      .execute[ManageYourTaxAgentsData]
