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
import scala.concurrent.Future
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import akka.actor._

import kuzminki.api._
import kuzminki.jdbc.JdbcExecutor
import kuzminki.render.{
  RenderedQuery,
  RenderedOperation
}


object Kuzminki {

  Class.forName("org.postgresql.Driver")

  def create(conf: DbConfig)(implicit system: ActorSystem): Kuzminki =
    new DefaultApi(conf, system)

  def createSplit(getConf: DbConfig, setConf: DbConfig)(implicit system: ActorSystem): Kuzminki =
    new SplitApi(getConf, setConf, system)
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


private class DefaultApi(conf: DbConfig, system: ActorSystem) extends Kuzminki {

  implicit val ec = system.dispatchers.defaultGlobalDispatcher

  val pool = new JdbcExecutor(
    new HikariDataSource(new HikariConfig(conf.props)),
    system.dispatchers.lookup(conf.dispatcher)
  )

  def query[R](render: => RenderedQuery[R]): Future[List[R]] =
    pool.query(render)

  def queryAs[R, T](render: => RenderedQuery[R], transform: R => T): Future[List[T]] = 
    pool.query(render).map(_.map(transform))

  def queryHead[R](render: => RenderedQuery[R]): Future[R] =
    pool.query(render).map(_.head)

  def queryHeadAs[R, T](render: => RenderedQuery[R], transform: R => T): Future[T] =
    pool.query(render).map(_.head).map(transform)

  def queryHeadOpt[R](render: => RenderedQuery[R]): Future[Option[R]] =
    pool.query(render).map(_.headOption)

  def queryHeadOptAs[R, T](render: => RenderedQuery[R], transform: R => T): Future[Option[T]] =
    pool.query(render).map(_.headOption.map(transform))

  def exec(render: => RenderedOperation): Future[Unit] =
    pool.exec(render)

  def execNum(render: => RenderedOperation): Future[Int] =
    pool.execNum(render)

  def close: Future[Unit] = {
    pool.close
    Future.successful(())
  }
}


private class SplitApi(getConf: DbConfig, setConf: DbConfig, system: ActorSystem) extends Kuzminki {

  implicit val ec = system.dispatchers.defaultGlobalDispatcher

  val getPool = new JdbcExecutor(
    new HikariDataSource(new HikariConfig(getConf.props)),
    system.dispatchers.lookup(getConf.dispatcher)
  )

  val setPool = new JdbcExecutor(
    new HikariDataSource(new HikariConfig(setConf.props)),
    system.dispatchers.lookup(setConf.dispatcher)
  )

  def query[R](render: => RenderedQuery[R]): Future[List[R]] =
    getPool.query(render)

  def queryAs[R, T](render: => RenderedQuery[R], transform: R => T): Future[List[T]] = 
    getPool.query(render).map(_.map(transform))

  def queryHead[R](render: => RenderedQuery[R]): Future[R] =
    getPool.query(render).map(_.head)

  def queryHeadAs[R, T](render: => RenderedQuery[R], transform: R => T): Future[T] =
    getPool.query(render).map(_.head).map(transform)

  def queryHeadOpt[R](render: => RenderedQuery[R]): Future[Option[R]] =
    getPool.query(render).map(_.headOption)

  def queryHeadOptAs[R, T](render: => RenderedQuery[R], transform: R => T): Future[Option[T]] =
    getPool.query(render).map(_.headOption.map(transform))

  def exec(render: => RenderedOperation): Future[Unit] =
    setPool.exec(render)

  def execNum(render: => RenderedOperation): Future[Int] =
    setPool.execNum(render)

  def close: Future[Unit] = {
    getPool.close
    setPool.close
    Future.successful(())
  }
}




























