package uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey

sealed trait JourneyType

object JourneyType {
  private case object CreateAuthorisationRequestJourney extends JourneyType
  private case object DeAuthorisationJourney extends JourneyType

  def toEnum: String => JourneyType = {
    case "authorisation-request" => CreateAuthorisationRequestJourney
    case "deauth" => DeAuthorisationJourney
    case invalid => throw new Exception(s"Journey type $invalid not supported")
  }

  def fromEnum: JourneyType => String = {
    case JourneyType.CreateAuthorisationRequestJourney => "authorisation-request"
    case JourneyType.DeAuthorisationJourney => "deauth"
  }
}