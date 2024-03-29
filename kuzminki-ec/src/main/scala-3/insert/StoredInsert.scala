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

package kuzminki.insert

import scala.concurrent.{Future, ExecutionContext}
import scala.deriving.Mirror.ProductOf
import kuzminki.api.Kuzminki
import kuzminki.shape.{ParamConv, RowConv}
import kuzminki.run.RunQueryParams
import kuzminki.render.{
  RenderedOperation,
  RenderedQuery,
  JoinArgs
}


class StoredInsert[P](
  val statement: String,
  args: Vector[Any],
  paramConv: ParamConv[P]
) extends JoinArgs {

  def render(params: P) = RenderedOperation(
    statement,
    joinArgs(args, paramConv.fromShape(params))
  )

  def run(params: P)(
    using db: Kuzminki,
          ec: ExecutionContext
  ): Future[Unit] = db.exec(render(params))

  def runNum(params: P)(
    using db: Kuzminki,
          ec: ExecutionContext
  ): Future[Int] =
    db.execNum(render(params))

  def runList(paramList: Seq[P])(
    using db: Kuzminki,
          ec: ExecutionContext
  ): Future[Unit] = db.execList(paramList.map(render(_)))

  def runType[T <: Product](value: T)(
    using mirror: ProductOf[T],
          ev: P <:< mirror.MirroredElemTypes,
          db: Kuzminki,
          ec: ExecutionContext
  ): Future[Unit] = {
    run(Tuple.fromProductTyped(value).asInstanceOf[P])
  }

  def runNumType[T <: Product](value: T)(
    using mirror: ProductOf[T],
          ev: P <:< mirror.MirroredElemTypes,
          db: Kuzminki,
          ec: ExecutionContext
  ): Future[Int] = {
    runNum(Tuple.fromProductTyped(value).asInstanceOf[P])
  }

  def runListType[T <: Product](values: Seq[T])(
    using mirror: ProductOf[T],
          ev: P <:< mirror.MirroredElemTypes,
          db: Kuzminki,
          ec: ExecutionContext
  ): Future[Unit] = {
    runList(
      values.map(v => Tuple.fromProductTyped(v).asInstanceOf[P])
    )
  }

  def printSql: StoredInsert[P] = {
    println(statement)
    this
  }
  
  def printSqlAndArgs(params: P): StoredInsert[P] =
    render(params).printStatementAndArgs(this)
  
  def printSqlWithArgs(params: P): StoredInsert[P] =
    render(params).printStatementWithArgs(this)
}


class StoredInsertReturning[P, R](
  val statement: String,
  args: Vector[Any],
  paramConv: ParamConv[P],
  rowConv: RowConv[R]
) extends RunQueryParams[P, R] {

  def render(params: P) = RenderedQuery(
    statement,
    joinArgs(args, paramConv.fromShape(params)),
    rowConv
  )
}






