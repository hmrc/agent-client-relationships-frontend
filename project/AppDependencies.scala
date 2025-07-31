import sbt.*

object AppDependencies {

  private val bootstrapVersion = "9.18.0"
  private val hmrcMongoVersion = "2.7.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30"             % bootstrapVersion,
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30"             % "12.7.0",
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"                     % hmrcMongoVersion,
    "uk.gov.hmrc"       %% "play-conditional-form-mapping-play-30"  % "3.3.0",
    "uk.gov.hmrc"       %% "crypto-json-play-30"                    % "8.2.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "bootstrap-test-play-30"                 % bootstrapVersion  % Test,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-test-play-30"                % hmrcMongoVersion  % Test,
    "org.jsoup"         % "jsoup"                                   % "1.19.1"          % Test
  )

}
