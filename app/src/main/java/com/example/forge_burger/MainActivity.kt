package com.example.forge_burger

// ╔══════════════════════════════════════════════╗
// ║  MainActivity.kt                             ║
// ║  PONTO DE ENTRADA do aplicativo              ║
// ╚══════════════════════════════════════════════╝

import android.os.Bundle
import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.forge_burger.ui.theme.ForgeBurgerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // App é todo escuro: ícones da barra de status/navegação sempre claros
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        setContent {
            ForgeBurgerTheme {
                AppNavigation()
            }
        }
    }
}
