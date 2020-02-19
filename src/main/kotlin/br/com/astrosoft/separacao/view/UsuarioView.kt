package br.com.astrosoft.separacao.view

import br.com.astrosoft.framework.view.ViewLayout
import br.com.astrosoft.framework.view.multiselectComboBox
import br.com.astrosoft.separacao.model.beans.UserSaci
import br.com.astrosoft.separacao.viewmodel.IUsuarioView
import br.com.astrosoft.separacao.viewmodel.UsuarioViewModel
import com.github.mvysny.karibudsl.v10.alignSelf
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.checkBox
import com.github.mvysny.karibudsl.v10.formLayout
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.hr
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant.LUMO_ERROR
import com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY
import com.vaadin.flow.component.grid.ColumnTextAlign
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant.LUMO_COMPACT
import com.vaadin.flow.component.icon.VaadinIcon.CHECK_CIRCLE_O
import com.vaadin.flow.component.icon.VaadinIcon.CIRCLE_THIN
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.END
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import org.claspina.confirmdialog.ConfirmDialog
import org.vaadin.crudui.crud.CrudOperation
import org.vaadin.crudui.crud.CrudOperation.ADD
import org.vaadin.crudui.crud.CrudOperation.DELETE
import org.vaadin.crudui.crud.CrudOperation.READ
import org.vaadin.crudui.crud.CrudOperation.UPDATE
import org.vaadin.crudui.crud.impl.GridCrud
import org.vaadin.crudui.form.AbstractCrudFormFactory

@Route(layout = SeparacaoLayout::class)
@PageTitle("Usuário")
class UsuarioView: ViewLayout<UsuarioViewModel>(), IUsuarioView {
  override val viewModel = UsuarioViewModel(this)
  
  override fun isAccept(user: UserSaci) = user.admin
  
  init {
    form("Editor de usuários")
    setSizeFull()
    val crud: GridCrud<UserSaci> = gridCrud()
    // layout configuration
    setSizeFull()
    this.add(crud)
    // logic configuration
    setOperationd(crud)
  }
  
  private fun gridCrud(): GridCrud<UserSaci> {
    val captionFields = listOf("Numero", "Login", "Nome", "Duplicar", "Separar", "Editar", "Remover", "")
    val crud: GridCrud<UserSaci> = GridCrud<UserSaci>(UserSaci::class.java)
    crud.grid
      .setColumns(UserSaci::no.name, UserSaci::login.name, UserSaci::name.name)
    crud.grid.addColumnBool("Duplicar") {duplicar}
    crud.grid.addColumnBool("Separar") {separar}
    crud.grid.addColumnBool("Editar") {editar}
    crud.grid.addColumnBool("Remover") {remover}
  
    crud.grid.addThemeVariants(LUMO_COMPACT)
  
    crud.crudFormFactory = UserCrudFormFactory(viewModel)
    /*
    crud.crudFormFactory
      .setUseBeanValidation(true)
    val allFields = listOf(UserSaci::no.name,
                           UserSaci::login.name,
                           UserSaci::name.name,
                           UserSaci::duplicar.name,
                           UserSaci::separar.name,
                           UserSaci::editar.name,
                           UserSaci::remover.name,
                           UserSaci::listAbreviacoes.name)
    crud.crudFormFactory.setVisibleProperties(*allFields.toTypedArray())
    crud.crudFormFactory.setDisabledProperties(UPDATE, UserSaci::no.name, UserSaci::login.name, UserSaci::name.name)
    crud.crudFormFactory.setFieldCaptions(UPDATE, * captionFields.toTypedArray())
    crud.crudFormFactory.setDisabledProperties(READ, *allFields.toTypedArray())
    crud.crudFormFactory.setFieldCaptions(READ, * captionFields.toTypedArray())
    crud.crudFormFactory.setDisabledProperties(DELETE, *allFields.toTypedArray())
    crud.crudFormFactory.setFieldCaptions(DELETE, * captionFields.toTypedArray())
    crud.crudFormFactory.setDisabledProperties(ADD, UserSaci::no.name,
                                               UserSaci::name.name)
    crud.crudFormFactory.setVisibleProperties(ADD,
                                              UserSaci::login.name,
                                              UserSaci::duplicar.name,
                                              UserSaci::separar.name,
                                              UserSaci::editar.name,
                                              UserSaci::editar.name,
                                              UserSaci::listAbreviacoes.name)
    crud.crudFormFactory.setFieldCaptions(ADD, "Login", "Duplicar", "Separar", "Editar", "Remover", "Localizações")
    crud.crudFormFactory.setFieldProvider(UserSaci::listAbreviacoes.name) {
      TwinColSelect<String>().apply {
        this.setItems(viewModel.abreviacoes())
      }
    }
    */
    crud.setSizeFull()
    return crud
  }
  
  private fun setOperationd(crud: GridCrud<UserSaci>) {
    crud.setOperations(
      {viewModel.findAll()},
      {user: UserSaci -> viewModel.add(user)},
      {user: UserSaci? -> viewModel.update(user)},
      {user: UserSaci? -> viewModel.delete(user)})
  }
  
  private fun Grid<UserSaci>.addColumnBool(caption: String, value: UserSaci.() -> Boolean) {
    val column = this.addComponentColumn {bean ->
      if(bean.value()) CHECK_CIRCLE_O.create()
      else CIRCLE_THIN.create()
    }
    column.setHeader(caption)
    column.textAlign = ColumnTextAlign.CENTER
  }
}

class UserCrudFormFactory(private val viewModel: UsuarioViewModel): AbstractCrudFormFactory<UserSaci>() {
  override fun buildNewForm(operation: CrudOperation?,
                            domainObject: UserSaci?,
                            readOnly: Boolean,
                            cancelButtonClickListener: ComponentEventListener<ClickEvent<Button>>?,
                            operationButtonClickListener: ComponentEventListener<ClickEvent<Button>>?): Component {
    val binder = Binder<UserSaci>(UserSaci::class.java)
    return VerticalLayout().apply {
      isSpacing = false
      isMargin = false
      formLayout {
        if(operation in listOf(READ, DELETE, UPDATE))
          integerField("Número") {
            isReadOnly = true
            binder.bind(this, UserSaci::no.name)
          }
        if(operation in listOf(ADD, READ, DELETE, UPDATE))
          textField("Login") {
            binder.bind(this, UserSaci::login.name)
          }
        if(operation in listOf(READ, DELETE, UPDATE))
          textField("Nome") {
            isReadOnly = true
            binder.bind(this, UserSaci::name.name)
          }
        if(operation in listOf(ADD, READ, DELETE, UPDATE))
          multiselectComboBox<String> {
            this.label = "Localização"
            this.setItems(viewModel.abreviacoes())
            //placeholder = "Escolha as localizações"
            // isClearButtonVisible = true
            binder.bind(this, UserSaci::listAbreviacoes.name)
          }
        
        if(operation in listOf(ADD, READ, DELETE, UPDATE)) {
          checkBox("Duplicar") {
            binder.bind(this, UserSaci::duplicar.name)
          }
          checkBox("Separar") {
            binder.bind(this, UserSaci::separar.name)
          }
          checkBox("Editar") {
            binder.bind(this, UserSaci::editar.name)
          }
          checkBox("Remover") {
            binder.bind(this, UserSaci::remover.name)
          }
        }
      }
      hr()
      horizontalLayout {
        this.setWidthFull()
        this.justifyContentMode = JustifyContentMode.END
        button("Confirmar") {
          alignSelf = END
          addThemeVariants(LUMO_PRIMARY)
          addClickListener {
            binder.writeBean(domainObject)
            operationButtonClickListener?.onComponentEvent(it)
          }
        }
        button("Cancelar") {
          alignSelf = END
          addThemeVariants(LUMO_ERROR)
          addClickListener(cancelButtonClickListener)
        }
      }
      
      binder.readBean(domainObject)
    }
  }
  
  override fun buildCaption(operation: CrudOperation?, domainObject: UserSaci?): String {
    return operation?.let {crudOperation ->
      when(crudOperation) {
        READ   -> "Consulta"
        ADD    -> "Adiciona"
        UPDATE -> "Atualiza"
        DELETE -> "Remove"
      }
    } ?: "Erro"
  }
  
  override fun showError(operation: CrudOperation?, e: Exception?) {
    ConfirmDialog.createError()
      .withCaption("Erro do aplicativo")
      .withMessage(e?.message ?: "Erro desconhecido")
      .open()
  }
}