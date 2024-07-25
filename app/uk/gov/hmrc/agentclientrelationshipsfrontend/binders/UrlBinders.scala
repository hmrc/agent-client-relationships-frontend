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

package uk.gov.hmrc.agentclientrelationshipsfrontend.binders

import play.api.mvc.PathBindable.bindableString
import play.api.mvc.{PathBindable, QueryStringBindable}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.FilterFormStatus
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.InvitationIdHelper

import scala.util.Try

object UrlBinders:

  implicit val invitationIdBinder: PathBindable[String] = getInvitationIdBinder(bindableString)

  def getInvitationIdBinder(implicit stringBinder: PathBindable[String]): PathBindable[String] = new PathBindable[String]:

    override def bind(key: String, value: String): Either[String, String] =
      val isValidPrefix = value.headOption.exists(Seq('A', 'B', 'C').contains)

      if (isValidPrefix && InvitationIdHelper.isValid(value))
        Right(value)
      else
        Left(ErrorConstants.InvitationIdNotFound)

    override def unbind(key: String, id: String): String = stringBinder.unbind(key, id)


  implicit val filterFormStatusBinder: QueryStringBindable[FilterFormStatus] = getFilterFormStatusBinder

  def getFilterFormStatusBinder(implicit queryStringBinder: QueryStringBindable[String]): QueryStringBindable[FilterFormStatus] =
    new QueryStringBindable[FilterFormStatus]:

      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, FilterFormStatus]] =
        queryStringBinder.bind(key, params).map:
          case Right(s) => Try(FilterFormStatus.valueOf(s)).toEither.left.map(_ => ErrorConstants.StatusError)
          case _ => Left(ErrorConstants.StatusError)

      override def unbind(key: String, ffs: FilterFormStatus): String =
        queryStringBinder.unbind(key, ffs.toString)

object ErrorConstants:
  val InvitationIdNotFound = "INVITATION_ID_NOTFOUND"
  val StatusError = "FORM_FILTER_STATUS_INVALID"

