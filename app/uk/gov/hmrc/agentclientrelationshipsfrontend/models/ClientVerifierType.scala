
package uk.gov.hmrc.agentclientrelationshipsfrontend.models

sealed trait ClientVerifierType

object ClientVerifierType {
  case object Postcode extends ClientVerifierType

  case object CountryCode extends ClientVerifierType

  case object Date extends ClientVerifierType

  case object Email extends ClientVerifierType

  def toEnum: String => ClientVerifierType = {
    case "postcode" => Postcode
    case "countryCode" => CountryCode
    case "date" => Date
    case "email" => Email
    case invalid => throw new Exception(s"ClientVerifierType $invalid not supported")
  }

  def fromEnum: ClientVerifierType => String = {
    case Postcode => "postcode"
    case CountryCode => "countryCode"
    case Date => "date"
    case Email => "email"
  }

}
