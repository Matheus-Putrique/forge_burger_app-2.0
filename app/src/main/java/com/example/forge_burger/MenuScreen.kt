package com.example.forge_burger

// ╔══════════════════════════════════════════════╗
// ║  MenuScreen.kt                               ║
// ║  ABA INÍCIO: cardápio (lista de Produtos)    ║
// ║  Clique → detalhes · Segurar → remover (adm) ║
// ╚══════════════════════════════════════════════╝

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.forge_burger.ui.theme.CorCard
import com.example.forge_burger.ui.theme.CorFundo
import com.example.forge_burger.ui.theme.CorLaranja
import com.example.forge_burger.ui.theme.CorTextoCinza
import com.example.forge_burger.ui.theme.ForgeBurgerTheme
import com.example.forge_burger.ui.theme.formatarPreco

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MenuScreen(navController: NavHostController, viewModel: BurgerViewModel) {
    // null = "Todos"
    var categoriaSelecionada by rememberSaveable { mutableStateOf<Int?>(null) }
    var ordenarPorPreco by rememberSaveable { mutableStateOf(false) }
    var produtoParaRemover by remember { mutableStateOf<Produto?>(null) }
    // Cadastro e remoção só aparecem para o Administrador
    val isAdmin = viewModel.isAdmin

    // Se a categoria escolhida foi apagada, volta para "Todos"
    val categoriaAtual = categoriaSelecionada?.let { viewModel.buscarCategoria(it) }

    val produtosFiltrados = viewModel.produtos
        .filter { categoriaAtual == null || it.categoriaId == categoriaAtual.id }
        .let { lista -> if (ordenarPorPreco) lista.sortedBy { it.preco } else lista }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CorFundo)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 88.dp)
        ) {
            item {
                CabecalhoMarca(
                    onMenu = { navController.navigate(Rotas.CATEGORIAS) },
                    onPerfil = { navController.irParaAba(Rotas.PERFIL) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                BarraBuscaFalsa(onClick = { navController.irParaAba(Rotas.BUSCA) })
                Spacer(modifier = Modifier.height(16.dp))

                // Chips de categoria (vêm da lista de Categorias)
                // Quebram para a linha de baixo quando não cabem, assim nenhum fica cortado
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ChipCategoria("Todos", categoriaAtual == null) { categoriaSelecionada = null }
                    viewModel.categorias.forEach { categoria ->
                        ChipCategoria(categoria.nome, categoriaAtual?.id == categoria.id) {
                            categoriaSelecionada = categoria.id
                        }
                    }
                    if (isAdmin) {
                        ChipCategoria("+ Gerenciar", false) { navController.navigate(Rotas.CATEGORIAS) }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TituloSecao(
                        texto = categoriaAtual?.nome ?: "Lanches Artesanais",
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = if (ordenarPorPreco) "Preço ↑" else "Ordenar",
                        color = if (ordenarPorPreco) CorLaranja else CorTextoCinza,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { ordenarPorPreco = !ordenarPorPreco }
                            .padding(8.dp)
                    )
                }
                Text(
                    text = if (isAdmin) "Toque para personalizar • 🗑 ou segure para remover"
                    else "Toque para personalizar e adicionar ao carrinho",
                    color = CorTextoCinza,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (produtosFiltrados.isEmpty()) {
                item {
                    EstadoVazio(
                        "🍽️",
                        "Nenhum produto aqui",
                        if (isAdmin) "Toque em \"Novo lanche\" para cadastrar." else "Volte mais tarde para novidades."
                    )
                }
            }

            // Grade de 2 colunas: cada linha da LazyColumn tem 2 cards
            items(produtosFiltrados.chunked(2)) { linha ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    linha.forEachIndexed { indice, produto ->
                        if (indice == 1) Spacer(modifier = Modifier.width(12.dp))
                        CardMenuBurger(
                            produto = produto,
                            onClick = { navController.navigate(Rotas.produto(produto.id)) },
                            onRemover = if (isAdmin) ({ produtoParaRemover = produto }) else null,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (linha.size == 1) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        if (isAdmin) {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(Rotas.novoProduto(categoriaAtual?.id)) },
                containerColor = CorLaranja,
                contentColor = Color.Black,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Novo lanche", fontWeight = FontWeight.Bold) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )
        }
    }

    // Confirmação antes de remover
    produtoParaRemover?.takeIf { isAdmin }?.let { produto ->
        AlertDialog(
            onDismissRequest = { produtoParaRemover = null },
            title = { Text("Remover produto?") },
            text = { Text("\"${produto.nome}\" será removido do cardápio.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.removerProduto(produto)
                    produtoParaRemover = null
                }) { Text("Remover", color = CorLaranja) }
            },
            dismissButton = {
                TextButton(onClick = { produtoParaRemover = null }) { Text("Cancelar", color = CorTextoCinza) }
            },
            containerColor = CorCard
        )
    }
}

@Composable
private fun BarraBuscaFalsa(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(24.dp),
        color = CorCard
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = CorTextoCinza)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Buscar lanches, bebidas, porções...",
                color = CorTextoCinza,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun ChipCategoria(nome: String, ativo: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (ativo) CorLaranja else CorCard,
        modifier = Modifier.padding(end = 8.dp)
    ) {
        Text(
            text = nome,
            color = if (ativo) Color.Black else CorTextoCinza,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardMenuBurger(
    produto: Produto,
    onClick: () -> Unit,
    // null = sem permissão para remover (some a lixeira e o "segurar")
    onRemover: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    CardEscuro(
        modifier = modifier.combinedClickable(onClick = onClick, onLongClick = onRemover)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            FotoProduto(
                emoji = produto.emoji,
                foto = produto.foto,
                tamanhoEmoji = 48,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = produto.nome,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = CorLaranja, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "%.1f".format(produto.nota),
                    color = CorTextoCinza,
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )
                // Lixeira: remove o produto da lista (pede confirmação)
                if (onRemover != null) {
                    IconButton(onClick = onRemover, modifier = Modifier.size(28.dp)) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Remover",
                            tint = CorTextoCinza,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatarPreco(produto.preco),
                    color = CorLaranja,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    onClick = onClick,
                    shape = RoundedCornerShape(50),
                    color = CorLaranja,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "+", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun MenuScreenPreview() {
    ForgeBurgerTheme {
        MenuScreen(rememberNavController(), viewModel())
    }
}
