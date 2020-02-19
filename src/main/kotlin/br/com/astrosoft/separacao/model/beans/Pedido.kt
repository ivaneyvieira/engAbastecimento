package br.com.astrosoft.separacao.model.beans

import br.com.astrosoft.framework.util.mid
import br.com.astrosoft.separacao.model.enum.ETipoOrigem
import br.com.astrosoft.separacao.model.enum.ETipoOrigem.DUPLICADO
import br.com.astrosoft.separacao.model.saci

data class Pedido(val storeno: Int = 1, val ordno: Int, val ordnoMae: Int, val tipo: String) {
  fun compativel(pedido: Pedido) = storenoDestino == pedido.storenoDestino
  
  fun isNotEmpty(user: UserSaci?) = produtos(user).isNotEmpty()
  
  fun produtos(user: UserSaci?) = saci
    .listaProduto(ordno)
    .filtraLocalizacoes()
    .filter {user?.isLocalizacaoCompativel(it.localizacao) ?: false}
    .sortedWith(compareBy({it.localizacao}, {it.descricao}, {it.grade}))
  
  val storenoDestino
    get() = ordno.toString().mid(0, 1).toIntOrNull() ?: 0
  val tipoOrigem: ETipoOrigem
    get() = ETipoOrigem.value(tipo) ?: DUPLICADO
  
  fun abreviacoes(user: UserSaci?) = produtos(user)
    .filtraLocalizacoes()
    .groupBy {it.localizacao.mid(0, 4)}
    .entries
    .sortedBy {-it.value.size}
    .map {it.key}
  
  companion object {
    fun findPedidos(numeroOrigem: Int?): Pedido? {
      numeroOrigem ?: return null
      val user = UserSaci.userAtual
      return pedidos(user).firstOrNull {it.ordno == numeroOrigem}
    }
    
    fun proximoNumeroSeparado(storenoDestino: Int): Int {
      return if(storenoDestino == 0) 0
      else saci.proximoNumeroSeparado(storenoDestino)
    }
    
    fun atualizarQuantidade(ordno: Int, proximoNumero: Int, produto: ProdutoPedido, tipo: ETipoOrigem) {
      saci.atualizarQuantidade(ordno, proximoNumero, produto.prdno, produto.grade,
                               produto.localizacao, produto.qttyEdit, tipo)
    }
    
    fun retornaSaldo(pedido: Pedido, produto: ProdutoPedido) {
      saci.retornaSaldo(ordnoMae = pedido.ordnoMae,
                        ordno = pedido.ordno,
                        codigo = produto.prdno,
                        grade = produto.grade,
                        qttyEdit = produto.qttyEdit,
                        localizacao = produto.localizacao)
    }
  
    fun pedidos(user: UserSaci?) = saci.listaPedido().filter {it.storenoDestino in 2..5}
      .filter {it.isNotEmpty(user)}
      .sortedWith(compareBy(Pedido::ordno, Pedido::ordno))
  
    fun pedidos() = saci.listaPedido().filter {it.storenoDestino in 2..5}
      .sortedWith(compareBy(Pedido::ordno, Pedido::ordno))
  
    fun listaRelatorio(ordno: Int): List<Relatorio> {
      return saci.listaRelatorio(ordno)
    }
  
    fun proximoNumeroPedidoLoja(storenoDestino: Int, abreviacao : String): Int {
      return saci.proximoNumeroPedidoLoja(storenoDestino, abreviacao)
    }
  
    fun proximoNumeroDuplicado(storeno: Int): Int {
      return saci.proximoNumeroDuplicado(storeno)
    }
  
    fun duplicar(pedidoOrigem: Pedido, pedidoDestino: Pedido) {
      saci.duplicar(pedidoOrigem.ordno, pedidoDestino.ordno)
    }
  
    fun removePedido(pedidoIncial: Pedido, pedidoFinal: Pedido) {
      saci.removePedido(pedidoIncial.ordno, pedidoFinal.ordno)
    }
  
    fun findProduto(prdno: String): List<Produto> {
      return saci.findProduto(prdno)
    }
  
    fun adicionarProduto(pedido: Pedido, codigo: String, grade: String, qtty: Int, localizacao: String) {
      saci.adicionarProduto(pedido, codigo, grade, qtty, localizacao)
    }
  
    fun findAbreviacoes(): List<String> {
      return saci.findAbreviacoes()
    }
  }
  
  private fun List<ProdutoPedido>.filtraLocalizacoes(): List<ProdutoPedido> {
    return this.groupBy {ProdutoKey(it.prdno, it.grade)}
      .flatMap {entry ->
        val list = entry.value.filter {
          (!it.localizacao.startsWith("EXP4")) && (!it.localizacao.startsWith("CD00"))
        }
        if(list.isEmpty()) entry.value else list
      }
  }
}

data class ProdutoKey(val prdno: String, val grade: String)
