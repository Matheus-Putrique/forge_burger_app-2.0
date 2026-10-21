package com.example.forge_burger

import androidx.annotation.DrawableRes

data class Categoria(
    val id: Int,
    val nome: String,
    val emoji: String,
    val descricao: String
)

data class Produto(
    val id: Int,
    val nome: String,
    val descricao: String,
    val preco: Double,
    val categoriaId: Int,
    val emoji: String,
    val nota: Double = 5.0,
    val ingredientes: List<String> = emptyList(),
    @DrawableRes val foto: Int? = null
)

data class Adicional(
    val nome: String,
    val preco: Double
)

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

    val resumo: String
        get() = (listOf(pontoCarne) + adicionais.map { it.nome })
            .filter { it.isNotBlank() }
            .joinToString(", ")
}

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

enum class Perfil(val rotulo: String) {
    ADMIN("Administrador"),
    CLIENTE("Cliente")
}
