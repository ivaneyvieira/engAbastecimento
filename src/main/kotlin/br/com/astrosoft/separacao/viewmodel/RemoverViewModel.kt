package br.com.astrosoft.separacao.viewmodel

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.separacao.model.beans.Pedido

class RemoverViewModel(view: IRemoverView): ViewModel<IRemoverView>(view) {
  fun remover() {
    val pedidoIncial = view.numeroPedidoInicial ?: fail("Pedido inicial não encontrado")
    val pedidoFinal = view.numeroPedidoFinal ?: fail("Pedido final não encontrado")
    if(!pedidoIncial.compativel(pedidoFinal)) {
      fail("O pedido de destino não é compatível com o pedido de Origem")
    }
    else {
      Pedido.removePedido(pedidoIncial, pedidoFinal)
      view.showInformation("Pedidos excluidos com sucesso")
    }
  }
  
  fun pedidos(): List<Pedido> {
    return Pedido.pedidos()
  }
}

interface IRemoverView: IView {
  var numeroPedidoInicial: Pedido?
  var numeroPedidoFinal: Pedido?
}