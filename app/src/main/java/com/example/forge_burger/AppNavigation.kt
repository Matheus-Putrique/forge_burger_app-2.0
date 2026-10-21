package com.example.forge_burger

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.forge_burger.ui.theme.CorFundo

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val viewModel: BurgerViewModel = viewModel()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val rotaAtual = backStackEntry?.destination?.route

    LaunchedEffect(viewModel.perfilLogado, rotaAtual) {
        if (viewModel.perfilLogado == null && rotaAtual != null && rotaAtual != Rotas.LOGIN) {
            navController.irParaLogin()
        }
    }

    Scaffold(
        containerColor = CorFundo,
        bottomBar = {
            if (rotaAtual in Rotas.abas) {
                BottomNavBar(
                    navController = navController,
                    rotaAtual = rotaAtual,
                    itensNoCarrinho = viewModel.quantidadeNoCarrinho()
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Rotas.LOGIN,
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
        ) {
            composable(Rotas.LOGIN) { LoginScreen(navController, viewModel) }

            composable(Rotas.INICIO) { MenuScreen(navController, viewModel) }
            composable(Rotas.BUSCA) { BuscaScreen(navController, viewModel) }
            composable(Rotas.CARRINHO) { CartScreen(navController, viewModel) }
            composable(Rotas.PERFIL) { PerfilScreen(navController, viewModel) }

            composable(Rotas.CATEGORIAS) { CategoriasScreen(navController, viewModel) }
            composable(
                route = Rotas.NOVO_PRODUTO,
                arguments = listOf(navArgument("categoriaId") { type = NavType.IntType; defaultValue = -1 })
            ) { entry ->
                val categoriaId = entry.arguments?.getInt("categoriaId") ?: -1
                if (viewModel.isAdmin) {
                    NovoProdutoScreen(navController, viewModel, categoriaInicial = categoriaId)
                } else {
                    LaunchedEffect(Unit) { navController.popBackStack() }
                }
            }

            composable(Rotas.PRODUTO) { entry ->
                val id = entry.arguments?.getString("produtoId")?.toIntOrNull() ?: -1
                CustomizationScreen(navController, viewModel, produtoId = id)
            }
            composable(Rotas.CATEGORIA) { entry ->
                val id = entry.arguments?.getString("categoriaId")?.toIntOrNull() ?: -1
                CategoriaDetalhesScreen(navController, viewModel, categoriaId = id)
            }
            composable(Rotas.PEDIDO) { entry ->
                val numero = entry.arguments?.getString("numero")?.toIntOrNull() ?: -1
                PedidoScreen(navController, viewModel, numero = numero)
            }
        }
    }
}

fun NavHostController.irParaAba(rota: String) {
    navigate(rota) {
        popUpTo(Rotas.INICIO) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

fun NavHostController.irParaLogin() {
    navigate(Rotas.LOGIN) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
    Rotas.abas.forEach { clearBackStack(it) }
}
