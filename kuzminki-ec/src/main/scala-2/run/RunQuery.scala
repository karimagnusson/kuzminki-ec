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

import scala.concurrent.{Future, ExecutionContext}
import kuzminki.api.Kuzminki
import kuzminki.render.{RenderedQuery, JoinArgs}


trait RunQuery[R] {

  def render: RenderedQuery[R]

  def run(
    implicit db: Kuzminki,
             ec: ExecutionContext
  ): Future[List[R]] = db.query(render)

  def runHead(
    implicit db: Kuzminki,
             ec: ExecutionContext
  ): Future[R] = db.queryHead(render)

  def runHeadOpt(
    implicit db: Kuzminki,
             ec: ExecutionContext
  ): Future[Option[R]] = db.queryHeadOpt(render)

  def runAs[T](
    implicit transform: R => T,
             db: Kuzminki,
             ec: ExecutionContext
  ): Future[List[T]] = db.queryAs(render, transform)

  def runHeadAs[T](
    implicit transform: R => T,
             db: Kuzminki,
             ec: ExecutionContext
  ): Future[T] = db.queryHeadAs(render, transform)

  def runHeadOptAs[T](
    implicit transform: R => T,
             db: Kuzminki,
             ec: ExecutionContext
  ): Future[Option[T]] = db.queryHeadOptAs(render, transform)

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
    implicit db: Kuzminki,
             ec: ExecutionContext
  ): Future[List[R]] = db.query(render(params))

  def runHead(params: P)(
    implicit db: Kuzminki,
             ec: ExecutionContext
  ): Future[R] = db.queryHead(render(params))

  def runHeadOpt(params: P)(
    implicit db: Kuzminki,
             ec: ExecutionContext
  ): Future[Option[R]] = db.queryHeadOpt(render(params))

  def runAs[T](params: P)(
    implicit transform: R => T,
             db: Kuzminki,
             ec: ExecutionContext
  ): Future[List[T]] = db.queryAs(render(params), transform)

  def runHeadAs[T](params: P)(
    implicit transform: R => T,
             db: Kuzminki,
             ec: ExecutionContext
  ): Future[T] = db.queryHeadAs(render(params), transform)

  def runHeadOptAs[T](params: P)(
    implicit transform: R => T,
             db: Kuzminki,
             ec: ExecutionContext
  ): Future[Option[T]] = db.queryHeadOptAs(render(params), transform)

  def printSql: RunQueryParams[P, R] = {
    println(statement)
    this
  }
  
  def printSqlAndArgs(params: P): RunQueryParams[P, R] =
    render(params).printStatementAndArgs(this)
  
  def printSqlWithArgs(params: P): RunQueryParams[P, R] =
    render(params).printStatementWithArgs(this)
}


















