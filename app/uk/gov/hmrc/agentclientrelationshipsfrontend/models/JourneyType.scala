
package uk.gov.hmrc.agentclientrelationshipsfrontend.models

sealed trait JourneyType

object JourneyType {
  case object CreateAuthorisationRequestJourney extends JourneyType
  case object DeAuthorisationJourney extends JourneyType

  def toEnum: String => JourneyType = {
    case "create" => CreateAuthorisationRequestJourney
    case "delete" => DeAuthorisationJourney
    case invalid => throw new Exception(s"Journey type $invalid not supported")
  }

  def fromEnum: JourneyType => String = {
    case JourneyType.CreateAuthorisationRequestJourney => "create"
    case JourneyType.DeAuthorisationJourney => "delete"
  }
}