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

import scala.concurrent.{Future, ExecutionContext}
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

import kuzminki.jdbc.JdbcExecutor
import kuzminki.render.{
  RenderedQuery,
  RenderedOperation
}


object Kuzminki {

  Class.forName("org.postgresql.Driver")

  def create(conf: DbConfig, dbContext: ExecutionContext): Kuzminki = {
    new DefaultApi(conf, dbContext)
  }

  def createSplit(masterConf: DbConfig, slaveConf: DbConfig, dbContext: ExecutionContext): Kuzminki = {
    new SplitApi(masterConf, slaveConf, dbContext)
  }
}


trait Kuzminki {

  def query[R](render: => RenderedQuery[R])(implicit ec: ExecutionContext): Future[List[R]]

  def queryAs[R, T](render: => RenderedQuery[R], transform: R => T)(implicit ec: ExecutionContext): Future[List[T]] = 
    query(render).map(_.map(transform))

  def queryHead[R](render: => RenderedQuery[R])(implicit ec: ExecutionContext): Future[R] =
    query(render).map(_.head)

  def queryHeadAs[R, T](render: => RenderedQuery[R], transform: R => T)(implicit ec: ExecutionContext): Future[T] =
    query(render).map(_.head).map(transform)

  def queryHeadOpt[R](render: => RenderedQuery[R])(implicit ec: ExecutionContext): Future[Option[R]] =
    query(render).map(_.headOption)

  def queryHeadOptAs[R, T](render: => RenderedQuery[R], transform: R => T)(implicit ec: ExecutionContext): Future[Option[T]] =
    query(render).map(_.headOption.map(transform))

  def exec(render: => RenderedOperation)(implicit ec: ExecutionContext): Future[Unit]

  def execNum(render: => RenderedOperation)(implicit ec: ExecutionContext): Future[Int]

  def execList(stms: Seq[RenderedOperation])(implicit ec: ExecutionContext): Future[Unit]

  def close: Future[Unit]
}


private class DefaultApi(conf: DbConfig, dbContext: ExecutionContext) extends Kuzminki {

  val pool = new JdbcExecutor(
    new HikariDataSource(new HikariConfig(conf.props)),
    dbContext
  )

  def query[R](render: => RenderedQuery[R])(implicit ec: ExecutionContext) =
    pool.query(render)

  def exec(render: => RenderedOperation)(implicit ec: ExecutionContext) =
    pool.exec(render)

  def execNum(render: => RenderedOperation)(implicit ec: ExecutionContext) =
    pool.execNum(render)

  def execList(stms: Seq[RenderedOperation])(implicit ec: ExecutionContext) =
    pool.execList(stms)

  def close: Future[Unit] = {
    pool.close()
    Future.successful(())
  }
}


private class SplitApi(masterConf: DbConfig, slaveConf: DbConfig, dbContext: ExecutionContext) extends Kuzminki {

  val setPool = new JdbcExecutor(
    new HikariDataSource(new HikariConfig(masterConf.props)),
    dbContext
  )

  val getPool = new JdbcExecutor(
    new HikariDataSource(new HikariConfig(slaveConf.props)),
    dbContext
  )

  def router(stm: String) = stm.substring(0, 6).toUpperCase match {
    case "SELECT" => getPool
    case _ => setPool
  }

  def query[R](render: => RenderedQuery[R])(implicit ec: ExecutionContext) = {
    val stm = render
    router(stm.statement).query(stm)
  }

  def exec(render: => RenderedOperation)(implicit ec: ExecutionContext) =
    setPool.exec(render)

  def execNum(render: => RenderedOperation)(implicit ec: ExecutionContext) =
    setPool.execNum(render)

  def execList(stms: Seq[RenderedOperation])(implicit ec: ExecutionContext) =
    setPool.execList(stms)

  def close: Future[Unit] = {
    setPool.close()
    getPool.close()
    Future.successful(())
  }
}












