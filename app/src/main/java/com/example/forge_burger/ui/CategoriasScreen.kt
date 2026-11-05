package com.example.forge_burger.ui

import android.widget.Toast
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.forge_burger.data.Categoria
import com.example.forge_burger.data.Perfil
import com.example.forge_burger.ui.theme.CorFundo
import com.example.forge_burger.ui.theme.CorLaranja
import com.example.forge_burger.ui.theme.CorTextoCinza
import com.example.forge_burger.viewmodel.BurgerViewModel
import com.example.forge_burger.viewmodel.CategoriaViewModel
import com.example.forge_burger.viewmodel.ProdutoViewModel

@Composable
fun CategoriasScreen(
    navController: NavHostController,
    categoriaViewModel: CategoriaViewModel,
    produtoViewModel: ProdutoViewModel,
    burgerViewModel: BurgerViewModel
) {
    val context = LocalContext.current
    val estadoCategoria by categoriaViewModel.uiState.collectAsState()
    val estadoProduto by produtoViewModel.uiState.collectAsState()
    val estadoBurger by burgerViewModel.uiState.collectAsState()

    val isAdmin = estadoBurger.perfilLogado == Perfil.ADMIN

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
            if (isAdmin) {
                item {
                    CardEscuro(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = if (estadoCategoria.estouEditando) "Editar categoria" else "Nova categoria",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(Modifier.height(12.dp))
                            Row {
                                OutlinedTextField(
                                    value = estadoCategoria.emojiCampo,
                                    onValueChange = { categoriaViewModel.mudarEmoji(it) },
                                    label = { Text("Ícone") },
                                    placeholder = { Text("🍕") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = coresCampo(),
                                    modifier = Modifier.width(84.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                OutlinedTextField(
                                    value = estadoCategoria.nomeCampo,
                                    onValueChange = { categoriaViewModel.mudarNome(it) },
                                    label = { Text("Nome") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = coresCampo(),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = estadoCategoria.descricaoCampo,
                                onValueChange = { categoriaViewModel.mudarDescricao(it) },
                                label = { Text("Descrição") },
                                minLines = 2,
                                shape = RoundedCornerShape(12.dp),
                                colors = coresCampo(),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(12.dp))
                            BotaoLaranja(
                                texto = if (estadoCategoria.estouEditando) "Atualizar categoria" else "Adicionar categoria",
                                habilitado = estadoCategoria.nomeCampo.isNotBlank(),
                                onClick = { categoriaViewModel.salvarCategoria() },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            item {
                TituloSecao("Todas (${estadoCategoria.listaCategorias.size})", modifier = Modifier.padding(top = 8.dp))
            }

            items(estadoCategoria.listaCategorias, key = { it.id }) { categoria ->
                val quantidadeProdutos = estadoProduto.quantidadePorCategoria[categoria.id] ?: 0

                CardCategoria(
                    categoria = categoria,
                    quantidadeProdutos = quantidadeProdutos,
                    onClick = { navController.navigate(Rotas.categoria(categoria.id)) },
                    onAtualizar = if (isAdmin) ({
                        categoriaViewModel.comecarEdicao(categoria)
                    }) else null,
                    onRemover = if (isAdmin) ({
                        if (quantidadeProdutos > 0) {
                            Toast.makeText(
                                context,
                                "Remova os produtos de \"${categoria.nome}\" antes",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            categoriaViewModel.removerCategoria(categoria)
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
