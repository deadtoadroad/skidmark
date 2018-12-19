package com.deadtoadroad.skidmark.git.blog

import com.deadtoadroad.skidmark.db.model.{Comment, Post}
import com.deadtoadroad.skidmark.git.blog.PublishedPostIndexItemOps._
import com.deadtoadroad.skidmark.git.blog.StringBuilderOps._
import com.deadtoadroad.skidmark.text.Slugifier.slugify
import com.deadtoadroad.skidmark.time.dateFormatter

object PostWriter {
  private type Component = (Option[String], Triplet, Post) => StringBuilder => StringBuilder

  def apply(homeUrlPath: Option[String], triplet: Triplet, post: Post): String =
    Function.chain(Seq(
      header,
      body,
      footer
    ).map(_ (homeUrlPath, triplet, post)))(new StringBuilder).result()

  private lazy val header: Component = (homeUrlPath, triplet, post) => stringBuilder =>
    Function.chain(Seq(
      links,
      nl,
      title,
      nl,
      details
    ).map(_ (homeUrlPath, triplet, post)))(stringBuilder)

  private lazy val body: Component = (homeUrlPath, triplet, post) => stringBuilder =>
    Function.chain(Seq(
      nl,
      text,
      nl,
      comments,
    ).map(_ (homeUrlPath, triplet, post)))(stringBuilder)

  private lazy val footer: Component = (homeUrlPath, triplet, post) => stringBuilder =>
    Function.chain(Seq(
      nl,
      links,
      nl,
      blurb
    ).map(_ (homeUrlPath, triplet, post)))(stringBuilder)

  private lazy val links: Component = (homeUrlPath, triplet, _) => stringBuilder =>
    List(
      homeUrlPath.map(p => s"**[Home]($p)**<br/>"),
      triplet.previousPost.map(p => s"**Previous: [${p.title}](${p.urlPath})**<br/>"),
      triplet.nextPost.map(p => s"**Next: [${p.title}](${p.urlPath})**")
    )
      .flatten
      .foldLeft(stringBuilder)((sb, s) => sb.appendLine(s))

  private lazy val title: Component = (_, _, post) => stringBuilder =>
    stringBuilder
      .appendLine(s"# ${post.title}")

  private lazy val details: Component = (_, triplet, post) => stringBuilder =>
    stringBuilder
      .appendLine("<dl>")
      .indent(2).appendLine("<dt>Author</dt>")
      .indent(2).appendLine(s"<dd>${post.author}</dd>")
      .indent(2).appendLine("<dt>Published</dt>")
      .indent(2).appendLine(s"<dd>${triplet.post.publishedOn.format(dateFormatter)}</dd>")
      .indent(2).appendLine("<dt>Tags</dt>")
      .indent(2).appendLine(s"<dd>${post.tags.map(slugify).mkString(", ")}</dd>")
      .appendLine("</dl>")
      .appendLine()
      .appendLine("<hr/>")

  private lazy val text: Component = (_, _, post) => stringBuilder =>
    stringBuilder
      .appendLine(post.text)

  private lazy val comments: Component = (_, _, post) => stringBuilder => {
    stringBuilder
      .appendLine("<hr/>")
      .appendLine()
      .appendLine(s"## Comments")
    val comments = post.comments
      .filter(comment => comment.vettedOn.nonEmpty)
    if (comments.nonEmpty)
      comments
        .foldLeft(stringBuilder)((sb, c) => comment(c)(sb))
    else
      stringBuilder
        .appendLine()
        .appendLine("No comments.")
  }

  private lazy val comment: Comment => StringBuilder => StringBuilder = comment => stringBuilder =>
    comment.text.split(System.lineSeparator())
      .foldLeft(stringBuilder.appendLine())((sb, l) => sb.appendLine(s"> $l"))
      .appendLine()
      .appendLine("<p align=\"right\">")
      .indent(2).appendLine(s"- ${comment.author}, ${comment.createdOn.format(dateFormatter)}")
      .appendLine("</p>")

  private lazy val blurb: Component = (_, _, _) => stringBuilder =>
    stringBuilder
      .appendLine("*Published with [Skidmark](https://github.com/deadtoadroad/skidmark#readme).*")


  private lazy val nl: Component = (_, _, _) => stringBuilder =>
    stringBuilder
      .appendLine()

  private lazy val noop: Component = (_, _, _) => stringBuilder => stringBuilder
}
