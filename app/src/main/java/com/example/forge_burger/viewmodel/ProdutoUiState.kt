package com.example.forge_burger.viewmodel

import com.example.forge_burger.data.Adicional
import com.example.forge_burger.data.Produto

data class ProdutoUiState(
    val listaProdutos: List<Produto> = emptyList(),
    val carregando: Boolean = true,
    val categoriaFiltroId: Int? = null,
    val ordenarPorPreco: Boolean = false,
    val produtoParaRemover: Produto? = null,
    val estouEditando: Boolean = false,
    val produtoEmEdicao: Produto? = null,
    val emojiCampo: String = "",
    val nomeCampo: String = "",
    val precoCampo: String = "",
    val descricaoCampo: String = "",
    val categoriaIdEscolhida: Int = 1,
    val textoBusca: String = "",
    val ultimoProdutoDetalheId: Int = -1,
    val produtoDetalhe: Produto? = null,
    val quantidadeDetalhe: Int = 1,
    val pontoCarneEscolhido: String = "Ao ponto",
    val adicionaisEscolhidos: List<Adicional> = emptyList(),
    val observacaoDetalhe: String = "",
    val categoriaDetalheId: Int? = null,
    val produtosDaCategoriaDetalhe: List<Produto> = emptyList()
)
