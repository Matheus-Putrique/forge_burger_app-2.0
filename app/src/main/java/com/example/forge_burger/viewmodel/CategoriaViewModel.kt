package com.example.forge_burger.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forge_burger.data.Categoria
import com.example.forge_burger.data.repository.CategoriaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoriaViewModel(
    private val repository: CategoriaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriaUiState())
    val uiState: StateFlow<CategoriaUiState> = _uiState.asStateFlow()

    init {
        observarCategoriasDoBanco()
    }

    private fun observarCategoriasDoBanco() {
        viewModelScope.launch {
            repository.carregarIniciaisSeVazio()
            repository.buscarTodos().collect { categorias ->
                _uiState.update { estadoAtual ->
                    val detalheAtualizado = estadoAtual.categoriaDetalhe?.let { atual ->
                        categorias.find { it.id == atual.id }
                    }
                    estadoAtual.copy(
                        listaCategorias = categorias,
                        carregando = false,
                        categoriaDetalhe = detalheAtualizado ?: estadoAtual.categoriaDetalhe
                    )
                }
            }
        }
    }

    fun mudarNome(novoNome: String) {
        _uiState.update { it.copy(nomeCampo = novoNome) }
    }

    fun mudarEmoji(novoEmoji: String) {
        if (novoEmoji.length <= 2) {
            _uiState.update { it.copy(emojiCampo = novoEmoji) }
        }
    }

    fun mudarDescricao(novaDescricao: String) {
        _uiState.update { it.copy(descricaoCampo = novaDescricao) }
    }

    fun comecarEdicao(categoria: Categoria) {
        _uiState.update {
            it.copy(
                nomeCampo = categoria.nome,
                emojiCampo = categoria.emoji,
                descricaoCampo = categoria.descricao,
                estouEditando = true,
                categoriaEmEdicao = categoria
            )
        }
    }

    fun limparFormulario() {
        _uiState.update {
            it.copy(
                nomeCampo = "",
                emojiCampo = "",
                descricaoCampo = "",
                estouEditando = false,
                categoriaEmEdicao = null
            )
        }
    }

    fun salvarCategoria() {
        val estado = _uiState.value
        val nomeLimpo = estado.nomeCampo.trim()
        if (nomeLimpo.isBlank()) return

        val iconeEscolhido = estado.emojiCampo.ifBlank { "🍽️" }
        val descricaoLimpa = estado.descricaoCampo.trim()

        viewModelScope.launch {
            if (estado.estouEditando && estado.categoriaEmEdicao != null) {
                val categoriaAtualizada = estado.categoriaEmEdicao.copy(
                    nome = nomeLimpo,
                    emoji = iconeEscolhido,
                    descricao = descricaoLimpa
                )
                repository.atualizar(categoriaAtualizada)
            } else {
                val novaCategoria = Categoria(
                    id = 0,
                    nome = nomeLimpo,
                    emoji = iconeEscolhido,
                    descricao = descricaoLimpa
                )
                repository.inserir(novaCategoria)
            }
            limparFormulario()
        }
    }

    fun removerCategoria(categoria: Categoria) {
        viewModelScope.launch {
            repository.deletar(categoria)
            if (_uiState.value.categoriaEmEdicao?.id == categoria.id) {
                limparFormulario()
            }
        }
    }

    fun carregarCategoriaPorId(categoriaId: Int) {
        val naMemoria = _uiState.value.listaCategorias.find { it.id == categoriaId }
        _uiState.update { it.copy(categoriaDetalhe = naMemoria) }

        viewModelScope.launch {
            val doBanco = repository.buscarPorId(categoriaId)
            _uiState.update { it.copy(categoriaDetalhe = doBanco) }
        }
    }
}
