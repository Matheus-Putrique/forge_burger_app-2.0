package com.example.forge_burger.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forge_burger.data.Adicional
import com.example.forge_burger.data.Produto
import com.example.forge_burger.data.repository.ProdutoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProdutoViewModel(
    private val repository: ProdutoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProdutoUiState())
    val uiState: StateFlow<ProdutoUiState> = _uiState.asStateFlow()

    val pontosCarne = listOf("Mal passado", "Ao ponto", "Bem passado")

    val adicionaisDisponiveis = listOf(
        Adicional("Bacon Extra", 3.50),
        Adicional("Cheddar Extra", 2.50),
        Adicional("Ovo", 2.00),
        Adicional("Cebola Caramelizada", 2.00)
    )

    init {
        viewModelScope.launch {
            repository.carregarIniciaisSeVazio()
            repository.buscarTodos().collect { produtos ->
                _uiState.update { estadoAtual ->
                    val listaDaCategoria = if (estadoAtual.categoriaDetalheId != null) {
                        produtos.filter { it.categoriaId == estadoAtual.categoriaDetalheId }
                    } else {
                        estadoAtual.produtosDaCategoriaDetalhe
                    }
                    val detalheAtualizado = if (estadoAtual.ultimoProdutoDetalheId != -1) {
                        produtos.find { it.id == estadoAtual.ultimoProdutoDetalheId }
                    } else {
                        estadoAtual.produtoDetalhe
                    }
                    estadoAtual.copy(
                        listaProdutos = produtos,
                        carregando = false,
                        produtosDaCategoriaDetalhe = listaDaCategoria,
                        produtoDetalhe = detalheAtualizado
                    )
                }
            }
        }
    }

    fun selecionarFiltroCategoria(categoriaId: Int?) {
        _uiState.update { it.copy(categoriaFiltroId = categoriaId) }
    }

    fun alternarOrdenacaoPorPreco() {
        _uiState.update { it.copy(ordenarPorPreco = !it.ordenarPorPreco) }
    }

    fun pedirConfirmacaoRemocao(produto: Produto) {
        _uiState.update { it.copy(produtoParaRemover = produto) }
    }

    fun cancelarRemocao() {
        _uiState.update { it.copy(produtoParaRemover = null) }
    }

    fun confirmarRemocaoProduto() {
        val produto = _uiState.value.produtoParaRemover ?: return
        _uiState.update { it.copy(produtoParaRemover = null) }
        viewModelScope.launch {
            repository.deletar(produto)
        }
    }

    fun prepararNovoProduto(categoriaSugeridaId: Int?, primeiraCategoriaId: Int?) {
        val idInicial = when {
            categoriaSugeridaId != null && categoriaSugeridaId > 0 -> categoriaSugeridaId
            primeiraCategoriaId != null -> primeiraCategoriaId
            else -> 1
        }
        _uiState.update {
            it.copy(
                estouEditando = false,
                produtoEmEdicao = null,
                emojiCampo = "",
                nomeCampo = "",
                precoCampo = "",
                descricaoCampo = "",
                categoriaIdEscolhida = idInicial
            )
        }
    }

    fun prepararEdicaoProduto(produto: Produto) {
        _uiState.update {
            it.copy(
                estouEditando = true,
                produtoEmEdicao = produto,
                emojiCampo = produto.emoji,
                nomeCampo = produto.nome,
                precoCampo = produto.preco.toString(),
                descricaoCampo = produto.descricao,
                categoriaIdEscolhida = produto.categoriaId
            )
        }
    }

    fun limparEdicaoProduto() {
        _uiState.update {
            it.copy(
                estouEditando = false,
                produtoEmEdicao = null,
                emojiCampo = "",
                nomeCampo = "",
                precoCampo = "",
                descricaoCampo = ""
            )
        }
    }

    fun mudarEmoji(novoEmoji: String) {
        if (novoEmoji.length <= 2) {
            _uiState.update { it.copy(emojiCampo = novoEmoji) }
        }
    }

    fun mudarNome(novoNome: String) {
        _uiState.update { it.copy(nomeCampo = novoNome) }
    }

    fun mudarPreco(novoTexto: String) {
        val textoFiltrado = novoTexto.filter { caractere ->
            caractere.isDigit() || caractere == ',' || caractere == '.'
        }
        _uiState.update { it.copy(precoCampo = textoFiltrado) }
    }

    fun mudarDescricao(novaDescricao: String) {
        _uiState.update { it.copy(descricaoCampo = novaDescricao) }
    }

    fun escolherCategoriaFormulario(categoriaId: Int) {
        _uiState.update { it.copy(categoriaIdEscolhida = categoriaId) }
    }

    fun salvarProduto() {
        val estado = _uiState.value
        val nomeLimpo = estado.nomeCampo.trim()
        val precoConvertido = estado.precoCampo.replace(',', '.').toDoubleOrNull()
        if (nomeLimpo.isBlank() || precoConvertido == null || precoConvertido <= 0.0) return

        val iconeEscolhido = estado.emojiCampo.ifBlank { "🍔" }
        val descricaoLimpa = estado.descricaoCampo.trim()

        viewModelScope.launch {
            if (estado.estouEditando && estado.produtoEmEdicao != null) {
                val produtoAtualizado = estado.produtoEmEdicao.copy(
                    nome = nomeLimpo,
                    descricao = descricaoLimpa,
                    preco = precoConvertido,
                    categoriaId = estado.categoriaIdEscolhida,
                    emoji = iconeEscolhido
                )
                repository.atualizar(produtoAtualizado)
            } else {
                val novoProduto = Produto(
                    id = 0,
                    nome = nomeLimpo,
                    descricao = descricaoLimpa,
                    preco = precoConvertido,
                    categoriaId = estado.categoriaIdEscolhida,
                    emoji = iconeEscolhido
                )
                repository.inserir(novoProduto)
            }
            _uiState.update {
                it.copy(
                    estouEditando = false,
                    produtoEmEdicao = null,
                    emojiCampo = "",
                    nomeCampo = "",
                    precoCampo = "",
                    descricaoCampo = ""
                )
            }
        }
    }

    fun alternarFavorito(produtoId: Int) {
        _uiState.update { estado ->
            val novaLista = if (estado.favoritos.contains(produtoId)) {
                estado.favoritos - produtoId
            } else {
                estado.favoritos + produtoId
            }
            estado.copy(favoritos = novaLista)
        }
    }

    fun mudarTextoBusca(novoTexto: String) {
        _uiState.update { it.copy(textoBusca = novoTexto) }
    }

    fun limparBusca() {
        _uiState.update { it.copy(textoBusca = "") }
    }

    fun abrirDetalhesProduto(produtoId: Int) {
        val estadoAtual = _uiState.value
        val naMemoria = estadoAtual.listaProdutos.find { it.id == produtoId }

        if (estadoAtual.ultimoProdutoDetalheId != produtoId) {
            _uiState.update {
                it.copy(
                    ultimoProdutoDetalheId = produtoId,
                    produtoDetalhe = naMemoria,
                    quantidadeDetalhe = 1,
                    pontoCarneEscolhido = pontosCarne[1],
                    adicionaisEscolhidos = emptyList(),
                    observacaoDetalhe = ""
                )
            }
        }

        viewModelScope.launch {
            val doBanco = repository.buscarPorId(produtoId)
            _uiState.update { it.copy(produtoDetalhe = doBanco) }
        }
    }

    fun diminuirQuantidadeDetalhe() {
        _uiState.update { estado ->
            if (estado.quantidadeDetalhe > 1) {
                estado.copy(quantidadeDetalhe = estado.quantidadeDetalhe - 1)
            } else {
                estado
            }
        }
    }

    fun aumentarQuantidadeDetalhe() {
        _uiState.update { it.copy(quantidadeDetalhe = it.quantidadeDetalhe + 1) }
    }

    fun escolherPontoCarne(ponto: String) {
        _uiState.update { it.copy(pontoCarneEscolhido = ponto) }
    }

    fun alternarAdicional(adicional: Adicional) {
        _uiState.update { estado ->
            val novaLista = if (estado.adicionaisEscolhidos.contains(adicional)) {
                estado.adicionaisEscolhidos - adicional
            } else {
                estado.adicionaisEscolhidos + adicional
            }
            estado.copy(adicionaisEscolhidos = novaLista)
        }
    }

    fun mudarObservacaoDetalhe(novaObservacao: String) {
        _uiState.update { it.copy(observacaoDetalhe = novaObservacao) }
    }

    fun limparPersonalizacaoAtual() {
        _uiState.update {
            it.copy(
                ultimoProdutoDetalheId = -1,
                quantidadeDetalhe = 1,
                pontoCarneEscolhido = pontosCarne[1],
                adicionaisEscolhidos = emptyList(),
                observacaoDetalhe = ""
            )
        }
    }

    fun carregarProdutosDaCategoria(categoriaId: Int) {
        val naMemoria = _uiState.value.listaProdutos.filter { it.categoriaId == categoriaId }
        _uiState.update {
            it.copy(
                categoriaDetalheId = categoriaId,
                produtosDaCategoriaDetalhe = naMemoria
            )
        }

        viewModelScope.launch {
            val doBanco = repository.buscarPorCategoria(categoriaId)
            _uiState.update { it.copy(produtosDaCategoriaDetalhe = doBanco) }
        }
    }
}
