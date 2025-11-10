import uk.gov.hmrc.DefaultBuildSettings

val appName = "import-voluntary-disclosure-frontend"

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "3.3.6"

lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) // Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    libraryDependencySchemes ++= Seq("org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always),
    PlayKeys.playDefaultPort := 7950,
    routesImport += "models.SelectedDutyTypes.SelectedDutyType",
    TwirlKeys.templateImports ++= Seq(
      "config.AppConfig",
      "uk.gov.hmrc.govukfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.components._"
    ),
    scalacOptions += "-Wconf:src=routes/.*:s",
    scalacOptions += "-Wconf:cat=unused-imports&src=html/.*:s",
    // turn any warnings into errors
    //  scalastyleFailOnWarning := true,
    // run scalastyle on compile
    compileScalastyle   := (Compile / scalastyle).toTask("").value,
    (Compile / compile) := ((Compile / compile) dependsOn compileScalastyle).value,
    Concat.groups := Seq(
      "javascripts/application.js" ->
        group(
          Seq(
            "lib/govuk-frontend/govuk/index.js",
            "lib/hmrc-frontend/hmrc/all.js",
            "javascripts/app.js"
          )
        )
    ),
    pipelineStages := Seq(digest),
    // below line required to force asset pipeline to operate in dev rather than only prod
    Assets / pipelineStages := Seq(concat)
  )
  .configs(Test)
  .settings(CodeCoverageSettings.settings: _*)

val codeStyleIntegrationTest = taskKey[Unit]("enforce code style then integration test")

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test") // the "test->test" allows reusing test code and test dependencies
  .settings(DefaultBuildSettings.itSettings())
  .settings(libraryDependencies ++= AppDependencies.test)
  .settings(
    inConfig(Test)(ScalastylePlugin.rawScalastyleSettings()) ++ Seq(
      Test / scalastyleConfig          := (scalastyle / scalastyleConfig).value,
      Test / scalastyleTarget          := target.value / "scalastyle-it-results.xml",
      Test / scalastyleFailOnError     := (scalastyle / scalastyleFailOnError).value,
      (Test / scalastyleFailOnWarning) := (scalastyle / scalastyleFailOnWarning).value,
      Test / scalastyleSources         := (Test / unmanagedSourceDirectories).value,
      codeStyleIntegrationTest         := (Test / scalastyle).toTask("").value,
      (Test / test)                    := ((Test / test) dependsOn codeStyleIntegrationTest).value
    )
  )
