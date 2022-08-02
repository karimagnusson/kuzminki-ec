
organization := "io.github.karimagnusson"

sonatypeProfileName := "io.github.karimagnusson"
sonatypeCredentialHost := "s01.oss.sonatype.org"
sonatypeRepository := "https://s01.oss.sonatype.org/service/local"

publishConfiguration := publishConfiguration.value.withOverwrite(true)
publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true)

publishMavenStyle := true
publishTo := sonatypePublishToBundle.value

crossPaths := false

homepage := Some(
  url("https://github.com/karimagnusson/kuzminki-zio")
)

scmInfo := Some(
  ScmInfo(
    url("https://github.com/karimagnusson"),
    "git@github.com:karimagnusson/kuzminki-zio.git")
)

developers := List(
  Developer(
    "karimagnusson",
    "karimagnusson",
    "kotturinn@gmail.com",
    url("https://github.com/karimagnusson")
  )
)

licenses += (
  "Apache-2.0",
  url("http://www.apache.org/licenses/LICENSE-2.0")
)