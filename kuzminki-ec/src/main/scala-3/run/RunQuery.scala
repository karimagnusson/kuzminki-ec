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

package kuzminki.run

import java.sql.SQLException
import scala.concurrent.{Future, ExecutionContext}
import scala.deriving.Mirror.ProductOf
import kuzminki.api.Kuzminki
import kuzminki.render.{RenderedQuery, JoinArgs}


trait RunQuery[R] {

  def render: RenderedQuery[R]

  def run(
    using db: Kuzminki,
          ec: ExecutionContext
  ): Future[List[R]] =
    db.query(render)

  def runHead(
    using db: Kuzminki,
          ec: ExecutionContext
  ): Future[R] =
    db.queryHead(render)

  def runHeadOpt(
    using db: Kuzminki,
          ec: ExecutionContext
  ): Future[Option[R]] =
    db.queryHeadOpt(render)

  def runAs[T](
    using transform: R => T,
          db: Kuzminki,
          ec: ExecutionContext
  ): Future[List[T]] =
    db.queryAs(render, transform)

  def runHeadAs[T](
    using transform: R => T,
          db: Kuzminki,
          ec: ExecutionContext
  ): Future[T] =
    db.queryHeadAs(render, transform)

  def runHeadOptAs[T](
    using transform: R => T,
          db: Kuzminki,
          ec: ExecutionContext
  ): Future[Option[T]] =
    db.queryHeadOptAs(render, transform)

  def runType[T](
    using mirror: ProductOf[T],
          ev: R <:< mirror.MirroredElemTypes,
          db: Kuzminki,
          ec: ExecutionContext
  ): Future[List[T]] = {
    db.queryAs(render, (r: R) => mirror.fromProduct(r))
  }

  def runHeadType[T](
    using mirror: ProductOf[T],
          ev: R <:< mirror.MirroredElemTypes,
          db: Kuzminki,
          ec: ExecutionContext
  ): Future[T] = {
    db.queryHeadAs(render, (r: R) => mirror.fromProduct(r))
  }

  def runHeadOptType[T](
    using mirror: ProductOf[T],
          ev: R <:< mirror.MirroredElemTypes,
          db: Kuzminki,
          ec: ExecutionContext
  ): Future[Option[T]] = {
    db.queryHeadOptAs(render, (r: R) => mirror.fromProduct(r))
  }

  def printSql: RunQuery[R] =
    render.printStatement(this)

  def printSqlAndArgs: RunQuery[R] =
    render.printStatementAndArgs(this)

  def printSqlWithArgs: RunQuery[R] =
    render.printStatementWithArgs(this)
}


trait RunQueryParams[P, R] extends JoinArgs {

  val statement: String

  def render(params: P): RenderedQuery[R]

  def run(params: P)(
    using db: Kuzminki,
          ec: ExecutionContext
  ): Future[List[R]] = {
    db.query(render(params))
  }

  def runHead(params: P)(
    using db: Kuzminki,
          ec: ExecutionContext
  ): Future[R] = {
    db.queryHead(render(params))
  }

  def runHeadOpt(params: P)(
    using db: Kuzminki,
          ec: ExecutionContext
  ): Future[Option[R]] = {
    db.queryHeadOpt(render(params))
  }

  def runAs[T](params: P)(
    using transform: R => T,
          db: Kuzminki,
          ec: ExecutionContext
  ): Future[List[T]] = {
    db.queryAs(render(params), transform)
  }

  def runHeadAs[T](params: P)(
    using transform: R => T,
          db: Kuzminki,
          ec: ExecutionContext
  ): Future[T] = {
    db.queryHeadAs(render(params), transform)
  }

  def runHeadOptAs[T](params: P)(
    using transform: R => T,
          db: Kuzminki,
          ec: ExecutionContext
  ): Future[Option[T]] = {
    db.queryHeadOptAs(render(params), transform)
  }

  def runType[T](params: P)(
    using mirror: ProductOf[T],
          ev: R <:< mirror.MirroredElemTypes,
          db: Kuzminki,
          ec: ExecutionContext
  ): Future[List[T]] = {
    db.queryAs(render(params), (r: R) => mirror.fromProduct(r))
  }

  def runHeadType[T](params: P)(
    using mirror: ProductOf[T],
          ev: R <:< mirror.MirroredElemTypes,
          db: Kuzminki,
          ec: ExecutionContext
  ): Future[T] = {
    db.queryHeadAs(render(params), (r: R) => mirror.fromProduct(r))
  }

  def runHeadOptType[T](params: P)(
    using mirror: ProductOf[T],
          ev: R <:< mirror.MirroredElemTypes,
          db: Kuzminki,
          ec: ExecutionContext
  ): Future[Option[T]] = {
    db.queryHeadOptAs(render(params), (r: R) => mirror.fromProduct(r))
  }

  def printSql: RunQueryParams[P, R] = {
    println(statement)
    this
  }
  
  def printSqlAndArgs(params: P): RunQueryParams[P, R] =
    render(params).printStatementAndArgs(this)
  
  def printSqlWithArgs(params: P): RunQueryParams[P, R] =
    render(params).printStatementWithArgs(this)
}


















