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
    ".*testOnly.*",
    "testOnlyDoNotUseInAppConf.*",
    "$anon"
  )

  val settings: Seq[Setting[_]] = Seq(
    ScoverageKeys.coverageExcludedPackages := excludedPackages.mkString(","),
    ScoverageKeys.coverageMinimumStmtTotal := 100,
    ScoverageKeys.coverageFailOnMinimum := false,  //TODO change to true once P.O.C over
    ScoverageKeys.coverageHighlighting := true
  )
}
