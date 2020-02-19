package br.com.astrosoft.abastecimento.view

import br.com.astrosoft.abastecimento.model.beans.Pedido
import br.com.astrosoft.abastecimento.model.beans.ProdutoPedido
import br.com.astrosoft.abastecimento.model.beans.UserSaci
import br.com.astrosoft.abastecimento.viewmodel.EditarViewModel
import br.com.astrosoft.abastecimento.viewmodel.IEditarView
import br.com.astrosoft.abastecimento.viewmodel.ProdutoDlg
import br.com.astrosoft.framework.view.ViewLayout
import com.github.mvysny.karibudsl.v10.addColumnFor
import com.github.mvysny.karibudsl.v10.br
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.comboBox
import com.github.mvysny.karibudsl.v10.em
import com.github.mvysny.karibudsl.v10.formLayout
import com.github.mvysny.karibudsl.v10.getAll
import com.github.mvysny.karibudsl.v10.getColumnBy
import com.github.mvysny.karibudsl.v10.grid
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.isExpand
import com.github.mvysny.karibudsl.v10.refresh
import com.github.mvysny.karibudsl.v10.responsiveSteps
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY
import com.vaadin.flow.component.button.ButtonVariant.LUMO_SMALL
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.dependency.HtmlImport
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.grid.ColumnTextAlign.END
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.Grid.SelectionMode
import com.vaadin.flow.component.grid.GridSortOrder
import com.vaadin.flow.component.grid.GridVariant.LUMO_COMPACT
import com.vaadin.flow.component.icon.VaadinIcon.INSERT
import com.vaadin.flow.component.icon.VaadinIcon.SPLIT
import com.vaadin.flow.component.icon.VaadinIcon.TRASH
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant.LUMO_ALIGN_RIGHT
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.data.provider.SortDirection.ASCENDING
import com.vaadin.flow.data.renderer.NumberRenderer
import com.vaadin.flow.data.value.ValueChangeMode.EAGER
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import java.text.DecimalFormat

@Route(layout = SeparacaoLayout::class)
@PageTitle("Editar")
@HtmlImport("frontend://styles/shared-styles.html")
class EditarView: ViewLayout<EditarViewModel>(), IEditarView {
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
        this.valueChangeMode = EAGER
        this.addValueChangeListener {evento ->
          if(evento.isFromClient) {
            val pedido = viewModel.findPedidos(evento.value)
            updateGrid(pedido)
          }
        }
      }
      br()
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
    }
    toolbar {
      button("Processar") {
        icon = SPLIT.create()
        addThemeVariants(LUMO_PRIMARY)
        addClickListener {
          if(gridProduto.editor.isOpen)
            gridProduto.editor.save()
          //viewModel.processar()
        }
      }
      button("Novo produto") {
        icon = INSERT.create()
        addClickListener {
          if(gridProduto.editor.isOpen)
            gridProduto.editor.save()
          viewModel.novoProduto()
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
  
  override fun novoProduto(pedido: Pedido) {
    ProdutoDialog(viewModel, pedido).apply {
      open()
    }
  }
  
  private fun updateGrid(pedidoNovo: Pedido?) {
    val userSaci = UserSaci.userAtual
    dataProviderProdutos.items.clear()
    val user = userSaci
    val produto =
      pedidoNovo?.produtos(user)
        .orEmpty()
    dataProviderProdutos.items.addAll(produto)
    gridProduto.refresh()
  }
}

class ProdutoDialog(private val viewModel: EditarViewModel, val pedido: Pedido): Dialog() {
  private var produto: ProdutoDlg = ProdutoDlg(pedido)
  private lateinit var edtQtty: IntegerField
  private lateinit var edtDescricao: TextField
  private lateinit var edtGrade: ComboBox<String>
  private lateinit var edtLocalizacao: ComboBox<String>
  private lateinit var edtCodigo: TextField
  
  init {
    em("Adiciona Produto")
    formLayout {
      responsiveSteps {
        "0px"(4, top)
      }
      edtCodigo = textField("Código") {
        colspan = 1
        value = produto.codigo
        valueChangeMode = EAGER
        
        addValueChangeListener {event ->
          if(event.isFromClient) {
            val value = event.value
            val produtos = viewModel.findProduto(value)
            edtDescricao.value = produtos.firstOrNull()?.descricao ?: "Não encontrado"
            val grades =
              produtos.map {it.grade}
                .distinct()
                .sorted()
            edtGrade.setItems(grades)
            edtGrade.value = grades.firstOrNull()
            val localizacoes = produtos.map {it.localizacao}
              .distinct()
              .sorted()
            edtLocalizacao.setItems(localizacoes)
            edtLocalizacao.value = localizacoes.firstOrNull()
          }
        }
      }
      edtDescricao = textField("Descrição") {
        colspan = 3
        value = ""
        isReadOnly = true
      }
      edtGrade = comboBox("Grade") {
        colspan = 1
        isRequired = false
        isAllowCustomValue = false
      }
      edtQtty = integerField("Quantidade") {
        colspan = 1
        isAutoselect = true
        addThemeVariants(LUMO_ALIGN_RIGHT)
        value = produto.qtty
      }
      edtLocalizacao = comboBox("Localização") {
        colspan = 2
        isRequired = false
        isAllowCustomValue = false
      }
    }
    horizontalLayout {
      button("Salva") {
        addThemeVariants(LUMO_PRIMARY)
        addClickListener {
          val produto = produto.apply {
            codigo = edtCodigo.value ?: ""
            grade = edtGrade.value ?: ""
            qtty = edtQtty.value ?: 0
            produtos = viewModel.findProduto(codigo)
            localizacao = edtLocalizacao.value ?: ""
          }
          viewModel.salvaProduto(produto)
          this@ProdutoDialog.close()
        }
      }
      button("Cancela") {
        addClickListener {
          this@ProdutoDialog.close()
        }
      }
    }
  }
}