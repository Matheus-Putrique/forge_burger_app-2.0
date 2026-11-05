package com.example.forge_burger.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.forge_burger.ui.theme.CorFundo
import com.example.forge_burger.ui.theme.CorLaranja
import com.example.forge_burger.ui.theme.CorTextoCinza
import com.example.forge_burger.viewmodel.BurgerViewModel

@Composable
fun LoginScreen(navController: NavHostController, viewModel: BurgerViewModel) {
    val focusManager = LocalFocusManager.current
    val uiState by viewModel.uiState.collectAsState()

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
                    value = uiState.usuarioCampoLogin,
                    onValueChange = { viewModel.mudarUsuarioLogin(it) },
                    label = { Text("Usuário") },
                    singleLine = true,
                    isError = uiState.erroLogin != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii, imeAction = ImeAction.Next),
                    shape = RoundedCornerShape(12.dp),
                    colors = coresCampo(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.senhaCampoLogin,
                    onValueChange = { viewModel.mudarSenhaLogin(it) },
                    label = { Text("Senha") },
                    singleLine = true,
                    isError = uiState.erroLogin != null,
                    visualTransformation = if (uiState.mostrarSenhaLogin) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { confirmarEntradaUsuario(viewModel, focusManager, navController) }
                    ),
                    trailingIcon = {
                        TextButton(onClick = { viewModel.alternarVisibilidadeSenha() }) {
                            Text(if (uiState.mostrarSenhaLogin) "Ocultar" else "Mostrar", color = CorTextoCinza, fontSize = 12.sp)
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = coresCampo(),
                    modifier = Modifier.fillMaxWidth()
                )

                uiState.erroLogin?.let { mensagemErro ->
                    Spacer(Modifier.height(8.dp))
                    Text(mensagemErro, color = Color(0xFFFF6B6B), fontSize = 13.sp)
                }

                Spacer(Modifier.height(16.dp))
                BotaoLaranja(
                    texto = "Entrar",
                    onClick = { confirmarEntradaUsuario(viewModel, focusManager, navController) },
                    modifier = Modifier.fillMaxWidth()
                )
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

private fun confirmarEntradaUsuario(
    viewModel: BurgerViewModel,
    focusManager: FocusManager,
    navController: NavHostController
) {
    if (viewModel.tentarEntrar()) {
        focusManager.clearFocus()
        navController.navigate(Rotas.INICIO) {
            popUpTo(Rotas.LOGIN) { inclusive = true }
            launchSingleTop = true
        }
    }
}
