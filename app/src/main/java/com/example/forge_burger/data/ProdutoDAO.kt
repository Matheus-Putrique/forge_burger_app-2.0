package com.example.forge_burger.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProdutoDAO {

    @Insert
    suspend fun inserir(produto: Produto)

    @Query("SELECT * FROM produtos")
    fun buscarTodos(): Flow<List<Produto>>

    @Query("SELECT * FROM produtos WHERE id = :id")
    suspend fun buscarPorId(id: Int): Produto?

    @Query("SELECT * FROM produtos WHERE categoriaId = :categoriaId")
    suspend fun buscarPorCategoria(categoriaId: Int): List<Produto>

    @Update
    suspend fun atualizar(produto: Produto)

    @Delete
    suspend fun deletar(produto: Produto)
}
