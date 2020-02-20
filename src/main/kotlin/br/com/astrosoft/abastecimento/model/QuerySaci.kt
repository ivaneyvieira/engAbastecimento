package br.com.astrosoft.abastecimento.model

import br.com.astrosoft.abastecimento.model.beans.Pedido
import br.com.astrosoft.abastecimento.model.beans.Produto
import br.com.astrosoft.abastecimento.model.beans.ProdutoPedido
import br.com.astrosoft.abastecimento.model.beans.Relatorio
import br.com.astrosoft.abastecimento.model.beans.UserSaci
import br.com.astrosoft.framework.model.QueryDB
import br.com.astrosoft.framework.util.DB

class QuerySaci: QueryDB(driver, url, username, password) {
  fun findUser(login: String?): UserSaci? {
    login ?: return null
    val sql = "/sqlSaci/userSenha.sql"
    return query(sql) {q ->
      q.addParameter("login", login)
        .executeAndFetch(UserSaci::class.java)
        .firstOrNull()
        ?.initVars()
    }
  }
  
  fun findAllUser(): List<UserSaci> {
    val sql = "/sqlSaci/userSenha.sql"
    return query(sql) {q ->
      q.addParameter("login", "TODOS")
        .executeAndFetch(UserSaci::class.java)
        .map {user ->
          user.initVars()
        }
    }
  }
  
  fun updateUser(user: UserSaci) {
    val sql = "/sqlSaci/updateUser.sql"
    script(sql) {q ->
      q.addParameter("login", user.login)
      q.addParameter("bitAcesso", user.bitAcesso())
      q.addParameter("abreviacoes", user.abreviacoes)
      q.executeUpdate()
    }
  }
  
  fun listaProduto(ordno: Int): List<ProdutoPedido> {
    val storeno = 4
    val sql = "/sqlSaci/listaProdutos.sql"
    val lista = query(sql) {q ->
      q.addParameter("storeno", storeno)
      q.addParameter("ordno", ordno)
      q.executeAndFetch(ProdutoPedido::class.java)
    }
    lista.forEach {produto ->
      produto.qttyEdit = produto.qtty.toInt()
    }
    return lista
  }
  
  fun listaPedido(ordno: Int?): Pedido? {
    ordno ?: return null
    val storeno = 4
    val sql = "/sqlSaci/listaPedidos.sql"
    
    return query(sql) {q ->
      q.addParameter("storeno", storeno)
      q.addParameter("ordno", ordno)
      q.executeAndFetch(Pedido::class.java)
        .firstOrNull()
    }
  }
  
  fun findProduto(prdno: String): List<Produto> {
    val sql = "/sqlSaci/findProduto.sql"
    return query(sql) {q ->
      q.addOptionalParameter("prdno", prdno)
      q.executeAndFetch(Produto::class.java)
    }
  }
  
  fun findAbreviacoes(): List<String> {
    val sql = "/sqlSaci/findAbreviacoes.sql"
    return query(sql) {q ->
      q.executeScalarList(String::class.java)
    }
  }
  
  fun removePedido(ordno: Int, prdno: String, grade: String) {
    val sql = "/sqlSaci/removerPedido.sql"
    val storeno = 4
    return script(sql) {q ->
      q.addOptionalParameter("storeno", storeno)
      q.addOptionalParameter("ordno", ordno)
      q.addOptionalParameter("prdno", prdno)
      q.addOptionalParameter("grade", grade)
      q.executeUpdate()
    }
  }
  
  fun atualizarQuantidade(ordno: Int, prdno: String, grade: String, qtty: Int) {
    val sql = "/sqlSaci/atualizarQuantidade.sql"
    val storeno = 4
    return script(sql) {q ->
      q.addOptionalParameter("storeno", storeno)
      q.addOptionalParameter("ordno", ordno)
      q.addOptionalParameter("prdno", prdno)
      q.addOptionalParameter("grade", grade)
      q.addOptionalParameter("qtty", qtty)
      q.executeUpdate()
    }
  }
  
  fun listaRelatorio(ordno: Int): List<Relatorio> {
    val sql = "/sqlSaci/relatorio.sql"
    val storeno = 1
    return query(sql) {q ->
      q.addOptionalParameter("storeno", storeno)
      q.addOptionalParameter("ordno", ordno)
      q.executeAndFetch(Relatorio::class.java)
    }
  }
  
  companion object {
    private val db = DB("saci")
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

val saci = QuerySaci()