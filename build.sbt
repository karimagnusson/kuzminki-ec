scalaVersion := "2.13.8"

name := "kuzminki-akka"

version := "0.9.2-test"

scalacOptions ++= Seq(
  "-deprecation",
  "-feature"
)

lazy val root = (project in file("."))
  .settings(
    name := "kuzminki-zio",
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % "2.13.8",
      "com.typesafe.akka" %% "akka-actor" % "2.6.19",
      "org.postgresql" % "postgresql" % "42.2.24",
      "com.zaxxer" % "HikariCP" % "4.0.3"
      //"dev.zio" %% "zio" % "1.0.12"
    )
  )

