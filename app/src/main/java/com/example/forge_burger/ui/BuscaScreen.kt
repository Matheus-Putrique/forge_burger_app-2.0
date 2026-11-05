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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.forge_burger.ui.theme.CorFundo
import com.example.forge_burger.ui.theme.CorLaranja
import com.example.forge_burger.ui.theme.CorTextoCinza
import com.example.forge_burger.ui.theme.formatarPreco
import com.example.forge_burger.viewmodel.CategoriaViewModel
import com.example.forge_burger.viewmodel.ProdutoViewModel

@Composable
fun BuscaScreen(
    navController: NavHostController,
    produtoViewModel: ProdutoViewModel,
    categoriaViewModel: CategoriaViewModel
) {
    val estadoProduto by produtoViewModel.uiState.collectAsState()
    val estadoCategoria by categoriaViewModel.uiState.collectAsState()

    val texto = estadoProduto.textoBusca
    val resultados = estadoProduto.listaProdutos.filter {
        it.nome.contains(texto.trim(), ignoreCase = true) ||
            it.descricao.contains(texto.trim(), ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CorFundo)
    ) {
        CabecalhoMarca(
            onMenu = { navController.navigate(Rotas.CATEGORIAS) },
            onPerfil = { navController.irParaAba(Rotas.PERFIL) },
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        OutlinedTextField(
            value = texto,
            onValueChange = { produtoViewModel.mudarTextoBusca(it) },
            placeholder = { Text("Buscar lanches, bebidas, porções...", color = CorTextoCinza) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CorTextoCinza) },
            trailingIcon = {
                if (texto.isNotEmpty()) {
                    IconButton(onClick = { produtoViewModel.limparBusca() }) {
                        Icon(Icons.Default.Clear, contentDescription = "Limpar", tint = CorTextoCinza)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            colors = coresCampo(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (texto.isBlank()) {
                item { TituloSecao("Navegue por categoria") }
                items(estadoCategoria.listaCategorias, key = { it.id }) { categoria ->
                    val quantidadeNaCategoria = estadoProduto.listaProdutos.count { it.categoriaId == categoria.id }
                    CardEscuro(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate(Rotas.categoria(categoria.id)) }
                    ) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(categoria.emoji, fontSize = 28.sp)
                            Spacer(Modifier.width(12.dp))
                            Text(categoria.nome, color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Text("$quantidadeNaCategoria", color = CorLaranja, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                item {
                    Text(
                        "${resultados.size} resultado(s) para \"${texto.trim()}\"",
                        color = CorTextoCinza,
                        fontSize = 13.sp
                    )
                }
                items(resultados, key = { it.id }) { produto ->
                    val nomeDaCategoria = estadoCategoria.listaCategorias.find { it.id == produto.categoriaId }?.nome ?: ""
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
                                Text(
                                    nomeDaCategoria,
                                    color = CorTextoCinza,
                                    fontSize = 12.sp
                                )
                            }
                            Text(formatarPreco(produto.preco), color = CorLaranja, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
