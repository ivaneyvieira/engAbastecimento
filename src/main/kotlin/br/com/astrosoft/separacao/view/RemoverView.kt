package br.com.astrosoft.separacao.view

import br.com.astrosoft.framework.view.ViewLayout
import br.com.astrosoft.separacao.model.beans.Pedido
import br.com.astrosoft.separacao.model.beans.UserSaci
import br.com.astrosoft.separacao.viewmodel.IRemoverView
import br.com.astrosoft.separacao.viewmodel.RemoverViewModel
import com.github.mvysny.karibudsl.v10.br
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.comboBox
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route

@Route(layout = SeparacaoLayout::class)
@PageTitle("Remover")
class RemoverView: ViewLayout<RemoverViewModel>(), IRemoverView {
  private lateinit var cmbNumeroFinal: ComboBox<Pedido>
  private lateinit var cmbNumeroInicial: ComboBox<Pedido>
  override val viewModel = RemoverViewModel(this)
  
  override fun isAccept(user: UserSaci) = user.remover
  
  init {
    form("Remover") {
      cmbNumeroInicial = comboBox("Pedido incial") {
        colspan = 1
        setItems(viewModel.pedidos())
        setItemLabelGenerator {it.ordno.toString()}
        isAllowCustomValue = false
        isPreventInvalidInput = false
      }
      br()
      cmbNumeroFinal = comboBox("Pedido final") {
        colspan = 1
        setItems(viewModel.pedidos())
        setItemLabelGenerator {it.ordno.toString()}
        isAllowCustomValue = false
        isPreventInvalidInput = false
      }
    }
    toolbar {
      button("Excluir") {
        icon = VaadinIcon.TRASH.create()
        addClickListener {
          viewModel.remover()
        }
      }
    }
  }
  
  override var numeroPedidoInicial: Pedido?
    get() = cmbNumeroInicial.value
    set(value) {
      cmbNumeroInicial.value = value
    }
  override var numeroPedidoFinal: Pedido?
    get() = cmbNumeroFinal.value
    set(value) {
      cmbNumeroFinal.value = value
    }
}