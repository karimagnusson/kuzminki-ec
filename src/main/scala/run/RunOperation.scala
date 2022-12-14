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

import kuzminki.api.Kuzminki
import kuzminki.shape.ParamConv
import kuzminki.render.{RenderedOperation, JoinArgs}


trait RunOperation {

  def render: RenderedOperation

  def run(implicit db: Kuzminki) =
    db.exec(render)

  def runNum(implicit db: Kuzminki) =
    db.execNum(render)
}


trait RunOperationParams[P] extends JoinArgs {

  val statement: String

  def render(params: P): RenderedOperation

  def run(params: P)(implicit db: Kuzminki) =
    db.exec(render(params))

  def runNum(params: P)(implicit db: Kuzminki) =
    db.execNum(render(params))

  def runList(paramList: Seq[P])(implicit db: Kuzminki) =
    db.execList(paramList.map(render(_)))
}


























