package com.example.forge_burger.viewmodel

import androidx.lifecycle.ViewModel
import com.example.forge_burger.data.Adicional
import com.example.forge_burger.data.ItemCarrinho
import com.example.forge_burger.data.Pedido
import com.example.forge_burger.data.Perfil
import com.example.forge_burger.data.Produto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BurgerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BurgerUiState())
    val uiState: StateFlow<BurgerUiState> = _uiState.asStateFlow()

    val taxaEntrega = 4.99
    val formasPagamento = listOf("Pix", "Cartão na entrega", "Dinheiro")

    private val credenciais = mapOf(
        "admin" to ("admin123" to Perfil.ADMIN),
        "user" to ("1234" to Perfil.CLIENTE)
    )

    private var proximoIdItem = 1
    private var proximoNumeroPedido = 1001

    fun mudarUsuarioLogin(novoUsuario: String) {
        _uiState.update { it.copy(usuarioCampoLogin = novoUsuario, erroLogin = null) }
    }

    fun mudarSenhaLogin(novaSenha: String) {
        _uiState.update { it.copy(senhaCampoLogin = novaSenha, erroLogin = null) }
    }

    fun alternarVisibilidadeSenha() {
        _uiState.update { it.copy(mostrarSenhaLogin = !it.mostrarSenhaLogin) }
    }

    fun tentarEntrar(): Boolean {
        val estado = _uiState.value
        val usuarioLimpo = estado.usuarioCampoLogin.trim().lowercase()
        val senhaDigitada = estado.senhaCampoLogin

        if (usuarioLimpo.isBlank() || senhaDigitada.isBlank()) {
            _uiState.update { it.copy(erroLogin = "Preencha usuário e senha") }
            return false
        }

        val dadosConta = credenciais[usuarioLimpo]
        if (dadosConta == null || dadosConta.first != senhaDigitada) {
            _uiState.update { it.copy(erroLogin = "Usuário ou senha incorretos") }
            return false
        }

        _uiState.update {
            it.copy(
                perfilLogado = dadosConta.second,
                usuarioCampoLogin = "",
                senhaCampoLogin = "",
                mostrarSenhaLogin = false,
                erroLogin = null
            )
        }
        return true
    }

    fun sairDaConta() {
        _uiState.update {
            it.copy(
                perfilLogado = null,
                carrinho = emptyList()
            )
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

    fun adicionarAoCarrinho(
        produto: Produto,
        pontoCarne: String,
        adicionais: List<Adicional>,
        observacao: String,
        quantidade: Int
    ) {
        val novoItem = ItemCarrinho(
            id = proximoIdItem,
            produto = produto,
            pontoCarne = pontoCarne,
            adicionais = adicionais,
            observacao = observacao.trim(),
            quantidade = quantidade
        )
        proximoIdItem++
        _uiState.update { estado ->
            estado.copy(carrinho = estado.carrinho + novoItem)
        }
    }

    fun alterarQuantidadeCarrinho(item: ItemCarrinho, novaQuantidade: Int) {
        _uiState.update { estado ->
            val listaAtualizada = if (novaQuantidade <= 0) {
                estado.carrinho.filter { it.id != item.id }
            } else {
                estado.carrinho.map { atual ->
                    if (atual.id == item.id) atual.copy(quantidade = novaQuantidade) else atual
                }
            }
            estado.copy(carrinho = listaAtualizada)
        }
    }

    fun removerDoCarrinho(item: ItemCarrinho) {
        _uiState.update { estado ->
            estado.copy(carrinho = estado.carrinho.filter { it.id != item.id })
        }
    }

    fun escolherFormaPagamento(forma: String) {
        _uiState.update { it.copy(formaPagamentoEscolhida = forma) }
    }

    fun finalizarPedido(): Int {
        val estado = _uiState.value
        val subtotal = estado.carrinho.sumOf { it.total }
        val novoPedido = Pedido(
            numero = proximoNumeroPedido,
            itens = estado.carrinho,
            subtotal = subtotal,
            taxaEntrega = taxaEntrega,
            formaPagamento = estado.formaPagamentoEscolhida,
            endereco = estado.endereco
        )
        proximoNumeroPedido++
        _uiState.update {
            it.copy(
                pedidos = listOf(novoPedido) + it.pedidos,
                carrinho = emptyList()
            )
        }
        return novoPedido.numero
    }

    fun mudarNomePerfil(novoNome: String) {
        _uiState.update { it.copy(nomeCampoPerfil = novoNome) }
    }

    fun mudarEnderecoPerfil(novoEndereco: String) {
        _uiState.update { it.copy(enderecoCampoPerfil = novoEndereco) }
    }

    fun mudarTelefonePerfil(novoTelefone: String) {
        val filtrado = novoTelefone.filter { caractere ->
            caractere.isDigit() || caractere in " ()-+"
        }
        _uiState.update { it.copy(telefoneCampoPerfil = filtrado) }
    }

    fun salvarDadosPerfil() {
        _uiState.update { estado ->
            val nomeFinal = estado.nomeCampoPerfil.trim().ifBlank { "Visitante" }
            val enderecoFinal = estado.enderecoCampoPerfil.trim()
            val telefoneFinal = estado.telefoneCampoPerfil.trim()
            estado.copy(
                nomeUsuario = nomeFinal,
                endereco = enderecoFinal,
                telefone = telefoneFinal,
                nomeCampoPerfil = nomeFinal,
                enderecoCampoPerfil = enderecoFinal,
                telefoneCampoPerfil = telefoneFinal
            )
        }
    }
}
