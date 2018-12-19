package com.deadtoadroad.skidmark.api.services

import cats.effect.IO
import cats.implicits._
import com.deadtoadroad.skidmark.blog.{Blog, BlogSubscriber}
import com.deadtoadroad.skidmark.cqrs._
import com.deadtoadroad.skidmark.db.{Database, DatabaseSubscriber, DatabaseValidator, FileDatabase}
import com.deadtoadroad.skidmark.es.FileEventStore
import com.deadtoadroad.skidmark.git.GitSubscriber
import com.deadtoadroad.skidmark.git.blog.GitBlog
import com.deadtoadroad.skidmark.log.LogSubscriber
import com.deadtoadroad.skidmark.model.events.comment._
import com.deadtoadroad.skidmark.model.events.post._
import com.deadtoadroad.skidmark.model.{Comment, Post}
import org.http4s.HttpRoutes
import org.log4s.{Logger, getLogger}

class AllServices(fileRoot: String, urlRoot: String) extends Service {
  private val eventStore: EventStore = FileEventStore(s"$fileRoot/_es")
  private val database: Database = FileDatabase(s"$fileRoot/_db")
  private val validator: Validator = DatabaseValidator(database)
  private val blog: Blog = GitBlog(database, fileRoot, urlRoot)

  // Assemble the event pipeline in reverse order for dependency reasons.

  private val gitSubscriber: Subscriber = GitSubscriber(fileRoot, "Adam Boddington", "adam@boddington.net")
  private val blogRepublisher: Publisher =
    Function.chain(Seq(
      LogSubscriber("blog -> git"),
      gitSubscriber
    ).map(subscribeToAll))(Publisher())

  private val blogSubscriber: Subscriber = BlogSubscriber(database, blog, blogRepublisher)
  private val databaseRepublisher: Publisher =
    Function.chain(Seq(
      LogSubscriber("db -> blog"),
      blogSubscriber
    ).map(subscribeToAll))(Publisher())

  private val databaseSubscriber: Subscriber = DatabaseSubscriber(database, databaseRepublisher)
  private val publisher: Publisher =
    Function.chain(Seq(
      LogSubscriber("es -> db"),
      databaseSubscriber
    ).map(subscribeToAll))(Publisher())

  // Assemble the CQRS dispatcher and the HTTP routes.

  private val dispatcher: Dispatcher = SingleThreadedDispatcher(eventStore, validator, publisher)
  private val adminService: Service = AdminService(dispatcher)
  private val authorService: Service = AuthorService(dispatcher)
  private val userService: Service = UserService(dispatcher)

  private lazy val subscribeToAll: Subscriber => Publisher => Publisher = subscriber => publisher =>
    publisher
      .subscribe[Post, PostCreated](subscriber)
      .subscribe[Post, PostTitleUpdated](subscriber)
      .subscribe[Post, PostAuthorUpdated](subscriber)
      .subscribe[Post, PostTagsUpdated](subscriber)
      .subscribe[Post, PostTextUpdated](subscriber)
      .subscribe[Post, PostPublished](subscriber)
      .subscribe[Post, PostUnpublished](subscriber)
      .subscribe[Comment, CommentCreated](subscriber)
      .subscribe[Comment, CommentAuthorUpdated](subscriber)
      .subscribe[Comment, CommentTextUpdated](subscriber)
      .subscribe[Comment, CommentVetted](subscriber)
      .subscribe[Comment, CommentUnvetted](subscriber)

  override val service: HttpRoutes[IO] = adminService.service <+> authorService.service <+> userService.service
  override val logger: Logger = getLogger
}

object AllServices {
  def apply(fileRoot: String, urlRoot: String): Service = new AllServices(fileRoot, urlRoot)
}
