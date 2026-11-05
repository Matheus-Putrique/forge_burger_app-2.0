package com.example.forge_burger.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.forge_burger.data.Perfil
import com.example.forge_burger.ui.theme.CorFundo
import com.example.forge_burger.ui.theme.CorLaranja
import com.example.forge_burger.ui.theme.CorTextoCinza
import com.example.forge_burger.ui.theme.formatarPreco
import com.example.forge_burger.viewmodel.BurgerViewModel
import com.example.forge_burger.viewmodel.CategoriaViewModel
import com.example.forge_burger.viewmodel.ProdutoViewModel

@Composable
fun CategoriaDetalhesScreen(
    navController: NavHostController,
    categoriaViewModel: CategoriaViewModel,
    produtoViewModel: ProdutoViewModel,
    burgerViewModel: BurgerViewModel,
    categoriaId: Int
) {
    val estadoCategoria by categoriaViewModel.uiState.collectAsState()
    val estadoProduto by produtoViewModel.uiState.collectAsState()
    val estadoBurger by burgerViewModel.uiState.collectAsState()

    val isAdmin = estadoBurger.perfilLogado == Perfil.ADMIN

    LaunchedEffect(categoriaId) {
        categoriaViewModel.carregarCategoriaPorId(categoriaId)
        produtoViewModel.carregarProdutosDaCategoria(categoriaId)
    }

    val catAtual = estadoCategoria.categoriaDetalhe
    val produtos = estadoProduto.produtosDaCategoriaDetalhe

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CorFundo)
    ) {
        BarraTopo(catAtual?.nome ?: "Categoria", onVoltar = { navController.popBackStack() })

        if (catAtual == null) {
            EstadoVazio("🤷", "Categoria não encontrada", "Ela pode ter sido removida.")
            return@Column
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FotoProduto(catAtual.emoji, tamanhoEmoji = 56, modifier = Modifier.size(110.dp))
                    Spacer(Modifier.height(12.dp))
                    Text(catAtual.nome, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    if (catAtual.descricao.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(catAtual.descricao, color = CorTextoCinza, fontSize = 14.sp, textAlign = TextAlign.Center)
                    }
                }
            }

            item {
                Row(Modifier.fillMaxWidth()) {
                    CardEstatistica("Produtos", "${produtos.size}", Modifier.weight(1f))
                    Spacer(Modifier.width(8.dp))
                    CardEstatistica(
                        "Preço médio",
                        estadoProduto.precoMedioCategoriaDetalhe?.let { formatarPreco(it) } ?: "-",
                        Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    CardEstatistica(
                        "Mais barato",
                        estadoProduto.precoMinimoCategoriaDetalhe?.let { formatarPreco(it) } ?: "-",
                        Modifier.weight(1f)
                    )
                }
            }

            item { TituloSecao("Produtos desta categoria") }

            if (produtos.isEmpty()) {
                item {
                    Text(
                        if (isAdmin) "Nenhum produto ainda. Cadastre um pelo botão \"Novo produto\" abaixo."
                        else "Nenhum produto nesta categoria por enquanto.",
                        color = CorTextoCinza,
                        fontSize = 14.sp
                    )
                }
            }

            items(produtos, key = { it.id }) { produto ->
                CardEscuro(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate(Rotas.produto(produto.id)) }
                ) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        FotoProduto(produto.emoji, foto = produto.foto, modifier = Modifier.size(54.dp))
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(produto.nome, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(produto.descricao, color = CorTextoCinza, fontSize = 12.sp, maxLines = 2)
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(formatarPreco(produto.preco), color = CorLaranja, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (isAdmin) {
                item {
                    Spacer(Modifier.height(8.dp))
                    BotaoLaranja(
                        texto = "Novo produto",
                        onClick = {
                            produtoViewModel.prepararNovoProduto(catAtual.id, estadoCategoria.primeiraCategoriaId)
                            navController.navigate(Rotas.novoProduto(catAtual.id))
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun CardEstatistica(rotulo: String, valor: String, modifier: Modifier = Modifier) {
    CardEscuro(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(valor, color = CorLaranja, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(rotulo, color = CorTextoCinza, fontSize = 11.sp)
        }
    }
}
