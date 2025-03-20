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

package uk.gov.hmrc.agentclientrelationshipsfrontend.modules

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.Configuration
import uk.gov.hmrc.crypto.{Crypted, PlainBytes, PlainText}

import java.nio.charset.StandardCharsets
import java.util.Base64

class CryptoProviderModuleSpec extends AnyWordSpecLike with Matchers :

  val cryptoProviderModule = new CryptoProviderModule

  "CryptoProviderModule.aesGcmCryptoInstance" should :

    "return a real crypto instance when the 'mongoEncryption' config flag is enabled" in :
      val config = Configuration("mongoEncryption.key" -> "123", "mongoEncryption.enable" -> true)
      cryptoProviderModule.aesGcmCryptoInstance(config) should not be a[NoCrypto.type]

    "return a NoCrypto instance when the 'mongoEncryption' config flag is disabled" in :
      val config = Configuration("mongoEncryption.enable" -> false)
      cryptoProviderModule.aesGcmCryptoInstance(config) shouldBe a[NoCrypto.type]

  "NoCrypto" should :

    val text = "Not a secret"
    val bytes: Array[Byte] = Array(0x13, 0x37)
    val base64Bytes = new String(Base64.getEncoder.encode(bytes), StandardCharsets.UTF_8)

    "apply no encryption to plain text content" in :
      NoCrypto.encrypt(PlainText(text)).value shouldBe text

    "apply no encryption to plain text bytes (base64 encoded)" in :
      NoCrypto.encrypt(PlainBytes(bytes)).value shouldBe base64Bytes

    "return the plain text from a provided Crypted instance" in :
      NoCrypto.decrypt(Crypted(text)).value shouldBe text

    "return the plain bytes from a provided Crypted instance" in :
      NoCrypto.decryptAsBytes(Crypted(base64Bytes)).value shouldBe bytes