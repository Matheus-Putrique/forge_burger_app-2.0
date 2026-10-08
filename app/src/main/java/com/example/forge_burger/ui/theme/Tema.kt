package com.example.forge_burger.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Cores do layout (as mesmas usadas nas primeiras telas do projeto)
val CorFundo = Color(18, 18, 18)
val CorCard = Color(28, 28, 28)
val CorCardClaro = Color(45, 45, 45)
val CorLaranja = Color(255, 160, 0)
val CorTextoCinza = Color(160, 160, 160)
val CorVermelho = Color(229, 72, 77)
val CorVerde = Color(76, 175, 80)

private val EsquemaEscuro = darkColorScheme(
    primary = CorLaranja,
    onPrimary = Color.Black,
    secondary = CorLaranja,
    onSecondary = Color.Black,
    background = CorFundo,
    onBackground = Color.White,
    surface = CorCard,
    onSurface = Color.White,
    surfaceVariant = CorCardClaro,
    onSurfaceVariant = CorTextoCinza,
    surfaceContainer = CorCard,
    error = CorVermelho
)

@Composable
fun ForgeBurgerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaEscuro,
        content = content
    )
}

fun formatarPreco(valor: Double): String = "R$ " + "%.2f".format(valor).replace('.', ',')
