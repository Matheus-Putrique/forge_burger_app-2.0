package com.example.forge_burger

// ╔══════════════════════════════════════════════╗
// ║  NovoProdutoScreen.kt                        ║
// ║  FORMULÁRIO: adiciona Produto no cardápio    ║
// ╚══════════════════════════════════════════════╝

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.forge_burger.ui.theme.CorCard
import com.example.forge_burger.ui.theme.CorFundo
import com.example.forge_burger.ui.theme.CorLaranja
import com.example.forge_burger.ui.theme.CorTextoCinza
import com.example.forge_burger.ui.theme.ForgeBurgerTheme
import com.example.forge_burger.ui.theme.formatarPreco

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NovoProdutoScreen(navController: NavHostController, viewModel: BurgerViewModel, categoriaInicial: Int = -1) {
    var emoji by remember { mutableStateOf("") }
    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var precoTexto by remember { mutableStateOf("") }
    var categoriaId by remember {
        mutableIntStateOf(
            viewModel.buscarCategoria(categoriaInicial)?.id ?: viewModel.categorias.firstOrNull()?.id ?: -1
        )
    }

    // Aceita "12,90" ou "12.90"
    val preco = precoTexto.replace(',', '.').toDoubleOrNull()
    val precoInvalido = precoTexto.isNotBlank() && (preco == null || preco <= 0)
    val podeSalvar = nome.isNotBlank() && preco != null && preco > 0 && viewModel.buscarCategoria(categoriaId) != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CorFundo)
            .imePadding()
    ) {
        BarraTopo("Novo produto", onVoltar = { navController.popBackStack() })

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Prévia do card como vai aparecer no cardápio
            Row(verticalAlignment = Alignment.CenterVertically) {
                FotoProduto(emoji.ifBlank { "🍔" }, tamanhoEmoji = 40, modifier = Modifier.size(84.dp))
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(nome.ifBlank { "Nome do produto" }, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
                    value = emoji,
                    onValueChange = { if (it.length <= 2) emoji = it },
                    label = { Text("Ícone") },
                    placeholder = { Text("🍔") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = coresCampo(),
                    modifier = Modifier.width(84.dp)
                )
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome *") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = coresCampo(),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = precoTexto,
                onValueChange = { precoTexto = it.filter { c -> c.isDigit() || c == ',' || c == '.' } },
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
                value = descricao,
                onValueChange = { descricao = it },
                label = { Text("Descrição") },
                minLines = 3,
                shape = RoundedCornerShape(12.dp),
                colors = coresCampo(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))
            TituloSecao("Categoria *")
            Spacer(Modifier.height(8.dp))

            // Quebram para a linha de baixo quando não cabem, assim nenhuma fica cortada
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                viewModel.categorias.forEach { categoria ->
                    val ativo = categoria.id == categoriaId
                    Surface(
                        onClick = { categoriaId = categoria.id },
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
            texto = "Salvar produto",
            habilitado = podeSalvar,
            onClick = {
                viewModel.adicionarProduto(nome, descricao, preco ?: 0.0, categoriaId, emoji)
                navController.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun NovoProdutoScreenPreview() {
    ForgeBurgerTheme {
        NovoProdutoScreen(rememberNavController(), viewModel())
    }
}
