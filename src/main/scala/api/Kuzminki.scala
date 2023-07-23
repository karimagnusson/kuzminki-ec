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
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

import kuzminki.api._
import kuzminki.jdbc.JdbcExecutor
import kuzminki.render.{
  RenderedQuery,
  RenderedOperation
}


object Kuzminki {

  Class.forName("org.postgresql.Driver")

  def create(conf: DbConfig, dbContext: ExecutionContext)
            (implicit ec: ExecutionContext): Kuzminki = {
    new DefaultApi(conf, dbContext)(ec)
  }
}


trait Kuzminki {

  def getEc: ExecutionContext

  def query[R](render: => RenderedQuery[R]): Future[List[R]]

  def queryAs[R, T](render: => RenderedQuery[R], transform: R => T): Future[List[T]]

  def queryHead[R](render: => RenderedQuery[R]): Future[R]

  def queryHeadAs[R, T](render: => RenderedQuery[R], transform: R => T): Future[T]

  def queryHeadOpt[R](render: => RenderedQuery[R]): Future[Option[R]]

  def queryHeadOptAs[R, T](render: => RenderedQuery[R], transform: R => T): Future[Option[T]]

  def exec(render: => RenderedOperation): Future[Unit]

  def execNum(render: => RenderedOperation): Future[Int]

  def execList(stms: Seq[RenderedOperation]): Future[Unit]

  def close: Future[Unit]
}


private class DefaultApi(conf: DbConfig, dbContext: ExecutionContext)
                        (implicit ec: ExecutionContext) extends Kuzminki {

  def getEc = ec

  val pool = new JdbcExecutor(
    new HikariDataSource(new HikariConfig(conf.props)),
    dbContext
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

  def execList(stms: Seq[RenderedOperation]): Future[Unit] =
    pool.execList(stms)

  def close: Future[Unit] = {
    pool.close()
    Future.successful(())
  }
}












