package br.com.astrosoft.separacao.model.beans

import br.com.astrosoft.framework.model.RegistryUserInfo
import br.com.astrosoft.framework.util.mid
import br.com.astrosoft.separacao.model.saci
import kotlin.math.pow

class UserSaci {
  var no: Int? = 0
  var name: String? = ""
  var storeno: Int? = 0
  var login: String? = ""
  var senha: String? = ""
  private var bitAcesso: Int? = 0
  //Otiros campos
  var ativo: Boolean = true
  var duplicar: Boolean = false
  var separar: Boolean = false
  var remover: Boolean = false
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
    ativo = (bits and 2.toDouble().pow(0).toInt()) != 0 || admin
    duplicar = (bits and 2.toDouble().pow(1).toInt()) != 0 || admin
    separar = (bits and 2.toDouble().pow(2).toInt()) != 0 || admin
    remover = (bits and 2.toDouble().pow(3).toInt()) != 0 || admin
    editar = (bits and 2.toDouble().pow(4).toInt()) != 0 || admin
    return this
  }
  
  fun bitAcesso(): Int {
    val ativoSum = if(ativo) 2.toDouble().pow(0).toInt()
    else 0
    val duplicarSum = if(duplicar) 2.toDouble().pow(1).toInt()
    else 0
    val separarSum = if(separar) 2.toDouble().pow(2).toInt()
    else 0
    val removerSum = if(remover) 2.toDouble().pow(3).toInt()
    else 0
    val editarSum = if(editar) 2.toDouble().pow(4).toInt() else 0
    return ativoSum + duplicarSum + separarSum + removerSum + editarSum
  }
  
  fun isLocalizacaoCompativel(localizacao: String): Boolean {
    return when {
      localizacao == "" -> true
      admin             -> true
      editar            -> {
        val abreviacao = localizacao.mid(0, 4)
        abreviacao in listAbreviacoes
      }
      else              -> true
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
