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

package uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.client

import com.google.inject.Inject
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.agentclientrelationshipsfrontend.actions.Actions
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.connectors.AgentClientRelationshipsConnector
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.AuthorisationsCache
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{AuthorisationsCacheService, ClientServiceConfigurationService}
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.client.ManageYourTaxAgentsPage
import uk.gov.hmrc.mongo.cache.DataKey
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Singleton
import scala.concurrent.ExecutionContext

@Singleton
class ManageYourTaxAgentsController @Inject()(agentClientRelationshipsConnector: AgentClientRelationshipsConnector,
                                              serviceConfigurationService: ClientServiceConfigurationService,
                                              mytaPage: ManageYourTaxAgentsPage,
                                              authorisationsCacheService: AuthorisationsCacheService,
                                              actions: Actions,
                                              mcc: MessagesControllerComponents
                                             )(implicit val executionContext: ExecutionContext, appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport:

  def show: Action[AnyContent] = actions.clientAuthenticate.async:
    implicit request =>
      for {
        mytaData <- agentClientRelationshipsConnector.getManageYourTaxAgentsData()
        _ <- authorisationsCacheService
          .put[AuthorisationsCache](DataKey("authorisationsCache"), AuthorisationsCache(
            authorisations = Seq(mytaData.agentsAuthorisations.agentsAuthorisations).flatten.flatMap(_.authorisations))
          )
      } yield Ok(mytaPage(serviceConfigurationService.allSupportedServices.map(s => (s, serviceConfigurationService.getUrlPart(s))).toMap, mytaData))


