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

package uk.gov.hmrc.agentclientrelationshipsfrontend.views

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.{Invitation, TrackRequestsResult}
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.TrackRequestsPage

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDate, ZoneOffset}
import java.util.Locale

class TrackRequestsPageSpec extends ViewSpecSupport {

  val viewTemplate: TrackRequestsPage = app.injector.instanceOf[TrackRequestsPage]
  val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM uuuu", Locale.UK)
  val expiryDate: LocalDate = LocalDate.now().plusDays(10)
  val lastUpdated: Instant = Instant.now()
  val clientName: String = "Any Client"
  def invitation(status: InvitationStatus, service: String): Invitation =
    Invitation("anyInvitationId", service, expiryDate, "Any Client", status, Instant.now())

  def results(requests: Seq[Invitation], filtersApplied: Option[Map[String, String]] = None): TrackRequestsResult = TrackRequestsResult(
    pageNumber = 1,
    requests = requests,
    clientNames = requests.map(_.clientName).distinct,
    availableFilters = requests.map(r => r.status.toString).distinct.toSeq,
    totalResults = requests.size,
    filtersApplied = filtersApplied
  )

  object Expected {
    val title = "Manage your recent authorisation requests - Ask a client to authorise you - GOV.UK"
    val heading = "Manage your recent authorisation requests"
    val resendLink = "Resend link"
    val cancelRequestLink = "Cancel request"
    val deAuthoriseLink = "Deauthorise"
    val startNewLink = "Start a new request"
    val signClientUpLink = "Sign your client up (opens in a new tab)"
  }

  def statusLabels(status: InvitationStatus): String = status match {
    case Pending => "Client has not yet responded"
    case Accepted => "Accepted by client"
    case Rejected => "Declined by client"
    case DeAuthorised => "Authorisation has been removed"
    case PartialAuth => "Accepted by client"
    case Expired => "Expired"
    case Cancelled => "Cancelled"
  }

  "TrackRequestsPage view" when {
    "viewed without filters and 2 Pending results" should {
      val requests = Seq(
        invitation(Pending, "HMRC-MTD-IT"),
        invitation(Pending, "HMRC-MTD-VAT")
      )
      val view: HtmlFormat.Appendable = viewTemplate(results(requests, None), 1)
      val doc: Document = Jsoup.parse(view.body)
      "have the right title" in {
        doc.title() shouldBe Expected.title
      }
      "have a language switcher" in {
        doc.hasLanguageSwitch shouldBe true
      }
      "have the right heading" in {
        doc.mainContent.extractText(h1, 1).value shouldBe Expected.heading
      }
      "have the right number of results" in {
        doc.mainContent.extractTable(1, 4).value shouldBe TestTable(
          "Showing 1 to 2 of 2 requests",
          List(
            IndexedSeq(
              clientName,
              "Manage their Making Tax Digital for Income Tax",
              s"Client has not yet responded Expires on ${expiryDate.format(dateFormatter)}",
              s"${Expected.resendLink} ${Expected.cancelRequestLink}"
            ),
            IndexedSeq(
              clientName,
              "Manage their VAT",
              s"Client has not yet responded Expires on ${expiryDate.format(dateFormatter)}",
              s"${Expected.resendLink} ${Expected.cancelRequestLink}"
            )
          )
        )
      }

    }
    "viewed with filters and a mixture of status including Accepted and PartialAuth" should {
      val view: HtmlFormat.Appendable = viewTemplate(results(Seq(
        invitation(Pending, "HMRC-MTD-IT"),
        invitation(Pending, "HMRC-MTD-VAT"),
        invitation(Accepted, "HMRC-TERS-ORG"),
        invitation(Rejected, "HMRC-CGT-PD"),
        invitation(DeAuthorised, "HMRC-MTD-VAT"),
        invitation(PartialAuth, "HMRC-MTD-IT")

      ), Some(Map("clientFilter" -> "Any Client"))), 1)
      val doc: Document = Jsoup.parse(view.body)
      "have the right title" in {
        doc.title() shouldBe Expected.title
      }
      "have a language switcher" in {
        doc.hasLanguageSwitch shouldBe true
      }
      "have the right heading" in {
        doc.mainContent.extractText(h1, 1).value shouldBe Expected.heading
      }
      "have the correct client names to filter by" in {
        val expectedElement = TestSelect(
          "clientFilter",
          Seq(("", ""), ("Any Client", "Any Client"))
        )
        doc.mainContent.extractSelectElement(1).value shouldBe expectedElement
      }
      "have the correct available status values" in {
        // we expect there NOT to be a user facing option for partial auth - instead
        // the status should be displayed as accepted but not in addition to the existing Accepted status
        // there should only be one of each status type
        val expectedElement = TestSelect(
          "statusFilter",
          Seq(
            ("", "All statuses"),
            ("Pending", statusLabels(Pending)),
            ("Accepted", statusLabels(Accepted)),
            ("Rejected", statusLabels(Rejected)),
            ("DeAuthorised", statusLabels(DeAuthorised))
          )
        )
        doc.mainContent.extractSelectElement(2).value shouldBe expectedElement
      }
      "have the right number of results with the correct action links" in {
        doc.mainContent.extractTable(1, 4).value shouldBe TestTable(
          "Showing 1 to 6 of 6 requests",
          List(
            IndexedSeq(
              clientName,
              "Manage their Making Tax Digital for Income Tax",
              s"Client has not yet responded Expires on ${expiryDate.format(dateFormatter)}",
              s"${Expected.resendLink} ${Expected.cancelRequestLink}"
            ),
            IndexedSeq(
              clientName,
              "Manage their VAT",
              s"Client has not yet responded Expires on ${expiryDate.format(dateFormatter)}",
              s"${Expected.resendLink} ${Expected.cancelRequestLink}"
            ),
            IndexedSeq(
              clientName,
              "Maintain a trust or an estate",
              s"Accepted by client ${lastUpdated.atZone(ZoneOffset.UTC).toLocalDateTime.format(dateFormatter)}",
              s"${Expected.deAuthoriseLink}"
            ),
            IndexedSeq(
              clientName,
              "Manage their Capital Gains Tax on UK property account",
              s"Declined by client ${lastUpdated.atZone(ZoneOffset.UTC).toLocalDateTime.format(dateFormatter)}",
              s"${Expected.startNewLink}"
            ),
            IndexedSeq(
              clientName,
              "Manage their VAT",
              s"Authorisation has been removed ${lastUpdated.atZone(ZoneOffset.UTC).toLocalDateTime.format(dateFormatter)}",
              s"${Expected.startNewLink}"
            ),
            IndexedSeq(
              clientName,
              "Manage their Making Tax Digital for Income Tax",
              s"Accepted by client ${lastUpdated.atZone(ZoneOffset.UTC).toLocalDateTime.format(dateFormatter)}",
              s"${Expected.signClientUpLink} ${Expected.deAuthoriseLink}"
            )
          )
        )
      }

    }
    "viewed with filters and the only status is PartialAuth" should {
      val view: HtmlFormat.Appendable = viewTemplate(results(Seq(
        invitation(PartialAuth, "HMRC-MTD-IT"),
        invitation(PartialAuth, "HMRC-MTD-IT")

      ), Some(Map("clientFilter" -> "Any Client"))), 1)
      val doc: Document = Jsoup.parse(view.body)
      "have the right title" in {
        doc.title() shouldBe Expected.title
      }
      "have a language switcher" in {
        doc.hasLanguageSwitch shouldBe true
      }
      "have the right heading" in {
        doc.mainContent.extractText(h1, 1).value shouldBe Expected.heading
      }
      "have the correct client names to filter by" in {
        val expectedElement = TestSelect(
          "clientFilter",
          Seq(("", ""), ("Any Client", "Any Client"))
        )
        doc.mainContent.extractSelectElement(1).value shouldBe expectedElement
      }
      "have the correct available status values" in {
        // we expect there NOT to be a user facing option for partial auth - instead
        // the status should be displayed as accepted
        val expectedElement = TestSelect(
          "statusFilter",
          Seq(
            ("", "All statuses"),
            ("Accepted", statusLabels(Accepted))
          )
        )
        doc.mainContent.extractSelectElement(2).value shouldBe expectedElement
      }
      "have the right number of results with the correct action links" in {
        doc.mainContent.extractTable(1, 4).value shouldBe TestTable(
          "Showing 1 to 2 of 2 requests",
          List(
            IndexedSeq(
              clientName,
              "Manage their Making Tax Digital for Income Tax",
              s"Accepted by client ${lastUpdated.atZone(ZoneOffset.UTC).toLocalDateTime.format(dateFormatter)}",
              s"${Expected.signClientUpLink} ${Expected.deAuthoriseLink}"
            ),
            IndexedSeq(
              clientName,
              "Manage their Making Tax Digital for Income Tax",
              s"Accepted by client ${lastUpdated.atZone(ZoneOffset.UTC).toLocalDateTime.format(dateFormatter)}",
              s"${Expected.signClientUpLink} ${Expected.deAuthoriseLink}"
            )
          )
        )
      }

    }
  }
}
