[![Twitter URL](https://img.shields.io/twitter/url/https/twitter.com/bukotsunikki.svg?style=social&label=Follow%20%40kuzminki_lib)](https://twitter.com/kuzminki_lib)

# kuzminki-ec

Kuzminki is feature-rich query builder and access library for PostgreSQL written in Scala. It focuses on productivity by providing readable transparent syntax and making Postgres features available through the API.

The main goal of the latest version 0.9.5-RC4 is to provide support for Scala 3. It also has some import improvements and although it is a release candidate, it should be chosen over 0.9.4. Please report bugs if you find them and feel free to DM me on Twitter if you have any questions.

This version relies only on Scala ExecutionContext and is suitable for use with Pekko and Akka. To add support for streaming data to and from the database with Pekko and Akka streaming, add this library [kuzminki-ec-stream](https://github.com/karimagnusson/kuzminki-ec-stream)

Take a look at [kuzminki-play-demo](https://github.com/karimagnusson/kuzminki-play-demo) for an example of a REST API using this library and [Play](https://github.com/playframework/playframework).

This library is also available for [ZIO](https://github.com/karimagnusson/kuzminki-zio-2)

See full documentation at [https://kuzminki.info/](https://kuzminki.info/)

This latest version adds some methods to the API (see at the bottom).

#### Sbt
```sbt
// available for Scala 2.13 and Scala 3
libraryDependencies += "io.github.karimagnusson" %% "kuzminki-ec" % "0.9.5-RC4"
```

#### Example
```scala
import akka.actor._
import kuzminki.api._

object ExampleApp extends App {

  class Client extends Model("client") {
    val id = column[Int]("id")
    val username = column[String]("username")
    val age = column[Int]("age")
    def all = (id, username, age)
  }

  val client = Model.get[Client]

  implicit val system = ActorSystem()
  implicit val ec = system.dispatcher
  implicit val db = Kuzminki.create(
    DbConfig.forDb("company"),
    system.dispatchers.lookup("akka.actor.default-blocking-io-dispatcher")
  )

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

#### About Kuzminki
Kuzminki is feature-rich query builder and access library for PostgreSQL written in Scala. The approach of this library is to write SQL statements in Scala with the same structure as SQL is written. Rather than having the API natural to Scala logic, for example similar to how you work with collections, this approach seeks to make the Scala code as similar to the resulting SQL statement as possible. The goal of this approach is to make it easy to read the code and understand the resulting SQL statement. It should also make the API intuitive and easy to memorise. With this approach, it becomes practical to write quite complicated queries while still being able to understand what they do. For example subqueries and queries where column values are modified. With a different approach, the user would have to learn the libraries own unique logic. But since the logic of SQL is already known, such complexity becomes practical. With a feature-rich API, the user can find solutions and avoid writing raw SQL statements that are not checked by the compiler. The goal of this project is to offer a productive and readable API to work with Postgresql and take advantage of its many features.

#### Features
Kuzminki supports jsonb and array fields. When doing insert, update or delete, it offers one or more column values to be returned. It offers many options for searching and returning datetime column values. It supports subqueries, both as a condition in a search and collect values to be returned to the client. Transactions are supported, both as a way to do bulk-operations and to do multiple different operations. Rows can be delivered to the client as, tuples, case classes or vectors. Data for insert can be a tuple or a case class. Types are checked as much as possible and wild-card types that result in unclear errors are not used.

#### JSON
Postgresql comes with a jsonb field to store and query data in JSON format. Being able to take advantage of the jsonb field opens up great flexibility in organizing your data. You can work with structured and unstructured data to get the best of both worlds. Kuzminki offers extensive support for querying and updating jsonb fields. Also, Kuzminki offers the ability to return rows as JSON strings. This can be useful when, for example, you need to service JSON directly to the client you can do so without having to transform the data. You can organise how your object is formed from the table columns. For example if you need some columns to be in a nested object. If you need to create a big object from multiple tables, you can do so with a single query using subqueries. Take a look at the [kuzminki-play-demo](https://github.com/karimagnusson/kuzminki-play-demo) for examples of these features.

#### Performance
Statements can be cached for better performance and reusability. This means that the SQL string is created only once. Execution time should be the same as running a raw statement with JDBC. All statements can be cached except for SELECT statements with optional WHERE arguments.

#### Only Postgres
Kuzminki supports only Postgresql. It could be adapted for use with other databases if there is interest in that. But given that it has support for many postgres specific features, support for another database would require it’s own project rather than a size fits all approach. Therefore, at the moment the goal is to deliver a good library for Postgres. That being said, there are other Postgres compatible databases that work with Kuzminki. For example CockroachDB. For those looking to scale up, it might be a good choice.

#### Version 0.9.5-RC4
The latest version adds compiler checked methods to use types.

```scala
case class User(id: Int, name: String, age: Int)
case class UserInfo(name: String, age: Int)

// select

sql
  .select(user)
  .cols3(t => (
    t.id,
    t.name,
    t.age
  ))
  .where(_.age > 25)
  .orderBy(_.age.asc)
  .limit(10)
  .runType[User] // .runHeadType[User] .runHeadOptType[User]

// insert

sql
  .insert(user)
  .cols2(t => (
    t.name,
    t.age
  ))
  .valuesType(UserInfo("Bob", 25))
  .returning3(t => (
    t.actorId,
    t.firstName,
    t.lastName
  ))
  .runHeadType[User]

// insert cache

val stm = sql
  .insert(user)
  .cols2(t => (
    t.name,
    t.age
  ))
  .cache

stm.runListType(List(
  UserInfo("Bob", 25),
  UserInfo("Jane", 23)
))

// streaming

.select(oldUser)
  .cols2(t => (
    t.name,
    t.age
  ))
  .all
  .orderBy(_.id.asc)
  .streamType[UserInfo]
  .run(
    sql
      .insert(user)
      .cols2(t => (
        t.name,
        t.age
      ))
      .cache
      .asTypeSink[UserInfo]
  )
```






