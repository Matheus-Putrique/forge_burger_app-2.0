package com.example.forge_burger

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class BurgerViewModel : ViewModel() {

    val taxaEntrega = 4.99

    val pontosCarne = listOf("Mal passado", "Ao ponto", "Bem passado")

    val adicionaisDisponiveis = listOf(
        Adicional("Bacon Extra", 3.50),
        Adicional("Cheddar Extra", 2.50),
        Adicional("Ovo", 2.00),
        Adicional("Cebola Caramelizada", 2.00)
    )

    // Listas carregadas do banco Room (começam vazias em vez de lista fixa em memória)
    var categorias by mutableStateOf<List<Categoria>>(emptyList())
        private set

    var produtos by mutableStateOf<List<Produto>>(emptyList())
        private set

    fun atualizarListaCategorias(novaLista: List<Categoria>) {
        categorias = novaLista
    }

    fun atualizarListaProdutos(novaLista: List<Produto>) {
        produtos = novaLista
    }

    // Controle de edição de Produto (mesmo padrão da Aula 20)
    var estouEditandoProduto by mutableStateOf(false)
        private set

    var produtoEmEdicao by mutableStateOf<Produto?>(null)
        private set

    fun prepararEdicaoProduto(produto: Produto) {
        estouEditandoProduto = true
        produtoEmEdicao = produto
    }

    fun limparEdicaoProduto() {
        estouEditandoProduto = false
        produtoEmEdicao = null
    }

    val carrinho = mutableStateListOf<ItemCarrinho>()
    val pedidos = mutableStateListOf<Pedido>()
    val favoritos = mutableStateListOf<Int>()

    private val credenciais = mapOf(
        "admin" to ("admin123" to Perfil.ADMIN),
        "user" to ("1234" to Perfil.CLIENTE)
    )

    var perfilLogado by mutableStateOf<Perfil?>(null)
        private set

    val isAdmin: Boolean get() = perfilLogado == Perfil.ADMIN

    var nomeUsuario by mutableStateOf("Visitante")
        private set
    var endereco by mutableStateOf("Rua das Brasas, 120 - Curitiba")
        private set
    var telefone by mutableStateOf("")
        private set

    private var proximoIdItem by mutableIntStateOf(1)
    private var proximoNumeroPedido by mutableIntStateOf(1001)

    fun login(usuario: String, senha: String): Boolean {
        val (senhaCorreta, perfil) = credenciais[usuario.trim().lowercase()] ?: return false
        if (senha != senhaCorreta) return false
        perfilLogado = perfil
        return true
    }

    fun logout() {
        perfilLogado = null
        carrinho.clear()
    }

    fun buscarCategoria(id: Int): Categoria? = categorias.find { it.id == id }

    fun buscarProduto(id: Int): Produto? = produtos.find { it.id == id }

    fun produtosDaCategoria(categoriaId: Int): List<Produto> =
        produtos.filter { it.categoriaId == categoriaId }

    fun ehFavorito(produtoId: Int) = favoritos.contains(produtoId)

    fun alternarFavorito(produtoId: Int) {
        if (favoritos.contains(produtoId)) favoritos.remove(produtoId) else favoritos.add(produtoId)
    }

    fun adicionarAoCarrinho(
        produto: Produto,
        pontoCarne: String,
        adicionais: List<Adicional>,
        observacao: String,
        quantidade: Int
    ) {
        carrinho.add(
            ItemCarrinho(proximoIdItem, produto, pontoCarne, adicionais, observacao.trim(), quantidade)
        )
        proximoIdItem++
    }

    fun alterarQuantidade(item: ItemCarrinho, novaQuantidade: Int) {
        val indice = carrinho.indexOfFirst { it.id == item.id }
        if (indice == -1) return
        if (novaQuantidade <= 0) {
            carrinho.removeAt(indice)
        } else {
            carrinho[indice] = item.copy(quantidade = novaQuantidade)
        }
    }

    fun removerDoCarrinho(item: ItemCarrinho) {
        carrinho.removeAll { it.id == item.id }
    }

    fun subtotalCarrinho(): Double = carrinho.sumOf { it.total }

    fun quantidadeNoCarrinho(): Int = carrinho.sumOf { it.quantidade }

    fun finalizarPedido(formaPagamento: String): Int {
        val pedido = Pedido(
            numero = proximoNumeroPedido,
            itens = carrinho.toList(),
            subtotal = subtotalCarrinho(),
            taxaEntrega = taxaEntrega,
            formaPagamento = formaPagamento,
            endereco = endereco
        )
        pedidos.add(0, pedido)
        carrinho.clear()
        proximoNumeroPedido++
        return pedido.numero
    }

    fun buscarPedido(numero: Int): Pedido? = pedidos.find { it.numero == numero }

    fun salvarPerfil(nome: String, novoEndereco: String, novoTelefone: String) {
        nomeUsuario = nome.trim().ifBlank { "Visitante" }
        endereco = novoEndereco.trim()
        telefone = novoTelefone.trim()
    }
}
