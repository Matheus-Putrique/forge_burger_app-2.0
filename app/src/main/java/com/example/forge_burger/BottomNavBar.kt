package com.example.forge_burger

// ╔══════════════════════════════════════════════╗
// ║  BottomNavBar.kt                             ║
// ║  Início · Buscar · Carrinho · Perfil         ║
// ╚══════════════════════════════════════════════╝

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import com.example.forge_burger.ui.theme.CorCard
import com.example.forge_burger.ui.theme.CorLaranja
import com.example.forge_burger.ui.theme.CorTextoCinza

private data class ItemAba(val rota: String, val label: String, val icone: ImageVector)

private val itensAbas = listOf(
    ItemAba(Rotas.INICIO, "Início", Icons.Default.Home),
    ItemAba(Rotas.BUSCA, "Buscar", Icons.Default.Search),
    ItemAba(Rotas.CARRINHO, "Carrinho", Icons.Default.ShoppingCart),
    ItemAba(Rotas.PERFIL, "Perfil", Icons.Default.Person)
)

@Composable
fun BottomNavBar(navController: NavHostController, rotaAtual: String?, itensNoCarrinho: Int) {
    NavigationBar(containerColor = CorCard) {
        itensAbas.forEach { aba ->
            NavigationBarItem(
                selected = rotaAtual == aba.rota,
                onClick = { navController.irParaAba(aba.rota) },
                icon = {
                    if (aba.rota == Rotas.CARRINHO && itensNoCarrinho > 0) {
                        BadgedBox(badge = { Badge(containerColor = CorLaranja) { Text("$itensNoCarrinho", color = Color.Black) } }) {
                            Icon(aba.icone, contentDescription = aba.label)
                        }
                    } else {
                        Icon(aba.icone, contentDescription = aba.label)
                    }
                },
                label = { Text(aba.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CorLaranja,
                    selectedTextColor = CorLaranja,
                    indicatorColor = Color(60, 45, 20),
                    unselectedIconColor = CorTextoCinza,
                    unselectedTextColor = CorTextoCinza
                )
            )
        }
    }
}
