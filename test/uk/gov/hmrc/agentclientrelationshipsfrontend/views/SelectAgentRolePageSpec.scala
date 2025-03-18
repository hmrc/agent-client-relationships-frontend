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

package uk.gov.hmrc.agentclientrelationshipsfrontend.views

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.{HMRCMTDIT, HMRCMTDITSUPP}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.common.ServiceData
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.journey.SelectFromOptionsForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.AgentJourneyType.AuthorisationRequest
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{AgentJourney, AgentJourneyRequest}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.AgentRoleChangeType.NewRelationship
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.journey.SelectAgentRolePage

class SelectAgentRolePageSpec extends ViewSpecSupport {

  val testUrl = "https://www.gov.uk"
  val options = Seq(HMRCMTDIT, HMRCMTDITSUPP)
  val clientName = "Micheal Jackson"
  

  val viewTemplate: SelectAgentRolePage = app.injector.instanceOf[SelectAgentRolePage]
  val form = SelectFromOptionsForm.form("AgentRole", options, "")
  
  object Expected {
    val heading = s"How do you want to act for $clientName?"
    val title = s"$heading - Ask a client to authorise you - GOV.UK"

    val link = "Find out how main and supporting agents can act (opens in new tab)"

    val radioLabel1 = "As their main agent"
    val radioLabel2 = "As their supporting agent"
    val hint1 = "Main agents can carry out all tax functions for the client and submit client’s tax returns"
    val hint2 = "Supporting agents can carry out some tax functions for the client, but they cannot submit a client’s tax returns"
  }

  "SelectAgentRolePage view" should {

  }
}
