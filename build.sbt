name := """tdd_filmdb"""
organization := "de.avlaax"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.6"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies += "com.h2database" % "h2" % "1.4.192"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "3.0.1",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.1"
)

coverageExcludedPackages := """controllers\..*Reverse.*;router.Routes.*;views\..*"""

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "de.avlaax.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "de.avlaax.binders._"
