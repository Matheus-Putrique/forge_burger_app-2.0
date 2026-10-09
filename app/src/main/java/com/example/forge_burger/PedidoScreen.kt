package com.example.forge_burger

// ╔══════════════════════════════════════════════╗
// ║  PedidoScreen.kt                             ║
// ║  PEDIDO CONFIRMADO / detalhes de um pedido   ║
// ║  (recebe o número do pedido pela rota)       ║
// ╚══════════════════════════════════════════════╝

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun PedidoScreen(navController: NavHostController, viewModel: BurgerViewModel, numero: Int) {
    val pedido = viewModel.buscarPedido(numero)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CorFundo)
    ) {
        BarraTopo("Pedido #$numero", onVoltar = { navController.popBackStack() })

        if (pedido == null) {
            EstadoVazio("🤷", "Pedido não encontrado", "")
            return@Column
        }

        // Estimativa simples: 25 min + 3 min por item
        val minutos = 25 + pedido.itens.sumOf { it.quantidade } * 3

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("✅", fontSize = 56.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Pedido confirmado!", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("Chega em aproximadamente $minutos min", color = CorLaranja, fontSize = 14.sp)
                    Spacer(Modifier.height(8.dp))
                }
            }

            item {
                CardEscuro(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Entrega", color = CorTextoCinza, fontSize = 12.sp)
                        Text(pedido.endereco, color = Color.White, fontSize = 14.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("Pagamento", color = CorTextoCinza, fontSize = 12.sp)
                        Text(pedido.formaPagamento, color = Color.White, fontSize = 14.sp)
                    }
                }
            }

            item { TituloSecao("Itens") }

            items(pedido.itens, key = { it.id }) { item ->
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    FotoProduto(item.produto.emoji, foto = item.produto.foto, modifier = Modifier.size(44.dp), tamanhoEmoji = 22)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("${item.quantidade}x ${item.produto.nome}", color = Color.White, fontWeight = FontWeight.Bold)
                        if (item.resumo.isNotBlank()) Text(item.resumo, color = CorTextoCinza, fontSize = 12.sp)
                        if (item.observacao.isNotBlank()) Text("Obs: ${item.observacao}", color = CorTextoCinza, fontSize = 12.sp)
                    }
                    Text(formatarPreco(item.total), color = Color.White)
                }
            }

            item {
                HorizontalDivider(color = Color(40, 40, 40), modifier = Modifier.padding(vertical = 8.dp))
                LinhaValor("Subtotal", formatarPreco(pedido.subtotal))
                LinhaValor("Taxa de Entrega", formatarPreco(pedido.taxaEntrega))
                LinhaValor("Total", formatarPreco(pedido.total), destaque = true)
            }
        }

        BotaoLaranja(
            texto = "Voltar ao cardápio",
            onClick = {
                navController.navigate(Rotas.INICIO) {
                    popUpTo(Rotas.INICIO) { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}
