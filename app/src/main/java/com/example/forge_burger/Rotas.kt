package com.example.forge_burger

object Rotas {
    const val LOGIN = "login"

    const val INICIO = "inicio"
    const val BUSCA = "busca"
    const val CARRINHO = "carrinho"
    const val PERFIL = "perfil"

    const val CATEGORIAS = "categorias"
    const val NOVO_PRODUTO = "novo_produto?categoriaId={categoriaId}"

    const val PRODUTO = "produto/{produtoId}"
    const val CATEGORIA = "categoria/{categoriaId}"
    const val PEDIDO = "pedido/{numero}"

    fun produto(id: Int) = "produto/$id"
    fun categoria(id: Int) = "categoria/$id"
    fun pedido(numero: Int) = "pedido/$numero"
    fun novoProduto(categoriaId: Int? = null) =
        if (categoriaId == null) "novo_produto" else "novo_produto?categoriaId=$categoriaId"

    val abas = listOf(INICIO, BUSCA, CARRINHO, PERFIL)
}
