[![license](https://img.shields.io/github/license/rdbc-io/rdbc.svg?style=flat-square)](https://github.com/rdbc-io/rdbc/blob/master/LICENSE)
# kuzminki-akka

Kuzminki is query builder and access library for PostgreSQL written in Scala.
This version is for Akka.

Kuzminki is written for those who like SQL. Queries are written with the same logic you write SQL statements. As a result the code is easy to read and memorise while the resulting SQL statement is predictable.

This library is also available for [ZIO](https://zio.dev/) [kuzminki-zio](https://github.com/karimagnusson/kuzminki-zio)  
And for ZIO 2 [kuzminki-zio-2](https://github.com/karimagnusson/kuzminki-zio-2)

See full documentation at [https://kuzminki.io/](https://kuzminki.io/)

Release 0.9.4-RC4 adds the following features:
- Support for jsonb field
- Support for transactions
- Improved ability to cache statements
- Select row as JSON string
- Debug, print query
- Insert, update null values
- Timestamp, Date, Time methods

#### Sbt
```sbt
// compiled for Scala 2.13.8
libraryDependencies += "io.github.karimagnusson" % "kuzminki-ec" % "0.9.4-RC4"
```

This version of the library can be used Akka but does not depend on it.

#### Example
```scala
import akka.actor._
import akka.util.Timeout
import scala.concurrent.Await
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
    system.dispatchers.lookup("kuzminki-dispatcher")
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
    
    _ <- ZIO.foreach(clients) {
      case (id, username, age) =>
        putStrLn(s"$id $username $age")
    }
  } yield ()

  job.onComplete { _ =>
    db.close()
    system.terminate()
  }

  Await.result(job, 10.seconds)
}
```

#### Dispatcher
Add a dispatcher to your config. Unless specified, kuzminki-dispatcher will be used.
```sbt
kuzminki-dispatcher {
  type = "Dispatcher"
  executor = "thread-pool-executor"
  thread-pool-executor {
    fixed-pool-size = 10
  }
  throughput = 1
}
```




