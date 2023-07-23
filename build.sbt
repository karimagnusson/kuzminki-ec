scalaVersion := "2.13.8"

name := "kuzminki-ec"

version := "0.9.4-RC6"

scalacOptions ++= Seq(
  "-deprecation",
  "-feature"
)

lazy val root = (project in file("."))
  .settings(
    name := "kuzminki-zio",
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % "2.13.8",
      "org.postgresql" % "postgresql" % "42.2.24",
      "com.zaxxer" % "HikariCP" % "4.0.3"
    )
  )

