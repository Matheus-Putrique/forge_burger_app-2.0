package com.example.forge_burger

// ╔══════════════════════════════════════════════╗
// ║  CategoriaDetalhesScreen.kt                  ║
// ║  DETALHES DA CATEGORIA (recebe categoriaId)  ║
// ║  Combina as duas listas: mostra os produtos  ║
// ║  da categoria + estatísticas calculadas      ║
// ╚══════════════════════════════════════════════╝

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.forge_burger.ui.theme.CorFundo
import com.example.forge_burger.ui.theme.CorLaranja
import com.example.forge_burger.ui.theme.CorTextoCinza
import com.example.forge_burger.ui.theme.ForgeBurgerTheme
import com.example.forge_burger.ui.theme.formatarPreco

@Composable
fun CategoriaDetalhesScreen(navController: NavHostController, viewModel: BurgerViewModel, categoriaId: Int) {
    val categoria = viewModel.buscarCategoria(categoriaId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CorFundo)
    ) {
        BarraTopo(categoria?.nome ?: "Categoria", onVoltar = { navController.popBackStack() })

        if (categoria == null) {
            EstadoVazio("🤷", "Categoria não encontrada", "Ela pode ter sido removida.")
            return@Column
        }

        val produtos = viewModel.produtosDaCategoria(categoria.id)

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
                    FotoProduto(categoria.emoji, tamanhoEmoji = 56, modifier = Modifier.size(110.dp))
                    Spacer(Modifier.height(12.dp))
                    Text(categoria.nome, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    if (categoria.descricao.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(categoria.descricao, color = CorTextoCinza, fontSize = 14.sp, textAlign = TextAlign.Center)
                    }
                }
            }

            // Informações calculadas a partir da lista de produtos
            item {
                Row(Modifier.fillMaxWidth()) {
                    CardEstatistica("Produtos", "${produtos.size}", Modifier.weight(1f))
                    Spacer(Modifier.width(8.dp))
                    CardEstatistica(
                        "Preço médio",
                        if (produtos.isEmpty()) "-" else formatarPreco(produtos.map { it.preco }.average()),
                        Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    CardEstatistica(
                        "Mais barato",
                        produtos.minOfOrNull { it.preco }?.let { formatarPreco(it) } ?: "-",
                        Modifier.weight(1f)
                    )
                }
            }

            item { TituloSecao("Produtos desta categoria") }

            if (produtos.isEmpty()) {
                item {
                    Text(
                        if (viewModel.isAdmin) "Nenhum produto ainda. Cadastre um pelo botão \"Novo lanche\" no cardápio."
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

            // Cadastro só para o Administrador
            if (viewModel.isAdmin) item {
                Spacer(Modifier.height(8.dp))
                BotaoLaranja(
                    texto = "Novo produto",
                    onClick = { navController.navigate(Rotas.novoProduto(categoria.id)) },
                    modifier = Modifier.fillMaxWidth()
                )
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

@Preview(showBackground = true, heightDp = 800)
@Composable
fun CategoriaDetalhesScreenPreview() {
    ForgeBurgerTheme {
        CategoriaDetalhesScreen(rememberNavController(), viewModel(), categoriaId = 1)
    }
}
