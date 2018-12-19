name := "skidmark"

version := "0.1"

scalaVersion := "2.12.7"

scalacOptions := Seq(
  "-deprecation",
  "-Ypartial-unification"
)

val catsVersion = "1.4.0"
val catsEffectVersion = "1.0.0"
val fs2Version = "1.0.0"
val betterFilesVersion = "3.6.0"
val json4sVersion = "3.6.2"
val log4sVersion = "1.6.1"
val logbackClassicVersion = "1.2.3"
val http4sVersion = "0.20.0-M3"
val circeVersion = "0.10.1"
val jGitVersion = "5.1.3.+"

libraryDependencies ++= Seq(
  // cats also comes with cats-effect, fs2, and http4s.
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.typelevel" %% "cats-kernel" % catsVersion,
  "org.typelevel" %% "cats-macros" % catsVersion,
  // cats-effect also comes with fs2.
  "org.typelevel" %% "cats-effect" % catsEffectVersion,
  // fs2 also comes with http4s.
  "co.fs2" %% "fs2-core" % fs2Version,
  "co.fs2" %% "fs2-io" % fs2Version,
  "com.github.pathikrit" %% "better-files" % betterFilesVersion,
  "org.json4s" %% "json4s-native" % json4sVersion,
  // log4s also comes with http4s.
  "org.log4s" %% "log4s" % log4sVersion,
  "ch.qos.logback" % "logback-classic" % logbackClassicVersion,
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "org.eclipse.jgit" % "org.eclipse.jgit" % jGitVersion,
  "org.scalactic" %% "scalactic" % "3.0.5",
  "org.scalatest" %% "scalatest" % "3.0.5" % Test
)
