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

import org.mockito.Mockito.reset
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status.{FORBIDDEN, OK, SEE_OTHER}
import play.api.mvc.*
import play.api.test.FakeRequest
import play.api.test.Helpers.{defaultAwaitTimeout, redirectLocation, status}
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.client.{routes => clientRoutes}
import uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.routes
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.ClientExitType
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{ClientServiceConfigurationService, ServiceConstants}
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Individual, Organisation}
import uk.gov.hmrc.auth.core.ConfidenceLevel.{L200, L250, L50}
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AuthActionsSpec extends AnyWordSpecLike with Matchers with OptionValues with BeforeAndAfterEach with GuiceOneAppPerSuite with ServiceConstants {

  class FakeController(authAction: AuthActions,
                       actionBuilder: DefaultActionBuilder) {
    def agentAuth(): Action[AnyContent] = (actionBuilder andThen authAction.agentAuthAction) { _ => Results.Ok }

    def clientAuthWithEnrolmentCheck(taxService: String): Action[AnyContent] =
      (actionBuilder andThen authAction.clientAuthActionWithEnrolmentCheck(taxService)) { _ => Results.Ok }

    def clientAuth(): Action[AnyContent] = (actionBuilder andThen authAction.clientAuthAction) { _ => Results.Ok }
  }

  val mockAuthConnector: AuthConnector = mock[AuthConnector]
  val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
  val serviceConfig: ClientServiceConfigurationService = new ClientServiceConfigurationService
  val defaultActionBuilder: DefaultActionBuilder = app.injector.instanceOf[DefaultActionBuilder]

  given ExecutionContext = app.injector.instanceOf[ExecutionContext]

  override def afterEach(): Unit = {
    reset(mockAuthConnector)
    super.afterEach()
  }

  def failingControllerSetup(exception: Throwable): FakeController = {
    new FakeController(
      new AuthActions(FakeFailingAuthConnector(exception), appConfig, serviceConfig),
      defaultActionBuilder
    )
  }

  def agentControllerSetup(optArn: Option[String]): FakeController = {
    val enrolments = Enrolments(
      optArn.map(arn => Enrolment(
        "HMRC-AS-AGENT",
        Seq(EnrolmentIdentifier("AgentReferenceNumber", arn)),
        ""
      )).toSet
    )

    new FakeController(
      new AuthActions(FakePassingAuthConnector(Future.successful(enrolments)), appConfig, serviceConfig),
      defaultActionBuilder
    )
  }

  implicit class RetrievalCombiner[A](a: A) {
    def ~[B](b: B): A ~ B = new~(a, b)
  }

  def clientControllerSetup(affinityGroup: AffinityGroup, confidenceLevel: ConfidenceLevel, enrolments: Set[Enrolment]): FakeController = {
    val retrieval = Future.successful(Some(affinityGroup) ~ confidenceLevel ~ Enrolments(enrolments))

    new FakeController(
      new AuthActions(FakePassingAuthConnector(retrieval), appConfig, serviceConfig),
      defaultActionBuilder
    )
  }

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
  val testArn = "testArn"

  "agentAuthAction" should {
    "authorise an AS Agent with ARN" in {
      val controller = agentControllerSetup(Some(testArn))
      val result = controller.agentAuth()(fakeRequest)

      status(result) shouldBe OK
    }
    "block an AS Agent without ARN" in {
      val controller = agentControllerSetup(None)
      val result = controller.agentAuth()(fakeRequest)

      status(result) shouldBe FORBIDDEN
    }
    "redirect a user without session to the login" in {
      val controller = failingControllerSetup(BearerTokenExpired(""))
      val result = controller.agentAuth()(fakeRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result).value should startWith("http://localhost:9099/bas-gateway/sign-in")
    }
    "redirect an agent without the AS enrolment to the subscription url" in {
      val controller = failingControllerSetup(InsufficientEnrolments(""))
      val result = controller.agentAuth()(fakeRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result).value shouldBe "http://localhost:9437/agent-subscription/start"
    }
    "block an agent with the wrong auth provider" in {
      val controller = failingControllerSetup(UnsupportedAuthProvider(""))
      val result = controller.agentAuth()(fakeRequest)

      status(result) shouldBe FORBIDDEN
    }
  }

  "clientAuthActionWithEnrolmentCheck" should {
    s"authorise an individual client for $incomeTax with CL250" in {
      val controller = clientControllerSetup(Individual, L250, Set(Enrolment(HMRCMTDIT)))

      val result = controller.clientAuthWithEnrolmentCheck(incomeTax)(fakeRequest)

      status(result) shouldBe OK
    }
    s"redirect to client exit CannotFindAuthorisationRequest for $incomeTax" in {
      val controller = clientControllerSetup(Individual, L250, Set(Enrolment(HMRCMTDVAT)))

      val result = controller.clientAuthWithEnrolmentCheck(incomeTax)(fakeRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result).get shouldBe clientRoutes.ClientExitController.showUnauthorised(ClientExitType.CannotFindAuthorisationRequest).url
    }

    s"authorise an individual client for $incomeRecordViewer with CL250" in {
      val controller = clientControllerSetup(Individual, L250, Set(Enrolment(HMRCNI)))

      val result = controller.clientAuthWithEnrolmentCheck(incomeRecordViewer)(fakeRequest)

      status(result) shouldBe OK
    }
    s"redirect to client exit CannotFindAuthorisationRequest for $incomeRecordViewer" in {
      val controller = clientControllerSetup(Individual, L250, Set(Enrolment(HMRCMTDVAT)))

      val result = controller.clientAuthWithEnrolmentCheck(incomeRecordViewer)(fakeRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result).get shouldBe clientRoutes.ClientExitController.showUnauthorised(ClientExitType.CannotFindAuthorisationRequest).url
    }

    s"authorise an individual $capitalGainsTaxUkProperty client with CL50" in {
      val controller = clientControllerSetup(Individual, L50, Set(Enrolment(HMRCCGTPD)))
      val result = controller.clientAuthWithEnrolmentCheck(capitalGainsTaxUkProperty)(fakeRequest)

      status(result) shouldBe OK
    }
    "authorise an organisation client with CL50" in {
      val controller = clientControllerSetup(Organisation, L50, Set(Enrolment(HMRCMTDVAT)))
      val result = controller.clientAuthWithEnrolmentCheck(vat)(fakeRequest)

      status(result) shouldBe OK
    }
    "authorise an organisation ITSA client with CL250" in {
      val controller = clientControllerSetup(Organisation, L250, Set(Enrolment(HMRCMTDIT)))
      val result = controller.clientAuthWithEnrolmentCheck(incomeTax)(fakeRequest)

      status(result) shouldBe OK
    }
    "redirect an individual client with less than CL250 to IV" in {
      val controller = clientControllerSetup(Individual, L200, Set(Enrolment(HMRCMTDIT)))
      val result = controller.clientAuthWithEnrolmentCheck(incomeTax)(fakeRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result).value should startWith("http://localhost:9938/mdtp/uplift")
    }
    "redirect an organisation ITSA client with less than CL250 to IV" in {
      val controller = clientControllerSetup(Organisation, L200, Set(Enrolment(HMRCMTDIT)))
      val result = controller.clientAuthWithEnrolmentCheck(incomeTax)(fakeRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result).value should startWith("http://localhost:9938/mdtp/uplift")
    }
    "redirect an agent to Cannot view request page" in {
      val controller = clientControllerSetup(Agent, L50, Set(Enrolment("HMRC-AS-AGENT")))
      val result = controller.clientAuthWithEnrolmentCheck(incomeTax)(fakeRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result).value shouldBe routes.AuthorisationController.cannotViewRequest.url
    }
    "redirect a user without session to the login" in {
      val controller = failingControllerSetup(BearerTokenExpired(""))
      val result = controller.clientAuthWithEnrolmentCheck(incomeTax)(fakeRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result).value should startWith("http://localhost:9099/bas-gateway/sign-in")
    }
    "block a client without required enrolments (can't happen in theory)" in {
      val controller = failingControllerSetup(InsufficientEnrolments(""))
      val result = controller.clientAuthWithEnrolmentCheck(incomeTax)(fakeRequest)

      status(result) shouldBe FORBIDDEN
    }
    "block a client with the wrong auth provider" in {
      val controller = failingControllerSetup(UnsupportedAuthProvider(""))
      val result = controller.clientAuthWithEnrolmentCheck(incomeTax)(fakeRequest)

      status(result) shouldBe FORBIDDEN
    }
  }

  "clientAuthAction" should:

    s"authorise an individual client for $incomeTax with CL250" in:
      val controller = clientControllerSetup(Individual, L250, Set(Enrolment(HMRCMTDIT)))
      val result = controller.clientAuth()(fakeRequest)
      status(result) shouldBe OK

    s"authorise an individual client for $incomeRecordViewer with CL250" in:
      val controller = clientControllerSetup(Individual, L250, Set(Enrolment(HMRCNI)))
      val result = controller.clientAuth()(fakeRequest)
      status(result) shouldBe OK

    s"authorise an individual $capitalGainsTaxUkProperty client with CL50" in:
      val controller = clientControllerSetup(Individual, L50, Set(Enrolment(HMRCCGTPD)))
      val result = controller.clientAuth()(fakeRequest)
      status(result) shouldBe OK

    "authorise an organisation client with CL50" in:
      val controller = clientControllerSetup(Organisation, L50, Set(Enrolment(HMRCMTDVAT)))
      val result = controller.clientAuth()(fakeRequest)
      status(result) shouldBe OK

    "authorise an organisation ITSA client with CL250" in:
      val controller = clientControllerSetup(Organisation, L250, Set(Enrolment(HMRCMTDIT)))
      val result = controller.clientAuth()(fakeRequest)
      status(result) shouldBe OK

    "redirect an individual client with less than CL250 to IV" in:
      val controller = clientControllerSetup(Individual, L200, Set(Enrolment(HMRCMTDIT)))
      val result = controller.clientAuth()(fakeRequest)
      status(result) shouldBe SEE_OTHER
      redirectLocation(result).value should startWith("http://localhost:9938/mdtp/uplift")

    "redirect an organisation ITSA client with less than CL250 to IV" in:
      val controller = clientControllerSetup(Organisation, L200, Set(Enrolment(HMRCMTDIT)))
      val result = controller.clientAuth()(fakeRequest)
      status(result) shouldBe SEE_OTHER
      redirectLocation(result).value should startWith("http://localhost:9938/mdtp/uplift")

    "redirect an agent to Cannot view request page" in:
      val controller = clientControllerSetup(Agent, L50, Set(Enrolment("HMRC-AS-AGENT")))
      val result = controller.clientAuth()(fakeRequest)
      status(result) shouldBe SEE_OTHER
      redirectLocation(result).value shouldBe routes.AuthorisationController.cannotViewRequest.url

    "redirect a user without session to the login" in:
      val controller = failingControllerSetup(BearerTokenExpired(""))
      val result = controller.clientAuth()(fakeRequest)
      status(result) shouldBe SEE_OTHER
      redirectLocation(result).value should startWith("http://localhost:9099/bas-gateway/sign-in")

    "block a client without required enrolments (can't happen in theory)" in:
      val controller = failingControllerSetup(InsufficientEnrolments(""))
      val result = controller.clientAuth()(fakeRequest)
      status(result) shouldBe FORBIDDEN

    "block a client with the wrong auth provider" in:
      val controller = failingControllerSetup(UnsupportedAuthProvider(""))
      val result = controller.clientAuth()(fakeRequest)
      status(result) shouldBe FORBIDDEN
}

class FakeFailingAuthConnector @Inject()(exception: Throwable) extends AuthConnector {
  val serviceUrl: String = ""

  override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] =
    Future.failed(exception)
}

class FakePassingAuthConnector @Inject()(retrievals: Future[?]) extends AuthConnector {
  val serviceUrl: String = ""

  override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] =
    retrievals.map(_.asInstanceOf[A])
}