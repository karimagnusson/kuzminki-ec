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

package kuzminki.filter

import kuzminki.column.TypeCol
import kuzminki.assign._
import kuzminki.filter.types._
import kuzminki.fn.types._
import kuzminki.api.Arg


trait StringMethods extends TypeMethods[String] with InMethods[String] {

  // fn

  def concat(cols: TypeCol[_]*) = ConcatFn((Seq(col) ++ cols).toVector)
  def concatWs(glue: String, cols: TypeCol[_]*) = ConcatWsFn(glue, (Seq(col) ++ cols).toVector)
  def substr(start: Int) = SubstrFn(col, start, None)
  def substr(start: Int, len: Int) = SubstrFn(col, start, Some(len))
  def replace(from: String, to: String) = ReplaceFn(col, from, to)
  def trim = CustomStringFn(col, "trim(%s)")
  def upper = CustomStringFn(col, "upper(%s)")
  def lower = CustomStringFn(col, "lower(%s)")
  def initcap = CustomStringFn(col, "initcap(%s)")

  def asShort = CastShort(col)
  def asInt = CastInt(col)
  def asLong = CastLong(col)
  def asFloat = CastFloat(col)
  def asDouble = CastDouble(col)
  def asBigDecimal = CastBigDecimal(col)

  // filters

  def like(value: String): Filter = FilterLike(col, value)
  def startsWith(value: String): Filter = FilterStartsWith(col, value)
  def endsWith(value: String): Filter = FilterEndsWith(col, value)
  def similarTo(value: String): Filter = FilterSimilarTo(col, value)

  def ~(value: String): Filter = FilterReMatch(col, value)
  def ~*(value: String): Filter = FilterReIMatch(col, value)
  def !~(value: String): Filter = FilterReNotMatch(col, value)
  def !~*(value: String): Filter = FilterReNotIMatch(col, value)

  // optional

  def like(opt: Option[String]): Option[Filter] = opt.map(like)
  def startsWith(opt: Option[String]): Option[Filter] = opt.map(startsWith)
  def endsWith(opt: Option[String]): Option[Filter] = opt.map(endsWith)
  def similarTo(opt: Option[String]): Option[Filter] = opt.map(similarTo)

  def ~(opt: Option[String]): Option[Filter] = opt.map(~)
  def ~*(opt: Option[String]): Option[Filter] = opt.map(~*)
  def !~(opt: Option[String]): Option[Filter] = opt.map(!~)
  def !~*(opt: Option[String]): Option[Filter] = opt.map(!~*)

  // cache

  def use = StringCache(col)
}


case class StringCache(col: TypeCol[String]) extends TypeCache[String]
                                                with InCache[String] {
  
  def like(arg: Arg) = CacheLike(col, col.conv)
  def startsWith(arg: Arg) = CacheStartsWith(col, col.conv)
  def endsWith(arg: Arg) = CacheEndsWith(col, col.conv)
  def similarTo(arg: Arg) = CacheSimilarTo(col, col.conv)
  
  def ~(arg: Arg) = CacheReMatch(col, col.conv)
  def ~*(arg: Arg) = CacheReIMatch(col, col.conv)
  def !~(arg: Arg) = CacheReNotMatch(col, col.conv)
  def !~*(arg: Arg) = CacheReNotIMatch(col, col.conv)
}







