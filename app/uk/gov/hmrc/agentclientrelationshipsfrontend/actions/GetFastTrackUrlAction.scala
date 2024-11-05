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

package uk.gov.hmrc.agentclientrelationshipsfrontend.actions


import play.api.mvc.{ActionFunction, Result}
import uk.gov.hmrc.agentclientrelationshipsfrontend.connectors.SsoConnector
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.RequestSupport
import uk.gov.hmrc.http.{BadRequestException, HeaderCarrier}
import uk.gov.hmrc.play.bootstrap.binders.RedirectUrl.*
import uk.gov.hmrc.play.bootstrap.binders.{AbsoluteWithHostnameFromAllowlist, RedirectUrl, UnsafePermitAll}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.AgentFastTrackRequest

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

@Singleton
class GetFastTrackUrlAction @Inject(ssoConnector: SsoConnector, 
                                    requestSupport: RequestSupport)
                                   (implicit ec: ExecutionContext) {

  
  
  def getFastTrackUrlAction: ActionFunction[AgentRequest, AgentFastTrackRequest] = new ActionFunction[AgentRequest, AgentFastTrackRequest]:
    override protected def executionContext: ExecutionContext = ec

    override def invokeBlock[A](request: AgentRequest[A], block: AgentFastTrackRequest[A] => Future[Result]): Future[Result] = {
      
      for {
        redirectUrl <- getRedirectUrl(request.getQueryString("continue"))(requestSupport.headerCarrier(request))
        errorUrl <- getRedirectUrl(request.getQueryString("error"))(requestSupport.headerCarrier(request))
        refererUrl <- getRedirectUrl(request.headers.get("Referer"))(requestSupport.headerCarrier(request))
        result <- block(new AgentFastTrackRequest(request.arn, redirectUrl, errorUrl, refererUrl, request))
      } yield result
    }
  
  private def getRedirectUrl[A](urlOpt: Option[String])(implicit headerCarrier: HeaderCarrier): Future[Option[String]] =
    urlOpt match {
      case Some(redirectUrlString) =>
        Try(RedirectUrl(redirectUrlString)) match {
          case Success(redirectUrl) => 
            val unsafeUrl = redirectUrl.get(UnsafePermitAll).url
            if (RedirectUrl.isRelativeUrl(unsafeUrl)) Future.successful(Some(unsafeUrl))
            else redirectUrl.getEither( AbsoluteWithHostnameFromAllowlist(ssoConnector.getAllowlistedDomains())).map {
                case Right(safeRedirectUrl) => Some(safeRedirectUrl.url)
                case Left(errorMessage) => throw new BadRequestException(errorMessage)
              }
          case Failure(e) => throw new BadRequestException(s"[$redirectUrlString] is not a valid error URL, $e")
        }
      case None => Future.successful(None)
    }
  

}