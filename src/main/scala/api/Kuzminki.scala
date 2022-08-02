/*
* Copyright 2021 Kári Magnússon
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package kuzminki.api

import java.util.Properties
import scala.concurrent.{Future, ExecutionContext}
import com.typesafe.config.{Config, ConfigFactory}
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

import kuzminki.api._
import kuzminki.jdbc.JdbcExecutor
import kuzminki.render.{
  RenderedQuery,
  RenderedOperation
}

import akka.actor._
import akka.Done


object Kuzminki {

  Class.forName("org.postgresql.Driver")

  private def createPool(conf: Config): HikariDataSource = {
    val props = new Properties()
    props.setProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource")
    props.setProperty("dataSource.user", conf.getString("user"))
    props.setProperty("dataSource.password", conf.getString("password"))
    props.setProperty("dataSource.databaseName", conf.getString("db"))
    props.setProperty("dataSource.serverName", conf.getString("host"))
    props.setProperty("dataSource.portNumber", conf.getString("port"))
    new HikariDataSource(new HikariConfig(props))
  }

  def forConfig(system: ActorSystem, confName: String, dispatcherName: String): Kuzminki = {
    val baseConf = ConfigFactory.load()
    val conf = baseConf.getConfig(confName)
    val pool = createPool(conf)
    new DefaultApi(pool, system, dispatcherName)
  }

  def create(system: ActorSystem): Kuzminki =
    forConfig(system, "kuzminki", "akka.actor.kuzminki-dispatcher")
}


trait Kuzminki {

  def query[R](render: => RenderedQuery[R]): Future[List[R]]

  def queryAs[R, T](render: => RenderedQuery[R], transform: R => T): Future[List[T]]

  def queryHead[R](render: => RenderedQuery[R]): Future[R]

  def queryHeadAs[R, T](render: => RenderedQuery[R], transform: R => T): Future[T]

  def queryHeadOpt[R](render: => RenderedQuery[R]): Future[Option[R]]

  def queryHeadOptAs[R, T](render: => RenderedQuery[R], transform: R => T): Future[Option[T]]

  def exec(render: => RenderedOperation): Future[Unit]

  def execNum(render: => RenderedOperation): Future[Int]

  def close: Future[Unit]
}


private class DefaultApi(pool: HikariDataSource, system: ActorSystem, dispatcherName: String) extends Kuzminki {

  implicit val ec = system.dispatchers.defaultGlobalDispatcher

  val jdbc = new JdbcExecutor(pool, system.dispatchers.lookup(dispatcherName))

  def query[R](render: => RenderedQuery[R]): Future[List[R]] =
    jdbc.query(render)

  def queryAs[R, T](render: => RenderedQuery[R], transform: R => T): Future[List[T]] = 
    jdbc.query(render).map(_.map(transform))

  def queryHead[R](render: => RenderedQuery[R]): Future[R] =
    jdbc.query(render).map(_.head)

  def queryHeadAs[R, T](render: => RenderedQuery[R], transform: R => T): Future[T] =
    jdbc.query(render).map(_.head).map(transform)

  def queryHeadOpt[R](render: => RenderedQuery[R]): Future[Option[R]] =
    jdbc.query(render).map(_.headOption)

  def queryHeadOptAs[R, T](render: => RenderedQuery[R], transform: R => T): Future[Option[T]] =
    jdbc.query(render).map(_.headOption.map(transform))

  def exec(render: => RenderedOperation): Future[Unit] =
    jdbc.exec(render)

  def execNum(render: => RenderedOperation): Future[Int] =
    jdbc.execNum(render)

  def close: Future[Unit] = {
    pool.close()
    Future.successful(())
  }
}



























