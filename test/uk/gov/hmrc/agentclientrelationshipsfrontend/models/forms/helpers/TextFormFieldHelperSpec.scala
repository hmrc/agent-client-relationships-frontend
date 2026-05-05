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

package uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.helpers

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.data.Forms.single
import play.api.data.{Form, FormError, Mapping}

import scala.collection.immutable.ArraySeq

class TextFormFieldHelperSpec extends AnyWordSpec with Matchers :

  val fieldName = "fieldName"
  val messageKey = "messageKey"
  val fieldRegex_1 = "[A-Z]{3}"
  val fieldRegex_2 = "[a-z]{3}"
  val clientIdFormMapping: Mapping[String] = TextFormFieldHelper.clientIdTextFieldMapping(fieldName, messageKey, fieldRegex_1)
  val clientIdForm: Form[String] = Form(single(fieldName -> clientIdFormMapping))
  val emailFormMapping: Mapping[String] = TextFormFieldHelper.emailTextFieldMapping(fieldName, messageKey, fieldRegex_2)
  val emailForm: Form[String] = Form(single(fieldName -> emailFormMapping))
  val postcodeFormMapping: Mapping[String] = TextFormFieldHelper.postcodeTextFieldMapping(fieldName, messageKey, fieldRegex_1)
  val postcodeForm: Form[String] = Form(single(fieldName -> postcodeFormMapping))

  "The text form field helper" should :

    "validate a text field when the Client ID value adheres to the regex" in :
      val result = clientIdForm.bind(Map(fieldName -> "ABC"))
      result.hasErrors shouldBe false

    "invalidate a text field when the Client ID value does not adhere to the regex" in :
      val result = clientIdForm.bind(Map(fieldName -> "ABC23495302965"))
      result.hasErrors shouldBe true
      result.errors.head shouldBe FormError("fieldName", List("messageKey.error.invalid"), ArraySeq(("inputFieldClass", "fieldName")))

    "invalidate a text field when the Client ID value is empty" in :
      val result = clientIdForm.bind(Map(fieldName -> ""))
      result.hasErrors shouldBe true
      result.errors.head shouldBe FormError("fieldName", List("messageKey.error.required"), ArraySeq(("inputFieldClass", "fieldName")))

    "validate a text field when the Email value adheres to the regex" in :
      val result = emailForm.bind(Map(fieldName -> "ABC"))
      result.hasErrors shouldBe false

    "invalidate a text field when the Email value does not adhere to the regex" in :
      val result = emailForm.bind(Map(fieldName -> "ABC23495302965"))
      result.hasErrors shouldBe true
      result.errors.head shouldBe FormError("fieldName", List("messageKey.error.invalid"), ArraySeq(("inputFieldClass", "fieldName")))

    "invalidate a text field when the Email value is empty" in :
      val result = emailForm.bind(Map(fieldName -> ""))
      result.hasErrors shouldBe true
      result.errors.head shouldBe FormError("fieldName", List("messageKey.error.required"), ArraySeq(("inputFieldClass", "fieldName")))

    "validate a text field when the Postcode value adheres to the regex" in :
      val result = postcodeForm.bind(Map(fieldName -> "ABC"))
      result.hasErrors shouldBe false

    "invalidate a text field when the Postcode value does not adhere to the regex" in :
      val result = postcodeForm.bind(Map(fieldName -> "ABC23495302965"))
      result.hasErrors shouldBe true
      result.errors.head shouldBe FormError("fieldName", List("messageKey.error.invalid"), ArraySeq(("inputFieldClass", "fieldName")))

    "invalidate a text field when the Postcode value is empty" in :
      val result = postcodeForm.bind(Map(fieldName -> ""))
      result.hasErrors shouldBe true
      result.errors.head shouldBe FormError("fieldName", List("messageKey.error.required"), ArraySeq(("inputFieldClass", "fieldName")))
