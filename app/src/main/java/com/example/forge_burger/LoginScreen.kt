package com.example.forge_burger

// ╔══════════════════════════════════════════════╗
// ║  LoginScreen.kt                              ║
// ║  LOGIN em memória: admin/admin123 · user/1234║
// ║  Sucesso → Início (limpa a pilha)            ║
// ╚══════════════════════════════════════════════╝

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
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

@Composable
fun LoginScreen(navController: NavHostController, viewModel: BurgerViewModel) {
    val focusManager = LocalFocusManager.current

    var usuario by rememberSaveable { mutableStateOf("") }
    var senha by rememberSaveable { mutableStateOf("") }
    var mostrarSenha by rememberSaveable { mutableStateOf(false) }
    var erro by rememberSaveable { mutableStateOf<String?>(null) }

    fun entrar() {
        erro = when {
            usuario.isBlank() || senha.isBlank() -> "Preencha usuário e senha"
            !viewModel.login(usuario, senha) -> "Usuário ou senha incorretos"
            else -> null
        }
        if (erro == null) {
            focusManager.clearFocus()
            // Vai para o Início e tira o Login da pilha (voltar não retorna aqui)
            navController.navigate(Rotas.INICIO) {
                popUpTo(Rotas.LOGIN) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CorFundo)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🍔", fontSize = 64.sp)
        Spacer(Modifier.height(8.dp))
        Text("BURGERCRAFT", color = CorLaranja, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text("Entre para fazer seu pedido", color = CorTextoCinza, fontSize = 14.sp)

        Spacer(Modifier.height(32.dp))

        CardEscuro(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = usuario,
                    onValueChange = { usuario = it; erro = null },
                    label = { Text("Usuário") },
                    singleLine = true,
                    isError = erro != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii, imeAction = ImeAction.Next),
                    shape = RoundedCornerShape(12.dp),
                    colors = coresCampo(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = senha,
                    onValueChange = { senha = it; erro = null },
                    label = { Text("Senha") },
                    singleLine = true,
                    isError = erro != null,
                    visualTransformation = if (mostrarSenha) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { entrar() }),
                    trailingIcon = {
                        TextButton(onClick = { mostrarSenha = !mostrarSenha }) {
                            Text(if (mostrarSenha) "Ocultar" else "Mostrar", color = CorTextoCinza, fontSize = 12.sp)
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = coresCampo(),
                    modifier = Modifier.fillMaxWidth()
                )

                erro?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(it, color = Color(0xFFFF6B6B), fontSize = 13.sp)
                }

                Spacer(Modifier.height(16.dp))
                BotaoLaranja(texto = "Entrar", onClick = { entrar() }, modifier = Modifier.fillMaxWidth())
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(
            text = "Demonstração\nAdministrador: admin / admin123\nCliente: user / 1234",
            color = CorTextoCinza,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun LoginScreenPreview() {
    ForgeBurgerTheme {
        LoginScreen(rememberNavController(), viewModel())
    }
}