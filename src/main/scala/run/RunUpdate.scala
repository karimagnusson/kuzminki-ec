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

import scala.concurrent.ExecutionContext
import kuzminki.api.Kuzminki
import kuzminki.render.{
  RenderedQuery,
  RenderedOperation,
  JoinArgs
}


trait RunUpdate[P1, P2] extends JoinArgs {

  val statement: String

  def render(p1: P1, p2: P2): RenderedOperation

  def run(p1: P1, p2: P2)(implicit db: Kuzminki, ec: ExecutionContext) =
    db.exec(render(p1, p2))

  def runNum(p1: P1, p2: P2)(implicit db: Kuzminki, ec: ExecutionContext) =
    db.execNum(render(p1, p2))

  def runList(list: Seq[Tuple2[P1, P2]])(implicit db: Kuzminki, ec: ExecutionContext) =
    db.execList(list.map(p => render(p._1, p._2)))
}


trait RunUpdateReturning[P1, P2, R] extends JoinArgs {

  val statement: String

  def render(p1: P1, p2: P2): RenderedQuery[R]

  def run(p1: P1, p2: P2)(implicit db: Kuzminki, ec: ExecutionContext) =
    db.query(render(p1, p2))

  def runAs[T](p1: P1, p2: P2)(implicit transform: R => T, db: Kuzminki, ec: ExecutionContext) =
    db.queryAs(render(p1, p2), transform)

  def runHead(p1: P1, p2: P2)(implicit db: Kuzminki, ec: ExecutionContext) =
    db.queryHead(render(p1, p2))

  def runHeadAs[T](p1: P1, p2: P2)(implicit transform: R => T, db: Kuzminki, ec: ExecutionContext) =
    db.queryHeadAs(render(p1, p2), transform)

  def runHeadOpt(p1: P1, p2: P2)(implicit db: Kuzminki, ec: ExecutionContext) =
    db.queryHeadOpt(render(p1, p2))

  def runHeadOptAs[T](p1: P1, p2: P2)(implicit transform: R => T, db: Kuzminki, ec: ExecutionContext) =
    db.queryHeadOptAs(render(p1, p2), transform)
}










