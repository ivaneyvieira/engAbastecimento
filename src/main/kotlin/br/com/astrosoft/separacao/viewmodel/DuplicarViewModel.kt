package br.com.astrosoft.separacao.viewmodel

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.separacao.model.beans.Pedido
import br.com.astrosoft.separacao.model.beans.UserSaci

class DuplicarViewModel(view: IDuplicarView): ViewModel<IDuplicarView>(view) {
  override fun init() {
    view.informarNumero = true
  }
  
  fun proximoNumero(): Int? {
    val pedidoOrigem = Pedido(1, view.numeroOrigem ?: 0, 0, "")
    return run {
      val loja = pedidoOrigem.storenoDestino
      Pedido.proximoNumeroDuplicado(loja)
    }
  }
  
  fun duplicar() = exec {
    val userSaci = UserSaci.userAtual
    val pedidoOrigem = Pedido.findPedidos(view.numeroOrigem) ?: fail("Pedido de origem não encontrado")
    val pedidoDestino = Pedido.findPedidos(view.numeroDestino) ?: Pedido(1, view.numeroDestino ?: 0, 0, "")
    when {
      pedidoDestino.isNotEmpty(userSaci)      -> fail("O pedido de destino já existe")
      !pedidoDestino.compativel(pedidoOrigem) -> fail("O pedido de destino não é compatível com o pedido de Origem")
      else                                    -> {
        Pedido.duplicar(pedidoOrigem, pedidoDestino)
        if(view.informarNumero == false)
          view.numeroDestino = proximoNumero()
      }
    }
    view.showInformation("Pedido duplicado com sucesso. Novo pedido: ${pedidoDestino.ordno}")
  }
  
  fun pedidos(): List<Pedido> {
    return Pedido.pedidos()
  }
}

interface IDuplicarView: IView {
  var numeroOrigem: Int?
  var numeroDestino: Int?
  var informarNumero: Boolean?
}