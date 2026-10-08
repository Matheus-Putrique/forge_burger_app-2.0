package com.example.forge_burger

// ╔══════════════════════════════════════════════╗
// ║  Rotas.kt                                    ║
// ║  Constantes das ROTAS de navegação           ║
// ╚══════════════════════════════════════════════╝

object Rotas {
    // Abas do BottomNavigation
    const val INICIO = "inicio"
    const val BUSCA = "busca"
    const val CARRINHO = "carrinho"
    const val PERFIL = "perfil"

    // Telas sem BottomNavigation
    const val CATEGORIAS = "categorias"
    // categoriaId é opcional: já deixa a categoria escolhida no formulário
    const val NOVO_PRODUTO = "novo_produto?categoriaId={categoriaId}"

    // Rotas com argumento
    const val PRODUTO = "produto/{produtoId}"
    const val CATEGORIA = "categoria/{categoriaId}"
    const val PEDIDO = "pedido/{numero}"

    fun produto(id: Int) = "produto/$id"
    fun categoria(id: Int) = "categoria/$id"
    fun pedido(numero: Int) = "pedido/$numero"
    fun novoProduto(categoriaId: Int? = null) =
        if (categoriaId == null) "novo_produto" else "novo_produto?categoriaId=$categoriaId"

    // Rotas que mostram a barra inferior
    val abas = listOf(INICIO, BUSCA, CARRINHO, PERFIL)
}
