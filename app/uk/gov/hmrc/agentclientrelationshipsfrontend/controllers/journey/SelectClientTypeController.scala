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

package uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.journey

import play.api.mvc._
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.ClientTypeFieldName
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.journey.SelectFromOptionsForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{ClientServiceConfigurationService, JourneyService}
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.journey.SelectClientTypePage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
@Singleton
class SelectClientTypeController @Inject()(mcc: MessagesControllerComponents,
                                           serviceConfig: ClientServiceConfigurationService,
                                           journeyService: JourneyService,
                                           selectClientTypePage: SelectClientTypePage
                                          )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc):
  
  def show(journeyType: String): Action[AnyContent] = Action.async:
    request =>
      given MessagesRequest[AnyContent] = request
      journeyService.getAnswerFromSession(ClientTypeFieldName).map { clientTypeValue =>
        Ok(selectClientTypePage(
          form = SelectFromOptionsForm.form(ClientTypeFieldName, serviceConfig.allClientTypes, journeyType).fill(clientTypeValue),
          clientTypes = serviceConfig.allClientTypes
        ))
      }
      

  def onSubmit(journeyType: String): Action[AnyContent] = Action.async:
    request =>
      given MessagesRequest[AnyContent] = request
      SelectFromOptionsForm.form(ClientTypeFieldName, serviceConfig.allClientTypes, journeyType).bindFromRequest().fold(
        formWithErrors => {
          Future.successful(Ok(selectClientTypePage(
            form = formWithErrors,
            clientTypes = serviceConfig.allClientTypes
          )))
        },
        clientType => {
          journeyService.saveAnswerInSession(ClientTypeFieldName, clientType).map { _ =>
              Ok("Redirect to next page")
            }

        }
  )
