package com.example.forge_burger

// ╔══════════════════════════════════════════════╗
// ║  BuscaScreen.kt                              ║
// ║  ABA BUSCAR: filtra produtos pelo nome       ║
// ╚══════════════════════════════════════════════╝

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
fun BuscaScreen(navController: NavHostController, viewModel: BurgerViewModel) {
    var texto by rememberSaveable { mutableStateOf("") }

    val resultados = viewModel.produtos.filter {
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
            onValueChange = { texto = it },
            placeholder = { Text("Buscar lanches, bebidas, porções...", color = CorTextoCinza) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CorTextoCinza) },
            trailingIcon = {
                if (texto.isNotEmpty()) {
                    IconButton(onClick = { texto = "" }) {
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
                // Sem texto: atalhos para as categorias
                item { TituloSecao("Navegue por categoria") }
                items(viewModel.categorias, key = { it.id }) { categoria ->
                    CardEscuro(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate(Rotas.categoria(categoria.id)) }
                    ) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(categoria.emoji, fontSize = 28.sp)
                            Spacer(Modifier.width(12.dp))
                            Text(categoria.nome, color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Text("${viewModel.produtosDaCategoria(categoria.id).size}", color = CorLaranja, fontWeight = FontWeight.Bold)
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
                                    viewModel.buscarCategoria(produto.categoriaId)?.nome ?: "",
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

@Preview(showBackground = true, heightDp = 800)
@Composable
fun BuscaScreenPreview() {
    ForgeBurgerTheme {
        BuscaScreen(rememberNavController(), viewModel())
    }
}
