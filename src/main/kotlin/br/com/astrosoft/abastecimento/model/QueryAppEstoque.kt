package br.com.astrosoft.abastecimento.model

import br.com.astrosoft.framework.model.QueryDB
import br.com.astrosoft.framework.util.DB

class QueryAppEstoque: QueryDB(driver, url, username, password) {
  fun saldo(prdno: String, grade: String, abreviacao: String): Int {
    val sql = "/sqlSaci/saldoApp.sql"
    
    return query(sql) {q ->
      q.addParameter("prdno", prdno)
      q.addParameter("grade", grade)
      q.addParameter("abreviacao", abreviacao)
      q.executeScalarList(Double::class.java).firstOrNull() ?: 0.00
    }.toInt()
  }
  
  companion object {
    private val db = DB("db")
    internal val driver = db.driver
    internal val url = db.url
    internal val username = db.username
    internal val password = db.password
    internal val test = db.test
    val ipServer =
      url.split("/")
        .getOrNull(2)
  }
}

