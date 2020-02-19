package br.com.astrosoft.separacao.view

import br.com.astrosoft.framework.view.ViewLayout
import br.com.astrosoft.separacao.model.beans.UserSaci
import br.com.astrosoft.separacao.viewmodel.DefautlViewModel
import br.com.astrosoft.separacao.viewmodel.IDefaultView
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route

@Route(value = "", layout = SeparacaoLayout::class)
@PageTitle("")
class DefaultView: ViewLayout<DefautlViewModel>(), IDefaultView {
  override val viewModel = DefautlViewModel(this)
  
  override fun beforeEnter(event: BeforeEnterEvent?) {
    event?.forwardTo(DuplicarView::class.java)
    super.beforeEnter(event)
  }
  
  override fun isAccept(user: UserSaci): Boolean {
    return true
  }
}