import uk.gov.hmrc.DefaultBuildSettings

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "3.4.2"

lazy val microservice = Project("agent-client-relationships-frontend", file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(
    PlayKeys.playDefaultPort := 9448,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    routesImport += "uk.gov.hmrc.agentclientrelationshipsfrontend.binders.UrlBinders._",
    // scalacOptions += "-Wconf:src=routes/.*:s", // doesn't work in scala 3 yet - available in 3.5.0 RC1 and maybe 3.3.4 LTS
    // scalacOptions += "-Wconf:cat=unused-imports&src=html/.*:s", // doesn't work in scala 3
    scalacOptions := scalacOptions.value.diff(Seq("-Wunused:all")), // temp fix to hide all unused warnings
    pipelineStages := Seq(gzip)
  )
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(CodeCoverageSettings.settings)

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test")
  .settings(DefaultBuildSettings.itSettings())
  .settings(libraryDependencies ++= AppDependencies.test)
