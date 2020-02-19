package br.com.astrosoft.separacao.viewmodel

import br.com.astrosoft.framework.util.mid
import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.separacao.model.beans.Pedido
import br.com.astrosoft.separacao.model.beans.Produto
import br.com.astrosoft.separacao.model.beans.ProdutoPedido
import br.com.astrosoft.separacao.model.beans.UserSaci
import br.com.astrosoft.separacao.model.enum.ETipoOrigem.LOJA
import br.com.astrosoft.separacao.model.enum.ETipoOrigem.SEPARADO

class EditarViewModel(view: IEditarView): ViewModel<IEditarView>(view) {
  fun processar() = exec {
    val pedido = view.pedido ?: fail("Nenum pedido selecionado")
    val produtos = view.produtos
    val setPedidosLoja = mutableSetOf<PedidosLojaAbreviacao>()
    
    produtos.forEach {produto ->
      if(produto.estoqueLoja == true) {
        val abreviacao = produto.localizacao.mid(0, 4)
        val proximoNumero = Pedido.proximoNumeroPedidoLoja(pedido.storenoDestino, abreviacao)
        Pedido.atualizarQuantidade(ordno = pedido.ordno,
                                   proximoNumero = proximoNumero,
                                   produto = produto,
                                   tipo = LOJA)
        setPedidosLoja.add(PedidosLojaAbreviacao(proximoNumero, abreviacao))
      }
      else {
        Pedido.retornaSaldo(pedido, produto)
      }
    }
    setPedidosLoja.forEach {pedidosLojaAbreviacao ->
      pedidosLojaAbreviacao.run {
        view.showInformation("Foi criado o pedido para o estoque de loja número $numero ($abreviacao)")
      }
    }
    view.updateGrid()
  }
  
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
    Pedido.adicionarProduto(pedido, codigo, grade, qtty, localizacao)
    view.updateGrid()
  }
  
  fun removePedido(produto: ProdutoPedido?) = exec {
    val pedido = view.pedido ?: fail("Nenum pedido selecionado")
    produto ?: fail("Produto não selecionado")
    Pedido.retornaSaldo(pedido, produto.apply {qttyEdit = 0})
    
    view.updateGrid()
  }
  
  fun pedidos(): List<Pedido> {
    val user = UserSaci.userAtual
    return Pedido.pedidos(user)
  }
  
  val pedidosSeparacao: List<Pedido>
    get() = pedidos().filter {
      it.tipoOrigem == SEPARADO || it.tipoOrigem == LOJA
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