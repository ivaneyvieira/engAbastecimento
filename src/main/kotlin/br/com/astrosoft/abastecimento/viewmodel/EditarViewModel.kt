package br.com.astrosoft.abastecimento.viewmodel

import br.com.astrosoft.abastecimento.model.beans.Pedido
import br.com.astrosoft.abastecimento.model.beans.Produto
import br.com.astrosoft.abastecimento.model.beans.ProdutoPedido
import br.com.astrosoft.abastecimento.model.beans.UserSaci
import br.com.astrosoft.abastecimento.model.saci
import br.com.astrosoft.framework.util.mid
import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel
import br.com.astrosoft.framework.viewmodel.fail

class EditarViewModel(view: IEditarView): ViewModel<IEditarView>(view) {
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
    val pedido = Pedido.findPedidos(ordno) ?: return null
    return when {
      !pedido.isClienteValido -> {
        view.showError("O cliente não é válido")
        null
      }
      !pedido.isMetodoValido  -> {
        view.showError("O método de pagamento não é válido")
        null
      }
      !pedido.isStatusValido  -> {
        view.showError("O status do pedido não é orçamento")
        null
      }
      else                    -> pedido
    }
  }
  
  fun gravar() = exec {
    val pedido = view.pedido ?: fail("Nenum pedido selecionado")
    view.produtos.forEach {produto ->
      if(produto.qtty != produto.qttyEdit)
        if((produto.saldo - produto.qttyEdit) >= 0)
          saci.atualizarQuantidade(pedido.ordno, produto.prdno, produto.grade, produto.qttyEdit)
    }
    view.updateGrid()
  }
  
  fun imprimir() = exec {
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