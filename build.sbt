
inThisBuild(List(
  organization := "io.github.karimagnusson",
  homepage := Some(url("https://kuzminki.info/")),
  licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  developers := List(
    Developer(
      "karimagnusson",
      "Kari Magnusson",
      "kotturinn@gmail.com",
      url("https://github.com/karimagnusson")
    )
  )
))

ThisBuild / version := "0.9.5"
ThisBuild / versionScheme := Some("early-semver")

scalaVersion := "3.3.1"

lazy val scala3 = "3.3.1"
lazy val scala213 = "2.13.12"
lazy val supportedScalaVersions = List(scala213, scala3)

lazy val root = (project in file("."))
  .aggregate(kuzminkiEc)
  .settings(
    crossScalaVersions := Nil,
    publish / skip := true
  )

lazy val kuzminkiEc = (project in file("kuzminki-ec"))
  .settings(
    name := "kuzminki-ec",
    crossScalaVersions := supportedScalaVersions,
    libraryDependencies ++= Seq(
      "org.postgresql" % "postgresql" % "42.2.24",
      "com.zaxxer" % "HikariCP" % "5.0.1"
    ),
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, _)) =>
          Seq(
            "org.scala-lang" % "scala-reflect" % scalaVersion.value,
            "com.chuusai" %% "shapeless" % "2.3.10"
          )
        case Some((3, _)) =>
          Seq("org.tpolecat" %% "typename" % "1.1.0")
        case _ => Seq.empty
      }
    },
    Compile / scalacOptions ++= Seq(
      "-encoding", "utf8",
      "-feature",
      "-language:higherKinds",
      "-language:existentials",
      "-language:implicitConversions",
      "-deprecation",
      "-unchecked"
    ),
    Compile / scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((3, _))  => Seq("-rewrite")
        case _             => Seq("-Xlint")
      }
    },
  )





