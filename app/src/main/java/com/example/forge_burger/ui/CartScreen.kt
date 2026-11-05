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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
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
import com.example.forge_burger.data.ItemCarrinho
import com.example.forge_burger.ui.theme.CorFundo
import com.example.forge_burger.ui.theme.CorLaranja
import com.example.forge_burger.ui.theme.CorTextoCinza
import com.example.forge_burger.ui.theme.formatarPreco
import com.example.forge_burger.viewmodel.BurgerViewModel

@Composable
fun CartScreen(navController: NavHostController, viewModel: BurgerViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    val subtotal = uiState.carrinho.sumOf { it.total }
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

        if (uiState.carrinho.isEmpty()) {
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

            items(uiState.carrinho, key = { it.id }) { item ->
                CardItemDoPedido(
                    item = item,
                    onClick = { navController.navigate(Rotas.produto(item.produto.id)) },
                    aoDiminuir = { viewModel.alterarQuantidadeCarrinho(item, item.quantidade - 1) },
                    aoAumentar = { viewModel.alterarQuantidadeCarrinho(item, item.quantidade + 1) },
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
                            Text(uiState.endereco.ifBlank { "Cadastre um endereço" }, color = Color.White, fontSize = 14.sp)
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
                viewModel.formasPagamento.forEach { forma ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = uiState.formaPagamentoEscolhida == forma,
                                onClick = { viewModel.escolherFormaPagamento(forma) }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = uiState.formaPagamentoEscolhida == forma,
                            onClick = { viewModel.escolherFormaPagamento(forma) },
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
            habilitado = uiState.endereco.isNotBlank(),
            onClick = {
                val numero = viewModel.finalizarPedido()
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
