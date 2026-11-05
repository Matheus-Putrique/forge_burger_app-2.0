package com.example.forge_burger.viewmodel

import com.example.forge_burger.data.Adicional
import com.example.forge_burger.data.ID_CATEGORIA_LANCHES
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
    val produtosDaCategoriaDetalhe: List<Produto> = emptyList(),
    val favoritos: List<Int> = emptyList()
) {
    val produtosFiltrados: List<Produto>
        get() {
            val daCategoria = if (categoriaFiltroId == null) {
                listaProdutos
            } else {
                listaProdutos.filter { it.categoriaId == categoriaFiltroId }
            }
            return if (ordenarPorPreco) daCategoria.sortedBy { it.preco } else daCategoria
        }

    val resultadosBusca: List<Produto>
        get() {
            val termo = textoBusca.trim()
            return listaProdutos.filter {
                it.nome.contains(termo, ignoreCase = true) || it.descricao.contains(termo, ignoreCase = true)
            }
        }

    val quantidadePorCategoria: Map<Int, Int>
        get() = listaProdutos.groupingBy { it.categoriaId }.eachCount()

    val produtosFavoritos: List<Produto>
        get() {
            val produtoPorId = listaProdutos.associateBy { it.id }
            return favoritos.mapNotNull { produtoPorId[it] }
        }

    val precoUnitarioDetalhe: Double
        get() = (produtoDetalhe?.preco ?: 0.0) + adicionaisEscolhidos.sumOf { it.preco }

    val precoTotalDetalhe: Double
        get() = ((produtoDetalhe?.preco ?: 0.0) + adicionaisEscolhidos.sumOf { it.preco }) * quantidadeDetalhe

    val detalheEhLanche: Boolean
        get() = produtoDetalhe?.categoriaId == ID_CATEGORIA_LANCHES

    val detalheEhFavorito: Boolean
        get() {
            val idDoDetalhe = produtoDetalhe?.id ?: return false
            return favoritos.contains(idDoDetalhe)
        }

    val precoMedioCategoriaDetalhe: Double?
        get() {
            if (produtosDaCategoriaDetalhe.isEmpty()) {
                return null
            }
            return produtosDaCategoriaDetalhe.sumOf { it.preco } / produtosDaCategoriaDetalhe.size
        }

    val precoMinimoCategoriaDetalhe: Double?
        get() = produtosDaCategoriaDetalhe.minOfOrNull { it.preco }
}
