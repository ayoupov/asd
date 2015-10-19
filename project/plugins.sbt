logLevel := Level.Warn

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.3")

addSbtPlugin("com.github.shivawu" % "sbt-maven-plugin" % "0.1.3-SNAPSHOT")

//addSbtPlugin("com.github.shivawu" % "sbt-maven-plugin" % "0.1.2")

//addSbtPlugin("com.typesafe.sbt" % "sbt-play-ebean" % "1.0.0")

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

//resolvers += "Typesafe Public Repo" at "http://repo.typesafe.com/typesafe/releases"

resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"

resolvers += "commons-logging-not-exist" at "http://gradle.artifactoryonline.com/gradle/libs/"