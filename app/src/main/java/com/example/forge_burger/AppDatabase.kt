package com.example.forge_burger

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Categoria::class, Produto::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoriaDAO(): CategoriaDAO
    abstract fun produtoDAO(): ProdutoDAO

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

            val tempInstance = INSTANCE

            if (tempInstance != null) {
                return tempInstance
            } else {
                synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "app_database"
                    ).fallbackToDestructiveMigration(dropAllTables = true)
                        .build()
                    INSTANCE = instance
                    return instance
                }
            }
        }
    }
}