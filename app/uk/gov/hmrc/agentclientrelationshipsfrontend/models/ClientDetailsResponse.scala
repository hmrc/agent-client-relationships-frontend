
package uk.gov.hmrc.agentclientrelationshipsfrontend.models

// this is populated by the response from the agent-client-relationships service
// when we submit a tax identifier (clientId)
case class ClientDetailsResponse(
                                  requiredKnownFact: Option[ClientVerifierType] = None,
                                  factValue: Seq[String] = Seq.empty,
                                  agentTypeRequired: Boolean = false,
                                  clientName: Option[String] = None,
                                  existingRelationships: Option[Seq[ExistingRelationship]] = None,
                                )

case class ExistingRelationship(
                                       relationshipType: String, // for example "AuthorisationRequestExists", or "RelationshipExists",
                                       errorMessage: String
                                     )
