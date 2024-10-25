/*
 * Copyright 2023 HM Revenue & Customs
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

import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.play.bootstrap.binders.RedirectUrl.idFunctor
import uk.gov.hmrc.play.bootstrap.binders.{AbsoluteWithHostnameFromAllowlist, OnlyRelative, RedirectUrl}

import java.net.URLEncoder

object UrlHelper {

  def addParamsToUrl(url: String, params: (String, Option[String])*): String = {
    val query = params.collect { case (key, Some(value)) => s"$key=${URLEncoder.encode(value, "UTF-8")}" } mkString "&"
    if query.isEmpty then
      url
    else if url.endsWith("?") || url.endsWith("&") then
      url + query
    else
      val join = if url.contains("?") then "&" else "?"
      url + join + query
  }

  def validateRedirectUrl(redirectUrl: RedirectUrl)(implicit appConfig: AppConfig): String =
    if RedirectUrl.isRelativeUrl(redirectUrl.unsafeValue) then {
      redirectUrl.get(OnlyRelative).url
    } else {
      redirectUrl.get(AbsoluteWithHostnameFromAllowlist(appConfig.allowedRedirectHosts)).url
    }


}
