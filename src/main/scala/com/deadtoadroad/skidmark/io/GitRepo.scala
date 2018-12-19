package com.deadtoadroad.skidmark.io

import better.files.{File => BFile, _}
import cats.effect.{IO, Resource}
import cats.implicits._
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.log4s.{Logger, getLogger}

import scala.collection.JavaConverters._

class GitRepo(path: String) {
  private val logger: Logger = getLogger

  def ensure(): IO[Unit] =
    absolutePath()
      .map(path => logger.info(s"Looking for Git repository: $path"))
      .flatMap(_ => resource().use(git => IO {
        logger.info(s"Using Git repository: ${git.getRepository.getDirectory.getAbsolutePath}")
      }))
      .onError { case e => IO(logger.error(e)(e.getMessage)) }

  def addAllAndCommit(name: String, email: String, message: String): IO[Unit] =
    resource().use(git => IO {
      // Remove missing.
      git.status().call().getMissing.asScala.foreach(git.rm().addFilepattern(_).call())
      // Add new and changed.
      git.add().addFilepattern(".").call()
      // Commit if we have something to commit.
      if (git.status().call().hasUncommittedChanges)
        git.commit().setAuthor(name, email).setMessage(message).call()
    })

  private def resource(): Resource[IO, Git] =
    Resource.make(findOrCreate())(git => IO {
      val repository = git.getRepository
      repository.close()
      git.close()
    })

  private def findOrCreate(): IO[Git] =
    find()
      .onError { case e => IO(logger.warn(e.getMessage)) }
      .onError { case _ => absolutePath().map(path => logger.warn(s"Creating Git repository: $path")) }
      .orElse(create())

  private def find(): IO[Git] =
    IO(file"$path".toJava)
      .map(file =>
        new FileRepositoryBuilder()
          .addCeilingDirectory(file)
          .findGitDir(file)
      )
      .ensure(new Exception("No Git repository found."))(_.getGitDir != null)
      .map(builder => {
        val repository = builder.build()
        new Git(repository)
      })

  private def create(): IO[Git] =
    IO(file"$path".createDirectoryIfNotExists(createParents = true).toJava)
      .map(file => Git.init().setDirectory(file).call())

  private def absolutePath(): IO[String] = bFile().map(file => file.path.toAbsolutePath.toString)

  private def bFile(): IO[BFile] = IO(file"$path")
}

object GitRepo {
  def apply(path: String): GitRepo = new GitRepo(path)
}
