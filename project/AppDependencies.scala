import play.core.PlayVersion.current
import sbt.*

object AppDependencies {

  private val bootstrapVersion = "10.1.0"
  private val playVersion      = "play-30"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"                  %% s"bootstrap-frontend-$playVersion" % bootstrapVersion,
    "uk.gov.hmrc"                  %% s"play-frontend-hmrc-$playVersion" % "12.20.0",
    "uk.gov.hmrc.mongo"            %% s"hmrc-mongo-$playVersion"         % "2.10.0",
    "com.fasterxml.jackson.module" %% "jackson-module-scala"             % "2.16.1",
    "com.sun.mail"                  % "javax.mail"                       % "1.6.2"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalamock"          %% "scalamock"                    % "6.0.0",
    "uk.gov.hmrc"            %% s"bootstrap-test-$playVersion" % bootstrapVersion,
    "org.jsoup"               % "jsoup"                        % "1.17.2",
    "org.playframework"      %% "play-test"                    % current,
    "com.vladsch.flexmark"    % "flexmark-all"                 % "0.64.8",
    "org.scalatestplus.play" %% "scalatestplus-play"           % "7.0.2",
    "org.scalatest"          %% "scalatest"                    % "3.2.19",
  ).map(_ % Test)
}
