package com.example.forge_burger

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun CategoriasScreen(navController: NavHostController, viewModel: BurgerViewModel) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val categoriaDAO = db.categoriaDAO()
    val produtoDAO = db.produtoDAO()
    val isAdmin = viewModel.isAdmin

    var estouEditando by remember { mutableStateOf(false) }
    var categoriaEmEdicao by remember { mutableStateOf<Categoria?>(null) }

    var nome by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }

    // Carrega categorias e produtos do banco ao abrir a tela
    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val cats = carregarCategoriasDoBanco(categoriaDAO)
            val prods = carregarProdutosDoBanco(produtoDAO)
            viewModel.atualizarListaCategorias(cats)
            viewModel.atualizarListaProdutos(prods)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CorFundo)
            .imePadding()
    ) {
        BarraTopo("Categorias", onVoltar = { navController.popBackStack() })

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isAdmin) item {
                CardEscuro(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (estouEditando) "Editar categoria" else "Nova categoria",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(Modifier.height(12.dp))
                        Row {
                            OutlinedTextField(
                                value = emoji,
                                onValueChange = { if (it.length <= 2) emoji = it },
                                label = { Text("Ícone") },
                                placeholder = { Text("🍕") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = coresCampo(),
                                modifier = Modifier.width(84.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            OutlinedTextField(
                                value = nome,
                                onValueChange = { nome = it },
                                label = { Text("Nome") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = coresCampo(),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = descricao,
                            onValueChange = { descricao = it },
                            label = { Text("Descrição") },
                            minLines = 2,
                            shape = RoundedCornerShape(12.dp),
                            colors = coresCampo(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(12.dp))
                        BotaoLaranja(
                            texto = if (estouEditando) "Atualizar categoria" else "Adicionar categoria",
                            habilitado = nome.isNotBlank(),
                            onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    if (estouEditando && categoriaEmEdicao != null) {
                                        val catTemp = categoriaEmEdicao!!.copy(
                                            nome = nome.trim(),
                                            emoji = emoji.ifBlank { "🍽️" },
                                            descricao = descricao.trim()
                                        )
                                        atualizarCategoria(catTemp, categoriaDAO)
                                        estouEditando = false
                                    } else {
                                        inserirCategoria(
                                            Categoria(0, nome.trim(), emoji.ifBlank { "🍽️" }, descricao.trim()),
                                            categoriaDAO
                                        )
                                    }
                                    nome = ""
                                    emoji = ""
                                    descricao = ""
                                    categoriaEmEdicao = null
                                    val atualizadas = buscarCategorias(categoriaDAO)
                                    viewModel.atualizarListaCategorias(atualizadas)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            item {
                TituloSecao("Todas (${viewModel.categorias.size})", modifier = Modifier.padding(top = 8.dp))
            }

            items(viewModel.categorias, key = { it.id }) { categoria ->
                CardCategoria(
                    categoria = categoria,
                    quantidadeProdutos = viewModel.produtosDaCategoria(categoria.id).size,
                    onClick = { navController.navigate(Rotas.categoria(categoria.id)) },
                    onAtualizar = if (isAdmin) ({
                        nome = categoria.nome
                        emoji = categoria.emoji
                        descricao = categoria.descricao
                        estouEditando = true
                        categoriaEmEdicao = categoria
                    }) else null,
                    onRemover = if (isAdmin) ({
                        if (viewModel.produtosDaCategoria(categoria.id).isNotEmpty()) {
                            Toast.makeText(
                                context,
                                "Remova os produtos de \"${categoria.nome}\" antes",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            CoroutineScope(Dispatchers.IO).launch {
                                deletarCategoria(categoria, categoriaDAO)
                                val atualizadas = buscarCategorias(categoriaDAO)
                                viewModel.atualizarListaCategorias(atualizadas)
                            }
                            Toast.makeText(context, "Categoria removida", Toast.LENGTH_SHORT).show()
                        }
                    }) else null
                )
            }
        }
    }
}

@Composable
private fun CardCategoria(
    categoria: Categoria,
    quantidadeProdutos: Int,
    onClick: () -> Unit,
    onAtualizar: (() -> Unit)? = null,
    onRemover: (() -> Unit)?
) {
    CardEscuro(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FotoProduto(emoji = categoria.emoji, modifier = Modifier.size(54.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(categoria.nome, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = if (quantidadeProdutos == 1) "1 produto" else "$quantidadeProdutos produtos",
                    color = CorLaranja,
                    fontSize = 12.sp
                )
                if (categoria.descricao.isNotBlank()) {
                    Text(categoria.descricao, color = CorTextoCinza, fontSize = 12.sp, maxLines = 1)
                }
            }
            if (onAtualizar != null) {
                IconButton(onClick = onAtualizar) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = CorLaranja)
                }
            }
            if (onRemover != null) {
                IconButton(onClick = onRemover) {
                    Icon(Icons.Default.Delete, contentDescription = "Remover", tint = CorTextoCinza)
                }
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun CategoriasScreenPreview() {
    ForgeBurgerTheme {
        CategoriasScreen(rememberNavController(), viewModel())
    }
}
