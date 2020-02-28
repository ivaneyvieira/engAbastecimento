package br.com.astrosoft.abastecimento.viewmodel

import br.com.astrosoft.abastecimento.model.beans.Relatorio
import br.com.astrosoft.framework.model.RegistryUserInfo.usuario
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.util.lpad
import br.com.astrosoft.framework.util.mid
import br.com.astrosoft.framework.util.rpad
import br.com.astrosoft.framework.viewmodel.PrintText
import java.time.LocalDate
import java.time.LocalTime

class RelatorioTextEpson: PrintText<Relatorio>() {
  init {
    columText("Cod", 6) {prdno.lpad(6, "0")}
    columText("Descricao", 30) {name}
    columText("Grade", 8) {grade}
    columNumber("Qtd", 4) {qtty}
    columNumber("Estoq", 5) {saldoApp.toDouble()}
  
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
    return listOf("Abastecimento Loja MF $pedido $abreviacao".negrito(),
                  "Data: $data      Hora $hora".negrito(),
                  "",
                  "Requisitado: ${usuario?.rpad(12, " ")} Recebido: ____________________".negrito(),
                  "")
  }
}