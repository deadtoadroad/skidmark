package com.deadtoadroad.skidmark.io

import java.io.{BufferedReader, BufferedWriter}
import java.nio.charset.{Charset, StandardCharsets}
import java.nio.file.StandardOpenOption
import java.util.stream.Collectors

import better.files.{File => BFile, _}
import cats.effect.{IO, Resource}
import cats.implicits._

import scala.collection.JavaConverters._

class File(path: String) {
  def readText(): IO[String] =
    reader().use(r => IO(r.lines().collect(Collectors.joining(System.lineSeparator()))))

  def readLines(): IO[List[String]] =
    reader().use(r => IO(r.lines().iterator().asScala.toList))

  def writeText(text: String): IO[Unit] =
    writer().use(w => IO(w.write(text)))

  def appendLines(lines: List[String]): IO[Unit] =
    appender().use(a => IO(lines.foreach(l => {
      a.write(l)
      a.newLine()
    })))

  def deleteDirectory(): IO[Unit] =
    bFile()
      .ensure(new Exception("Path is not a directory."))(bf => !bf.exists || bf.isDirectory)
      .map(bf => if (!bf.exists) Unit else bf.delete())

  private def reader(): Resource[IO, BufferedReader] =
    resource(_.newBufferedReader(File.charset))

  private def writer(): Resource[IO, BufferedWriter] =
    resource(_.createIfNotExists(createParents = true).newBufferedWriter(File.charset, File.writeOptions))

  private def appender(): Resource[IO, BufferedWriter] =
    resource(_.createIfNotExists(createParents = true).newBufferedWriter(File.charset, File.appendOptions))

  private def resource[R <: AutoCloseable](selector: BFile => R): Resource[IO, R] =
    Resource.fromAutoCloseable(bFile().map(selector))

  private def bFile(): IO[BFile] = IO(file"$path")
}

object File {
  val charset: Charset = StandardCharsets.UTF_8
  val writeOptions: Seq[StandardOpenOption] = Seq(StandardOpenOption.TRUNCATE_EXISTING)
  val appendOptions: Seq[StandardOpenOption] = Seq(StandardOpenOption.APPEND)

  def apply(path: String): File = new File(path)
}
