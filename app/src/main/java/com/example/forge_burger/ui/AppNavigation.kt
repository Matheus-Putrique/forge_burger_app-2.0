package com.example.forge_burger.ui

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.forge_burger.data.Perfil
import com.example.forge_burger.data.repository.CategoriaRepository
import com.example.forge_burger.data.repository.ProdutoRepository
import com.example.forge_burger.ui.theme.CorFundo
import com.example.forge_burger.viewmodel.BurgerViewModel
import com.example.forge_burger.viewmodel.CategoriaViewModel
import com.example.forge_burger.viewmodel.CategoriaViewModelFactory
import com.example.forge_burger.viewmodel.ProdutoViewModel
import com.example.forge_burger.viewmodel.ProdutoViewModelFactory

@Composable
fun AppNavigation(
    categoriaRepository: CategoriaRepository,
    produtoRepository: ProdutoRepository
) {
    val navController = rememberNavController()

    val categoriaViewModel: CategoriaViewModel = viewModel(
        factory = CategoriaViewModelFactory(categoriaRepository)
    )
    val produtoViewModel: ProdutoViewModel = viewModel(
        factory = ProdutoViewModelFactory(produtoRepository)
    )
    val burgerViewModel: BurgerViewModel = viewModel()

    val estadoBurger by burgerViewModel.uiState.collectAsState()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val rotaAtual = backStackEntry?.destination?.route

    LaunchedEffect(estadoBurger.perfilLogado, rotaAtual) {
        if (estadoBurger.perfilLogado == null && rotaAtual != null && rotaAtual != Rotas.LOGIN) {
            navController.irParaLogin()
        }
    }

    val quantidadeNoCarrinho = estadoBurger.carrinho.sumOf { it.quantidade }

    Scaffold(
        containerColor = CorFundo,
        bottomBar = {
            if (rotaAtual in Rotas.abas) {
                BottomNavBar(
                    navController = navController,
                    rotaAtual = rotaAtual,
                    itensNoCarrinho = quantidadeNoCarrinho
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
            composable(Rotas.LOGIN) {
                LoginScreen(navController, burgerViewModel)
            }

            composable(Rotas.INICIO) {
                MenuScreen(navController, produtoViewModel, categoriaViewModel, burgerViewModel)
            }

            composable(Rotas.BUSCA) {
                BuscaScreen(navController, produtoViewModel, categoriaViewModel)
            }

            composable(Rotas.CARRINHO) {
                CartScreen(navController, burgerViewModel)
            }

            composable(Rotas.PERFIL) {
                PerfilScreen(navController, burgerViewModel, produtoViewModel)
            }

            composable(Rotas.CATEGORIAS) {
                CategoriasScreen(navController, categoriaViewModel, produtoViewModel, burgerViewModel)
            }

            composable(
                route = Rotas.NOVO_PRODUTO,
                arguments = listOf(navArgument("categoriaId") { type = NavType.IntType; defaultValue = -1 })
            ) {
                if (estadoBurger.perfilLogado == Perfil.ADMIN) {
                    NovoProdutoScreen(navController, produtoViewModel, categoriaViewModel)
                } else {
                    LaunchedEffect(Unit) { navController.popBackStack() }
                }
            }

            composable(Rotas.PRODUTO) { entry ->
                val id = entry.arguments?.getString("produtoId")?.toIntOrNull() ?: -1
                CustomizationScreen(navController, produtoViewModel, categoriaViewModel, burgerViewModel, produtoId = id)
            }

            composable(Rotas.CATEGORIA) { entry ->
                val id = entry.arguments?.getString("categoriaId")?.toIntOrNull() ?: -1
                CategoriaDetalhesScreen(navController, categoriaViewModel, produtoViewModel, burgerViewModel, categoriaId = id)
            }

            composable(Rotas.PEDIDO) { entry ->
                val numero = entry.arguments?.getString("numero")?.toIntOrNull() ?: -1
                PedidoScreen(navController, burgerViewModel, numero = numero)
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
