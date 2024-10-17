package uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey

import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.KnownFactType

// this is populated from the response from the agent-client-realtionships service
// when we submit a tax identifier (clientId)
case class ClientDetailsResponse(
                                  requiredKnownFact: Option[KnownFactType],
                                  factValue: Seq[String] = Seq.empty,
                                  agentTypeRequired: Boolean = false,
                                  clientName: Option[String] = None
                                )
