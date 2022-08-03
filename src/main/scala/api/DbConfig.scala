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

package kuzminki.api

import java.util.Properties
import scala.collection.mutable.Map
import scala.deprecated


object DbConfig {
  def forDb(db: String) = new DbConfig(db)
}


class DbConfig(val db: String) {

  var dispatcher = "kuzminki-dispatcher"

  val props = new Properties()
  props.setProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource")
  props.setProperty("dataSource.databaseName", db)
  props.setProperty("dataSource.serverName", "localhost")
  props.setProperty("dataSource.portNumber", "5432")

  def withDispatcher(value: String) = {
    dispatcher = value
    this
  }

  def withUser(value: String) = {
    props.setProperty("dataSource.user", value)
    this
  }

  def withPassword(value: String) = {
    props.setProperty("dataSource.password", value)
    this
  }

  def withHost(value: String) = {
    props.setProperty("dataSource.serverName", value)
    this
  }

  def withPort(value: String) = {
    props.setProperty("dataSource.portNumber", value)
    this
  }

  def withOptions(options: Map[String, String]) = {
    options.foreach {
      case (key, value) =>
        props.setProperty(key, value)
    }
    this
  }

  @deprecated("Not used", "01-08-2022")
  def getConfig = this
}

















