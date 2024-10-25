/*
 * Copyright 2022 HM Revenue & Customs
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

package uk.gov.hmrc.agentclientrelationshipsfrontend.utils

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Writes
import play.api.libs.ws.DefaultBodyWritables.writeableOf_String
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import play.api.mvc.{AnyContent, Request}
import play.api.test.FakeRequest
import play.api.test.Helpers.*

import scala.concurrent.ExecutionContext
import uk.gov.hmrc.http.SessionKeys

trait ComponentSpecHelper
  extends AnyWordSpec
    with Matchers
    with CustomMatchers
    with WiremockHelper
    with BeforeAndAfterAll
    with BeforeAndAfterEach
    with GuiceOneServerPerSuite {

  def downstreamServices: Map[String, String] = Seq(
    "auth",
    "identity-verification-frontend"
  ).flatMap { service =>
    Seq(
      s"microservice.services.$service.host" -> mockHost,
      s"microservice.services.$service.port" -> mockPort
    )
  }.toMap

  def extraConfig(): Map[String, String] = Map.empty

  override lazy val app: Application = new GuiceApplicationBuilder()
    .configure(config ++ extraConfig() ++ downstreamServices)
    .configure("play.http.router" -> "testOnlyDoNotUseInAppConf.Routes")
    .build()

  val mockHost: String = WiremockHelper.wiremockHost
  val mockPort: String = WiremockHelper.wiremockPort.toString
  val mockUrl: String = s"http://$mockHost:$mockPort"

  def config: Map[String, String] = Map(
    "auditing.enabled"                                      -> "false",
    "play.filters.csrf.header.bypassHeaders.Csrf-Token"     -> "nocheck",
    "auditing.consumer.baseUri.host"                        -> mockHost,
    "auditing.consumer.baseUri.port"                        -> mockPort
  )

  implicit val ws: WSClient = app.injector.instanceOf[WSClient]
  implicit val executionContext: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  override def beforeAll(): Unit = {
    startWiremock()
    super.beforeAll()
  }

  override def afterAll(): Unit = {
    stopWiremock()
    super.afterAll()
  }

  override def beforeEach(): Unit = {
    resetWiremock()
    super.beforeEach()
  }

  def get[T](uri: String): WSResponse = {
    await(buildClient(uri).withHttpHeaders("Authorization" -> "Bearer 123").get())
  }

  def post[T](uri: String)(body: T)(implicit writes: Writes[T]): WSResponse = {
    await(
      buildClient(uri)
        .withHttpHeaders("Content-Type" -> "application/json", "Authorization" -> "Bearer 123")
        .post(writes.writes(body).toString())
    )
  }

  def put[T](uri: String)(body: T)(implicit writes: Writes[T]): WSResponse = {
    await(
      buildClient(uri)
        .withHttpHeaders("Content-Type" -> "application/json", "Authorization" -> "Bearer 123")
        .put(writes.writes(body).toString())
    )
  }

  def delete[T](uri: String): WSResponse = {
    await(buildClient(uri).withHttpHeaders("Authorization" -> "Bearer 123").delete())
  }

  val baseUrl: String = "/agent-client-relationships"

  private def buildClient(path: String, headers: Seq[(String, String)] = sessionHeaders): WSRequest =
    ws.url(s"http://localhost:$port$baseUrl${path.replace(baseUrl, "")}")
      .withFollowRedirects(false)
      .withHttpHeaders(sessionHeaders*)

  val sessionHeaders: Seq[(String, String)] = Seq(
    SessionKeys.authToken -> "testAuth",
    SessionKeys.sessionId -> "testSession"
  )

  implicit val request: Request[AnyContent] = FakeRequest().withSession(sessionHeaders*)

}
