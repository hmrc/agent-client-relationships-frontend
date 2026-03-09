import sbt.*

object AppDependencies {

  private val bootstrapVersion = "9.19.0"
  private val hmrcMongoVersion = "2.12.0"
  private val playFrameworkVersion = "play-30"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% s"bootstrap-frontend-$playFrameworkVersion"             % bootstrapVersion,
    "uk.gov.hmrc"       %% s"play-frontend-hmrc-$playFrameworkVersion"             % "12.32.0",
    "uk.gov.hmrc.mongo" %% s"hmrc-mongo-$playFrameworkVersion"                     % hmrcMongoVersion,
    "uk.gov.hmrc"       %% s"play-conditional-form-mapping-$playFrameworkVersion"  % "3.5.0",
    "uk.gov.hmrc"       %% s"crypto-json-$playFrameworkVersion"                    % "8.4.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% s"bootstrap-test-$playFrameworkVersion"                 % bootstrapVersion  % Test,
    "uk.gov.hmrc.mongo" %% s"hmrc-mongo-test-$playFrameworkVersion"                % hmrcMongoVersion  % Test,
    "org.jsoup"         % "jsoup"                                                  % "1.22.1"          % Test
  )

}
