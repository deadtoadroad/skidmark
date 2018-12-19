Skidmark is a blog API that:

* Records state in a [Git](https://git-scm.com) based event store, locally and off-server.
* Reactively maintains blog files, which by default are [Markdown](https://daringfireball.net/projects/markdown/) files in a Git repository for publishing on Git hosting services like [GitHub](https://github.com).

Skidmark offers (or will offer) the following:

* An API usable by admins, authors and readers from anywhere and with any device.
* Freedom from traditional database maintenance. The only "backup" to worry about is the Git based event store; with that everything else can be regenerated.
* Scalable and distributed content hosting through services like GitHub.

To see Skidmark in action, check out the [demo](https://github.com/deadtoadroad/skidmark-demo#readme).

## Limitations

Skidmark is best suited for low write activity blogs due to the following limitations:

<dl>
  <dt>Single Instance</dt>
  <dd>
    Because Skidmark is dependent on a local file system for persistence, it's not currently possible to scale Skidmark's API by adding extra servers or containers.
  </dd>
  <dt>Single Threaded</dt>
  <dd>
    Skidmark uses a single threaded dispatcher to avoid incorrectly sequenced events in the event store and data overwrites in the query database. It may be possible to develop a more granular locking strategy later, but it's not currently a priority.
  </dd>
</dl>

*These limitations only affect the Skidmark API. The blog content itself is highly accessible when hosted on services like GitHub.*

## Components

Skidmark currently consists of the following components:

<dl>
  <dt>API</dt>
  <dd>
    Through the API, administrators, authors and readers can issue commands or actions against the blog and perform limited queries.
  </dd>
  <dt>CQRS</dt>
  <dd>
    Skidmark uses a small CQRS engine for assembling aggregates from events, validating commands, and publishing new events.
  </dd>
  <dt>File Event Store</dt>
  <dd>
    The event store is used to record the previous and current state of all aggregates and entities. To avoid database maintenance for an infrequently used blog (the original purpose of this project), events are stored in flat files and persisted off-server via Git.
  </dd>
  <dt>File Query Database</dt>
  <dd>
    A subscriber listens for events and updates the query database. The query database is used to record the current state of all aggregates and entities. Being file based the query database is also persisted off-server via Git (not strictly necessary).
  </dd>
  <dt>Blog Writer</dt>
  <dd>
    A subscriber listens for events and uses a blog writer to maintain the Markdown files that represent the published blog.
  </dd>
  <dt>Git Persistence</dt>
  <dd>
    A subscriber listens for events and commits file changes to one or more local Git repositories.
  </dd>
</dl>

## Next Steps

A short list of things still to do:

* Authentication and authorisation.
* Split the event store, query database and blog files into their own Git repositories. The event store and query database should only be persisted to private repositories once authentication is implemented.
* More API methods, including queries.
* Diffs for text update events (posts and comments).
* Image attachments for posts.
* Post summaries.
* Tag summaries.
* Substitution of alternative components, like a custom event store, query database or blog writer.
* Adapt the existing blog writer for [GitHub Pages](https://pages.github.com), or create a new blog writer for that purpose.
