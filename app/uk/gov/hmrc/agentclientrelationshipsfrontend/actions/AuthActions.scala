/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.agentclientrelationshipsfrontend.actions

import play.api.Logging
import play.api.mvc.Results.*
import play.api.mvc.{ActionFunction, Request, Result, WrappedRequest}
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.{AsAgent, CgtPd, MtdIncomeTax}
import uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.routes
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.AuthorisedClient
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.UrlHelper
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.auth.core.AuthProvider.GovernmentGateway
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals.*
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.binders.RedirectUrl
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

class AgentRequest[A](val arn: String,
                      val request: Request[A])
  extends WrappedRequest[A](request)

class ClientRequest[A](val client: AuthorisedClient,
                       val request: Request[A])
  extends WrappedRequest[A](request)

@Singleton
class AuthActions @Inject()(val authConnector: AuthConnector,
                            appConfig: AppConfig)
                           (implicit ec: ExecutionContext)
  extends AuthorisedFunctions with Logging {

  private val requiredCL = ConfidenceLevel.L250

  def agentAuthAction: ActionFunction[Request, AgentRequest] = new ActionFunction[Request, AgentRequest]:
    val executionContext: ExecutionContext = ec

    override def invokeBlock[A](request: Request[A], block: AgentRequest[A] => Future[Result]): Future[Result] =
      given Request[A] = request

      given HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

      authorised(Enrolment(AsAgent) and AuthProviders(GovernmentGateway))
        .retrieve(authorisedEnrolments) { enrolments =>
          getArn(enrolments) match {
            case Some(arn) =>
              block(AgentRequest(arn, request))
            case None =>
              logger.warn("Arn not found for the logged in agent")
              Future.successful(Forbidden)
          }
        }
        .recover {
          handleFailure(isAgent = true)
        }

  def clientAuthAction: ActionFunction[Request, ClientRequest] = new ActionFunction[Request, ClientRequest]:
    val executionContext: ExecutionContext = ec

    override def invokeBlock[A](request: Request[A], block: ClientRequest[A] => Future[Result]): Future[Result] =
      given Request[A] = request

      given HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

      authorised(AuthProviders(GovernmentGateway))
        .retrieve(affinityGroup and confidenceLevel and allEnrolments) {
          case Some(affinity) ~ confidence ~ enrols =>
            (affinity, confidence) match {
              case (AffinityGroup.Individual, cl) =>
                withConfidenceLevelUplift(cl, enrols) {
                  block(ClientRequest(AuthorisedClient(affinity, enrols), request))
                }
              case (AffinityGroup.Organisation, cl) =>
                if enrols.enrolments.map(_.key).contains(MtdIncomeTax) then withConfidenceLevelUplift(cl, enrols) {
                  block(ClientRequest(AuthorisedClient(affinity, enrols), request))
                }
                else block(ClientRequest(AuthorisedClient(affinity, enrols), request))
              case (AffinityGroup.Agent, _) =>
                Future.successful(Redirect(routes.AuthorisationController.cannotViewRequest))
              case (affinityGroup, _) =>
                logger.warn(s"unknown affinity group: $affinityGroup - cannot determine auth status")
                Future.successful(Forbidden)
            }
          case _ =>
            logger.warn("the logged in client had no affinity group")
            Future.successful(Forbidden)
        }
        .recover {
          handleFailure(isAgent = false)
        }

  private def withConfidenceLevelUplift(currentLevel: ConfidenceLevel, enrols: Enrolments)
                                       (body: => Future[Result])
                                       (implicit request: Request[?]): Future[Result] = {
    // APB-4856: Clients with only CGT enrol dont need to go through IV
    val isCgtOnlyClient: Boolean = {
      enrols.enrolments.map(_.key).intersect(appConfig.supportedServices) == Set(CgtPd)
    }

    if currentLevel >= requiredCL || isCgtOnlyClient then {
      body
    } else {
      redirectToIdentityVerification()
    }
  }

  private def successUrl(implicit request: Request[?]) =
    appConfig.appExternalUrl + request.uri

  private def redirectToIdentityVerification()(implicit request: Request[?]) = {
    val failureUrl = appConfig.appExternalUrl +
      routes.AuthorisationController.cannotConfirmIdentity(continueUrl = Some(RedirectUrl(successUrl))).url

    Future.successful(Redirect(UrlHelper.addParamsToUrl(
      appConfig.ivUpliftUrl,
      "origin" -> Some(appConfig.appName),
      "confidenceLevel" -> Some(requiredCL.toString),
      "completionURL" -> Some(successUrl),
      "failureURL" -> Some(failureUrl)
    )))
  }

  private def getArn(enrolments: Enrolments) =
    for
      enrolment <- enrolments.getEnrolment(AsAgent)
      identifier <- enrolment.getIdentifier("AgentReferenceNumber")
    yield identifier.value

  private def handleFailure(isAgent: Boolean)
                           (implicit request: Request[?]): PartialFunction[Throwable, Result] = {
    case _: NoActiveSession =>
      Redirect(UrlHelper.addParamsToUrl(
        appConfig.signInUrl,
        "origin" -> Some(appConfig.appName),
        "continue_url" -> Some(successUrl)
      ))
    case _: InsufficientEnrolments =>
      logger.warn(s"Logged in user does not have required enrolments")
      if isAgent then Redirect(appConfig.subscriptionUrl) else Forbidden
    case _: UnsupportedAuthProvider =>
      logger.warn(s"user logged in with unsupported auth provider")
      Forbidden
  }
}
