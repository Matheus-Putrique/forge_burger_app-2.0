package com.example.forge_burger

// ╔══════════════════════════════════════════════╗
// ║  BurgerViewModel.kt                          ║
// ║  Guarda as listas do app (mutableStateList)  ║
// ║  Compartilhado entre todas as telas          ║
// ╚══════════════════════════════════════════════╝

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

    // ---------- Lista 1: Categorias ----------
    val categorias = mutableStateListOf(
        Categoria(1, "Hambúrgueres", "🍔", "Smash burgers artesanais no pão brioche."),
        Categoria(2, "Bebidas", "🥤", "Refrigerantes, sucos e cervejas artesanais."),
        Categoria(3, "Combos", "🍱", "Lanche + acompanhamento + bebida com desconto."),
        Categoria(4, "Acompanhamentos", "🍟", "Batatas, onion rings e porções.")
    )

    // ---------- Lista 2: Produtos ----------
    val produtos = mutableStateListOf(
        Produto(
            1, "Classic Bacon Burger",
            "Burger grelhado, cheddar derretido, bacon, alface, tomate, cebola roxa e molho especial no pão com gergelim.",
            32.90, 1, "🍔", 4.8,
            listOf("Burger Grelhado (180g)", "Queijo Cheddar", "Bacon", "Alface, Tomate e Cebola Roxa", "Molho Especial", "Pão com Gergelim"),
            foto = R.drawable.foto_classic_bacon
        ),
        Produto(
            2, "Jalapeño Brioche",
            "Burger suculento, queijo branco derretido, bacon crocante, relish de jalapeño, cebola roxa grelhada, tomate e rúcula no brioche tostado.",
            36.90, 1, "🌶️", 4.7,
            listOf("Burger Grelhado (180g)", "Queijo Branco Derretido", "Bacon Crocante", "Relish de Jalapeño", "Cebola Roxa Grelhada", "Tomate e Rúcula", "Maionese da Casa", "Pão Brioche"),
            foto = R.drawable.foto_jalapeno_brioche
        ),
        Produto(
            3, "Black Sesame Gourmet",
            "Burger, cheddar e queijo prato, bacon, cebola caramelizada, tomate-cereja, rúcula e espinafre no pão com gergelim preto.",
            38.90, 1, "🧅", 4.9,
            listOf("Burger Grelhado (180g)", "Cheddar e Queijo Prato", "Bacon", "Cebola Caramelizada", "Tomate-Cereja", "Rúcula e Espinafre", "Molho Barbecue", "Pão com Gergelim Preto"),
            foto = R.drawable.foto_black_sesame
        ),
        Produto(
            4, "Doritos Double Smash",
            "Duas carnes smash, cheddar duplo, bacon, chips de Doritos crocantes e molho especial. Acompanha pote extra de molho.",
            42.90, 1, "🔥", 4.9,
            listOf("2x Carnes Smash (90g cada)", "Cheddar Duplo", "Bacon", "Chips de Doritos", "Molho Especial", "Molho Barbecue", "Pão com Gergelim"),
            foto = R.drawable.foto_doritos_smash
        ),
        Produto(5, "Ice Tea de Limão com Canela", "Chá gelado com limão-siciliano, hortelã e canela em pau. Copo 400ml.", 12.90, 2, "🍹", 4.7, foto = R.drawable.foto_ice_tea_canela),
        Produto(6, "Milkshake de Chocolate", "Milkshake cremoso de chocolate com chantilly, calda e raspas de chocolate. 400ml.", 19.90, 2, "🥤", 4.9, foto = R.drawable.foto_milkshake_chocolate),
        Produto(7, "Chá Gelado com Limão e Hortelã", "Chá gelado com gás, rodela de limão e folhas de hortelã. Copo 350ml.", 10.90, 2, "🍋", 4.6, foto = R.drawable.foto_cha_gelado),
        Produto(8, "Batata Frita Tradicional", "Porção de batata frita crocante com sal e pimenta-do-reino.", 14.90, 4, "🍟", 4.7, foto = R.drawable.foto_batata_frita),
        Produto(9, "Batata Rústica com Alecrim", "Batatas rústicas assadas com alecrim fresco, alho e sal grosso.", 18.90, 4, "🥔", 4.8, foto = R.drawable.foto_batata_rustica),
        Produto(10, "Batata com Pulled Pork", "Batata rústica coberta com pulled pork, molho barbecue, cebola crispy, cebolinha e coleslaw.", 29.90, 4, "🍖", 4.9, foto = R.drawable.foto_batata_pulled_pork)
    )

    // ---------- Carrinho, pedidos e favoritos ----------
    val carrinho = mutableStateListOf<ItemCarrinho>()
    val pedidos = mutableStateListOf<Pedido>()
    val favoritos = mutableStateListOf<Int>()

    // ---------- Sessão (login em memória) ----------
    // Credenciais fixas de demonstração: usuário → (senha, perfil)
    private val credenciais = mapOf(
        "admin" to ("admin123" to Perfil.ADMIN),
        "user" to ("1234" to Perfil.CLIENTE)
    )

    // null = ninguém logado
    var perfilLogado by mutableStateOf<Perfil?>(null)
        private set

    val isAdmin: Boolean get() = perfilLogado == Perfil.ADMIN

    // ---------- Perfil ----------
    var nomeUsuario by mutableStateOf("Visitante")
        private set
    var endereco by mutableStateOf("Rua das Brasas, 120 - Curitiba")
        private set
    var telefone by mutableStateOf("")
        private set

    private var proximoIdCategoria by mutableIntStateOf(5)
    private var proximoIdProduto by mutableIntStateOf(11)
    private var proximoIdItem by mutableIntStateOf(1)
    private var proximoNumeroPedido by mutableIntStateOf(1001)

    // ===== Sessão =====
    // Retorna true se usuário e senha baterem com alguma credencial
    fun login(usuario: String, senha: String): Boolean {
        val (senhaCorreta, perfil) = credenciais[usuario.trim().lowercase()] ?: return false
        if (senha != senhaCorreta) return false
        perfilLogado = perfil
        return true
    }

    // Encerra a sessão: sem permissões e carrinho vazio para o próximo perfil
    fun logout() {
        perfilLogado = null
        carrinho.clear()
    }

    // ===== Categorias =====
    fun buscarCategoria(id: Int): Categoria? = categorias.find { it.id == id }

    fun adicionarCategoria(nome: String, emoji: String, descricao: String) {
        categorias.add(
            Categoria(proximoIdCategoria, nome.trim(), emoji.ifBlank { "🍽️" }, descricao.trim())
        )
        proximoIdCategoria++
    }

    // Só deixa remover se não tiver produto usando a categoria
    fun removerCategoria(categoria: Categoria): Boolean {
        if (produtosDaCategoria(categoria.id).isNotEmpty()) return false
        categorias.remove(categoria)
        return true
    }

    // ===== Produtos =====
    fun buscarProduto(id: Int): Produto? = produtos.find { it.id == id }

    fun produtosDaCategoria(categoriaId: Int): List<Produto> =
        produtos.filter { it.categoriaId == categoriaId }

    fun adicionarProduto(nome: String, descricao: String, preco: Double, categoriaId: Int, emoji: String) {
        produtos.add(
            Produto(
                id = proximoIdProduto,
                nome = nome.trim(),
                descricao = descricao.trim(),
                preco = preco,
                categoriaId = categoriaId,
                emoji = emoji.ifBlank { "🍔" }
            )
        )
        proximoIdProduto++
    }

    fun removerProduto(produto: Produto) {
        produtos.remove(produto)
        favoritos.remove(produto.id)
        carrinho.removeAll { it.produto.id == produto.id }
    }

    // ===== Favoritos =====
    fun ehFavorito(produtoId: Int) = favoritos.contains(produtoId)

    fun alternarFavorito(produtoId: Int) {
        if (favoritos.contains(produtoId)) favoritos.remove(produtoId) else favoritos.add(produtoId)
    }

    // ===== Carrinho =====
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

    // ===== Pedidos =====
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

    // ===== Perfil =====
    fun salvarPerfil(nome: String, novoEndereco: String, novoTelefone: String) {
        nomeUsuario = nome.trim().ifBlank { "Visitante" }
        endereco = novoEndereco.trim()
        telefone = novoTelefone.trim()
    }
}
