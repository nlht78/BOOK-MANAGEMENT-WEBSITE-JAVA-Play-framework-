name := """last"""
organization := "test"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.15"
libraryDependencies += guice
libraryDependencies += "jakarta.persistence" % "jakarta.persistence-api" % "3.0.0"
libraryDependencies += "org.hibernate.orm" % "hibernate-core" % "6.1.7.Final"
libraryDependencies += "mysql" % "mysql-connector-java" % "8.0.33"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.9.2"
libraryDependencies += "com.auth0" % "java-jwt" % "3.18.1"






