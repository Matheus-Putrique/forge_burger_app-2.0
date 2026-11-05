package com.example.forge_burger.viewmodel

import com.example.forge_burger.data.Categoria

data class CategoriaUiState(
    val listaCategorias: List<Categoria> = emptyList(),
    val carregando: Boolean = true,
    val nomeCampo: String = "",
    val emojiCampo: String = "",
    val descricaoCampo: String = "",
    val estouEditando: Boolean = false,
    val categoriaEmEdicao: Categoria? = null,
    val categoriaDetalhe: Categoria? = null
) {
    val categoriaPorId: Map<Int, Categoria>
        get() = listaCategorias.associateBy { it.id }

    val primeiraCategoriaId: Int?
        get() = listaCategorias.firstOrNull()?.id
}
