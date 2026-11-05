package com.example.forge_burger

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.forge_burger.data.AppDatabase
import com.example.forge_burger.data.repository.CategoriaRepository
import com.example.forge_burger.data.repository.ProdutoRepository
import com.example.forge_burger.ui.AppNavigation
import com.example.forge_burger.ui.theme.ForgeBurgerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val banco = AppDatabase.getDatabase(applicationContext)
        val categoriaRepository = CategoriaRepository(banco.categoriaDAO())
        val produtoRepository = ProdutoRepository(banco.produtoDAO())

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        setContent {
            ForgeBurgerTheme {
                AppNavigation(
                    categoriaRepository = categoriaRepository,
                    produtoRepository = produtoRepository
                )
            }
        }
    }
}
