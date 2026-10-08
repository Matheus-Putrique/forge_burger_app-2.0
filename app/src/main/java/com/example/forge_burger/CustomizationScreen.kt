package com.example.forge_burger

// ╔══════════════════════════════════════════════╗
// ║  CustomizationScreen.kt                      ║
// ║  DETALHES DO PRODUTO (recebe produtoId)      ║
// ║  + ponto da carne, adicionais, observação,   ║
// ║    preço calculado e categoria do produto    ║
// ╚══════════════════════════════════════════════╝

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.forge_burger.ui.theme.CorCard
import com.example.forge_burger.ui.theme.CorCardClaro
import com.example.forge_burger.ui.theme.CorFundo
import com.example.forge_burger.ui.theme.CorLaranja
import com.example.forge_burger.ui.theme.CorTextoCinza
import com.example.forge_burger.ui.theme.ForgeBurgerTheme
import com.example.forge_burger.ui.theme.formatarPreco

@Composable
fun CustomizationScreen(navController: NavHostController, viewModel: BurgerViewModel, produtoId: Int) {
    val produto = viewModel.buscarProduto(produtoId)

    if (produto == null) {
        Column(Modifier.fillMaxSize().background(CorFundo)) {
            BarraTopo("Produto", onVoltar = { navController.popBackStack() })
            EstadoVazio("🤷", "Produto não encontrado", "Ele pode ter sido removido do cardápio.")
        }
        return
    }

    val categoria = viewModel.buscarCategoria(produto.categoriaId)
    val ehLanche = produto.categoriaId == 1

    var quantidade by remember { mutableIntStateOf(1) }
    var pontoCarne by remember { mutableStateOf(viewModel.pontosCarne[1]) }
    val adicionaisEscolhidos = remember { mutableStateListOf<Adicional>() }
    var observacao by remember { mutableStateOf("") }

    // Informação calculada: preço base + adicionais, vezes a quantidade
    val precoUnitario = produto.preco + adicionaisEscolhidos.sumOf { it.preco }
    val precoTotalAtual = precoUnitario * quantidade

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CorFundo)
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(Color(40, 30, 25))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = produto.emoji, fontSize = 110.sp)
                }

                BotaoRedondo(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp)
                        .align(Alignment.TopStart)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                }

                BotaoRedondo(
                    onClick = { viewModel.alternarFavorito(produto.id) },
                    modifier = Modifier
                        .padding(end = 16.dp, top = 16.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (viewModel.ehFavorito(produto.id)) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favoritar",
                        tint = CorLaranja
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = produto.nome,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = formatarPreco(produto.preco),
                        color = CorLaranja,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Dado vindo da OUTRA lista: a categoria do produto (clicável)
                if (categoria != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        onClick = { navController.navigate(Rotas.categoria(categoria.id)) },
                        shape = RoundedCornerShape(20.dp),
                        color = CorCard
                    ) {
                        Text(
                            text = "${categoria.emoji} ${categoria.nome}  ›",
                            color = CorLaranja,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = produto.descricao,
                    color = CorTextoCinza,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                if (produto.ingredientes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    CardEscuro(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Ingredientes Inclusos",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = produto.ingredientes.joinToString("\n") { "• $it" },
                                color = CorTextoCinza,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                if (ehLanche) {
                    Spacer(modifier = Modifier.height(24.dp))
                    TituloSecao("Ponto da Carne")
                    Spacer(modifier = Modifier.height(8.dp))
                    viewModel.pontosCarne.forEach { ponto ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(selected = pontoCarne == ponto, onClick = { pontoCarne = ponto }),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = pontoCarne == ponto,
                                onClick = { pontoCarne = ponto },
                                colors = RadioButtonDefaults.colors(selectedColor = CorLaranja)
                            )
                            Text(ponto, color = Color.White, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    TituloSecao("Adicionais")
                    Spacer(modifier = Modifier.height(8.dp))
                    viewModel.adicionaisDisponiveis.forEach { adicional ->
                        val marcado = adicionaisEscolhidos.contains(adicional)
                        val alternar = {
                            if (marcado) adicionaisEscolhidos.remove(adicional) else adicionaisEscolhidos.add(adicional)
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { alternar() },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = marcado,
                                onCheckedChange = { alternar() },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = CorLaranja,
                                    checkmarkColor = Color.Black,
                                    uncheckedColor = CorTextoCinza
                                )
                            )
                            Text(adicional.nome, color = Color.White, fontSize = 14.sp, modifier = Modifier.weight(1f))
                            Text("+ " + formatarPreco(adicional.preco), color = CorTextoCinza, fontSize = 13.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                TituloSecao("Observações")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = observacao,
                    onValueChange = { observacao = it },
                    placeholder = { Text("Ex: sem picles, molho à parte...", color = CorTextoCinza) },
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    colors = coresCampo(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(24, 24, 24)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SeletorQuantidade(
                    quantidade = quantidade,
                    aoDiminuir = { if (quantidade > 1) quantidade-- },
                    aoAumentar = { quantidade++ },
                    tamanhoBotao = 32
                )

                Spacer(modifier = Modifier.width(12.dp))

                BotaoLaranja(
                    texto = "Adicionar • " + formatarPreco(precoTotalAtual),
                    onClick = {
                        viewModel.adicionarAoCarrinho(
                            produto = produto,
                            pontoCarne = if (ehLanche) pontoCarne else "",
                            adicionais = adicionaisEscolhidos.toList(),
                            observacao = observacao,
                            quantidade = quantidade
                        )
                        navController.irParaAba(Rotas.CARRINHO)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BotaoRedondo(onClick: () -> Unit, modifier: Modifier = Modifier, conteudo: @Composable () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = Color(0, 0, 0, 120),
        modifier = modifier.size(40.dp)
    ) {
        Box(contentAlignment = Alignment.Center) { conteudo() }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun CustomizationScreenPreview() {
    ForgeBurgerTheme {
        CustomizationScreen(rememberNavController(), viewModel(), produtoId = 4)
    }
}
