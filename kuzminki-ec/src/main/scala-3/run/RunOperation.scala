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
import kuzminki.render.RenderedOperation


trait RunOperation {

  def render: RenderedOperation

  def run(
    using db: Kuzminki,
          ec: ExecutionContext
  ): Future[Unit] = db.exec(render)

  def runNum(
    using db: Kuzminki,
          ec: ExecutionContext
  ): Future[Int] = db.execNum(render)

  def printSql =
    render.printStatement(this)

  def printSqlAndArgs =
    render.printStatementAndArgs(this)

  def printSqlWithArgs =
    render.printStatementWithArgs(this)
}




























