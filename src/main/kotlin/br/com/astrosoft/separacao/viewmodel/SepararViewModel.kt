package br.com.astrosoft.separacao.viewmodel

import br.com.astrosoft.framework.util.ECupsPrinter
import br.com.astrosoft.framework.util.Ssh
import br.com.astrosoft.framework.util.execCommand
import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.separacao.model.beans.Pedido
import br.com.astrosoft.separacao.model.beans.ProdutoPedido
import br.com.astrosoft.separacao.model.beans.UserSaci
import br.com.astrosoft.separacao.model.enum.ETipoOrigem.SEPARADO

class SepararViewModel(view: ISepararView): ViewModel<ISepararView>(view) {
  fun separar() = exec {
    val pedido = view.pedido ?: fail("Pedido inválido")
    val storenoDestino = pedido.storenoDestino
    val ordno = pedido.ordno
    val proximoNumero = Pedido.proximoNumeroSeparado(storenoDestino)
    val produtosSelecionados = view.produtosSelecionados
    if(produtosSelecionados.isEmpty())
      fail("Não há nenhum produto selecionado")
    else
      produtosSelecionados.forEach {produto ->
        Pedido.atualizarQuantidade(ordno, proximoNumero, produto, SEPARADO)
      }
    print(proximoNumero)
    view.showInformation("Foi gerado o pedido número $proximoNumero")
    view.updateGrid()
  }
  
  fun proximoNumero(): Int {
    val pedido = view.pedido
    val storenoDestino = pedido?.storenoDestino ?: 0
    return Pedido.proximoNumeroSeparado(storenoDestino)
  }
  
  fun imprimir() = exec {
    val pedido = view.pedido ?: fail("Pedido inválido")
    print(pedido.ordno)
  }
  
  private fun print(ordno: Int) {
    try {
      RelatorioText().print("RESSUPRIMENTO", Pedido.listaRelatorio(ordno))
    } catch(e: ECupsPrinter) {
      view.showError(e.message ?: "Erro de impressão")
      Ssh("172.20.47.1", "ivaney", "ivaney").shell {
        execCommand("/u/saci/shells/printRessuprimento.sh $ordno")
      }
    }
  }
  
  fun pedidos(): List<Pedido> {
    val user = UserSaci.userAtual
    return Pedido.pedidos(user)
  }
}

interface ISepararView: IView {
  val pedido: Pedido?
  val produtosSelecionados: List<ProdutoPedido>
  
  fun updateGrid()
}