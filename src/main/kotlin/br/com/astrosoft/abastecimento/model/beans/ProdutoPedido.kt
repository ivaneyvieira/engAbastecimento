package br.com.astrosoft.abastecimento.model.beans

import br.com.astrosoft.abastecimento.model.QueryAppEstoque
import br.com.astrosoft.framework.util.lpad

data class ProdutoPedido(
  val prdno: String,
  val grade: String,
  val descricao: String,
  val embalagem: Int,
  val centrodelucro: String,
  val abreviacao: String,
  val qtty: Int
                        ) {
  val saldo: Int
    get() = saldoApp(prdno, grade, abreviacao)
  val saldoFinal: Int
    get() = saldo - qtty
  val codigo
    get() = prdno.lpad(6, "0")
  var qttyEdit: Int = 0
  
  companion object {
    private val appEstoque = QueryAppEstoque()
    
    fun saldoApp(prdno: String, grade: String, abreviacao: String): Int {
      return appEstoque.saldo(prdno, grade, abreviacao)
    }
  }
}