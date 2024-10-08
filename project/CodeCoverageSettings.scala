import sbt.Setting
import scoverage.ScoverageKeys

object CodeCoverageSettings {

  // TODO: this is a starter repo, there are a lot of excluded controllers here that
  // should be removed from exclusions once controllers are populated
  private val excludedPackages: Seq[String] = Seq(
    "<empty>",
    "Reverse.*",
    "uk.gov.hmrc.BuildInfo",
    "app.*",
    "prod.*",
    ".*Routes.*",
    "testOnly.*",
    "testOnlyDoNotUseInAppConf.*",
    ".*models.*",
    ".*views.*",
    ".*binders.*",
    ".*utils.*",
    ".*controllers.agentInvitation.AgentInvitationErrorController.*",
    ".*controllers.agentInvitation.ConfirmCountryCodeController.*",
    ".*controllers.agentInvitation.DeleteAuthorisationController.*",
    ".*controllers.agentInvitation.InvitationSentController.*",
    ".*controllers.agentInvitation.ReviewAuthorisationsController.*",
    ".*controllers.agentInvitation.SelectTaxServiceController.*",
    ".*controllers.agentInvitation.ConfirmClientController.*",
    ".*controllers.agentInvitation.ConfirmPostcodeController.*",
    ".*controllers.agentInvitation.IdentifyClientController.*",
    ".*controllers.agentInvitation.LegacyAuthorisationDetectedController.*",
    ".*controllers.agentInvitation.SelectClientTypeController.*",
    ".*controllers.agentInvitation.StartController.*",
    ".*controllers.agentInvitationFastTrack.AgentFastTrackErrorController.*",
    ".*controllers.agentInvitationFastTrack.CheckDetailsController.*",
    ".*controllers.agentInvitationFastTrack.ConfirmCgtClientController.*",
    ".*controllers.agentInvitationFastTrack.ConfirmClientController.*",
    ".*controllers.agentInvitationFastTrack.ConfirmCountryCodeController.*",
    ".*controllers.agentInvitationFastTrack.ConfirmPostcodeController.*",
    ".*controllers.agentInvitationFastTrack.ConfirmPptClientController.*",
    ".*controllers.agentInvitationFastTrack.ConfirmPptRegDateController.*",
    ".*controllers.agentInvitationFastTrack.ConfirmTrustClientController.*",
    ".*controllers.agentInvitationFastTrack.IdentifyClientController.*",
    ".*controllers.agentInvitationFastTrack.InvitationSentController.*",
    ".*controllers.agentInvitationFastTrack.KnownFactController.*",
    ".*controllers.agentInvitationFastTrack.LegacyAuthorisationDetectedController.*",
    ".*controllers.agentInvitationFastTrack.ProgressToController.*",
    ".*controllers.agentInvitationFastTrack.SelectClientTypeController.*",
    ".*controllers.agentInvitationFastTrack.StartController.*",
    ".*controllers.agentLedDeauth.DeauthErrorController.*",
    ".*controllers.agentLedDeauth.CancelAuthorisationController.*",
    ".*controllers.agentLedDeauth.ConfirmClientController.*",
    ".*controllers.agentLedDeauth.ConfirmCountryCodeController.*",
    ".*controllers.agentLedDeauth.ConfirmPostcodeController.*",
    ".*controllers.agentLedDeauth.IdentifyClientController.*",
    ".*controllers.agentLedDeauth.SelectClientTypeController.*",
    ".*controllers.agentLedDeauth.StartController.*",
    ".*controllers.agentLedDeauth.LegacyAuthorisationDetectedController.*",
    ".*controllers.agentLedDeauth.InvitationSentController.*",
    ".*controllers.agentLedDeauth.ReviewAuthorisationsController.*",
    ".*controllers.agentLedDeauth.SelectTaxServiceController.*",
    ".*controllers.agentLedDeauth.ConfirmClientController.*",
    ".*controllers.agentsRequestTracking.CancelRequestController.*",
    ".*controllers.agentsRequestTracking.TrackRequestsController.*",
    ".*controllers.agentsRequestTracking.ResendLinkController.*",
    ".*controllers.agentsRequestTracking.CancelAuthorisationController.*",
    ".*controllers.clientInvitation.ChangeConsentController.*",
    ".*controllers.clientInvitation.InvitationErrorController.*",
    ".*controllers.clientInvitation.CheckYourAnswersController.*",
    ".*controllers.clientInvitation.ConfirmDeclineController.*",
    ".*controllers.clientInvitation.ConsentController.*",
    ".*controllers.clientInvitation.CreateNewUserIdController.*",
    ".*controllers.clientInvitation.InvitationHistoryController.*",
    ".*controllers.clientInvitation.SelectTaxServiceController.*",
    ".*controllers.clientInvitation.SuspendedAgentController.*",
    ".*controllers.clientInvitation.UserIdNeededController.*",
    ".*controllers.clientInvitation.WarmUpController.*",
    ".*controllers.SignOutController.*"
  )

  val settings: Seq[Setting[_]] = Seq(
    ScoverageKeys.coverageExcludedPackages := excludedPackages.mkString(","),
    ScoverageKeys.coverageMinimumStmtTotal := 100,
    ScoverageKeys.coverageFailOnMinimum := false,  //TODO change to true once P.O.C over
    ScoverageKeys.coverageHighlighting := true
  )
}
