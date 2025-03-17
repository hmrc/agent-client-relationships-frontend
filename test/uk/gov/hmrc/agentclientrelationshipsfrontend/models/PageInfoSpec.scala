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

package uk.gov.hmrc.agentclientrelationshipsfrontend.models

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class PageInfoSpec extends AnyWordSpecLike with Matchers {

  "PageInfo" should {
    "use the correct offset for mongo queries" in {
      PageInfo(1, 10, 100).offset shouldBe 0
      PageInfo(2, 10, 100).offset shouldBe 10
      PageInfo(3, 10, 100).offset shouldBe 20
    }

    "calculate the next page" in {
      PageInfo(1, 10, 100).nextPage shouldBe 2
      PageInfo(2, 10, 100).nextPage shouldBe 3
      PageInfo(3, 10, 100).nextPage shouldBe 4
    }

    "calculate the previous page" in {
      PageInfo(1, 10, 100).previousPage shouldBe 0
      PageInfo(2, 10, 100).previousPage shouldBe 1
      PageInfo(3, 10, 100).previousPage shouldBe 2
    }

    "calculate the number of pages" in {
      PageInfo(1, 10, 100).numberOfPages shouldBe 10
      PageInfo(2, 10, 100).numberOfPages shouldBe 10
      PageInfo(3, 10, 100).numberOfPages shouldBe 10
      PageInfo(1, 10, 101).numberOfPages shouldBe 11
      PageInfo(1, 10, 99).numberOfPages shouldBe 10
    }

    "calculate the last item on the page" in {
      PageInfo(1, 10, 100).lastItemOnPage shouldBe 10
      PageInfo(2, 10, 100).lastItemOnPage shouldBe 20
      PageInfo(3, 10, 100).lastItemOnPage shouldBe 30
      PageInfo(10, 10, 100).lastItemOnPage shouldBe 100
    }
  }

}
