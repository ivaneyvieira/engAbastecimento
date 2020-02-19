package br.com.astrosoft.abastecimento.view

import br.com.astrosoft.abastecimento.model.beans.UserSaci
import br.com.astrosoft.abastecimento.viewmodel.DefautlViewModel
import br.com.astrosoft.abastecimento.viewmodel.IDefaultView
import br.com.astrosoft.framework.view.ViewLayout
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route

@Route(value = "", layout = SeparacaoLayout::class)
@PageTitle("")
class DefaultView: ViewLayout<DefautlViewModel>(), IDefaultView {
  override val viewModel = DefautlViewModel(this)
  
  override fun beforeEnter(event: BeforeEnterEvent?) {
    event?.forwardTo(EditarView::class.java)
    super.beforeEnter(event)
  }
  
  override fun isAccept(user: UserSaci): Boolean {
    return true
  }
}