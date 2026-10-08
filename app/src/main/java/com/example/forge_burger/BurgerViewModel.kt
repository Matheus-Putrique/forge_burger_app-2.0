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
            1, "Smash Triplo Bacon",
            "Três carnes smash de 90g, cheddar triplo e muito bacon crocante.",
            12.90, 1, "🍔", 4.9,
            listOf("3x Carnes Smash (90g cada)", "Cheddar Triplo", "Bacon Crocante", "Pão Brioche Selado")
        ),
        Produto(
            2, "Smash Jalapeño",
            "Carne smash, pepper jack, jalapeños frescos e maionese picante.",
            11.50, 1, "🌶️", 4.7,
            listOf("2x Carnes Smash (90g cada)", "Queijo Pepper Jack", "Jalapeños", "Maionese Picante")
        ),
        Produto(
            3, "Cogumelo Trufado",
            "Carne smash, cogumelos salteados, queijo suíço e maionese de trufas.",
            13.50, 1, "🍄", 4.8,
            listOf("2x Carnes Smash (90g cada)", "Cogumelos Salteados", "Queijo Suíço", "Maionese Trufada")
        ),
        Produto(
            4, "Smash Clássico",
            "Dois hambúrgueres artesanais smash de 90g, queijo cheddar duplo derretido, molho secreto artesanal e picles da casa no pão brioche tostado na manteiga.",
            14.90, 1, "🍔", 4.6,
            listOf("2x Carnes Smash (90g cada)", "Queijo Cheddar Artesanal", "Picles Fatiado", "Molho Especial BurgerCraft", "Pão Brioche Selado")
        ),
        Produto(5, "Cerveja Artesanal IPA", "Lata gelada 350ml.", 14.00, 2, "🍺", 4.8),
        Produto(6, "Refrigerante Lata", "Coca-Cola, Guaraná ou Sprite 350ml.", 6.00, 2, "🥤", 4.5),
        Produto(7, "Combo Smash + Batata + Refri", "Smash Clássico, batata média e refrigerante.", 29.90, 3, "🍱", 4.9),
        Produto(8, "Batata Frita Rústica", "Batata rústica com sal de alecrim.", 4.90, 4, "🍟", 4.7),
        Produto(9, "Onion Rings", "Anéis de cebola empanados com molho barbecue.", 8.90, 4, "🧅", 4.4)
    )

    // ---------- Carrinho, pedidos e favoritos ----------
    val carrinho = mutableStateListOf<ItemCarrinho>()
    val pedidos = mutableStateListOf<Pedido>()
    val favoritos = mutableStateListOf<Int>()

    // ---------- Perfil ----------
    var nomeUsuario by mutableStateOf("Visitante")
        private set
    var endereco by mutableStateOf("Rua das Brasas, 120 - Curitiba")
        private set
    var telefone by mutableStateOf("")
        private set

    private var proximoIdCategoria by mutableIntStateOf(5)
    private var proximoIdProduto by mutableIntStateOf(10)
    private var proximoIdItem by mutableIntStateOf(1)
    private var proximoNumeroPedido by mutableIntStateOf(1001)

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
