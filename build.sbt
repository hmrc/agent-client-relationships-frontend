import play.sbt.routes.RoutesKeys
import uk.gov.hmrc.DefaultBuildSettings

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "3.5.2"

lazy val microservice = Project("agent-client-relationships-frontend", file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(
    PlayKeys.playDefaultPort := 9435,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    scalacOptions ++= Seq(
      "-Wconf:src=routes/.*:s",
      "-Wconf:msg=Flag.*repeatedly:s",
      "-Wconf:msg=unused import.*&src=html/.*:s"
    ),
    pipelineStages := Seq(gzip)
  )
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(CodeCoverageSettings.settings)
  .settings(
    RoutesKeys.routesImport ++= Seq(
      "uk.gov.hmrc.play.bootstrap.binders.RedirectUrl",
      "uk.gov.hmrc.agentclientrelationshipsfrontend.binders.UrlBinders._",
      "uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.JourneyType",
      "uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.JourneyExitType",
      "uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.ClientExitType"
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
