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

package kuzminki

import scala.concurrent.{Future, ExecutionContext}

import shapeless._
import shapeless.ops.hlist.Tupler

import kuzminki.api.{Kuzminki, Model}
import kuzminki.run._
import kuzminki.update.StoredUpdateReturning
import kuzminki.insert.{
  Values,
  InsertOptions,
  StoredInsert
}


trait datatypes {

  implicit class RunQueryType[R, B <: HList](query: RunQuery[R]) {

    def runType[T](
      implicit untupler: Generic.Aux[R, B],
               generic: Generic.Aux[T, B],
               db: Kuzminki,
               ec: ExecutionContext
    ): Future[List[T]] = {
      query.runAs((r: R) => generic.from(untupler.to(r)), db, ec)
    }
    
    def runHeadType[T](
      implicit untupler: Generic.Aux[R, B],
               generic: Generic.Aux[T, B],
               db: Kuzminki,
               ec: ExecutionContext
    ): Future[T] = {
      query.runHeadAs((r: R) => generic.from(untupler.to(r)), db, ec)
    }

    def runHeadOptType[T](
      implicit untupler: Generic.Aux[R, B],
               generic: Generic.Aux[T, B],
               db: Kuzminki,
               ec: ExecutionContext
    ): Future[Option[T]] = {
      query.runHeadOptAs((r: R) => generic.from(untupler.to(r)), db, ec)
    }
  }

  implicit class RunQueryParamsType[P, R, B <: HList](query: RunQueryParams[P, R]) {

    def runType[T](params: P)(
      implicit untupler: Generic.Aux[R, B],
               generic: Generic.Aux[T, B],
               db: Kuzminki,
               ec: ExecutionContext
    ): Future[List[T]] = {
      query.runAs(params)((r: R) => generic.from(untupler.to(r)), db, ec)
    }

    def runHeadType[T](params: P)(
      implicit untupler: Generic.Aux[R, B],
               generic: Generic.Aux[T, B],
               db: Kuzminki,
               ec: ExecutionContext
    ): Future[T] =
      query.runHeadAs(params)((r: R) => generic.from(untupler.to(r)), db, ec)

    def runHeadOptType[T](params: P)(
      implicit untupler: Generic.Aux[R, B],
               generic: Generic.Aux[T, B],
               db: Kuzminki,
               ec: ExecutionContext
    ): Future[Option[T]] = {
      query.runHeadOptAs(params)((r: R) => generic.from(untupler.to(r)), db, ec)
    }
  }

  implicit class StoredUpdateReturningType[P1, P2, R, B <: HList](query: StoredUpdateReturning[P1, P2, R]) {

    def runType[T](p1: P1, p2: P2)(
      implicit untupler: Generic.Aux[R, B],
               generic: Generic.Aux[T, B],
               db: Kuzminki,
               ec: ExecutionContext
    ): Future[List[T]] = {
      query.runAs(p1, p2)((r: R) => generic.from(untupler.to(r)), db, ec)
    }

    def runHeadType[T](p1: P1, p2: P2)(
      implicit untupler: Generic.Aux[R, B],
               generic: Generic.Aux[T, B],
               db: Kuzminki,
               ec: ExecutionContext
    ): Future[T] =
      query.runHeadAs(p1, p2)((r: R) => generic.from(untupler.to(r)), db, ec)

    def runHeadOptType[T](p1: P1, p2: P2)(
      implicit untupler: Generic.Aux[R, B],
               generic: Generic.Aux[T, B],
               db: Kuzminki,
               ec: ExecutionContext
    ): Future[Option[T]] = {
      query.runHeadOptAs(p1, p2)((r: R) => generic.from(untupler.to(r)), db, ec)
    }
  }

  implicit class InsertOptionsType[M <: Model, P, B <: HList](query: InsertOptions[M, P]) {

    def valuesType[T <: Product](value: T)(
      implicit generic: Generic.Aux[T, B],
               tupler: Tupler.Aux[B, P]
    ): Values[M] = {
      query.values(Generic[T].to(value).tupled)
    }
  }

  implicit class StoredInsertType[P, B <: HList](query: StoredInsert[P]) {

    def runType[T <: Product](value: T)(
      implicit generic: Generic.Aux[T, B],
               tupler: Tupler.Aux[B, P],
               db: Kuzminki,
               ec: ExecutionContext
    ): Future[Unit] = {
      query.run(Generic[T].to(value).tupled)
    }

    def runNumType[T <: Product](value: T)(
      implicit generic: Generic.Aux[T, B],
               tupler: Tupler.Aux[B, P],
               db: Kuzminki,
               ec: ExecutionContext
    ): Future[Int] = {
      query.runNum(Generic[T].to(value).tupled)
    }

    def runListType[T <: Product](values: Seq[T])(
      implicit generic: Generic.Aux[T, B],
               tupler: Tupler.Aux[B, P],
               db: Kuzminki,
               ec: ExecutionContext
    ): Future[Unit] = {
      query.runList(values.map((t: T) => Generic[T].to(t).tupled))
    }
  }
}



















