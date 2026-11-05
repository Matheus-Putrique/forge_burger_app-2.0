package com.example.forge_burger.viewmodel

import com.example.forge_burger.data.ItemCarrinho
import com.example.forge_burger.data.Pedido
import com.example.forge_burger.data.Perfil

data class BurgerUiState(
    val perfilLogado: Perfil? = null,
    val usuarioCampoLogin: String = "",
    val senhaCampoLogin: String = "",
    val mostrarSenhaLogin: Boolean = false,
    val erroLogin: String? = null,
    val nomeUsuario: String = "Visitante",
    val endereco: String = "Rua das Brasas, 120 - Curitiba",
    val telefone: String = "",
    val nomeCampoPerfil: String = "Visitante",
    val enderecoCampoPerfil: String = "Rua das Brasas, 120 - Curitiba",
    val telefoneCampoPerfil: String = "",
    val carrinho: List<ItemCarrinho> = emptyList(),
    val formaPagamentoEscolhida: String = "Pix",
    val pedidos: List<Pedido> = emptyList(),
    val favoritos: List<Int> = emptyList()
)
