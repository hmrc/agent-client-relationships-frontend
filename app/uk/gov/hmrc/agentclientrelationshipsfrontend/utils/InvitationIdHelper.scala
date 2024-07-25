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

package uk.gov.hmrc.agentclientrelationshipsfrontend.utils

object InvitationIdHelper:

  private val pattern = "^[ABCDEFGHJKLMNOPRSTUWXYZ123456789]{13}$".r

  def isValid(identifier: String): Boolean = identifier match
    case pattern(_*) => checksumDigits(identifier.take(11)) == identifier.takeRight(2)
    case _ => false

  private def checksumDigits(toChecksum: String) =
    val checksum10Bits = CRC10.calculate(toChecksum)
    val lsb5BitsChecksum = to5BitAlphaNumeric(checksum10Bits & 0x1F)
    val msb5BitsChecksum = to5BitAlphaNumeric((checksum10Bits & 0x3E0) >> 5)

    s"$lsb5BitsChecksum$msb5BitsChecksum"

  private def to5BitAlphaNumeric(fiveBitNum: Int) =
    require(fiveBitNum >= 0 && fiveBitNum <= 31)

    "ABCDEFGHJKLMNOPRSTUWXYZ123456789"(fiveBitNum)

  private object CRC10:

    /* Params for CRC-10 */
    val bitWidth = 10
    val poly = 0x233
    val initial = 0
    val xorOut = 0
    val widthMask: Int = (1 << bitWidth) - 1

    val table: Seq[Int] =
      val top = 1 << (bitWidth - 1)
      for (i <- 0 until 256) yield
        var crc = i << (bitWidth - 8)
        for (_ <- 0 until 8)
          crc = if ((crc & top) != 0) (crc << 1) ^ poly else crc << 1
        crc & widthMask

    def calculate(string: String): Int = calculate(string.getBytes())

    def calculate(input: Array[Byte]): Int =
      val length = input.length
      var crc = initial ^ xorOut

      for (i <- 0 until length)
        crc = table(((crc >>> (bitWidth - 8)) ^ input(i)) & 0xff) ^ (crc << 8)
      crc & widthMask
