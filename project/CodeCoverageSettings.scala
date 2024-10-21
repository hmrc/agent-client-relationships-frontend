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
    ".*controllers.TrackRequestsController.*",
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
