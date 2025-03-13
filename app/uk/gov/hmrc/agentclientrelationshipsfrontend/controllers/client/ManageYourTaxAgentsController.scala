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
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.{AgentsAuthorisationsResponse, AuthorisationsCache, AuthorisedAgent}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.client.ConfirmDeauthForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{AgentClientRelationshipsService, AuthorisationsCacheService, ClientServiceConfigurationService}
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.PageNotFound
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.client.{ConfirmDeauthPage, ConfirmationOfDeauthPage, ManageYourTaxAgentsPage}
import uk.gov.hmrc.mongo.cache.DataKey
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ManageYourTaxAgentsController @Inject()(
                                               serviceConfigurationService: ClientServiceConfigurationService,
                                               agentClientRelationshipsService: AgentClientRelationshipsService,
                                               mytaPage: ManageYourTaxAgentsPage,
                                               confirmDeauthPage: ConfirmDeauthPage,
                                               deauthCompletePage: ConfirmationOfDeauthPage,
                                               pageNotFound: PageNotFound,
                                               authorisationsCacheService: AuthorisationsCacheService,
                                               actions: Actions,
                                               mcc: MessagesControllerComponents
                                             )(implicit val executionContext: ExecutionContext, appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport:

  def show: Action[AnyContent] = actions.clientAuthenticate.async:
    implicit request =>
      for {
        mytaData <- agentClientRelationshipsService.getManageYourTaxAgentsData()
        _ <- authorisationsCacheService
          .put[AuthorisationsCache](DataKey("authorisationsCache"), AuthorisationsCache(
            authorisations = Seq(mytaData.agentsAuthorisations.agentsAuthorisations).flatten.flatMap(_.authorisations))
          )
      } yield Ok(mytaPage(serviceConfigurationService.allSupportedServices.map(s => (s, serviceConfigurationService.getUrlPart(s))).toMap, mytaData))

  def showConfirmDeauth(id: String): Action[AnyContent] = actions.clientAuthenticate.async:
    implicit request =>
      authorisationsCacheService.getAuthorisation(id).map {
        case Some(authorisation) if authorisation.deauthorised.isEmpty => Ok(confirmDeauthPage(ConfirmDeauthForm.form, authorisation))
        case Some(_) => Redirect(routes.ManageYourTaxAgentsController.show.url)
        case None => NotFound(pageNotFound())
      }

  def submitDeauth(id: String): Action[AnyContent] = actions.clientAuthenticate.async:
    implicit request =>
      ConfirmDeauthForm.form.bindFromRequest().fold(
        formWithErrors => authorisationsCacheService.getAuthorisation(id).map {
          case Some(authorisation) if authorisation.deauthorised.isEmpty => BadRequest(confirmDeauthPage(formWithErrors, authorisation))
          case _ => NotFound(pageNotFound())
        },
        confirmDeauth =>
          authorisationsCacheService.getAuthorisation(id).flatMap {
            case Some(authorisation) if authorisation.deauthorised.isEmpty =>
              if confirmDeauth then
                agentClientRelationshipsService.cancelAuthorisation(authorisation.arn, authorisation.clientId, authorisation.service).flatMap {
                  case () =>
                    // destroy all other authorisation items from session cache and mark this one as deauthorised
                    for {
                      _ <- authorisationsCacheService.put[AuthorisationsCache](
                        DataKey("authorisationsCache"),
                        AuthorisationsCache(
                          authorisations = Seq(authorisation.copy(deauthorised = Some(true)))
                        )
                      )
                    } yield Redirect(routes.ManageYourTaxAgentsController.deauthComplete(id).url)

                  case _ => Future.successful(InternalServerError)
                }
              else Future.successful(Redirect(routes.ManageYourTaxAgentsController.show.url))
            case _ => Future.successful(Redirect(routes.ManageYourTaxAgentsController.show.url))
          }
      )

  def deauthComplete(id: String): Action[AnyContent] = actions.clientAuthenticate.async:
    implicit request =>
      authorisationsCacheService.getAuthorisation(id).flatMap {
        case Some(authorisation) if authorisation.deauthorised.contains(true) => Future.successful(Ok(deauthCompletePage(authorisation)))
        case _ => Future.successful(NotFound(pageNotFound()))
      }
