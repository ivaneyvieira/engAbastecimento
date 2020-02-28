package br.com.astrosoft.abastecimento.view

import br.com.astrosoft.abastecimento.model.beans.Pedido
import br.com.astrosoft.abastecimento.model.beans.ProdutoPedido
import br.com.astrosoft.abastecimento.model.beans.UserSaci
import br.com.astrosoft.abastecimento.viewmodel.EditarViewModel
import br.com.astrosoft.abastecimento.viewmodel.IEditarView
import br.com.astrosoft.framework.view.ViewLayout
import com.github.mvysny.karibudsl.v10.addColumnFor
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.getAll
import com.github.mvysny.karibudsl.v10.getColumnBy
import com.github.mvysny.karibudsl.v10.grid
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.isExpand
import com.github.mvysny.karibudsl.v10.label
import com.github.mvysny.karibudsl.v10.refresh
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY
import com.vaadin.flow.component.button.ButtonVariant.LUMO_SMALL
import com.vaadin.flow.component.dependency.HtmlImport
import com.vaadin.flow.component.grid.ColumnTextAlign.END
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.Grid.SelectionMode
import com.vaadin.flow.component.grid.GridSortOrder
import com.vaadin.flow.component.grid.GridVariant.LUMO_COMPACT
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.VaadinIcon.CHECK
import com.vaadin.flow.component.icon.VaadinIcon.PRINT
import com.vaadin.flow.component.icon.VaadinIcon.TRASH
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.data.provider.SortDirection.ASCENDING
import com.vaadin.flow.data.renderer.NumberRenderer
import com.vaadin.flow.data.value.ValueChangeMode.ON_CHANGE
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import java.text.DecimalFormat

@Route(layout = AbastecimentoLayout::class)
@PageTitle("Editar")
@HtmlImport("frontend://styles/shared-styles.html")
class EditarView: ViewLayout<EditarViewModel>(), IEditarView {
  private lateinit var lblGravado: Label
  private var gridProduto: Grid<ProdutoPedido>
  private lateinit var edtPedido: IntegerField
  override val viewModel: EditarViewModel = EditarViewModel(this)
  private val dataProviderProdutos = ListDataProvider<ProdutoPedido>(mutableListOf())
  
  override fun isAccept(user: UserSaci) = user.editar
  
  init {
    form("Editar pedidos") {
      isExpand = false
      edtPedido = integerField("Numero do pedido") {
        colspan = 1
        this.valueChangeMode = ON_CHANGE
        this.addValueChangeListener {evento ->
          if(evento.isFromClient) {
            val pedido = viewModel.findPedidos(evento.value)
            if(pedido == null) value = null
      
            updateGrid(pedido)
          }
        }
      }
      lblGravado = label {
        //this.horizontalAlignSelf = Alignment.END
      }
    }
    gridProduto = grid(dataProvider = dataProviderProdutos) {
      isExpand = true
      isMultiSort = true
      addThemeVariants(LUMO_COMPACT)
      setSelectionMode(SelectionMode.SINGLE)
      val binder = Binder<ProdutoPedido>(ProdutoPedido::class.java)
      editor.binder = binder
      editor.isBuffered = true
      val edtQtty = IntegerField().apply {
        this.isAutoselect = true
        this.width = "100%"
        this.isAutofocus = true
        this.element
          .addEventListener("keydown") {
            editor.save()
          }
          .filter = "event.key === 'Enter'"
        this.element
          .addEventListener("keydown") {
            editor.cancel()
          }
          .filter = "event.key === 'Escape'"
      }
      binder.bind(edtQtty, ProdutoPedido::qttyEdit.name)
      
      addItemClickListener {event ->
        when {
          editor.isOpen -> {
            editor.save()
          }
          else          -> {
            editor.editItem(event.item)
            edtQtty.focus()
          }
        }
      }
  
      editor.addSaveListener {event ->
        val produto = event.item
    
        binder.writeBean(produto)
      }
  
      addColumnFor(ProdutoPedido::codigo) {
        setHeader("Código")
        flexGrow = 1
        this.textAlign = END
      }
      addColumnFor(ProdutoPedido::descricao) {
        setHeader("Descrição")
        flexGrow = 8
      }
      addColumnFor(ProdutoPedido::grade) {
        setHeader("Grade")
        flexGrow = 1
      }
      addColumnFor(ProdutoPedido::abreviacao) {
        setHeader("Localização")
        flexGrow = 3
      }
  
      addColumnFor(ProdutoPedido::embalagem, NumberRenderer(ProdutoPedido::embalagem, DecimalFormat("0"))) {
        setHeader("Embalagem")
        flexGrow = 1
        this.textAlign = END
      }
      addColumnFor(ProdutoPedido::qttyEdit, NumberRenderer(ProdutoPedido::qttyEdit, DecimalFormat("0"))) {
        setHeader("QTD Pedido")
        flexGrow = 1
        this.textAlign = END
        setEditorComponent(edtQtty)
      }
      addColumnFor(ProdutoPedido::saldo, NumberRenderer(ProdutoPedido::saldo, DecimalFormat("0"))) {
        setHeader("Saldo Atual")
        flexGrow = 1
        this.textAlign = END
      }
      addColumnFor(ProdutoPedido::saldoFinal, NumberRenderer(ProdutoPedido::saldoFinal, DecimalFormat("0"))) {
        setHeader("Saldo Final")
        flexGrow = 1
        this.textAlign = END
      }
  
      addComponentColumn {produto ->
        Button(TRASH.create()).apply {
          this.addThemeVariants(LUMO_SMALL)
          addClickListener {
            val produtoInfo = "${produto.prdno}${if(produto.grade == "") "" else " - ${produto.grade}"}"
            showQuestion("Pode excluir o produto $produtoInfo?") {
              viewModel.removePedido(produto)
            }
          }
        }
      }
  
      sort(listOf(
        GridSortOrder(getColumnBy(ProdutoPedido::abreviacao), ASCENDING),
        GridSortOrder(getColumnBy(ProdutoPedido::descricao), ASCENDING),
        GridSortOrder(getColumnBy(ProdutoPedido::grade), ASCENDING)
                 ))
  
      this.setClassNameGenerator {
        when {
          it.saldoInsuficiente -> "error_row"
          it.qttyAlterada      -> "info_row"
          else                 -> null
        }
      }
    }
    toolbar {
      button("Gravar") {
        icon = CHECK.create()
        addThemeVariants(LUMO_PRIMARY)
        addClickListener {
          if(gridProduto.editor.isOpen)
            gridProduto.editor.save()
          viewModel.gravar()
        }
      }
      button("Imprimir") {
        icon = PRINT.create()
        addClickListener {
          if(gridProduto.editor.isOpen)
            gridProduto.editor.save()
          viewModel.imprimir()
        }
      }
    }
  }
  
  override val pedido: Pedido?
    get() = Pedido.findPedidos(edtPedido.value)
  override val produtos: List<ProdutoPedido>
    get() = dataProviderProdutos.getAll()
  
  override fun updateGrid() {
    val pedidoAtual = pedido
    updateGrid(pedidoAtual)
  }
  
  private fun updateGrid(pedidoNovo: Pedido?) {
    val userSaci = UserSaci.userAtual
    dataProviderProdutos.items.clear()
    val user = userSaci
    val produtos =
      pedidoNovo?.produtos(user)
        .orEmpty()
    dataProviderProdutos.items.addAll(produtos)
    gridProduto.refresh()
  
    if(pedidoNovo?.gravado == true)
      lblGravado.text = "GRAVADO"
    else
      lblGravado.text = ""
  }
}
