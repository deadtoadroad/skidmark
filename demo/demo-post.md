Skidmark is a blog API that:

* Records state in a [Git](https://git-scm.com) based event store, locally and off-server.
* Reactively maintains blog files, which by default are [Markdown](https://daringfireball.net/projects/markdown/) files in a Git repository for publishing on Git hosting services like [GitHub](https://github.com).

This post demonstrates usage of Skidmark. To read more about Skidmark itself, visit the [project](https://github.com/deadtoadroad/skidmark#readme).

## Setup

First, clone the [Skidmark](https://github.com/deadtoadroad/skidmark) repository and run the project from the root directory using [the Scala build tool or a compatible IDE](https://www.scala-lang.org/download/).

```bash
sbt run
```

This will start the API which will listen on http://localhost:8080 by default and will also create a sibling repository at `../skidmark-demo` in which to record state and generate Markdown files.

There are some tools that may be useful in following along with the demo:

* [HTTPie](https://httpie.org) (although curl or another REST client will also do)

It's also recommended to create a repository on GitHub and push the `skidmark-demo` repository there after each step to see the published result. At the end of this demo the repository will be almost identical to https://github.com/deadtoadroad/skidmark-demo.

## Create a Post

Create the demo post and give it published status.

```bash
http post :8080/post \
  id=1235048b-1a27-4722-848f-1a74feec8132 \
  title='Demo Post' \
  author=deadtoadroad \
  tags:='["demo", "skidmark", "scala"]' \
  text=@demo/demo-post.md

http put :8080/post/publish \
  id=1235048b-1a27-4722-848f-1a74feec8132 \
  version=0 \
  publishedOn=2018-12-17T00:00:00.000+11:00[Australia/Sydney]
```

*The `id` field in the create commands is optional; if it's not provided the API will generate one instead. One is specified here to make scripting easier.*

The demo post will now be visible in the following locations:

* [/readme.md](/readme.md#readme)
* [/posts/2018/12/demo-post/readme.md](/posts/2018/12/demo-post/readme.md#readme)

Each published post gets its own location with the most recent one also displayed on the home page.

## Hello World

Create another post, this time a "Hello World" post with an earlier publish date.

```bash
http post :8080/post \
  id=c5d38190-f6bf-481b-bbe4-da40f0be3462 \
  title='Hello World' \
  author=deadtoadroad \
  tags:='["hello-world", "skidmark", "scala"]' \
  text=@demo/hello-world.md

http put :8080/post/publish \
  id=c5d38190-f6bf-481b-bbe4-da40f0be3462 \
  version=0 \
  publishedOn=2018-12-16T00:00:00.000+11:00[Australia/Sydney]
```

The [home page](/readme.md#readme) still shows the demo post as it has the most recent published date, but this time there is also a link in the header and footer to the earlier "Hello World" post:

* [/posts/2018/12/hello-world/readme.md](/posts/2018/12/hello-world/readme.md#readme)

For Skidmark to generate these Markdown files, it uses an [event store](/_es/_all) embedded in the Git repo to record state. To illustrate, unpublish the "Hello World" post.

```bash
http put :8080/post/unpublish \
  id=c5d38190-f6bf-481b-bbe4-da40f0be3462 \
  version=1
```

The unpublish command generated an event the blog writer was listening for and reacted to by unpublishing the "Hello World" post. On its own the unpublish event doesn't have enough information to determine the path of the file to delete, but by using the [event store](/_es/_all) and the derived [query database](/_db), Skidmark can figure it out. The "Hello World" post is now gone.

Bring back the "Hello World" post with another publish command.

```bash
http put :8080/post/publish \
  id=c5d38190-f6bf-481b-bbe4-da40f0be3462 \
  version=2 \
  publishedOn=2018-12-16T00:00:00.000+11:00[Australia/Sydney]
```

## Create a Comment

Create a comment on the "Hello World" post.

```bash
http post :8080/comment \
  id=b9d9aa34-7588-4f71-a72a-deb4a664da32 \
  postId=c5d38190-f6bf-481b-bbe4-da40f0be3462 \
  author=anon \
  text='Nice, but it needs more sparkles.'

http put :8080/admin/comment/vet \
  id=b9d9aa34-7588-4f71-a72a-deb4a664da32 \
  version=0
```

The comment is asking for more sparkles. How could anyone say no?

```bash
cat demo/hello-world.md | \
  sed 's/:sparkles:$/:sparkles:<br\/>/' | \
  cat - demo/more-sparkles.md > \
  demo/hello-world-with-more-sparkles.md

http put :8080/post \
  id=c5d38190-f6bf-481b-bbe4-da40f0be3462 \
  version=3 \
  text=@demo/hello-world-with-more-sparkles.md

http post :8080/comment \
  id=62bc0151-b668-4795-b110-2941d0bf711b \
  postId=c5d38190-f6bf-481b-bbe4-da40f0be3462 \
  author=deadtoadroad \
  text='There you go.'

http put :8080/admin/comment/vet \
  id=62bc0151-b668-4795-b110-2941d0bf711b \
  version=0

http post :8080/comment \
  id=086125de-0f2d-49c4-b2d8-e362cf1ff508 \
  postId=c5d38190-f6bf-481b-bbe4-da40f0be3462 \
  author=anon \
  text='Great, thanks.'

http put :8080/admin/comment/vet \
  id=086125de-0f2d-49c4-b2d8-e362cf1ff508 \
  version=0
```

Check out [Skidmark](https://github.com/deadtoadroad/skidmark#readme) to read more.
