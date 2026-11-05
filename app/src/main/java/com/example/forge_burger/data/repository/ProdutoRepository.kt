package com.example.forge_burger.data.repository

import android.util.Log
import com.example.forge_burger.data.Produto
import com.example.forge_burger.data.ProdutoDAO
import com.example.forge_burger.data.produtosIniciais
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class ProdutoRepository(private val produtoDao: ProdutoDAO) {

    fun buscarTodos(): Flow<List<Produto>> {
        return produtoDao.buscarTodos()
    }

    suspend fun buscarPorId(id: Int): Produto? {
        try {
            return produtoDao.buscarPorId(id)
        } catch (e: Exception) {
            Log.e("Erro ao buscar por ID", "Msg: ${e.message}")
            return null
        }
    }

    suspend fun buscarPorCategoria(categoriaId: Int): List<Produto> {
        try {
            return produtoDao.buscarPorCategoria(categoriaId)
        } catch (e: Exception) {
            Log.e("Erro ao buscar categoria", "Msg: ${e.message}")
            return emptyList()
        }
    }

    suspend fun inserir(produto: Produto) {
        try {
            produtoDao.inserir(produto)
        } catch (e: Exception) {
            Log.e("Erro ao adicionar", "Msg: ${e.message}")
        }
    }

    suspend fun atualizar(produto: Produto) {
        try {
            produtoDao.atualizar(produto)
        } catch (e: Exception) {
            Log.e("Erro ao atualizar", "Msg: ${e.message}")
        }
    }

    suspend fun deletar(produto: Produto) {
        try {
            produtoDao.deletar(produto)
        } catch (e: Exception) {
            Log.e("Erro ao deletar", "Msg: ${e.message}")
        }
    }

    suspend fun carregarIniciaisSeVazio() {
        try {
            val listaAtual = produtoDao.buscarTodos().first()
            if (listaAtual.isEmpty()) {
                for (produto in produtosIniciais) {
                    produtoDao.inserir(produto)
                }
            }
        } catch (e: Exception) {
            Log.e("Erro na carga inicial", "Msg: ${e.message}")
        }
    }
}
