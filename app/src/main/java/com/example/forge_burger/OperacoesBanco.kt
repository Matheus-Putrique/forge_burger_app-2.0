package com.example.forge_burger

import android.util.Log

// ==================== OPERAÇÕES DE PRODUTO ====================

suspend fun inserirProduto(produto: Produto, produtoDao: ProdutoDAO) {
    try {
        produtoDao.inserir(produto)
    } catch (e: Exception) {
        Log.e("Erro ao adicionar", "Msg: ${e.message}")
    }
}

suspend fun buscarProdutos(produtoDao: ProdutoDAO): List<Produto> {
    try {
        return produtoDao.buscarTodos()
    } catch (e: Exception) {
        Log.e("Erro ao buscar", "Msg: ${e.message}")
        return emptyList()
    }
}

suspend fun buscarProdutoPorId(id: Int, produtoDao: ProdutoDAO): Produto? {
    try {
        return produtoDao.buscarPorId(id)
    } catch (e: Exception) {
        Log.e("Erro ao buscar por ID", "Msg: ${e.message}")
        return null
    }
}

suspend fun buscarProdutosPorCategoria(categoriaId: Int, produtoDao: ProdutoDAO): List<Produto> {
    try {
        return produtoDao.buscarPorCategoria(categoriaId)
    } catch (e: Exception) {
        Log.e("Erro ao buscar categoria", "Msg: ${e.message}")
        return emptyList()
    }
}

suspend fun deletarProduto(produto: Produto, produtoDao: ProdutoDAO) {
    try {
        produtoDao.deletar(produto)
    } catch (e: Exception) {
        Log.e("Erro ao deletar", "Msg: ${e.message}")
    }
}

suspend fun atualizarProduto(produto: Produto, produtoDao: ProdutoDAO) {
    try {
        produtoDao.atualizar(produto)
    } catch (e: Exception) {
        Log.e("Erro ao atualizar", "Msg: ${e.message}")
    }
}

// ==================== OPERAÇÕES DE CATEGORIA ====================

suspend fun inserirCategoria(categoria: Categoria, categoriaDao: CategoriaDAO) {
    try {
        categoriaDao.inserir(categoria)
    } catch (e: Exception) {
        Log.e("Erro ao adicionar", "Msg: ${e.message}")
    }
}

suspend fun buscarCategorias(categoriaDao: CategoriaDAO): List<Categoria> {
    try {
        return categoriaDao.buscarTodos()
    } catch (e: Exception) {
        Log.e("Erro ao buscar", "Msg: ${e.message}")
        return emptyList()
    }
}

suspend fun buscarCategoriaPorId(id: Int, categoriaDao: CategoriaDAO): Categoria? {
    try {
        return categoriaDao.buscarPorId(id)
    } catch (e: Exception) {
        Log.e("Erro ao buscar por ID", "Msg: ${e.message}")
        return null
    }
}

suspend fun deletarCategoria(categoria: Categoria, categoriaDao: CategoriaDAO) {
    try {
        categoriaDao.deletar(categoria)
    } catch (e: Exception) {
        Log.e("Erro ao deletar", "Msg: ${e.message}")
    }
}

suspend fun atualizarCategoria(categoria: Categoria, categoriaDao: CategoriaDAO) {
    try {
        categoriaDao.atualizar(categoria)
    } catch (e: Exception) {
        Log.e("Erro ao atualizar", "Msg: ${e.message}")
    }
}

// ==================== CARGA INICIAL DO BANCO ====================

suspend fun carregarCategoriasDoBanco(categoriaDao: CategoriaDAO): List<Categoria> {
    var lista = buscarCategorias(categoriaDao)
    if (lista.isEmpty()) {
        for (cat in categoriasIniciais) {
            inserirCategoria(cat, categoriaDao)
        }
        lista = buscarCategorias(categoriaDao)
    }
    return lista
}

suspend fun carregarProdutosDoBanco(produtoDao: ProdutoDAO): List<Produto> {
    var lista = buscarProdutos(produtoDao)
    if (lista.isEmpty()) {
        for (prod in produtosIniciais) {
            inserirProduto(prod, produtoDao)
        }
        lista = buscarProdutos(produtoDao)
    }
    return lista
}
