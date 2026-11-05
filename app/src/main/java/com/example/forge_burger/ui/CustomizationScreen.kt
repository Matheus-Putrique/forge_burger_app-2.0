package com.example.forge_burger.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.forge_burger.data.fotoDoProduto
import com.example.forge_burger.ui.theme.CorCard
import com.example.forge_burger.ui.theme.CorFundo
import com.example.forge_burger.ui.theme.CorLaranja
import com.example.forge_burger.ui.theme.CorTextoCinza
import com.example.forge_burger.ui.theme.formatarPreco
import com.example.forge_burger.viewmodel.BurgerViewModel
import com.example.forge_burger.viewmodel.CategoriaViewModel
import com.example.forge_burger.viewmodel.ProdutoViewModel

@Composable
fun CustomizationScreen(
    navController: NavHostController,
    produtoViewModel: ProdutoViewModel,
    categoriaViewModel: CategoriaViewModel,
    burgerViewModel: BurgerViewModel,
    produtoId: Int
) {
    val estadoProduto by produtoViewModel.uiState.collectAsState()
    val estadoCategoria by categoriaViewModel.uiState.collectAsState()
    val estadoBurger by burgerViewModel.uiState.collectAsState()

    LaunchedEffect(produtoId) {
        produtoViewModel.abrirDetalhesProduto(produtoId)
    }

    val produto = estadoProduto.produtoDetalhe
    if (produto == null) {
        Column(Modifier.fillMaxSize().background(CorFundo)) {
            BarraTopo("Produto", onVoltar = { navController.popBackStack() })
            EstadoVazio("🤷", "Produto não encontrado", "Ele pode ter sido removido do cardápio.")
        }
        return
    }

    val categoria = estadoCategoria.listaCategorias.find { it.id == produto.categoriaId }
    val ehLanche = produto.categoriaId == 1
    val ehFavorito = estadoBurger.favoritos.contains(produto.id)

    val precoUnitario = produto.preco + estadoProduto.adicionaisEscolhidos.sumOf { it.preco }
    val precoTotalAtual = precoUnitario * estadoProduto.quantidadeDetalhe

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CorFundo)
            .imePadding()
    ) {
        BarraTopo(
            titulo = "Detalhes do produto",
            onVoltar = { navController.popBackStack() },
            acoes = {
                IconButton(onClick = { burgerViewModel.alternarFavorito(produto.id) }) {
                    Icon(
                        imageVector = if (ehFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favoritar",
                        tint = CorLaranja
                    )
                }
            }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(Color(40, 30, 25)),
                contentAlignment = Alignment.Center
            ) {
                val fotoId = fotoDoProduto(produto.foto)
                if (fotoId != null) {
                    Image(
                        painter = painterResource(fotoId),
                        contentDescription = produto.nome,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(text = produto.emoji, fontSize = 110.sp)
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

                if (produto.ingredientes.isNotBlank()) {
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
                                text = produto.ingredientes,
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
                    produtoViewModel.pontosCarne.forEach { ponto ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = estadoProduto.pontoCarneEscolhido == ponto,
                                    onClick = { produtoViewModel.escolherPontoCarne(ponto) }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = estadoProduto.pontoCarneEscolhido == ponto,
                                onClick = { produtoViewModel.escolherPontoCarne(ponto) },
                                colors = RadioButtonDefaults.colors(selectedColor = CorLaranja)
                            )
                            Text(ponto, color = Color.White, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    TituloSecao("Adicionais")
                    Spacer(modifier = Modifier.height(8.dp))
                    produtoViewModel.adicionaisDisponiveis.forEach { adicional ->
                        val marcado = estadoProduto.adicionaisEscolhidos.contains(adicional)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { produtoViewModel.alternarAdicional(adicional) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = marcado,
                                onCheckedChange = { produtoViewModel.alternarAdicional(adicional) },
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
                    value = estadoProduto.observacaoDetalhe,
                    onValueChange = { produtoViewModel.mudarObservacaoDetalhe(it) },
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
                    quantidade = estadoProduto.quantidadeDetalhe,
                    aoDiminuir = { produtoViewModel.diminuirQuantidadeDetalhe() },
                    aoAumentar = { produtoViewModel.aumentarQuantidadeDetalhe() },
                    tamanhoBotao = 32
                )

                Spacer(modifier = Modifier.width(12.dp))

                BotaoLaranja(
                    texto = "Adicionar • " + formatarPreco(precoTotalAtual),
                    onClick = {
                        burgerViewModel.adicionarAoCarrinho(
                            produto = produto,
                            pontoCarne = if (ehLanche) estadoProduto.pontoCarneEscolhido else "",
                            adicionais = estadoProduto.adicionaisEscolhidos,
                            observacao = estadoProduto.observacaoDetalhe,
                            quantidade = estadoProduto.quantidadeDetalhe
                        )
                        produtoViewModel.limparPersonalizacaoAtual()
                        navController.irParaAba(Rotas.CARRINHO)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
