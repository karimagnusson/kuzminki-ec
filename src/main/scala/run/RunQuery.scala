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
import kuzminki.render.{RenderedQuery, JoinArgs}


trait RunQuery[R] {

  def render: RenderedQuery[R]

  def run(implicit db: Kuzminki, ec: ExecutionContext) =
    db.query(render)

  def runAs[T](implicit transform: R => T, db: Kuzminki, ec: ExecutionContext) =
    db.queryAs(render, transform)

  def runHead(implicit db: Kuzminki, ec: ExecutionContext) =
    db.queryHead(render)

  def runHeadAs[T](implicit transform: R => T, db: Kuzminki, ec: ExecutionContext) =
    db.queryHeadAs(render, transform)

  def runHeadOpt(implicit db: Kuzminki, ec: ExecutionContext) =
    db.queryHeadOpt(render)

  def runHeadOptAs[T](implicit transform: R => T, db: Kuzminki, ec: ExecutionContext) =
    db.queryHeadOptAs(render, transform)
}


trait RunQueryParams[P, R] extends JoinArgs {

  val statement: String

  def render(params: P): RenderedQuery[R]

  def run(params: P)(implicit db: Kuzminki, ec: ExecutionContext) =
    db.query(render(params))

  def runAs[T](params: P)(implicit transform: R => T, db: Kuzminki, ec: ExecutionContext) =
    db.queryAs(render(params), transform)

  def runHead(params: P)(implicit db: Kuzminki, ec: ExecutionContext) =
    db.queryHead(render(params))

  def runHeadAs[T](params: P)(implicit transform: R => T, db: Kuzminki, ec: ExecutionContext) =
    db.queryHeadAs(render(params), transform)

  def runHeadOpt(params: P)(implicit db: Kuzminki, ec: ExecutionContext) =
    db.queryHeadOpt(render(params))

  def runHeadOptAs[T](params: P)(implicit transform: R => T, db: Kuzminki, ec: ExecutionContext) =
    db.queryHeadOptAs(render(params), transform)
}


















