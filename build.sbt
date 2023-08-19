name := "rest-api-example"

ThisBuild / version := "0.1"

ThisBuild / scalaVersion := "2.13.11"
ThisBuild / organization := "com.example"

lazy val restApiExample = (project in file("."))
  .settings(name := "restApiExample")