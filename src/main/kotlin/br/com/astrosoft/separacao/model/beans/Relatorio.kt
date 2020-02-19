package br.com.astrosoft.separacao.model.beans

data class Relatorio(
  val ordno: Int,
  val storeno: Int,
  val prdno: String,
  val localizacao: String,
  val name: String,
  val grade: String,
  val mfno_ref: String,
  val tipo: String,
  val qtty: Double,
  val fornecedor: Int,
  val estoque: Double,
  val embalagem: Double
                    )