name := "ScalaWorksheets"

version := "0.1"

scalaVersion := "2.13.2"

libraryDependencies += "org.typelevel" %% "cats-core" % "2.1.1"
libraryDependencies += "org.typelevel" %% "cats-effect" % "2.1.3"

lazy val Doobie = "0.9.0"

lazy val doobieDependencies: Seq[ModuleID] = Seq(
  "org.tpolecat" %% "doobie-core" % Doobie,
  "org.tpolecat" %% "doobie-postgres" % Doobie,
  "org.tpolecat" %% "doobie-specs2" % Doobie,
  "org.tpolecat" %% "doobie-hikari" % Doobie,
  "org.tpolecat" %% "doobie-quill" % Doobie,
  "org.tpolecat" %% "doobie-postgres-circe" % Doobie
)

libraryDependencies ++= doobieDependencies