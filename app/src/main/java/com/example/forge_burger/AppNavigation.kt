package com.example.forge_burger

// ╔══════════════════════════════════════════════╗
// ║  AppNavigation.kt                            ║
// ║  NavHost com TODAS as telas do app           ║
// ║  BottomNavBar só aparece nas abas principais ║
// ╚══════════════════════════════════════════════╝

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

    // ViewModel criado UMA VEZ e compartilhado entre todas as telas
    val viewModel: BurgerViewModel = viewModel()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val rotaAtual = backStackEntry?.destination?.route

    // Sem sessão (logout ou app recriado) → volta para o Login
    // rotaAtual != null garante que o NavHost já montou o grafo
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
            // ----- Login -----
            composable(Rotas.LOGIN) { LoginScreen(navController, viewModel) }

            // ----- Abas -----
            composable(Rotas.INICIO) { MenuScreen(navController, viewModel) }
            composable(Rotas.BUSCA) { BuscaScreen(navController, viewModel) }
            composable(Rotas.CARRINHO) { CartScreen(navController, viewModel) }
            composable(Rotas.PERFIL) { PerfilScreen(navController, viewModel) }

            // ----- Listas / formulários -----
            composable(Rotas.CATEGORIAS) { CategoriasScreen(navController, viewModel) }
            composable(
                route = Rotas.NOVO_PRODUTO,
                arguments = listOf(navArgument("categoriaId") { type = NavType.IntType; defaultValue = -1 })
            ) { entry ->
                val categoriaId = entry.arguments?.getInt("categoriaId") ?: -1
                // Cadastro é só do Administrador: cliente que cair aqui volta
                if (viewModel.isAdmin) {
                    NovoProdutoScreen(navController, viewModel, categoriaInicial = categoriaId)
                } else {
                    LaunchedEffect(Unit) { navController.popBackStack() }
                }
            }

            // ----- Detalhes (recebem argumento pela rota) -----
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

// Troca de aba sem empilhar várias cópias da mesma tela
fun NavHostController.irParaAba(rota: String) {
    navigate(rota) {
        popUpTo(Rotas.INICIO) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

// Limpa toda a pilha e abre o Login (usado no logout)
fun NavHostController.irParaLogin() {
    navigate(Rotas.LOGIN) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
    // Descarta as pilhas salvas das abas (irParaAba usa saveState),
    // senão o próximo perfil reabriria as telas da sessão anterior
    Rotas.abas.forEach { clearBackStack(it) }
}
