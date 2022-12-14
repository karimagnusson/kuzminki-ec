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

import kuzminki.shape.{ParamConv, RowConv}
import kuzminki.render.{
  RenderedOperation,
  RenderedQuery
}
import kuzminki.run.{
  RunUpdate,
  RunUpdateReturning
}


class StoredUpdate[P1, P2](
  val statement: String,
  args: Vector[Any],
  changes: ParamConv[P1],
  filters: ParamConv[P2]
) extends RunUpdate[P1, P2] {

  def render(p1: P1, p2: P2) = RenderedOperation(
    statement,
    joinArgs(args, changes.fromShape(p1) ++ filters.fromShape(p2))
  )
}


class StoredUpdateReturning[P1, P2, R](
  val statement: String,
  args: Vector[Any],
  changes: ParamConv[P1],
  filters: ParamConv[P2],
  rowConv: RowConv[R]
) extends RunUpdateReturning[P1, P2, R] {

  def render(p1: P1, p2: P2) = RenderedQuery(
    statement,
    joinArgs(args, changes.fromShape(p1) ++ filters.fromShape(p2)),
    rowConv
  )
}








