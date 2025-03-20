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

package uk.gov.hmrc.agentclientrelationshipsfrontend.util

import org.mockito.Mockito.{reset, when}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import org.scalatestplus.mockito.MockitoSugar.mock
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.UrlHelper
import uk.gov.hmrc.play.bootstrap.binders.RedirectUrl

class UrlHelperSpec extends AnyWordSpecLike with Matchers with OptionValues with BeforeAndAfterEach {

  implicit val appConfig: AppConfig = mock[AppConfig]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(appConfig)
  }

  val testUrl = "/url"
  val testUrlAbsolute = "http://localhost:1234/url"
  val key1 = "key1"
  val param1 = "param1"
  val key2 = "key2"
  val param2 = "param2"
  val keyUrl = "keyUrl"

  "addParamsToUrl" should {

    "add the list of parameters to a url without any" in {
      val url = UrlHelper.addParamsToUrl(testUrl, (key1, Some(param1)), (key2, Some(param2)))

      url shouldBe "/url?key1=param1&key2=param2"
    }

    "add the list of parameters to a url without some appended already" in {
      val urlWithParams = UrlHelper.addParamsToUrl(testUrl, (key1, Some(param1)))
      val url = UrlHelper.addParamsToUrl(urlWithParams, (key2, Some(param2)))

      url shouldBe "/url?key1=param1&key2=param2"
    }

    "add the list of parameters cleanly to a url that ends with '?'" in {
      val url = UrlHelper.addParamsToUrl(testUrl + "?", (key1, Some(param1)))

      url shouldBe "/url?key1=param1"
    }

    "add the list of parameters cleanly to a url that ends with '&'" in {
      val urlWithParams = UrlHelper.addParamsToUrl(testUrl, (key1, Some(param1)))
      val url = UrlHelper.addParamsToUrl(urlWithParams + "&", (key2, Some(param2)))

      url shouldBe "/url?key1=param1&key2=param2"
    }

    "encode parameters it is trying to append" in {
      val paramUrl = UrlHelper.addParamsToUrl(testUrl, (key1, Some(param1)), (key2, Some(param2)))
      val url = UrlHelper.addParamsToUrl(testUrl, (keyUrl, Some(paramUrl)))

      url shouldBe "/url?keyUrl=%2Furl%3Fkey1%3Dparam1%26key2%3Dparam2"
    }

    "not alter the url if no parameters are provided" in {
      val url = UrlHelper.addParamsToUrl(testUrl)
      url shouldBe testUrl
    }
  }

  "validateRedirectUrl" should {
    "validate a RedirectUrl if it is relative" in {
      val testUrl = "/url"

      UrlHelper.validateRedirectUrl(RedirectUrl(testUrl)) shouldBe testUrl
    }
    "validate a RedirectUrl with a valid host" in {
      when(appConfig.allowedRedirectHosts).thenReturn(Set("localhost"))
      val testUrl = "http://localhost:9000/url"

      UrlHelper.validateRedirectUrl(RedirectUrl(testUrl)) shouldBe testUrl
    }
    "fail on a RedirectUrl with an invalid host" in {
      when(appConfig.allowedRedirectHosts).thenReturn(Set("localhost"))
      val testUrl = "http://invalidhost.com/url"

      intercept[IllegalArgumentException](UrlHelper.validateRedirectUrl(RedirectUrl(testUrl))).getMessage shouldBe
        "Provided URL [http://invalidhost.com/url] doesn't comply with redirect policy"
    }
  }

  "getRedirectUrl" should {

    "return a relative url" in {
      val url = UrlHelper.getRedirectUrl(testUrl)
      url shouldBe Some(testUrl)
    }

    "return an absolute url if the host is in the allow list" in {
      when(appConfig.allowedRedirectHosts).thenReturn(Set("localhost"))
      val url = UrlHelper.getRedirectUrl(testUrlAbsolute)
      url shouldBe Some(testUrlAbsolute)
    }

    "return None when the absolute url host is not in the allow list" in {
      when(appConfig.allowedRedirectHosts).thenReturn(Set(""))
      val url = UrlHelper.getRedirectUrl(testUrlAbsolute)
      url shouldBe None
    }

    "return None when the url contains an invalid character" in {
      val url = UrlHelper.getRedirectUrl("/invalid-@-character")
      url shouldBe None
    }
  }
}
