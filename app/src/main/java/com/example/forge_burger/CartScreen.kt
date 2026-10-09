package com.example.forge_burger

// ╔══════════════════════════════════════════════╗
// ║  CartScreen.kt                               ║
// ║  ABA CARRINHO: itens, entrega, pagamento     ║
// ║  e finalização do pedido                     ║
// ╚══════════════════════════════════════════════╝

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
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
import com.example.forge_burger.ui.theme.CorFundo
import com.example.forge_burger.ui.theme.CorLaranja
import com.example.forge_burger.ui.theme.CorTextoCinza
import com.example.forge_burger.ui.theme.ForgeBurgerTheme
import com.example.forge_burger.ui.theme.formatarPreco

private val formasPagamento = listOf("Pix", "Cartão na entrega", "Dinheiro")

@Composable
fun CartScreen(navController: NavHostController, viewModel: BurgerViewModel) {
    var pagamento by rememberSaveable { mutableStateOf(formasPagamento[0]) }

    val subtotal = viewModel.subtotalCarrinho()
    val totalGeral = subtotal + viewModel.taxaEntrega

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

        if (viewModel.carrinho.isEmpty()) {
            EstadoVazio(
                emoji = "🛍️",
                titulo = "Seu carrinho está vazio",
                subtitulo = "Escolha um lanche no cardápio para começar.",
                modifier = Modifier.weight(1f)
            )
            BotaoLaranja(
                texto = "Ver cardápio",
                onClick = { navController.irParaAba(Rotas.INICIO) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            return@Column
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Meu Carrinho",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            items(viewModel.carrinho, key = { it.id }) { item ->
                CardItemDoPedido(
                    item = item,
                    onClick = { navController.navigate(Rotas.produto(item.produto.id)) },
                    aoDiminuir = { viewModel.alterarQuantidade(item, item.quantidade - 1) },
                    aoAumentar = { viewModel.alterarQuantidade(item, item.quantidade + 1) },
                    aoRemover = { viewModel.removerDoCarrinho(item) }
                )
            }

            item {
                CardEscuro(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = CorLaranja)
                        Spacer(Modifier.width(8.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Entregar em", color = CorTextoCinza, fontSize = 12.sp)
                            Text(viewModel.endereco.ifBlank { "Cadastre um endereço" }, color = Color.White, fontSize = 14.sp)
                        }
                        Text(
                            "Alterar",
                            color = CorLaranja,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier
                                .clickable { navController.irParaAba(Rotas.PERFIL) }
                                .padding(8.dp)
                        )
                    }
                }
            }

            item {
                TituloSecao("Pagamento")
                formasPagamento.forEach { forma ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(selected = pagamento == forma, onClick = { pagamento = forma }),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = pagamento == forma,
                            onClick = { pagamento = forma },
                            colors = RadioButtonDefaults.colors(selectedColor = CorLaranja)
                        )
                        Text(forma, color = Color.White, fontSize = 14.sp)
                    }
                }
            }

            item {
                HorizontalDivider(color = Color(40, 40, 40))
                Spacer(modifier = Modifier.height(8.dp))
                LinhaValor("Subtotal", formatarPreco(subtotal))
                LinhaValor("Taxa de Entrega", formatarPreco(viewModel.taxaEntrega))
                Spacer(modifier = Modifier.height(4.dp))
                LinhaValor("Total", formatarPreco(totalGeral), destaque = true)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        BotaoLaranja(
            texto = "Finalizar Pedido",
            habilitado = viewModel.endereco.isNotBlank(),
            onClick = {
                val numero = viewModel.finalizarPedido(pagamento)
                navController.navigate(Rotas.pedido(numero))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Composable
fun CardItemDoPedido(
    item: ItemCarrinho,
    onClick: () -> Unit,
    aoDiminuir: () -> Unit,
    aoAumentar: () -> Unit,
    aoRemover: () -> Unit
) {
    CardEscuro(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FotoProduto(emoji = item.produto.emoji, foto = item.produto.foto, modifier = Modifier.size(54.dp))

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.produto.nome,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (item.resumo.isNotBlank()) {
                    Text(text = item.resumo, color = CorTextoCinza, fontSize = 12.sp)
                }
                if (item.observacao.isNotBlank()) {
                    Text(text = "Obs: ${item.observacao}", color = CorTextoCinza, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatarPreco(item.total),
                    color = CorLaranja,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                SeletorQuantidade(item.quantidade, aoDiminuir, aoAumentar)
            }

            IconButton(onClick = aoRemover) {
                Icon(Icons.Default.Delete, contentDescription = "Remover", tint = CorTextoCinza)
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun CartScreenPreview() {
    ForgeBurgerTheme {
        CartScreen(rememberNavController(), viewModel())
    }
}
