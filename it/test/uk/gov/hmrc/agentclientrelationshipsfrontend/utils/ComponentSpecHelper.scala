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
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, OptionValues}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Writes
import play.api.libs.ws.DefaultBodyWritables.{writeableOf_String, writeableOf_urlEncodedForm}
import play.api.libs.ws.{DefaultWSCookie, WSClient, WSCookie, WSRequest, WSResponse}
import play.api.mvc.{AnyContentAsFormUrlEncoded, Request, Session, SessionCookieBaker}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.crypto.PlainText
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.play.bootstrap.frontend.filters.crypto.SessionCookieCrypto

import scala.concurrent.ExecutionContext

trait ComponentSpecHelper
  extends AnyWordSpec
    with Matchers
    with CustomMatchers
    with WiremockHelper
    with BeforeAndAfterAll
    with BeforeAndAfterEach
    with GuiceOneServerPerSuite
    with OptionValues {

  // Add all services required by our (or bootstrap's) connectors here
  def downstreamServices: Map[String, String] = Seq(
    "auth",
    "identity-verification-frontend",
    "agent-client-relationships"
  ).flatMap { service =>
    Seq(
      s"microservice.services.$service.host" -> mockHost,
      s"microservice.services.$service.port" -> mockPort
    )
  }.toMap

  def extraConfig(): Map[String, String] = Map.empty

  override lazy val app: Application = new GuiceApplicationBuilder()
    .configure(config ++ extraConfig() ++ downstreamServices)
    .build()

  val mockHost: String = WiremockHelper.wiremockHost
  val mockPort: String = WiremockHelper.wiremockPort.toString
  val mockUrl: String = s"http://$mockHost:$mockPort"

  def config: Map[String, String] = Map(
    "auditing.enabled" -> "false",
    "play.filters.csrf.header.bypassHeaders.Csrf-Token" -> "nocheck",
    "auditing.consumer.baseUri.host" -> mockHost,
    "auditing.consumer.baseUri.port" -> mockPort,
    "play.http.router" -> "testOnlyDoNotUseInAppConf.Routes",
    "mongoEncryption.enabled" -> "true"
  )

  implicit val ws: WSClient = app.injector.instanceOf[WSClient]
  implicit val executionContext: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  val appConfig: AppConfig = app.injector.instanceOf[AppConfig]

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

  def get[T](uri: String): WSResponse =
    await(buildClient(uri).withHttpHeaders("Authorization" -> "Bearer 123").get())

  def post(uri: String)(body: Map[String, Seq[String]])(queryParam: Seq[(String, String)]): WSResponse =
    await(
      buildClient(uri)
        .withHttpHeaders("Authorization" -> "Bearer 123", "Csrf-Token" -> "nocheck")
        .withQueryStringParameters(queryParam*)
        .post(body)
    )

  def post(uri: String)(body: Map[String, Seq[String]]): WSResponse =
    await(
      buildClient(uri)
        .withHttpHeaders("Authorization" -> "Bearer 123", "Csrf-Token" -> "nocheck")
        .post(body)
    )

  def post[T](uri: String)(body: T)(implicit writes: Writes[T]): WSResponse =
    await(
      buildClient(uri)
        .withHttpHeaders("Authorization" -> "Bearer 123", "Csrf-Token" -> "nocheck")
        .post(writes.writes(body).toString())
    )

  def put[T](uri: String)(body: T)(implicit writes: Writes[T]): WSResponse =
    await(
      buildClient(uri)
        .withHttpHeaders("Authorization" -> "Bearer 123", "Csrf-Token" -> "nocheck")
        .put(writes.writes(body).toString())
    )

  def delete[T](uri: String): WSResponse =
    await(buildClient(uri).withHttpHeaders("Authorization" -> "Bearer 123").delete())

  val baseUrl: String = "/agent-client-relationships"

  private def buildClient(path: String): WSRequest =
    ws.url(s"http://localhost:$port$baseUrl${path.replace(baseUrl, "")}")
      .withFollowRedirects(false)
      .withCookies(
        DefaultWSCookie("PLAY_LANG", "en"),
        mockSessionCookie
      )

  val sessionHeaders: Map[String, String] = Map(
    SessionKeys.lastRequestTimestamp -> System.currentTimeMillis().toString,
    SessionKeys.authToken -> "mock-bearer-token",
    SessionKeys.sessionId -> "mock-sessionid"
  )

  implicit val request: Request[AnyContentAsFormUrlEncoded] = FakeRequest().withSession(sessionHeaders.toSeq *).withFormUrlEncodedBody()

  def mockSessionCookie: WSCookie = {

    val cookieCrypto = app.injector.instanceOf[SessionCookieCrypto]
    val cookieBaker = app.injector.instanceOf[SessionCookieBaker]
    val sessionCookie = cookieBaker.encodeAsCookie(Session(sessionHeaders))
    val encryptedValue = cookieCrypto.crypto.encrypt(PlainText(sessionCookie.value))
    val cookie = sessionCookie.copy(value = encryptedValue.value)

    new WSCookie() {
      override def name: String = cookie.name
      override def value: String = cookie.value
      override def domain: Option[String] = cookie.domain
      override def path: Option[String] = Some(cookie.path)
      override def maxAge: Option[Long] = cookie.maxAge.map(_.toLong)
      override def secure: Boolean = cookie.secure
      override def httpOnly: Boolean = cookie.httpOnly
    }
  }
}
