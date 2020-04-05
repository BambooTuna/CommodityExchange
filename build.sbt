import Settings._

lazy val domain = (project in file("domain"))
  .enablePlugins(JavaAppPackaging, AshScriptPlugin, DockerPlugin)
  .settings(commonSettings)
  .settings(
    resolvers += AkkaServerSupport.resolver,
    libraryDependencies ++= Seq(
      AkkaServerSupport.core,
      AkkaServerSupport.authentication,
      AkkaServerSupport.cooperation,
    ) ++ `doobie-quill`.all
  )

lazy val usecase = (project in file("usecase"))
  .enablePlugins(JavaAppPackaging, AshScriptPlugin, DockerPlugin)
  .settings(commonSettings)
  .dependsOn(domain)

lazy val interface = (project in file("interface"))
  .enablePlugins(JavaAppPackaging, AshScriptPlugin, DockerPlugin)
  .settings(commonSettings)
  .settings(
    resolvers += Resolver.bintrayRepo("dnvriend", "maven"),
    libraryDependencies ++= Seq(
      MySQLConnectorJava.version,
      Redis.client,
      "org.iq80.leveldb" % "leveldb" % "0.7",
      "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
      "javax.ws.rs" % "javax.ws.rs-api" % "2.0.1",
      "com.github.swagger-akka-http" %% "swagger-akka-http" % "2.0.4",
      "org.simplejavamail" % "simple-java-mail" % "6.0.3",
      "com.github.dnvriend" %% "akka-persistence-inmemory" % "2.5.15.2"
    )
  )
  .dependsOn(usecase)

lazy val apiServer = (project in file("apiServer"))
  .enablePlugins(JavaAppPackaging, AshScriptPlugin, DockerPlugin)
  .settings(commonSettings)
  .settings(dockerSettings)
  .settings(
    libraryDependencies ++= Seq(
    )
  )
  .dependsOn(interface)

lazy val root =
  (project in file("."))
    .aggregate(domain, usecase, interface, apiServer)

