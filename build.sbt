val beamVersion = "2.18.0"
val betterFilesVersion = "3.8.0"
val circeVersion = "0.12.3"
val circeDerivationVersion = "0.12.0-M7"
val logbackVersion = "1.2.3"
val scioVersion = "0.8.1"
val uPickleVersion = "0.9.7"

val scalatestVersion = "3.1.0"

lazy val `monster-scio-utils` = project
  .in(file("."))
  .settings(publish / skip := true)
  .aggregate(`msg-utils`, `scio-utils`, `scio-test-utils`)

lazy val `msg-utils` = project
  .in(file("utils/msg"))
  .enablePlugins(MonsterLibraryPlugin)
  .settings(
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "upickle" % uPickleVersion,
      "io.circe" %% "circe-parser" % circeVersion
    ),
    libraryDependencies += "org.scalatest" %% "scalatest" % scalatestVersion % Test
  )

lazy val `scio-utils` = project
  .in(file("utils/scio"))
  .enablePlugins(MonsterLibraryPlugin)
  .dependsOn(`msg-utils`)
  .settings(
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % logbackVersion,
      "com.lihaoyi" %% "upickle" % uPickleVersion,
      "com.spotify" %% "scio-core" % scioVersion
    ),
    libraryDependencies ++= Seq(
      "org.apache.beam" % "beam-runners-direct-java" % beamVersion,
      "org.apache.beam" % "beam-runners-google-cloud-dataflow-java" % beamVersion
    ).map(_ % Runtime),
    libraryDependencies ++= Seq(
      "com.github.pathikrit" %% "better-files" % betterFilesVersion,
      "com.spotify" %% "scio-test" % scioVersion,
      "io.circe" %% "circe-derivation" % circeDerivationVersion,
      "org.scalatest" %% "scalatest" % scalatestVersion
    ).map(_ % Test),
    Test / scalacOptions += "-language:higherKinds"
  )

lazy val `scio-test-utils` = project
  .in(file("utils/scio-test"))
  .enablePlugins(MonsterLibraryPlugin)
  .dependsOn(`scio-utils`)
  .settings(
    libraryDependencies ++= Seq(
      "com.github.pathikrit" %% "better-files" % betterFilesVersion,
      "com.spotify" %% "scio-test" % scioVersion,
      "org.scalatest" %% "scalatest" % scalatestVersion
    )
  )

