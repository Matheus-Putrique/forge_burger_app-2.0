package com.example.forge_burger.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.forge_burger.data.Perfil
import com.example.forge_burger.data.Produto
import com.example.forge_burger.ui.theme.CorCard
import com.example.forge_burger.ui.theme.CorFundo
import com.example.forge_burger.ui.theme.CorLaranja
import com.example.forge_burger.ui.theme.CorTextoCinza
import com.example.forge_burger.ui.theme.formatarPreco
import com.example.forge_burger.viewmodel.BurgerViewModel
import com.example.forge_burger.viewmodel.CategoriaViewModel
import com.example.forge_burger.viewmodel.ProdutoViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MenuScreen(
    navController: NavHostController,
    produtoViewModel: ProdutoViewModel,
    categoriaViewModel: CategoriaViewModel,
    burgerViewModel: BurgerViewModel
) {
    val estadoProduto by produtoViewModel.uiState.collectAsState()
    val estadoCategoria by categoriaViewModel.uiState.collectAsState()
    val estadoBurger by burgerViewModel.uiState.collectAsState()

    val isAdmin = estadoBurger.perfilLogado == Perfil.ADMIN

    val categoriaAtual = estadoProduto.categoriaFiltroId?.let { idFiltro ->
        estadoCategoria.listaCategorias.find { it.id == idFiltro }
    }

    val produtosFiltrados = estadoProduto.listaProdutos
        .filter { categoriaAtual == null || it.categoriaId == categoriaAtual.id }
        .let { lista -> if (estadoProduto.ordenarPorPreco) lista.sortedBy { it.preco } else lista }

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

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ChipCategoria("Todos", categoriaAtual == null) {
                        produtoViewModel.selecionarFiltroCategoria(null)
                    }
                    estadoCategoria.listaCategorias.forEach { categoria ->
                        ChipCategoria(categoria.nome, categoriaAtual?.id == categoria.id) {
                            produtoViewModel.selecionarFiltroCategoria(categoria.id)
                        }
                    }
                    if (isAdmin) {
                        ChipCategoria("+ Gerenciar", false) {
                            navController.navigate(Rotas.CATEGORIAS)
                        }
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
                        text = if (estadoProduto.ordenarPorPreco) "Preço ↑" else "Ordenar",
                        color = if (estadoProduto.ordenarPorPreco) CorLaranja else CorTextoCinza,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { produtoViewModel.alternarOrdenacaoPorPreco() }
                            .padding(8.dp)
                    )
                }
                Text(
                    text = if (isAdmin) "Toque para personalizar • ✏️ para editar • 🗑 para remover"
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

            items(produtosFiltrados.chunked(2)) { linha ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    linha.forEachIndexed { indice, produto ->
                        if (indice == 1) Spacer(modifier = Modifier.width(12.dp))
                        CardMenuBurger(
                            produto = produto,
                            onClick = { navController.navigate(Rotas.produto(produto.id)) },
                            onEditar = if (isAdmin) ({
                                produtoViewModel.prepararEdicaoProduto(produto)
                                navController.navigate(Rotas.novoProduto(produto.categoriaId))
                            }) else null,
                            onRemover = if (isAdmin) ({
                                produtoViewModel.pedirConfirmacaoRemocao(produto)
                            }) else null,
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
                onClick = {
                    val primeiraCat = estadoCategoria.listaCategorias.firstOrNull()?.id
                    produtoViewModel.prepararNovoProduto(categoriaAtual?.id, primeiraCat)
                    navController.navigate(Rotas.novoProduto(categoriaAtual?.id))
                },
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

    estadoProduto.produtoParaRemover?.takeIf { isAdmin }?.let { produto ->
        AlertDialog(
            onDismissRequest = { produtoViewModel.cancelarRemocao() },
            title = { Text("Remover produto?") },
            text = { Text("\"${produto.nome}\" será removido do cardápio.") },
            confirmButton = {
                TextButton(onClick = { produtoViewModel.confirmarRemocaoProduto() }) {
                    Text("Remover", color = CorLaranja)
                }
            },
            dismissButton = {
                TextButton(onClick = { produtoViewModel.cancelarRemocao() }) {
                    Text("Cancelar", color = CorTextoCinza)
                }
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
    onEditar: (() -> Unit)? = null,
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
                if (onEditar != null) {
                    IconButton(onClick = onEditar, modifier = Modifier.size(28.dp)) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = CorLaranja,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
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
