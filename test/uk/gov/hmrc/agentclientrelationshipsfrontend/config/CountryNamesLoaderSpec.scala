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

package uk.gov.hmrc.agentclientrelationshipsfrontend.config

import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatestplus.mockito.MockitoSugar

import scala.collection.immutable.ListMap

class CountryNamesLoaderSpec extends AnyWordSpecLike with Matchers with MockitoSugar :

  implicit val mockAppConfig: AppConfig = mock[AppConfig]
  def countryNamesLoader = new CountryNamesLoader()

  ".load" should :

    "return a ListMap of countries when the CSV file is loaded and parsed correctly" in :
      when(mockAppConfig.isoCountryListLocation).thenReturn("/testCountryList.csv")
      countryNamesLoader.load(
        namesAsValues = false,
        location = mockAppConfig.isoCountryListLocation
      )
        .shouldBe(ListMap(
          "AF" -> "Afghanistan",
          "AL" -> "Albania"
        ))

    "return a ListMap of country names as values when the CSV file is loaded with namesAsValues being true" in :
      when(mockAppConfig.isoCountryListLocation).thenReturn("/testCountryList.csv")
      countryNamesLoader.load(
        namesAsValues = true,
        location = mockAppConfig.isoCountryListLocation
      )
        .shouldBe(ListMap(
        "Afghanistan" -> "Afghanistan",
        "Albania" -> "Albania"
      ))

    "return an exception" when :

      "the CSV file is loaded but no countries were parsed" in :
        when(mockAppConfig.isoCountryListLocation).thenReturn("/testNoCountries.csv")
        intercept[RuntimeException](countryNamesLoader.load(
          namesAsValues = false,
          location = mockAppConfig.isoCountryListLocation
        ))
          .getMessage
          .shouldBe("No country codes or names found")

      "the file path is empty" in :
        when(mockAppConfig.isoCountryListLocation).thenReturn("")
        intercept[RuntimeException](countryNamesLoader.load(
          namesAsValues = false,
          location = mockAppConfig.isoCountryListLocation
        ))
          .getMessage
          .shouldBe("requirement failed: The country list path should not be empty")

      "the target file is not a CSV type" in :
        when(mockAppConfig.isoCountryListLocation).thenReturn("/testInvalidFileType.txt")
        intercept[RuntimeException](countryNamesLoader.load(
          namesAsValues = false,
          location = mockAppConfig.isoCountryListLocation
        ))
          .getMessage
          .shouldBe("requirement failed: The country list file should be a csv file")
