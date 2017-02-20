name := "asd"

version := "1.0"

//lazy val `asd` = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

lazy val `asd` = (project in file(".")).enablePlugins(PlayJava)

//lazy val `asd` = (project in file(".")).enablePlugins(PlayJava, SbtWeb)

scalaVersion := "2.11.7"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

//javaHome := Some(file("C:/Progra~1/Java/jdk1.8.0_31/"))

scalacOptions ++= Seq("-target:jvm-1.8")

libraryDependencies ++= Seq(javaJdbc , cache , javaWs)

//pipelineStages := Seq(gzip, cssCompress)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

publishArtifact in (Compile, packageDoc) := false

publishArtifact in packageDoc := false

//(managedClasspath in Runtime) += (packageBin in Assets).value

sources in (Compile,doc) := Seq.empty

fork in run := true

//updateOptions := updateOptions.value.withCachedResolution(true)
