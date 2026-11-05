package com.example.forge_burger.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.forge_burger.data.Perfil
import com.example.forge_burger.ui.theme.CorFundo
import com.example.forge_burger.ui.theme.CorLaranja
import com.example.forge_burger.ui.theme.CorTextoCinza
import com.example.forge_burger.ui.theme.formatarPreco
import com.example.forge_burger.viewmodel.BurgerViewModel
import com.example.forge_burger.viewmodel.ProdutoViewModel

@Composable
fun PerfilScreen(
    navController: NavHostController,
    burgerViewModel: BurgerViewModel,
    produtoViewModel: ProdutoViewModel
) {
    val context = LocalContext.current
    val estadoBurger by burgerViewModel.uiState.collectAsState()
    val estadoProduto by produtoViewModel.uiState.collectAsState()

    val favoritos = estadoProduto.produtosFavoritos
    val isAdmin = estadoBurger.perfilLogado == Perfil.ADMIN

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
                Text("Olá, ${estadoBurger.nomeUsuario}!", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(
                    "${estadoBurger.pedidos.size} pedido(s) • ${favoritos.size} favorito(s)",
                    color = CorTextoCinza,
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isAdmin) CorLaranja else Color(60, 45, 20)
                ) {
                    Text(
                        text = estadoBurger.perfilLogado?.rotulo ?: "Sem sessão",
                        color = if (isAdmin) Color.Black else CorLaranja,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                    )
                }
                Spacer(Modifier.height(12.dp))
                OutlinedButton(onClick = { burgerViewModel.sairDaConta() }) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = CorLaranja)
                    Spacer(Modifier.width(8.dp))
                    Text("Sair", color = CorLaranja, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            CardEscuro(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Dados de entrega", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = estadoBurger.nomeCampoPerfil,
                        onValueChange = { burgerViewModel.mudarNomePerfil(it) },
                        label = { Text("Nome") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = coresCampo(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = estadoBurger.enderecoCampoPerfil,
                        onValueChange = { burgerViewModel.mudarEnderecoPerfil(it) },
                        label = { Text("Endereço") },
                        minLines = 2,
                        shape = RoundedCornerShape(12.dp),
                        colors = coresCampo(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = estadoBurger.telefoneCampoPerfil,
                        onValueChange = { burgerViewModel.mudarTelefonePerfil(it) },
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
                            burgerViewModel.salvarDadosPerfil()
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
                    Text(if (isAdmin) "Gerenciar categorias" else "Ver categorias", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
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
                    FotoProduto(produto.emoji, foto = produto.foto, modifier = Modifier.size(44.dp), tamanhoEmoji = 22)
                    Spacer(Modifier.width(12.dp))
                    Text(produto.nome, color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text(formatarPreco(produto.preco), color = CorLaranja, fontWeight = FontWeight.Bold)
                }
            }
        }

        item { TituloSecao("Meus pedidos", Modifier.padding(top = 8.dp)) }
        if (estadoBurger.pedidos.isEmpty()) {
            item { Text("Você ainda não fez nenhum pedido.", color = CorTextoCinza, fontSize = 13.sp) }
        }
        items(estadoBurger.pedidos, key = { "pedido-${it.numero}" }) { pedido ->
            CardEscuro(
                Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(Rotas.pedido(pedido.numero)) }
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Pedido #${pedido.numero}", color = Color.White, fontWeight = FontWeight.Bold)
                        Text(
                            "${pedido.quantidadeItens} item(ns) • ${pedido.formaPagamento}",
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
