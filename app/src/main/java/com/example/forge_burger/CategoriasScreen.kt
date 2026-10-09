package com.example.forge_burger

// ╔══════════════════════════════════════════════╗
// ║  CategoriasScreen.kt                         ║
// ║  LISTA DE CATEGORIAS                         ║
// ║  Adicionar · Remover: só Administrador       ║
// ║  Clique no card → detalhes da categoria      ║
// ╚══════════════════════════════════════════════╝

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.saveable.rememberSaveable
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

@Composable
fun CategoriasScreen(navController: NavHostController, viewModel: BurgerViewModel) {
    val context = LocalContext.current
    val isAdmin = viewModel.isAdmin

    var nome by rememberSaveable { mutableStateOf("") }
    var emoji by rememberSaveable { mutableStateOf("") }
    var descricao by rememberSaveable { mutableStateOf("") }

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
            // ----- Formulário de nova categoria (só admin) -----
            if (isAdmin) item {
                CardEscuro(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Nova categoria", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                            texto = "Adicionar categoria",
                            habilitado = nome.isNotBlank(),
                            onClick = {
                                viewModel.adicionarCategoria(nome, emoji, descricao)
                                nome = ""
                                emoji = ""
                                descricao = ""
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            item {
                TituloSecao("Todas (${viewModel.categorias.size})", modifier = Modifier.padding(top = 8.dp))
            }

            // ----- Lista de categorias -----
            items(viewModel.categorias, key = { it.id }) { categoria ->
                CardCategoria(
                    categoria = categoria,
                    quantidadeProdutos = viewModel.produtosDaCategoria(categoria.id).size,
                    onClick = { navController.navigate(Rotas.categoria(categoria.id)) },
                    onRemover = if (isAdmin) ({
                        val removeu = viewModel.removerCategoria(categoria)
                        val msg = if (removeu) "Categoria removida"
                        else "Remova os produtos de \"${categoria.nome}\" antes"
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
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
    // null = sem permissão (lixeira escondida)
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
