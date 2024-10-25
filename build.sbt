import play.sbt.routes.RoutesKeys
import uk.gov.hmrc.DefaultBuildSettings
import scoverage.ScoverageKeys

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "3.5.1"

lazy val microservice = Project("agent-client-relationships-frontend", file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(
    PlayKeys.playDefaultPort := 9448,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    // scalacOptions += "-Wconf:src=routes/.*:s", // doesn't work in scala 3 yet - available in 3.5.0 RC1 and maybe 3.3.4 LTS
    // scalacOptions += "-Wconf:cat=unused-imports&src=html/.*:s", // doesn't work in scala 3
    scalacOptions := scalacOptions.value.diff(Seq("-Wunused:all")), // temp fix to hide all unused warnings
    pipelineStages := Seq(gzip)
  )
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(CodeCoverageSettings.settings)
  .settings(
    RoutesKeys.routesImport ++= Seq(
      "uk.gov.hmrc.play.bootstrap.binders.RedirectUrl",
      "uk.gov.hmrc.agentclientrelationshipsfrontend.binders.UrlBinders.journeyTypeBinder",
      "uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.JourneyType"
    )
  )
  .settings(
    TwirlKeys.templateImports ++= Seq(
      "uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.Layout",
      "uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.components._",
      "uk.gov.hmrc.govukfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.components._",
    )
  )

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test")
  .settings(DefaultBuildSettings.itSettings())
  .settings(libraryDependencies ++= AppDependencies.test)
