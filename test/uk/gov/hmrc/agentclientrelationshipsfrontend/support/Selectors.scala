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

package uk.gov.hmrc.agentclientrelationshipsfrontend.support

trait Selectors {
  // Outside main-content
  val languageSwitcher = ".hmrc-language-select"
  val main = "#main-content"

  // Inside main-content
  val h1 = "h1"
  val h2 = "h2"
  val h3 = "h3"
  val p = "p"
  val label = "label"
  val visuallyHidden = ".govuk-visually-hidden"
  val link = ".govuk-link"
  val list = ".govuk-list"
  val inset = ".govuk-inset-text"
  val captionM = ".govuk-caption-m"
  val captionL = ".govuk-caption-l"
  val captionXL = ".govuk-caption-xl"
  val button = ".govuk-button"

  val errorSummary = ".govuk-error-summary"
  val errorSummaryList = ".govuk-error-summary__list"
  val fieldSet = ".govuk-fieldset"
  val fieldSetLegend = ".govuk-fieldset__legend"
  val hint = ".govuk-hint"
  val formGroup = ".govuk-form-group"
  val input = ".govuk-input"
  val select = ".govuk-select"

  val table = ".govuk-table"
  val tableCaption = ".govuk-table__caption"
  val tableHead = ".govuk-table__head"
  val tableBody = ".govuk-table__body"
  val tableRow = ".govuk-table__row"
  val tableRowHeader = ".govuk-table__header"
  val tableRowCell = ".govuk-table__cell"

  val summaryList = ".govuk-summary-list"
  val summaryListRow = ".govuk-summary-list__row"
  val summaryListRowKey = ".govuk-summary-list__key"
  val summaryListRowValue = ".govuk-summary-list__value"
  val summaryListRowAction = ".govuk-summary-list__action"

  val notificationBannerTitle = ".govuk-notification-banner__title"
  val notificationBannerContent = ".govuk-notification-banner__content"

  val detailsSummary = ".govuk-details__summary-text"
  val detailsContent = ".govuk-details__text"

  val warning = ".govuk-warning-text"
}
