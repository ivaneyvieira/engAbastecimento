package br.com.astrosoft.abastecimento.model.beans

import br.com.astrosoft.abastecimento.model.saci
import br.com.astrosoft.framework.model.RegistryUserInfo
import kotlin.math.pow

class UserSaci {
  var no: Int? = 0
  var name: String? = ""
  var storeno: Int? = 0
  var login: String? = ""
  var senha: String? = ""
  val prntno : Int = 0
  val impressora : String = ""
  private var bitAcesso: Int? = 0
  
  //Otiros campos
  var ativo: Boolean = true

  var editar: Boolean = false
  var abreviacoes: String = ""
  val admin
    get() = login == "ADM"
  var listAbreviacoes: Set<String>
    get() = abreviacoes.trim()
      .split(",")
      .toList()
      .filter {it != ""}
      .toSet()
    set(value) {
      abreviacoes = value.joinToString(separator = ",")
        .trim()
    }
  
  fun initVars(): UserSaci {
    val bits = bitAcesso ?: 0
    ativo = (bits and 2.toDouble().pow(5).toInt()) != 0 || admin
    editar = (bits and 2.toDouble().pow(6).toInt()) != 0 || admin
    return this
  }
  
  fun bitAcesso(): Int {
    val ativoSum = if(ativo) 2.toDouble().pow(5).toInt()
    else 0
    val editarSum = if(editar) 2.toDouble().pow(6).toInt() else 0
    return ativoSum + editarSum
  }
  
  fun isLocalizacaoCompativel(abreviacao: String): Boolean {
    return when {
      abreviacao == "" -> false
      admin            -> true
      editar           -> {
        abreviacao in listAbreviacoes
      }
      else             -> true
    }
  }
  
  companion object {
    val userAtual
      get() = saci.findUser(RegistryUserInfo.usuario)
  
    fun findAll(): List<UserSaci>? {
      return saci.findAllUser()
        .filter {it.ativo}
    }
  
    fun updateUser(user: UserSaci) {
      saci.updateUser(user)
    }
  
    fun findUser(login: String?): UserSaci? {
      return saci.findUser(login)
    }
  }
}
