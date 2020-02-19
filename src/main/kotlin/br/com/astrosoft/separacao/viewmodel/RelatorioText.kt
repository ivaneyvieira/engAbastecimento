package br.com.astrosoft.separacao.viewmodel

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.util.lpad
import br.com.astrosoft.framework.util.mid
import br.com.astrosoft.framework.viewmodel.PrintText
import br.com.astrosoft.separacao.model.beans.Relatorio
import java.time.LocalDate
import java.time.LocalTime

class RelatorioText: PrintText<Relatorio>() {
  init {
    columText("Cod", 6) {prdno.lpad(6, "0")}
    columText("Descricao", 30) {name}
    columText("Grade", 8) {grade}
    columNumber("Qtd", 8) {qtty}
    columNumber("Estoque", 8, lineBreak = true) {estoque}
    
    columText("Forn", 6) {
      fornecedor.toString()
        .lpad(6, " ")
    }
    columText("Local", 19) {localizacao}
    columText("Referencia", 27) {mfno_ref}
  }
  
  override fun sumaryLine(): List<String> {
    return listOf("",
                  "",
                  "DOCUMENTO NAO FISCAL".negrito(),
                  "")
  }
  
  override fun titleLines(bean: Relatorio): List<String> {
    val pedido = "${bean.ordno}"
    val abreviacao = bean.localizacao.mid(0, 4)
    val data =
      LocalDate.now()
        .format()
    val hora =
      LocalTime.now()
        .format()
    return listOf("Ressuprimento Pedido $pedido $abreviacao".negrito(),
                  "Data: $data      Hora $hora".negrito(),
                  "",
                  "Pedido Loja: _________________ Separador: __________________".negrito(),
                  "",
                  "00$pedido".barras())
  }
}