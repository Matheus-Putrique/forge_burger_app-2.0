package com.example.forge_burger.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.forge_burger.ui.theme.CorCard
import com.example.forge_burger.ui.theme.CorFundo
import com.example.forge_burger.ui.theme.CorLaranja
import com.example.forge_burger.ui.theme.CorTextoCinza
import com.example.forge_burger.ui.theme.formatarPreco
import com.example.forge_burger.viewmodel.CategoriaViewModel
import com.example.forge_burger.viewmodel.ProdutoViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NovoProdutoScreen(
    navController: NavHostController,
    produtoViewModel: ProdutoViewModel,
    categoriaViewModel: CategoriaViewModel
) {
    val estadoProduto by produtoViewModel.uiState.collectAsState()
    val estadoCategoria by categoriaViewModel.uiState.collectAsState()

    val preco = estadoProduto.precoCampo.replace(',', '.').toDoubleOrNull()
    val precoInvalido = estadoProduto.precoCampo.isNotBlank() && (preco == null || preco <= 0)
    val categoriaExiste = estadoCategoria.listaCategorias.any { it.id == estadoProduto.categoriaIdEscolhida }
    val podeSalvar = estadoProduto.nomeCampo.isNotBlank() && preco != null && preco > 0 && categoriaExiste

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CorFundo)
            .imePadding()
    ) {
        BarraTopo(
            titulo = if (estadoProduto.estouEditando) "Editar produto" else "Novo produto",
            onVoltar = {
                produtoViewModel.limparEdicaoProduto()
                navController.popBackStack()
            }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                FotoProduto(
                    emoji = estadoProduto.emojiCampo.ifBlank { "🍔" },
                    foto = estadoProduto.produtoEmEdicao?.foto,
                    tamanhoEmoji = 40,
                    modifier = Modifier.size(84.dp)
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        estadoProduto.nomeCampo.ifBlank { "Nome do produto" },
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (preco != null && preco > 0) formatarPreco(preco) else "R$ --",
                        color = CorLaranja,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Row {
                OutlinedTextField(
                    value = estadoProduto.emojiCampo,
                    onValueChange = { produtoViewModel.mudarEmoji(it) },
                    label = { Text("Ícone") },
                    placeholder = { Text("🍔") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = coresCampo(),
                    modifier = Modifier.width(84.dp)
                )
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = estadoProduto.nomeCampo,
                    onValueChange = { produtoViewModel.mudarNome(it) },
                    label = { Text("Nome *") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = coresCampo(),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = estadoProduto.precoCampo,
                onValueChange = { produtoViewModel.mudarPreco(it) },
                label = { Text("Preço (R$) *") },
                placeholder = { Text("12,90") },
                singleLine = true,
                isError = precoInvalido,
                supportingText = { if (precoInvalido) Text("Digite um preço válido") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(12.dp),
                colors = coresCampo(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(4.dp))

            OutlinedTextField(
                value = estadoProduto.descricaoCampo,
                onValueChange = { produtoViewModel.mudarDescricao(it) },
                label = { Text("Descrição") },
                minLines = 3,
                shape = RoundedCornerShape(12.dp),
                colors = coresCampo(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))
            TituloSecao("Categoria *")
            Spacer(Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                estadoCategoria.listaCategorias.forEach { categoria ->
                    val ativo = categoria.id == estadoProduto.categoriaIdEscolhida
                    Surface(
                        onClick = { produtoViewModel.escolherCategoriaFormulario(categoria.id) },
                        shape = RoundedCornerShape(20.dp),
                        color = if (ativo) CorLaranja else CorCard,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "${categoria.emoji} ${categoria.nome}",
                            color = if (ativo) Color.Black else CorTextoCinza,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        BotaoLaranja(
            texto = if (estadoProduto.estouEditando) "Atualizar produto" else "Salvar produto",
            habilitado = podeSalvar,
            onClick = {
                produtoViewModel.salvarProduto()
                navController.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}
