package com.example.forge_burger.data.repository

import android.util.Log
import com.example.forge_burger.data.Categoria
import com.example.forge_burger.data.CategoriaDAO
import com.example.forge_burger.data.categoriasIniciais
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class CategoriaRepository(private val categoriaDao: CategoriaDAO) {

    fun buscarTodos(): Flow<List<Categoria>> {
        return categoriaDao.buscarTodos()
    }

    suspend fun buscarPorId(id: Int): Categoria? {
        try {
            return categoriaDao.buscarPorId(id)
        } catch (e: Exception) {
            Log.e("Erro ao buscar por ID", "Msg: ${e.message}")
            return null
        }
    }

    suspend fun inserir(categoria: Categoria) {
        try {
            categoriaDao.inserir(categoria)
        } catch (e: Exception) {
            Log.e("Erro ao adicionar", "Msg: ${e.message}")
        }
    }

    suspend fun atualizar(categoria: Categoria) {
        try {
            categoriaDao.atualizar(categoria)
        } catch (e: Exception) {
            Log.e("Erro ao atualizar", "Msg: ${e.message}")
        }
    }

    suspend fun deletar(categoria: Categoria) {
        try {
            categoriaDao.deletar(categoria)
        } catch (e: Exception) {
            Log.e("Erro ao deletar", "Msg: ${e.message}")
        }
    }

    suspend fun carregarIniciaisSeVazio() {
        try {
            val listaAtual = categoriaDao.buscarTodos().first()
            if (listaAtual.isEmpty()) {
                for (categoria in categoriasIniciais) {
                    categoriaDao.inserir(categoria)
                }
            }
        } catch (e: Exception) {
            Log.e("Erro na carga inicial", "Msg: ${e.message}")
        }
    }
}
