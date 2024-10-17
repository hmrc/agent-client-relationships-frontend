package uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey

sealed trait KnownFactType

object KnownFactType {
  case object Postcode extends KnownFactType

  case object CountryCode extends KnownFactType

  case object Date extends KnownFactType

  case object Email extends KnownFactType

  def toEnum: String => KnownFactType = {
    case "postcode" => Postcode
    case "countryCode" => CountryCode
    case "date" => Date
    case "email" => Email
    case invalid => throw new Exception(s"KnownFactType $invalid not supported")
  }

  def fromEnum: KnownFactType => String = {
    case Postcode => "postcode"
    case CountryCode => "countryCode"
    case Date => "date"
    case Email => "email"
  }

}
