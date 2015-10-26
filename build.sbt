name := "asd"

version := "1.0"

//lazy val `asd` = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

lazy val `asd` = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq( javaJdbc , cache , javaWs)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

publishArtifact in (Compile, packageDoc) := false

publishArtifact in packageDoc := false

sources in (Compile,doc) := Seq.empty

fork in run := true

//updateOptions := updateOptions.value.withCachedResolution(true)
