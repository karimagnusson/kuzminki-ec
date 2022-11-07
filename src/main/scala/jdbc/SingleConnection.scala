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

package kuzminki.jdbc

import java.util.Properties
import java.util.UUID
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.{Statement, PreparedStatement}
import java.sql.ResultSet
import java.sql.Time
import java.sql.Date
import java.sql.Timestamp

import scala.util.{Try, Success, Failure}
import scala.concurrent.{Future, ExecutionContext}
import scala.collection.mutable.ListBuffer

import com.zaxxer.hikari.HikariDataSource
import org.postgresql.util.PGInterval
import org.postgresql.util.PGobject

import kuzminki.shape.RowConv
import kuzminki.conv.{TypeNull, TypeArray}
import kuzminki.api.{
  DbConfig,
  KuzminkiError,
  Jsonb,
  NoArg
}
import kuzminki.render.{
  RenderedQuery,
  RenderedOperation
}


class JdbcExecutor(pool: HikariDataSource, dbContext: ExecutionContext) {

  private val notNoArg: Any => Boolean = {
    case NoArg => false
    case _ => true
  }

  private def arrayArg(conn: Connection, value: TypeArray) = {
    conn.createArrayOf(value.typeName, value.vec.toArray)
  }

  private def jsonbArg(value: Jsonb) = {
    val obj = new PGobject()
    obj.setType("jsonb")
    obj.setValue(value.value)
    obj
  }

  private def setArg(jdbcStm: PreparedStatement, arg: Any, index: Int): Unit = {
    arg match {
      case value: String        => jdbcStm.setString(index, value)
      case value: Boolean       => jdbcStm.setBoolean(index, value)
      case value: Short         => jdbcStm.setShort(index, value)
      case value: Int           => jdbcStm.setInt(index, value)
      case value: Long          => jdbcStm.setLong(index, value)
      case value: Float         => jdbcStm.setFloat(index, value)
      case value: Double        => jdbcStm.setDouble(index, value)
      case value: BigDecimal    => jdbcStm.setBigDecimal(index, value.bigDecimal)
      case value: Time          => jdbcStm.setTime(index, value)
      case value: Date          => jdbcStm.setDate(index, value)
      case value: Timestamp     => jdbcStm.setTimestamp(index, value)
      case value: Jsonb         => jdbcStm.setObject(index, jsonbArg(value))
      case value: UUID          => jdbcStm.setObject(index, value)
      case value: PGInterval    => jdbcStm.setObject(index, value)
      case value: TypeNull      => jdbcStm.setNull(index, value.typeId)
      case value: TypeArray     => jdbcStm.setArray(index, arrayArg(jdbcStm.getConnection,value))
      case _ => throw KuzminkiError(s"type not supported [$arg]")
    }
  }

  protected def runStatement[R](sql: String, args: Vector[Any])(body: PreparedStatement => R) = {
    val conn = pool.getConnection()
    val jdbcStm = conn.prepareStatement(sql)
    Try {
      if (args.nonEmpty) {
        args.filter(notNoArg).zipWithIndex.foreach {
          case (arg, index) =>
            setArg(jdbcStm, arg, index + 1)
        }
      }
      body(jdbcStm)
    } match {
      case Success(res) =>
        jdbcStm.close()
        conn.close()
        res
      case Failure(ex) =>
        jdbcStm.close()
        conn.close()
        throw ex
    }
  }

  def query[R](stm: RenderedQuery[R]): Future[List[R]] = {
    Future {
      runStatement(stm.statement, stm.args) { jdbcStm =>
        val jdbcResultSet = jdbcStm.executeQuery()
        var buff = ListBuffer.empty[R]
        while (jdbcResultSet.next()) {
          buff += stm.rowConv.fromRow(jdbcResultSet)
        }
        jdbcResultSet.close()
        buff.toList
      }
    } (dbContext)
  }

  def exec(stm: RenderedOperation): Future[Unit] = {
    Future {
      runStatement(stm.statement, stm.args) { jdbcStm =>
        jdbcStm.execute()
        ()
      }
    } (dbContext)
  }

  def execNum(stm: RenderedOperation): Future[Int] = {
    Future {
      runStatement(stm.statement, stm.args) { jdbcStm =>
        jdbcStm.executeUpdate()
      }
    } (dbContext)
  }

  def execList(stms: Seq[RenderedOperation]): Future[Unit] = {
    Future {
      val conn = pool.getConnection()
      Try {
        conn.setAutoCommit(false)
        stms.foreach { stm => 
          val jdbcStm = conn.prepareStatement(stm.statement)
          if (stm.args.nonEmpty) {
            stm.args.filter(notNoArg).zipWithIndex.foreach {
              case (arg, index) =>
                setArg(jdbcStm, arg, index + 1)
            }
          }
          jdbcStm.execute()
        }
        conn.commit()
      } match {
        case Success(_) =>
          conn.setAutoCommit(true)
          conn.close()
          ()
        case Failure(ex) =>
          conn.rollback()
          conn.setAutoCommit(true)
          conn.close()
          throw ex
      }
    } (dbContext)
  }

  def close() = {
    pool.close()
  }
}

















