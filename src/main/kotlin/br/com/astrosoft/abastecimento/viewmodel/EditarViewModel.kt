package br.com.astrosoft.abastecimento.viewmodel

import br.com.astrosoft.abastecimento.model.beans.Pedido
import br.com.astrosoft.abastecimento.model.beans.Produto
import br.com.astrosoft.abastecimento.model.beans.ProdutoPedido
import br.com.astrosoft.abastecimento.model.beans.UserSaci
import br.com.astrosoft.framework.util.mid
import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel
import br.com.astrosoft.framework.viewmodel.fail

class EditarViewModel(view: IEditarView): ViewModel<IEditarView>(view) {
  fun novoProduto() = exec {
    view.pedido?.let {pedido ->
      view.novoProduto(pedido)
    }
  }
  
  fun findProduto(prdno: String?): List<Produto> {
    prdno ?: return emptyList()
    return Pedido.findProduto(prdno)
  }
  
  fun salvaProduto(produto: ProdutoDlg) = exec {
    val userSaci = UserSaci.userAtual
    produto.validadialog()
    val pedido = view.pedido ?: fail("Pedido não selecionado")
    val codigo = produto.codigo
    val grade = produto.grade
    val localizacao = produto.localizacao
    val qtty = produto.qtty ?: fail("Quantidade não informada")
    if(pedido.produtos(userSaci).any {it.prdno == produto.codigo && it.grade == produto.grade})
      fail("O produto já está adicionado")
    // Pedido.adicionarProduto(pedido, codigo, grade, qtty, localizacao)
    view.updateGrid()
  }
  
  fun removePedido(produto: ProdutoPedido?) = exec {
    val pedido = view.pedido ?: fail("Nenum pedido selecionado")
    produto ?: fail("Produto não selecionado")
    Pedido.removeProduto(pedido, produto)
    
    view.updateGrid()
  }
  
  fun findPedidos(ordno: Int?): Pedido? {
    return Pedido.findPedidos(ordno)
  }
}

interface IEditarView: IView {
  val pedido: Pedido?
  val produtos: List<ProdutoPedido>
  
  fun updateGrid()
  fun novoProduto(pedido: Pedido)
}

class ProdutoDlg(val pedido: Pedido) {
  var codigo: String = ""
  var grade: String = ""
  var localizacao: String = ""
  var qtty: Int? = 0
  var produtos: List<Produto> = emptyList()
  
  fun validadialog() {
    val userSaci = UserSaci.userAtual
    if(produtos.isEmpty())
      fail("Produto inválido")
    val quantidade = qtty ?: 0
    if(quantidade <= 0)
      fail("Quantidade inválida")
    val abreviacao = localizacao.mid(0, 4)
    if(!pedido.abreviacoes(userSaci).contains(abreviacao))
      fail("A localização $abreviacao não é compativel")
  }
}

data class PedidosLojaAbreviacao(val numero: Int, val abreviacao: String)