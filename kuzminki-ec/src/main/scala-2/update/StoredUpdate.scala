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

package kuzminki.update

import scala.concurrent.{Future, ExecutionContext}
import kuzminki.api.Kuzminki
import kuzminki.shape.{ParamConv, RowConv}
import kuzminki.render.{
  RenderedQuery,
  RenderedOperation,
  JoinArgs
}


class StoredUpdate[P1, P2](
  val statement: String,
  args: Vector[Any],
  changes: ParamConv[P1],
  filters: ParamConv[P2]
) extends JoinArgs {

  def render(p1: P1, p2: P2) = RenderedOperation(
    statement,
    joinArgs(args, changes.fromShape(p1) ++ filters.fromShape(p2))
  )

  def run(p1: P1, p2: P2)(
    implicit db: Kuzminki,
             ec: ExecutionContext
  ): Future[Unit] = db.exec(render(p1, p2))

  def runNum(p1: P1, p2: P2)(
    implicit db: Kuzminki,
             ec: ExecutionContext
  ): Future[Int] = db.execNum(render(p1, p2))

  def runList(list: Seq[Tuple2[P1, P2]])(
    implicit db: Kuzminki,
             ec: ExecutionContext
  ): Future[Unit] = db.execList(list.map(p => render(p._1, p._2)))

  def printSql: StoredUpdate[P1, P2] = {
    println(statement)
    this
  }
    
  def printSqlAndArgs(p1: P1, p2: P2): StoredUpdate[P1, P2] =
    render(p1, p2).printStatementAndArgs(this)
    
  def printSqlWithArgs(p1: P1, p2: P2): StoredUpdate[P1, P2] =
    render(p1, p2).printStatementWithArgs(this)
}


class StoredUpdateReturning[P1, P2, R](
  val statement: String,
  args: Vector[Any],
  changes: ParamConv[P1],
  filters: ParamConv[P2],
  rowConv: RowConv[R]
) extends JoinArgs {

  def render(p1: P1, p2: P2) = RenderedQuery(
    statement,
    joinArgs(args, changes.fromShape(p1) ++ filters.fromShape(p2)),
    rowConv
  )

  def run(p1: P1, p2: P2)(
    implicit db: Kuzminki,
             ec: ExecutionContext
  ): Future[List[R]] =
    db.query(render(p1, p2))

  def runHead(p1: P1, p2: P2)(
    implicit db: Kuzminki,
             ec: ExecutionContext
  ): Future[R] =
    db.queryHead(render(p1, p2))

  def runHeadOpt(p1: P1, p2: P2)(
    implicit db: Kuzminki,
             ec: ExecutionContext
  ): Future[Option[R]] =
    db.queryHeadOpt(render(p1, p2))

  def runAs[T](p1: P1, p2: P2)(
    implicit transform: R => T,
             db: Kuzminki,
             ec: ExecutionContext
  ): Future[List[T]] =
    db.queryAs(render(p1, p2), transform)

  def runHeadAs[T](p1: P1, p2: P2)(
    implicit transform: R => T,
             db: Kuzminki,
             ec: ExecutionContext
  ): Future[T] =
    db.queryHeadAs(render(p1, p2), transform)

  def runHeadOptAs[T](p1: P1, p2: P2)(
    implicit transform: R => T,
             db: Kuzminki,
             ec: ExecutionContext
  ): Future[Option[T]] =
    db.queryHeadOptAs(render(p1, p2), transform)

  def printSql: StoredUpdateReturning[P1, P2, R] = {
    println(statement)
    this
  }
    
  def printSqlAndArgs(p1: P1, p2: P2): StoredUpdateReturning[P1, P2, R] =
    render(p1, p2).printStatementAndArgs(this)
    
  def printSqlWithArgs(p1: P1, p2: P2): StoredUpdateReturning[P1, P2, R] =
    render(p1, p2).printStatementWithArgs(this)
}








