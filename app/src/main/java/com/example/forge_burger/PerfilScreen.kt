package com.example.forge_burger

// ╔══════════════════════════════════════════════╗
// ║  PerfilScreen.kt                             ║
// ║  ABA PERFIL: dados de entrega, favoritos     ║
// ║  e histórico de pedidos                      ║
// ╚══════════════════════════════════════════════╝

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
fun PerfilScreen(navController: NavHostController, viewModel: BurgerViewModel) {
    val context = LocalContext.current

    // Rascunho local até apertar "Salvar"
    var nome by remember { mutableStateOf(viewModel.nomeUsuario) }
    var endereco by remember { mutableStateOf(viewModel.endereco) }
    var telefone by remember { mutableStateOf(viewModel.telefone) }

    val favoritos = viewModel.favoritos.mapNotNull { viewModel.buscarProduto(it) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CorFundo)
            .imePadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    modifier = Modifier.size(84.dp),
                    shape = RoundedCornerShape(50),
                    color = Color(80, 60, 50)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(44.dp))
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text("Olá, ${viewModel.nomeUsuario}!", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(
                    "${viewModel.pedidos.size} pedido(s) • ${favoritos.size} favorito(s)",
                    color = CorTextoCinza,
                    fontSize = 13.sp
                )
            }
        }

        item {
            CardEscuro(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Dados de entrega", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = nome,
                        onValueChange = { nome = it },
                        label = { Text("Nome") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = coresCampo(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = endereco,
                        onValueChange = { endereco = it },
                        label = { Text("Endereço") },
                        minLines = 2,
                        shape = RoundedCornerShape(12.dp),
                        colors = coresCampo(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = telefone,
                        onValueChange = { telefone = it.filter { c -> c.isDigit() || c in " ()-+" } },
                        label = { Text("Telefone") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(12.dp),
                        colors = coresCampo(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))
                    BotaoLaranja(
                        texto = "Salvar",
                        onClick = {
                            viewModel.salvarPerfil(nome, endereco, telefone)
                            Toast.makeText(context, "Dados salvos", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        item {
            CardEscuro(
                Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(Rotas.CATEGORIAS) }
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🗂️", fontSize = 22.sp)
                    Spacer(Modifier.width(12.dp))
                    Text("Gerenciar categorias", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text("›", color = CorLaranja, fontSize = 22.sp)
                }
            }
        }

        item { TituloSecao("Favoritos", Modifier.padding(top = 8.dp)) }
        if (favoritos.isEmpty()) {
            item { Text("Toque no ♡ de um produto para favoritar.", color = CorTextoCinza, fontSize = 13.sp) }
        }
        items(favoritos, key = { "fav-${it.id}" }) { produto ->
            CardEscuro(
                Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(Rotas.produto(produto.id)) }
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    FotoProduto(produto.emoji, modifier = Modifier.size(44.dp), tamanhoEmoji = 22)
                    Spacer(Modifier.width(12.dp))
                    Text(produto.nome, color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text(formatarPreco(produto.preco), color = CorLaranja, fontWeight = FontWeight.Bold)
                }
            }
        }

        item { TituloSecao("Meus pedidos", Modifier.padding(top = 8.dp)) }
        if (viewModel.pedidos.isEmpty()) {
            item { Text("Você ainda não fez nenhum pedido.", color = CorTextoCinza, fontSize = 13.sp) }
        }
        items(viewModel.pedidos, key = { "pedido-${it.numero}" }) { pedido ->
            CardEscuro(
                Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(Rotas.pedido(pedido.numero)) }
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Pedido #${pedido.numero}", color = Color.White, fontWeight = FontWeight.Bold)
                        Text(
                            "${pedido.itens.sumOf { it.quantidade }} item(ns) • ${pedido.formaPagamento}",
                            color = CorTextoCinza,
                            fontSize = 12.sp
                        )
                    }
                    Text(formatarPreco(pedido.total), color = CorLaranja, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun PerfilScreenPreview() {
    ForgeBurgerTheme {
        PerfilScreen(rememberNavController(), viewModel())
    }
}
