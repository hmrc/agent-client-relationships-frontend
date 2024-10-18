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

package uk.gov.hmrc.agentclientrelationshipsfrontend.messages

import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

import scala.io.Source

class MessagesSpec extends PlaySpec with GuiceOneAppPerSuite with MockitoSugar {

  private def getMessageKeys(fileName: String) = {
    Source.fromResource(fileName)
      .getLines()
      .filter(!_.startsWith("#"))
      .filter(_.nonEmpty)
      .map(_.split('=').head)
      .map(_.trim)
      .toList
  }

  val english: List[String] = getMessageKeys("messages")
  val welsh: List[String] = getMessageKeys("messages.cy")

  "The English messages file" ignore {
    "have a corresponding Welsh translation" in {
      val missingKeys: List[String] = english.filterNot(welsh.contains)

      if (missingKeys.nonEmpty) fail(s"Missing keys in Welsh translation: ${missingKeys.mkString("\n", "\n", "\n")}")
    }

    "have no duplicates" in {
      val duplicateKeys: List[String] = english.diff(english.distinct).distinct

      if (duplicateKeys.nonEmpty) fail(s"Duplicate keys in English translation: ${duplicateKeys.mkString("\n", "\n", "\n")}")
    }
  }

  "The Welsh messages file" ignore {
    "have a corresponding English translation" in {
      val missingKeys: List[String] = welsh.filterNot(english.contains)

      if (missingKeys.nonEmpty) fail(s"Missing keys in English translation: ${missingKeys.mkString("\n", "\n", "\n")}")
    }


    "have no duplicates" in {
      val duplicateKeys: List[String] = welsh.diff(welsh.distinct).distinct

      if (duplicateKeys.nonEmpty) fail(s"Duplicate keys in Welsh translation: ${duplicateKeys.mkString("\n", "\n", "\n")}")
    }
  }

}