package br.com.astrosoft.framework.model

import br.com.astrosoft.framework.util.SystemUtils.readFile
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.sql2o.Connection
import org.sql2o.Query
import org.sql2o.Sql2o

open class QueryDB(driver: String, url: String, username: String, password: String) {
  private val sql2o: Sql2o
  
  init {
    registerDriver(driver)
    val config = HikariConfig()
    config.jdbcUrl = url
    config.username = username
    config.password = password
    config.addDataSourceProperty("cachePrepStmts", "true")
    config.addDataSourceProperty("prepStmtCacheSize", "250")
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
    config.isAutoCommit = false
    val ds = HikariDataSource(config)
    ds.maximumPoolSize = 5
  
    this.sql2o = Sql2o(url, username, password)
  }
  
  private fun registerDriver(driver: String) {
    try {
      Class.forName(driver)
    } catch(e: ClassNotFoundException) {
      //throw RuntimeException(e)
    }
  }
  
  private fun <T> buildQuery(file: String, proc: (Connection, Query) -> T): T {
    val sql = if(file.startsWith("/")) readFile(file) else file
    return this.sql2o.open()
      .use {con ->
        val query = con.createQuery(sql)
        val time = System.currentTimeMillis()
        println("SQL2O ==> $sql")
        val result = proc(con, query)
        val difTime = System.currentTimeMillis() - time
        println("######################## TEMPO QUERY $difTime ms ########################")
        result
      }
  }
  
  protected fun <T> query(file: String, lambda: (Query) -> T): T {
    return buildQuery(file) {con, query ->
      val ret = lambda(query)
      con.close()
      ret
    }
  }
  
  protected fun script(file: String, lambda: (Query) -> Unit) {
    val stratments =
      readFile(file)?.split(";")
        .orEmpty()
        .filter {it.isNotBlank() || it.isNotEmpty()}
    transaction {con ->
      stratments.forEach {sql ->
        val query = con.createQuery(sql)
        lambda(query)
      }
    }
  }
  
  fun Query.addOptionalParameter(name: String, value: String?): Query {
    if(this.paramNameToIdxMap.containsKey(name)) this.addParameter(name, value)
    return this
  }
  
  fun Query.addOptionalParameter(name: String, value: Int): Query {
    if(this.paramNameToIdxMap.containsKey(name)) this.addParameter(name, value)
    return this
  }
  
  fun Query.addOptionalParameter(name: String, value: Double): Query {
    if(this.paramNameToIdxMap.containsKey(name)) this.addParameter(name, value)
    return this
  }
  
  private fun transaction(block: (Connection) -> Unit) {
    sql2o.beginTransaction()
      .use {con ->
        block(con)
        con.commit()
      }
  }
}
