[![Twitter URL](https://img.shields.io/twitter/url/https/twitter.com/bukotsunikki.svg?style=social&label=Follow%20%40kuzminki_lib)](https://twitter.com/kuzminki_lib)

# Kuzminki

A PostgreSQL query builder for Scala that mirrors SQL structure directly in code.

```scala
// available for Scala 2.13 and Scala 3
libraryDependencies += "io.github.karimagnusson" %% "kuzminki-ec" % "0.9.5"
```

## Why Kuzminki?

Most query builders abstract SQL behind collection-like APIs. Kuzminki takes the opposite approach: your Scala code reads like the SQL it generates. This makes complex queries readable and the API intuitive - you already know SQL.

```scala
sql
  .select(client)
  .cols3(_.all)
  .where(_.age > 25)
  .orderBy(_.username.asc)
  .limit(5)
  .run
```

## About this version

This is the ExecutionContext version of Kuzminki (`kuzminki-ec`). It uses Scala's standard `ExecutionContext` and `Future`, making it compatible with any Scala application - whether you're using Pekko, Akka, Play Framework, or plain Scala.

For streaming support with Pekko, add [kuzminki-pekko](https://github.com/karimagnusson/kuzminki-pekko).

For ZIO integration, see [kuzminki-zio-2](https://github.com/karimagnusson/kuzminki-zio-2).

## Features

- Works with any ExecutionContext - Pekko, Akka, Play, or plain Scala
- Full JSONB support - query, update, and return rows as JSON
- Array field operations
- Subqueries in WHERE clauses and SELECT columns
- Streaming with Pekko (via kuzminki-pekko)
- Statement caching for JDBC-level performance
- Transactions for bulk and mixed operations
- Type-safe throughout - no wildcard types with unclear errors

## Postgres by design

Kuzminki focuses exclusively on PostgreSQL rather than targeting lowest-common-denominator SQL. This allows deep support for Postgres-specific features like JSONB and arrays. Works with Postgres-compatible databases like CockroachDB.

## Example

```scala
import org.apache.pekko.actor._
import kuzminki.api._

object ExampleApp extends App {

  implicit val system = ActorSystem()
  implicit val ec = system.dispatcher
  implicit val db = Kuzminki.create(
    DbConfig.forDb("company"),
    system.dispatchers.lookup("pekko.actor.default-blocking-io-dispatcher")
  )

  class Client extends Model("client") {
    val id = column[Int]("id")
    val username = column[String]("username")
    val age = column[Int]("age")
    def all = (id, username, age)
  }

  val client = Model.get[Client]

  val job = for {
    _ <- sql
      .insert(client)
      .cols2(t => (t.username, t.age))
      .values(("Joe", 35))
      .run
    
    _ <- sql
      .update(client)
      .set(_.age ==> 24)
      .where(_.id === 4)
      .run
    
    _ <- sql.delete(client).where(_.id === 7).run
    
    clients <- sql
      .select(client)
      .cols3(_.all)
      .where(_.age > 25)
      .limit(5)
      .run
    
  } yield clients

  job.onComplete { _ =>
    db.close()
    system.terminate()
  }
}
```

## Resources

- [Full documentation](https://kuzminki.kotturinn.com/)
- [Play Framework demo project](https://github.com/karimagnusson/kuzminki-play-demo)
- [Pekko streaming support](https://github.com/karimagnusson/kuzminki-pekko)
- [ZIO version](https://github.com/karimagnusson/kuzminki-zio-2)

Please report bugs if you find them and feel free to DM me on Twitter if you have any questions.