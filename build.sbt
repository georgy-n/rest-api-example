name := "rest-api-example"

ThisBuild / version := "0.1"

ThisBuild / scalaVersion := "2.13.11"
ThisBuild / organization := "com.example"


val dependency = Seq(
  "com.github.pureconfig" %% "pureconfig" % "0.17.4",
  "com.softwaremill.sttp.tapir" %% "tapir-core" % "1.7.2",
  "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % "1.7.2",
  "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % "1.7.2",
  "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % "1.7.2",
  "org.http4s" %% "http4s-ember-server" % "0.23.23",
  "com.softwaremill.sttp.tapir" %% "tapir-sttp-stub-server" % "1.7.3",
  "org.scalatest" %% "scalatest" % "3.2.17" % "test"
)
lazy val restApiExample = (project in file("."))
  .settings(
    name := "restApiExample",
    libraryDependencies := dependency
  )
