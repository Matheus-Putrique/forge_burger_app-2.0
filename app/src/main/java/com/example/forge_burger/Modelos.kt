package com.example.forge_burger

// ╔══════════════════════════════════════════════╗
// ║  Modelos.kt                                  ║
// ║  Data classes do app                         ║
// ║  Categoria · Produto · ItemCarrinho · Pedido ║
// ╚══════════════════════════════════════════════╝

// Lista 1: categorias do cardápio (Hambúrgueres, Bebidas...)
data class Categoria(
    val id: Int,
    val nome: String,
    val emoji: String,
    val descricao: String
)

// Lista 2: produtos do cardápio — cada produto pertence a uma Categoria
data class Produto(
    val id: Int,
    val nome: String,
    val descricao: String,
    val preco: Double,
    val categoriaId: Int,
    val emoji: String,
    val nota: Double = 5.0,
    val ingredientes: List<String> = emptyList()
)

// Adicional que pode ser colocado no lanche na tela de personalização
data class Adicional(
    val nome: String,
    val preco: Double
)

// Produto já personalizado, dentro do carrinho
data class ItemCarrinho(
    val id: Int,
    val produto: Produto,
    val pontoCarne: String,
    val adicionais: List<Adicional>,
    val observacao: String,
    val quantidade: Int
) {
    val precoUnitario: Double get() = produto.preco + adicionais.sumOf { it.preco }
    val total: Double get() = precoUnitario * quantidade

    // Texto curto que aparece embaixo do nome no carrinho
    val resumo: String
        get() = (listOf(pontoCarne) + adicionais.map { it.nome })
            .filter { it.isNotBlank() }
            .joinToString(", ")
}

// Pedido finalizado
data class Pedido(
    val numero: Int,
    val itens: List<ItemCarrinho>,
    val subtotal: Double,
    val taxaEntrega: Double,
    val formaPagamento: String,
    val endereco: String
) {
    val total: Double get() = subtotal + taxaEntrega
}
