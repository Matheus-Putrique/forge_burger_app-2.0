package com.example.forge_burger

// ╔══════════════════════════════════════════════╗
// ║  Componentes.kt                              ║
// ║  Pedaços de tela reaproveitados              ║
// ╚══════════════════════════════════════════════╝

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.forge_burger.ui.theme.CorCard
import com.example.forge_burger.ui.theme.CorCardClaro
import com.example.forge_burger.ui.theme.CorFundo
import com.example.forge_burger.ui.theme.CorLaranja
import com.example.forge_burger.ui.theme.CorTextoCinza

// TopAppBar com botão de voltar (usada nas telas internas)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraTopo(titulo: String, onVoltar: () -> Unit, acoes: @Composable () -> Unit = {}) {
    TopAppBar(
        title = { Text(titulo, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = onVoltar) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
            }
        },
        actions = { acoes() },
        windowInsets = TopAppBarDefaults.windowInsets,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = CorFundo,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = CorLaranja
        )
    )
}

// Cabeçalho "BURGERCRAFT" das abas principais
@Composable
fun CabecalhoMarca(onMenu: () -> Unit, onPerfil: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onMenu) {
            Icon(Icons.Default.Menu, contentDescription = "Categorias", tint = Color.White)
        }
        Text(
            text = "BURGERCRAFT",
            color = CorLaranja,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onPerfil) {
            Surface(
                modifier = Modifier.size(34.dp),
                shape = RoundedCornerShape(50),
                color = Color(80, 60, 50)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, contentDescription = "Perfil", tint = Color.White)
                }
            }
        }
    }
}

// Foto do produto: imagem quando houver, senão o emoji num quadrado escuro
@Composable
fun FotoProduto(
    emoji: String,
    modifier: Modifier = Modifier,
    tamanhoEmoji: Int = 28,
    @DrawableRes foto: Int? = null
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = CorCardClaro,
        modifier = modifier
    ) {
        if (foto != null) {
            Image(
                painter = painterResource(foto),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(contentAlignment = Alignment.Center) {
                Text(text = emoji, fontSize = tamanhoEmoji.sp)
            }
        }
    }
}

// Seletor  -  2  +
@Composable
fun SeletorQuantidade(
    quantidade: Int,
    aoDiminuir: () -> Unit,
    aoAumentar: () -> Unit,
    tamanhoBotao: Int = 26
) {
    Surface(shape = RoundedCornerShape(20.dp), color = Color(42, 42, 42)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Button(
                onClick = aoDiminuir,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(tamanhoBotao.dp)
            ) {
                Text("-", color = Color.White)
            }
            Text(
                text = "$quantidade",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Button(
                onClick = aoAumentar,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(tamanhoBotao.dp)
            ) {
                Text("+", color = Color.White)
            }
        }
    }
}

// Botão laranja grande (Adicionar, Finalizar, Salvar...)
@Composable
fun BotaoLaranja(texto: String, onClick: () -> Unit, modifier: Modifier = Modifier, habilitado: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = habilitado,
        shape = RoundedCornerShape(25.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = CorLaranja,
            contentColor = Color.Black,
            disabledContainerColor = CorCardClaro,
            disabledContentColor = CorTextoCinza
        ),
        modifier = modifier.height(50.dp)
    ) {
        Text(texto, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

// Cores padrão dos campos de formulário
@Composable
fun coresCampo() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = CorLaranja,
    unfocusedBorderColor = CorCardClaro,
    focusedLabelColor = CorLaranja,
    unfocusedLabelColor = CorTextoCinza,
    cursorColor = CorLaranja,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedContainerColor = CorCard,
    unfocusedContainerColor = CorCard
)

// Mensagem de "nada aqui"
@Composable
fun EstadoVazio(emoji: String, titulo: String, subtitulo: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Text(emoji, fontSize = 56.sp)
        Spacer(Modifier.height(12.dp))
        Text(titulo, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text(subtitulo, color = CorTextoCinza, fontSize = 14.sp, textAlign = TextAlign.Center)
    }
}

// Linha "Subtotal ........ R$ 10,00"
@Composable
fun LinhaValor(rotulo: String, valor: String, destaque: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(
            text = rotulo,
            color = if (destaque) Color.White else CorTextoCinza,
            fontSize = if (destaque) 20.sp else 14.sp,
            fontWeight = if (destaque) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = valor,
            color = if (destaque) CorLaranja else Color.White,
            fontSize = if (destaque) 20.sp else 14.sp,
            fontWeight = if (destaque) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun TituloSecao(texto: String, modifier: Modifier = Modifier) {
    Text(
        text = texto,
        color = Color.White,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Composable
fun CardEscuro(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CorCard, contentColor = Color.White),
        modifier = modifier
    ) {
        content()
    }
}
