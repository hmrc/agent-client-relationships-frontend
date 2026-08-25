import sbt.*

object AppDependencies {

  private val playVer = "play-30"
  private val bootstrapVer = "9.19.0"
  private val mongoVer = "2.13.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% s"bootstrap-frontend-$playVer"            % bootstrapVer,
    "uk.gov.hmrc.mongo" %% s"hmrc-mongo-$playVer"                    % mongoVer,
    "uk.gov.hmrc"       %% s"play-frontend-hmrc-$playVer"            % "13.11.0",
    "uk.gov.hmrc"       %% s"play-conditional-form-mapping-$playVer" % "3.5.0",
    "uk.gov.hmrc"       %% s"crypto-json-$playVer"                   % "8.4.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% s"bootstrap-test-$playVer"  % bootstrapVer,
    "uk.gov.hmrc.mongo" %% s"hmrc-mongo-test-$playVer" % mongoVer,
    "org.jsoup"         % "jsoup"                      % "1.22.1"
  ).map(_ % Test)

}
