package com.example.forge_burger

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categorias")
data class Categoria(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val emoji: String,
    val descricao: String
)

@Entity(tableName = "produtos")
data class Produto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val descricao: String,
    val preco: Double,
    val categoriaId: Int,
    val emoji: String,
    val nota: Double = 5.0,
    val ingredientes: String = "",
    val foto: String? = null
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

val fotosProdutos = mapOf(
    "foto_classic_bacon" to R.drawable.foto_classic_bacon,
    "foto_jalapeno_brioche" to R.drawable.foto_jalapeno_brioche,
    "foto_black_sesame" to R.drawable.foto_black_sesame,
    "foto_doritos_smash" to R.drawable.foto_doritos_smash,
    "foto_ice_tea_canela" to R.drawable.foto_ice_tea_canela,
    "foto_milkshake_chocolate" to R.drawable.foto_milkshake_chocolate,
    "foto_cha_gelado" to R.drawable.foto_cha_gelado,
    "foto_batata_frita" to R.drawable.foto_batata_frita,
    "foto_batata_rustica" to R.drawable.foto_batata_rustica,
    "foto_batata_pulled_pork" to R.drawable.foto_batata_pulled_pork
)

fun fotoDoProduto(nome: String?): Int? = fotosProdutos[nome]

val categoriasIniciais = listOf(
    Categoria(1, "Hambúrgueres", "🍔", "Smash burgers artesanais no pão brioche."),
    Categoria(2, "Bebidas", "🥤", "Refrigerantes, sucos e cervejas artesanais."),
    Categoria(3, "Combos", "🍱", "Lanche + acompanhamento + bebida com desconto."),
    Categoria(4, "Acompanhamentos", "🍟", "Batatas, onion rings e porções.")
)

val produtosIniciais = listOf(
    Produto(
        0, "Classic Bacon Burger",
        "Burger grelhado, cheddar derretido, bacon, alface, tomate, cebola roxa e molho especial no pão com gergelim.",
        32.90, 1, "🍔", 4.8,
        "• Burger Grelhado (180g)\n• Queijo Cheddar\n• Bacon\n• Alface, Tomate e Cebola Roxa\n• Molho Especial\n• Pão com Gergelim",
        foto = "foto_classic_bacon"
    ),
    Produto(
        0, "Jalapeño Brioche",
        "Burger suculento, queijo branco derretido, bacon crocante, relish de jalapeño, cebola roxa grelhada, tomate e rúcula no brioche tostado.",
        36.90, 1, "🌶️", 4.7,
        "• Burger Grelhado (180g)\n• Queijo Branco Derretido\n• Bacon Crocante\n• Relish de Jalapeño\n• Cebola Roxa Grelhada\n• Tomate e Rúcula\n• Maionese da Casa\n• Pão Brioche",
        foto = "foto_jalapeno_brioche"
    ),
    Produto(
        0, "Black Sesame Gourmet",
        "Burger, cheddar e queijo prato, bacon, cebola caramelizada, tomate-cereja, rúcula e espinafre no pão com gergelim preto.",
        38.90, 1, "🧅", 4.9,
        "• Burger Grelhado (180g)\n• Cheddar e Queijo Prato\n• Bacon\n• Cebola Caramelizada\n• Tomate-Cereja\n• Rúcula e Espinafre\n• Molho Barbecue\n• Pão com Gergelim Preto",
        foto = "foto_black_sesame"
    ),
    Produto(
        0, "Doritos Double Smash",
        "Duas carnes smash, cheddar duplo, bacon, chips de Doritos crocantes e molho especial. Acompanha pote extra de molho.",
        42.90, 1, "🔥", 4.9,
        "• 2x Carnes Smash (90g cada)\n• Cheddar Duplo\n• Bacon\n• Chips de Doritos\n• Molho Especial\n• Molho Barbecue\n• Pão com Gergelim",
        foto = "foto_doritos_smash"
    ),
    Produto(0, "Ice Tea de Limão com Canela", "Chá gelado com limão-siciliano, hortelã e canela em pau. Copo 400ml.", 12.90, 2, "🍹", 4.7, foto = "foto_ice_tea_canela"),
    Produto(0, "Milkshake de Chocolate", "Milkshake cremoso de chocolate com chantilly, calda e raspas de chocolate. 400ml.", 19.90, 2, "🥤", 4.9, foto = "foto_milkshake_chocolate"),
    Produto(0, "Chá Gelado com Limão e Hortelã", "Chá gelado com gás, rodela de limão e folhas de hortelã. Copo 350ml.", 10.90, 2, "🍋", 4.6, foto = "foto_cha_gelado"),
    Produto(0, "Batata Frita Tradicional", "Porção de batata frita crocante com sal e pimenta-do-reino.", 14.90, 4, "🍟", 4.7, foto = "foto_batata_frita"),
    Produto(0, "Batata Rústica com Alecrim", "Batatas rústicas assadas com alecrim fresco, alho e sal grosso.", 18.90, 4, "🥔", 4.8, foto = "foto_batata_rustica"),
    Produto(0, "Batata com Pulled Pork", "Batata rústica coberta com pulled pork, molho barbecue, cebola crispy, cebolinha e coleslaw.", 29.90, 4, "🍖", 4.9, foto = "foto_batata_pulled_pork")
)
