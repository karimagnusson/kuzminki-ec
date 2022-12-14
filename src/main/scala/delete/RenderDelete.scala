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

package kuzminki.delete

import kuzminki.shape.{RowConv, ParamConv}
import kuzminki.run.{RunQuery, RunOperation}
import kuzminki.render._


class RenderDelete[M](
    model: M,
    coll: SectionCollector
  ) extends PickDeleteReturning(model, coll)
       with RunOperation {
  
  def render = RenderedOperation(
    coll.render,
    coll.args
  )

  def cache = new StoredOperation(
    coll.render,
    coll.args
  )
}


class RenderDeleteReturning[R](
    coll: SectionCollector,
    rowConv: RowConv[R]
  ) extends RunQuery[R] {

  def render = RenderedQuery(
    coll.render,
    coll.args,
    rowConv
  )

  def cache = new StoredQuery(
    coll.render,
    coll.args,
    rowConv
  )
}


class RenderStoredDelete[M, P](
  model: M,
  coll: SectionCollector,
  paramConv: ParamConv[P]
) extends PickStoredDeleteReturning(model, coll, paramConv) {

  def cache = {
    new StoredDelete(
      coll.render,
      coll.args,
      paramConv
    )
  }
}


class RenderStoredDeleteReturning[P, R](
  coll: SectionCollector,
  paramConv: ParamConv[P],
  rowConv: RowConv[R]
) {

  def cache = {
    new StoredDeleteReturning(
      coll.render,
      coll.args,
      paramConv,
      rowConv
    )
  }
}







