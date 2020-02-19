package br.com.astrosoft.framework.view

import br.com.astrosoft.framework.model.RegistryUserInfo
import br.com.astrosoft.framework.view.LoginView.Companion.LOGIN_PATH
import br.com.astrosoft.separacao.model.saci
import br.com.astrosoft.separacao.view.DuplicarView
import br.com.astrosoft.separacao.view.EditarView
import br.com.astrosoft.separacao.view.RemoverView
import br.com.astrosoft.separacao.view.SeparacaoLayout
import br.com.astrosoft.separacao.view.SepararView
import com.github.mvysny.karibudsl.v10.navigateToView
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.BeforeEnterObserver
import com.vaadin.flow.router.Route
import com.vaadin.flow.theme.Theme
import com.vaadin.flow.theme.lumo.Lumo

@Route(LOGIN_PATH)
@Theme(value = Lumo::class, variant = Lumo.DARK)
class LoginView: VerticalLayout(), BeforeEnterObserver {
  private val appName = RegistryUserInfo.appName
  private val version = "Verssão ${RegistryUserInfo.version}"
  private val loginForm = LoginFormApp(appName, version) {
    SeparacaoLayout.updateLayout()
    navigateToView<LoginView>()
  }
  
  init {
    //add(loginForm)
    element.appendChild(loginForm.element)
    loginForm.isOpened = true
  }
  
  companion object {
    const val LOGIN_PATH = "login"
  }
  
  override fun beforeEnter(event: BeforeEnterEvent?) {
    saci.findUser(RegistryUserInfo.usuario)
      ?.let {usuario ->
        when {
          usuario.duplicar -> event?.rerouteTo(DuplicarView::class.java)
          usuario.separar  -> event?.rerouteTo(SepararView::class.java)
          usuario.remover  -> event?.rerouteTo(RemoverView::class.java)
          usuario.editar   -> event?.rerouteTo(EditarView::class.java)
          else             -> null
        }
      }
  }
}